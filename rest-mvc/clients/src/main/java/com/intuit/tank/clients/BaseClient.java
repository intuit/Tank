/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.clients;

import com.intuit.tank.clients.util.*;

import jakarta.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public abstract class BaseClient {
    private static final Logger LOGGER = LogManager.getLogger(BaseClient.class);

    protected String baseUrl;

    protected String token;

    protected HttpClient client;
    protected RestUrlBuilder urlBuilder;

    public BaseClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        setBaseUrl(serviceUrl, token);
        client = createHttpClient(proxyServer, proxyPort);
        LOGGER.info(new ObjectMessage(Map.of("Message", "HttpClient created for URL " + baseUrl + ": proxy="
                + (proxyServer != null ? proxyServer + ":" + proxyPort : "none"))));
    }

    private HttpClient createHttpClient(final String proxyServer, final Integer proxyPort) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3));

        if(proxyServer != null && proxyPort != null) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(proxyServer, proxyPort)));
        }

        return builder.build();
    }

    protected HttpRequest.Builder requestBuilder(String path) {
        String url = urlBuilder.buildUrl(path);

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Accept-Encoding", "gzip")
                .timeout(Duration.ofSeconds(3));
    }

    protected HttpRequest.Builder requestBuilder(String path,  @Nullable Object... parameters) {
        String url = urlBuilder.buildUrl(path, parameters);

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Accept-Encoding", "gzip")
                .timeout(Duration.ofSeconds(30));
    }

    /**
     * Like {@link HttpResponse.BodyHandlers#ofString()}, but gunzips the body when the response is gzip encoded.
     * java.net.http.HttpClient does not decompress responses itself.
     */
    protected static HttpResponse.BodyHandler<String> ofString() {
        return responseInfo -> {
            if (!isGzip(responseInfo.headers())) {
                return HttpResponse.BodyHandlers.ofString().apply(responseInfo);
            }
            Charset charset = charsetFrom(responseInfo.headers());
            return HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray(), body -> {
                try (InputStream in = new GZIPInputStream(new ByteArrayInputStream(body))) {
                    return new String(in.readAllBytes(), charset);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        };
    }

    /**
     * Like {@link HttpResponse.BodyHandlers#ofInputStream()}, but gunzips the body when the response is gzip encoded.
     */
    protected static HttpResponse.BodyHandler<InputStream> ofInputStream() {
        return responseInfo -> {
            HttpResponse.BodySubscriber<InputStream> subscriber = HttpResponse.BodySubscribers.ofInputStream();
            if (!isGzip(responseInfo.headers())) {
                return subscriber;
            }
            // GZIPInputStream reads the gzip header in its constructor, so open it on first read
            // rather than blocking the client's executor while the body is mapped
            return HttpResponse.BodySubscribers.mapping(subscriber, LazyGzipInputStream::new);
        };
    }

    private static boolean isGzip(HttpHeaders headers) {
        return headers.firstValue("Content-Encoding").map(v -> v.trim().equalsIgnoreCase("gzip")).orElse(false);
    }

    private static Charset charsetFrom(HttpHeaders headers) {
        String contentType = headers.firstValue("Content-Type").orElse("");
        for (String param : contentType.split(";")) {
            String[] pair = param.trim().split("=", 2);
            if (pair.length == 2 && pair[0].trim().equalsIgnoreCase("charset")) {
                try {
                    return Charset.forName(pair[1].trim().replace("\"", ""));
                } catch (IllegalArgumentException e) {
                    break;
                }
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static final class LazyGzipInputStream extends FilterInputStream {
        private final InputStream compressed;

        private LazyGzipInputStream(InputStream compressed) {
            super(null);
            this.compressed = compressed;
        }

        private InputStream delegate() throws IOException {
            if (in == null) {
                in = new GZIPInputStream(compressed);
            }
            return in;
        }

        @Override
        public int read() throws IOException {
            return delegate().read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return delegate().read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return delegate().skip(n);
        }

        @Override
        public int available() throws IOException {
            return delegate().available();
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public void close() throws IOException {
            if (in != null) {
                in.close();
            } else {
                compressed.close();
            }
        }
    }

    public void setBaseUrl(String baseUrl, String token) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl + getServiceBaseUrl();
            urlBuilder = new RestUrlBuilder(this.baseUrl);
        }
        this.token = (token == null) ? this.token : token;
    }

    protected abstract String getServiceBaseUrl();

    public static boolean checkStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    protected void handleError(HttpRequest request, Exception e){
        LOGGER.error("Failed to {} {}: {}", request.method(), request.uri(), e.getMessage());
        throw new RuntimeException("Error during " + request.method() + " to " + request.uri() + ": " + e.getMessage(), e);
    }

    public String ping() {
        HttpRequest request = requestBuilder("/ping").GET().build();
        try {
            HttpResponse<String> response = client.send(request, ofString());
            if(checkStatusCode(response.statusCode())) {
                return response.body();
            } else {
                throw new ClientException(response.body(), response.statusCode());
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }
}
