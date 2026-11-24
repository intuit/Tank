package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
 * %%
 * Copyright (C) 2011 - 2026 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Protocol;
import com.intuit.tank.conversation.Request;
import com.intuit.tank.conversation.Response;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.vm.exception.WatsParseException;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Reads a HAR (HTTP Archive) file, as saved by the Chrome, Firefox or Safari developer tools, into script steps.
 * <p>
 * Each HAR entry becomes the same {@link Transaction} the Tank Proxy records, so {@link OwaspReader} and the script
 * filters treat both alike. Entries are read one at a time, so a large HAR is never held in memory as a whole tree.
 * <p>
 * What is left out:
 * <ul>
 * <li>entries that are not http or https (data:, ws:, chrome-extension:)</li>
 * <li>entries with no response (blocked, cancelled or failed requests)</li>
 * <li>response bodies that are binary (base64) or not text, because script steps keep the response as a string</li>
 * </ul>
 */
public class HarReader implements RecordedScriptReader {
    private static final Logger LOG = LogManager.getLogger(HarReader.class);

    // entries are read one at a time from a shared parser, so the tokens after each one are expected
    private static final JsonMapper MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS).build();

    private static final Map<Integer, String> REASONS = Map.ofEntries(
            Map.entry(100, "Continue"), Map.entry(101, "Switching Protocols"),
            Map.entry(200, "OK"), Map.entry(201, "Created"), Map.entry(202, "Accepted"),
            Map.entry(204, "No Content"), Map.entry(206, "Partial Content"),
            Map.entry(301, "Moved Permanently"), Map.entry(302, "Found"), Map.entry(303, "See Other"),
            Map.entry(304, "Not Modified"), Map.entry(307, "Temporary Redirect"), Map.entry(308, "Permanent Redirect"),
            Map.entry(400, "Bad Request"), Map.entry(401, "Unauthorized"), Map.entry(403, "Forbidden"),
            Map.entry(404, "Not Found"), Map.entry(405, "Method Not Allowed"), Map.entry(409, "Conflict"),
            Map.entry(410, "Gone"), Map.entry(412, "Precondition Failed"), Map.entry(415, "Unsupported Media Type"),
            Map.entry(422, "Unprocessable Entity"), Map.entry(429, "Too Many Requests"),
            Map.entry(500, "Internal Server Error"), Map.entry(501, "Not Implemented"), Map.entry(502, "Bad Gateway"),
            Map.entry(503, "Service Unavailable"), Map.entry(504, "Gateway Timeout"));

    /**
     * Tells a HAR from Tank Proxy XML by its first character (a HAR is a JSON object, the recording is XML). The
     * reader is left where it started.
     */
    public static boolean looksLikeHar(BufferedReader reader) throws IOException {
        reader.mark(8192);
        try {
            int c;
            do {
                c = reader.read();
            } while (c == '\uFEFF' || (c != -1 && Character.isWhitespace(c)));
            return c == '{';
        } finally {
            reader.reset();
        }
    }

    @Override
    public List<ScriptStep> read(String har) throws WatsParseException {
        return read(new StringReader(har));
    }

    @Override
    public List<ScriptStep> read(Reader reader) throws WatsParseException {
        return new OwaspReader().transactionsToRequest(toTransactions(reader));
    }

    /**
     * Converts a HAR into the transactions the Tank Proxy would have recorded, in the order the requests started.
     *
     * @throws WatsParseException
     *             if the reader does not hold JSON in HAR form (an object with log.entries)
     */
    public List<Transaction> toTransactions(Reader reader) throws WatsParseException {
        List<Timed> converted = new ArrayList<>();
        boolean foundEntries = false;
        int skipped = 0;
        try (JsonParser parser = MAPPER.createParser(reader)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw notHar("expected a JSON object");
            }
            while (parser.nextToken() == JsonToken.PROPERTY_NAME) {
                String top = parser.currentName();
                parser.nextToken();
                if (!"log".equals(top)) {
                    parser.skipChildren();
                    continue;
                }
                if (parser.currentToken() != JsonToken.START_OBJECT) {
                    throw notHar("log is not an object");
                }
                while (parser.nextToken() == JsonToken.PROPERTY_NAME) {
                    String name = parser.currentName();
                    JsonToken value = parser.nextToken();
                    if (!"entries".equals(name)) {
                        parser.skipChildren();
                        continue;
                    }
                    if (value != JsonToken.START_ARRAY) {
                        throw notHar("log.entries is not an array");
                    }
                    foundEntries = true;
                    while (parser.nextToken() == JsonToken.START_OBJECT) {
                        JsonNode entry = MAPPER.readTree(parser);
                        Transaction transaction = toTransaction(entry);
                        if (transaction == null) {
                            skipped++;
                        } else {
                            converted.add(new Timed(parseStart(entry), converted.size(), transaction));
                        }
                    }
                }
            }
        } catch (WatsParseException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new WatsParseException(e, "not a readable HAR file: " + e.getMessage(), 0, 0);
        }
        if (!foundEntries) {
            throw notHar("it has no log.entries");
        }
        if (converted.isEmpty()) {
            throw new WatsParseException(null, "the HAR has no http or https requests with a response", 0, 0);
        }
        if (skipped > 0) {
            LOG.info("HAR import skipped {} entries that were not http(s) or had no response", skipped);
        }
        // browsers write entries in start order already, but not every tool does
        if (converted.stream().allMatch(t -> t.started() != null)) {
            converted.sort(Comparator.<Timed, OffsetDateTime>comparing(Timed::started).thenComparingInt(Timed::position));
        }
        List<Transaction> result = new ArrayList<>(converted.size());
        converted.forEach(t -> result.add(t.transaction()));
        return result;
    }

    private static WatsParseException notHar(String why) {
        return new WatsParseException(null, "not a HAR file: " + why, 0, 0);
    }

    private static OffsetDateTime parseStart(JsonNode entry) {
        try {
            return OffsetDateTime.parse(entry.path("startedDateTime").asString(""));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * @return the transaction for one HAR entry, or null when the entry is not an http(s) request that got a response
     */
    static Transaction toTransaction(JsonNode entry) {
        JsonNode req = entry.path("request");
        JsonNode resp = entry.path("response");
        int status = resp.path("status").asInt(0);
        if (status <= 0) {
            return null;
        }
        String url = req.path("url").asString("");
        String scheme = scheme(url);
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            return null;
        }

        Request request = new Request();
        request.setProtocol(Protocol.valueOf(scheme));
        String version = httpVersion(req.path("httpVersion").asString(""));
        request.setFirstLine(req.path("method").asString("GET").toUpperCase(Locale.ROOT) + " " + pathAndQuery(url)
                + " " + version);
        String authority = addHeaders(req.path("headers"), request::addHeader);
        if (!hasHeader(request.getHeaders(), "host")) {
            request.addHeader("Host", StringUtils.defaultIfBlank(authority, authority(url)));
        }
        if (!hasHeader(request.getHeaders(), "cookie") && req.path("cookies").isArray() && req.path("cookies").size() > 0) {
            StringJoiner cookies = new StringJoiner("; ");
            req.path("cookies").forEach(c -> cookies.add(c.path("name").asString("") + "=" + c.path("value").asString("")));
            request.addHeader("Cookie", cookies.toString());
        }
        request.setBody(requestBody(req.path("postData")));

        Response response = new Response();
        String statusText = resp.path("statusText").asString("");
        response.setFirstLine(httpVersion(resp.path("httpVersion").asString("")) + " " + status + " "
                + (StringUtils.isBlank(statusText) ? REASONS.getOrDefault(status, "Unknown") : statusText));
        addHeaders(resp.path("headers"), response::addHeader);
        response.setBody(responseBody(resp.path("content")));

        Transaction transaction = new Transaction();
        transaction.setRequest(request);
        transaction.setResponse(response);
        return transaction;
    }

    /**
     * Adds the real headers, skipping HTTP/2 pseudo-headers such as :authority, which Chrome records as headers.
     *
     * @return the value of :authority, if there was one
     */
    private static String addHeaders(JsonNode headers, BiConsumer<String, String> sink) {
        String authority = null;
        if (headers.isArray()) {
            for (JsonNode header : headers) {
                String name = header.path("name").asString("");
                String value = header.path("value").asString("");
                if (name.startsWith(":")) {
                    if (":authority".equals(name)) {
                        authority = value;
                    }
                } else if (!name.isEmpty()) {
                    sink.accept(name, value);
                }
            }
        }
        return authority;
    }

    private static boolean hasHeader(List<Header> headers, String name) {
        return headers.stream().anyMatch(h -> h.getKey().equalsIgnoreCase(name));
    }

    private static byte[] requestBody(JsonNode postData) {
        if (postData.isMissingNode()) {
            return null;
        }
        String text = postData.path("text").asString("");
        if (text.isEmpty() && postData.path("params").isArray()
                && postData.path("mimeType").asString("").toLowerCase(Locale.ROOT).contains("x-www-form-urlencoded")) {
            StringJoiner form = new StringJoiner("&");
            for (JsonNode param : postData.path("params")) {
                form.add(URLEncoder.encode(param.path("name").asString(""), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(param.path("value").asString(""), StandardCharsets.UTF_8));
            }
            text = form.toString();
        }
        return text.isEmpty() ? null : text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] responseBody(JsonNode content) {
        String text = content.path("text").asString("");
        if (text.isEmpty() || "base64".equalsIgnoreCase(content.path("encoding").asString(""))) {
            return null;
        }
        String mime = content.path("mimeType").asString("").toLowerCase(Locale.ROOT);
        boolean textual = mime.isEmpty() || mime.startsWith("text/") || mime.contains("json") || mime.contains("xml")
                || mime.contains("javascript") || mime.contains("x-www-form-urlencoded");
        return textual ? text.getBytes(StandardCharsets.UTF_8) : null;
    }

    private static String httpVersion(String har) {
        String v = har.trim().toLowerCase(Locale.ROOT);
        if (v.isEmpty()) {
            return "HTTP/1.1";
        }
        return switch (v) {
            case "h2", "http/2", "http/2.0" -> "HTTP/2.0";
            case "h3", "http/3", "http/3.0" -> "HTTP/3.0";
            default -> v.startsWith("http/") ? "HTTP/" + v.substring(5) : "HTTP/1.1";
        };
    }

    private static String scheme(String url) {
        int colon = url.indexOf(':');
        return colon > 0 ? url.substring(0, colon).toLowerCase(Locale.ROOT) : "";
    }

    /** host or host:port, with the port only when the URL has one */
    private static String authority(String url) {
        int start = url.indexOf("//");
        if (start < 0) {
            return "";
        }
        int end = url.length();
        for (char c : new char[] { '/', '?', '#' }) {
            int i = url.indexOf(c, start + 2);
            if (i >= 0 && i < end) {
                end = i;
            }
        }
        String authority = url.substring(start + 2, end);
        int at = authority.lastIndexOf('@');
        return at >= 0 ? authority.substring(at + 1) : authority;
    }

    /** The path and query exactly as the request line carries them, e.g. /a/b?c=d */
    private static String pathAndQuery(String url) {
        try {
            URI uri = new URI(url);
            String path = StringUtils.defaultIfEmpty(uri.getRawPath(), "/");
            return uri.getRawQuery() == null ? path : path + "?" + uri.getRawQuery();
        } catch (URISyntaxException e) {
            // a URL with a raw space or other character java.net.URI refuses: take what follows the authority
            int hash = url.indexOf('#');
            String noFragment = hash >= 0 ? url.substring(0, hash) : url;
            int start = noFragment.indexOf("//");
            if (start < 0) {
                return "/";
            }
            int slash = noFragment.indexOf('/', start + 2);
            int query = noFragment.indexOf('?', start + 2);
            if (query >= 0 && (slash < 0 || query < slash)) {
                return "/" + noFragment.substring(query);
            }
            return slash >= 0 ? noFragment.substring(slash) : "/";
        }
    }

    private record Timed(OffsetDateTime started, int position, Transaction transaction) {
    }
}
