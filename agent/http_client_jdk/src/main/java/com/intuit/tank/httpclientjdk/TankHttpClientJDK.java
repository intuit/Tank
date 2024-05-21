package com.intuit.tank.httpclientjdk;

import java.io.ByteArrayInputStream;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.math.BigInteger;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpUtil;
import com.intuit.tank.http.TankHttpUtil.PartHolder;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.settings.AgentConfig;

public class TankHttpClientJDK implements TankHttpClient {

    private static final Logger LOG = LogManager.getLogger(TankHttpClientJDK.class);

    private HttpClient httpclient;
    private HttpClient.Builder httpclientBuilder;
    private final CookieManager cookieManager = new CookieManager();

    /**
     * no-arg constructor for client
     */
    public TankHttpClientJDK() {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        httpclientBuilder = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .connectTimeout(Duration.ofSeconds(30))
                .followRedirects(HttpClient.Redirect.ALWAYS);
        httpclient = httpclientBuilder.build();
    }

    public Object createHttpClient() { return null; }

    public void setHttpClient(Object httpClient) {}

    public void setConnectionTimeout(long connectionTimeout) {
        httpclientBuilder.connectTimeout(Duration.ofMillis(connectionTimeout));
        httpclient = httpclientBuilder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient3.TankHttpClient#doGet(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doGet(BaseRequest request) {
        HttpRequest.Builder httpget = HttpRequest.newBuilder(URI.create(request.getRequestUrl()));
        request.getHeaderInformation().forEach(httpget::header);
        sendRequest(request, httpget.build(), request.getBody());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient3.TankHttpClient#doPut(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPut(BaseRequest request) {
        String requestBody = request.getBody();
        HttpRequest.Builder httpput = HttpRequest.newBuilder(URI.create(request.getRequestUrl()))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", request.getContentType());
        request.getHeaderInformation().forEach(httpput::header);
        sendRequest(request, httpput.build(), requestBody);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient3.TankHttpClient#doDelete(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doDelete(BaseRequest request) {
        HttpRequest.Builder httpdelete = HttpRequest.newBuilder(URI.create(request.getRequestUrl())).DELETE();
        request.getHeaderInformation().forEach(httpdelete::header);
        sendRequest(request, httpdelete.build(), request.getBody());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient3.TankHttpClient#doOptions(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doOptions(BaseRequest request) {
        HttpRequest.Builder httpoptions = HttpRequest.newBuilder(URI.create(request.getRequestUrl()))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody());
        request.getHeaderInformation().forEach(httpoptions::header);
        sendRequest(request, httpoptions.build(), request.getBody());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient3.TankHttpClient#doPost(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPost(BaseRequest request) {
        String requestBody = request.getBody();
        HttpRequest.Builder httppost = HttpRequest.newBuilder(URI.create(request.getRequestUrl()));
        if (request.getContentType().toLowerCase().startsWith(BaseRequest.CONTENT_TYPE_MULTIPART)) {
            String boundary = new BigInteger(256, new Random()).toString();
            httppost.POST(ofMimeMultipartData(request, boundary))
                    .header("Content-Type", "multipart/form-data;boundary=" + boundary);
        } else {
            httppost.POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", request.getContentType());
        }
        request.getHeaderInformation().forEach(httppost::header);
        sendRequest(request, httppost.build(), requestBody);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient3.TankHttpClient#addAuth(com.intuit.tank.http.
     * AuthCredentials)
     */
    @Override
    public void addAuth(AuthCredentials creds) {}

    /**
     *
     */
    @Override
    public void setCookie(TankCookie cookie) {
        HttpCookie sessionCookie = new HttpCookie(cookie.getName(), cookie.getValue());
        sessionCookie.setDomain(cookie.getDomain());
        sessionCookie.setPath(cookie.getPath());

        cookieManager.getCookieStore().add(URI.create(cookie.getDomain()), sessionCookie);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.httpclient3.TankHttpClient#clearSession()
     */
    @Override
    public void clearSession() {
        cookieManager.getCookieStore().removeAll();
    }

    @Override
    public void setProxy(String proxyhost, int proxyport) {
        if (StringUtils.isNotBlank(proxyhost)) {
            httpclientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyhost, proxyport)));
        } else {
            httpclientBuilder.proxy(HttpClient.Builder.NO_PROXY);
        }
        httpclient = httpclientBuilder.build();
    }

    private void sendRequest(BaseRequest request, @Nonnull HttpRequest method, String requestBody) {
        String uri = null;
        long waitTime = 0L;

        try {
            uri = method.uri().toString();
            if (LOG.isDebugEnabled()) LOG.debug(request.getLogUtil().getLogMessage(
                    "About to " + method.method() + " request to " + uri + " with requestBody  " + requestBody, LogEventType.Informational));
            List<String> cookies = cookieManager.getCookieStore().getCookies().stream().map(httpcookie -> "REQUEST COOKIE: " + httpcookie.toString()).collect(Collectors.toList());
            request.logRequest(uri, requestBody, method.method(), request.getHeaderInformation(), cookies, false);
            long startTime = System.currentTimeMillis();
            request.setTimestamp(new Date(startTime));
            HttpResponse<byte[]> response = httpclient.send(method, HttpResponse.BodyHandlers.ofByteArray());
            // read response body
            byte[] responseBody = (response.statusCode() != 203 && response.statusCode() != 202 && response.statusCode() != 204)
                    ? response.body()
                    : new byte[0];
            waitTime = System.currentTimeMillis() - startTime;
            processResponse(responseBody, waitTime, request, response.version().name(), response.statusCode(), response.headers());
        } catch (UnknownHostException uhex) {
            LOG.error(request.getLogUtil().getLogMessage("UnknownHostException to url: " + uri + " |  error: " + uhex.toString(), LogEventType.IO), uhex);
        } catch (SocketException sex) {
            LOG.error(request.getLogUtil().getLogMessage("SocketException to url: " + uri + " |  error: " + sex.toString(), LogEventType.IO), sex);
        } catch (Exception ex) {
            LOG.error(request.getLogUtil().getLogMessage("Could not do " + method.method() + " to url " + uri + " |  error: " + ex.toString(), LogEventType.IO), ex);
            throw new RuntimeException(ex);
        } finally {
            if (method.method().equalsIgnoreCase("post") && request.getLogUtil().getAgentConfig().getLogPostResponse()) {
                LOG.info(request.getLogUtil().getLogMessage(
                        "Response from POST to " + request.getRequestUrl() + " got status code " + request.getResponse().getHttpCode() + " BODY { " + request.getResponse().getBody() + " }",
                        LogEventType.Informational));
            }
        }
        if (waitTime != 0) {
            doWaitDueToLongResponse(request, waitTime, uri);
        }
    }

    /**
     * Wait for the amount of time it took to get a response from the system if
     * the response time is over some threshold specified in the properties
     * file. This will ensure users don't bunch up together after a blip on the
     * system under test
     * 
     * @param responseTime
     *            - response time of the request; this will also be the time to
     *            sleep
     * @param uri
     */
    private void doWaitDueToLongResponse(BaseRequest request, long responseTime, String uri) {
        try {
            AgentConfig config = request.getLogUtil().getAgentConfig();
            long maxAgentResponseTime = config.getMaxAgentResponseTime();
            if (maxAgentResponseTime < responseTime) {
                long waitTime = Math.min(config.getMaxAgentWaitTime(), responseTime);
                LOG.warn(request.getLogUtil().getLogMessage("Response time to slow | delaying " + waitTime + " ms | url --> " + uri, LogEventType.Script));
                Thread.sleep(waitTime);
            }
        } catch (InterruptedException e) {
            LOG.warn("Interrupted", e);
        }
    }

    /**
     * Process the response data
     */
    private void processResponse(byte[] bResponse, long waitTime, BaseRequest request, String message, int httpCode, HttpHeaders headers) {
        BaseResponse response = request.getResponse();
        try {
            if (response == null) {
                // Create new response object based on content-type
                String contentType = headers.map().entrySet().stream()
                        .filter(h -> "content-type".equalsIgnoreCase(h.getKey()))
                        .map(Map.Entry::getValue)
                        .map(list -> StringUtils.join(list, ','))
                        .findFirst().orElse("");
                response = TankHttpUtil.newResponseObject(contentType);
                request.setResponse(response);
            }

            // Get response detail information
            response.setHttpMessage(message);
            response.setHttpCode(httpCode);

            // Get response header information
            for (Map.Entry<String, List<String>> header : headers.map().entrySet()) {
                if (!header.getKey().equalsIgnoreCase(":status")) {
                    response.setHeader(header.getKey(), StringUtils.join(header.getValue(), ','));
                }
            }

            List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
            if (!cookies.isEmpty()) {
                for (HttpCookie cookie : cookies) {
                    response.setCookie(cookie.getName(), cookie.getValue());
                }
            }
            response.setResponseTime(waitTime);

            String contentEncoding = response.getHttpHeader("Content-Encoding");
            bResponse = StringUtils.equalsIgnoreCase(contentEncoding, "gzip") ?
                    new GZIPInputStream(new ByteArrayInputStream(bResponse)).readAllBytes() :
                    bResponse;
            response.setResponseBody(bResponse);

        } catch (Exception ex) {
            LOG.warn("Unable to get response: " + ex.getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("******** RESPONSE ***********");
                LOG.debug(response.getLogMsg());
            }
        }
    }

    private HttpRequest.BodyPublisher ofMimeMultipartData(BaseRequest request, String boundary) {
        // Result request body
        List<byte[]> byteArrays = new ArrayList<>();
        // Separator with boundary
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);
        // Iterating over data parts
        for (PartHolder part : TankHttpUtil.getPartsFromBody(request)) {
            // Opening boundary
            byteArrays.add(separator);
            byteArrays.add(("\"" + part.getPartName() + "\"\r\n\r\n" + part.getBodyAsString() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
        // Closing boundary
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        // Serializing as byte array
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
