package com.intuit.tank.httpclient5;

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

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;


import com.intuit.tank.vm.settings.TankConfig;
import jakarta.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.DeflateInputStream;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.HostnameVerificationPolicy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brotli.dec.BrotliInputStream;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpUtil;
import com.intuit.tank.http.TankHttpUtil.PartHolder;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.settings.AgentConfig;

public class TankHttpClient5 implements TankHttpClient {

    private static final Logger LOG = LogManager.getLogger(TankHttpClient5.class);

    /**
     * Response decoders, in the order advertised in Accept-Encoding. Brotli uses org.brotli:dec
     * since HttpClient's built-in brotli support requires brotli4j.
     */
    private static final LinkedHashMap<String, InputStreamFactory> CONTENT_DECODERS = new LinkedHashMap<>();
    static {
        CONTENT_DECODERS.put("gzip", GZIPInputStream::new);
        CONTENT_DECODERS.put("x-gzip", GZIPInputStream::new);
        CONTENT_DECODERS.put("deflate", DeflateInputStream::new);
        CONTENT_DECODERS.put("br", BrotliInputStream::new);
    }

    private CloseableHttpClient httpclient;
    private final HttpClientContext context;
    private final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private final Collection<String> mimeTypes = new TankConfig().getAgentConfig().getTextMimeTypeRegex();

    /**
     * no-arg constructor for client
     */
    public TankHttpClient5() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofSeconds(30))
                .setCircularRedirectsAllowed(true)
                .setAuthenticationEnabled(true)
                .setRedirectsEnabled(true)
                .setCookieSpec(StandardCookieSpec.RELAXED)
                .setMaxRedirects(100)
                .build();

        // Make sure the same context is used to execute logically related
        // requests
        context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        context.setUserToken(UUID.randomUUID());
        context.setCookieStore(new BasicCookieStore());
        context.setRequestConfig(requestConfig);
    }

    public Object createHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setTlsSocketStrategy(ClientTlsStrategyBuilder.create()
                                .setHostVerificationPolicy(HostnameVerificationPolicy.CLIENT)
                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .buildClassic())
                        .setDefaultConnectionConfig(ConnectionConfig.custom()
                                .setConnectTimeout(Timeout.ofSeconds(30))
                                .setSocketTimeout(Timeout.ofSeconds(30))
                                .build())
                        .setMaxConnPerRoute(10240)
                        .setMaxConnTotal(20480)
                        .build())
                // Keep pooled connections tied to the virtual user that opened them
                .setUserTokenHandler((route, httpContext) -> HttpClientContext.castOrCreate(httpContext).getUserToken())
                .setContentDecoderRegistry(CONTENT_DECODERS)
                .evictIdleConnections(TimeValue.ofMinutes(1))
                .evictExpiredConnections()
                .build();
    }

    public void setHttpClient(Object httpClient) {
        if (httpClient instanceof CloseableHttpClient) {
            this.httpclient = (CloseableHttpClient) httpClient;
        } else {
            this.httpclient = (CloseableHttpClient) createHttpClient();
        }
    }

    @SuppressWarnings("deprecation") // per-request connect timeout is only available on RequestConfig
    public void setConnectionTimeout(long connectionTimeout) {
        RequestConfig requestConfig = RequestConfig.copy(context.getRequestConfig())
                .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))
                .build();
        context.setRequestConfig(requestConfig);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intuit.tank.http.TankHttpClient#doGet(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doGet(BaseRequest request) {
        HttpGet httpget = new HttpGet(request.getRequestUrl());
        sendRequest(request, httpget, request.getBody());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intuit.tank.http.TankHttpClient#doPut(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPut(BaseRequest request) {
        HttpPut httpput = new HttpPut(request.getRequestUrl());
        String requestBody = request.getBody();
        httpput.setEntity(buildEntity(request, httpput, requestBody));
        sendRequest(request, httpput, requestBody);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intuit.tank.http.TankHttpClient#doDelete(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doDelete(BaseRequest request) {
        HttpDelete httpdelete = new HttpDelete(request.getRequestUrl());
        // Multiple calls can be expensive, so get it once
        String requestBody = request.getBody();
        String type = request.getHeaderInformation().get("Content-Type");
        if (StringUtils.isBlank(type)) {
            request.getHeaderInformation().put("Content-Type", "application/json");
        }
        sendRequest(request, httpdelete, requestBody);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intuit.tank.http.TankHttpClient#doOptions(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doOptions(BaseRequest request) {
        HttpOptions httpoptions = new HttpOptions(request.getRequestUrl());
        // Multiple calls can be expensive, so get it once
        String requestBody = request.getBody();
        String type = request.getHeaderInformation().get("Content-Type");
        if (StringUtils.isBlank(type)) {
            request.getHeaderInformation().put("Content-Type", "application/json");
        }
        sendRequest(request, httpoptions, requestBody);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intuit.tank.http.TankHttpClient#doPost(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPost(BaseRequest request) {
        HttpPost httppost = new HttpPost(request.getRequestUrl());
        String requestBody = request.getBody();
        httppost.setEntity(buildEntity(request, httppost, requestBody));
        sendRequest(request, httppost, requestBody);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intuit.tank.http.TankHttpClient#doPatch(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPatch(BaseRequest request) {
        HttpPatch httpPatch = new HttpPatch(request.getRequestUrl());
        String requestBody = request.getBody();
        httpPatch.setEntity(buildEntity(request, httpPatch, requestBody));
        sendRequest(request, httpPatch, requestBody);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intuit.tank.http.TankHttpClient#addAuth(com.intuit.tank.http.
     * AuthCredentials)
     */
    @Override
    @SuppressWarnings("deprecation") // NTLM is deprecated in HttpClient 5 but still supported by Tank scripts
    public void addAuth(AuthCredentials creds) {
        String host = (StringUtils.isBlank(creds.getHost()) || "*".equals(creds.getHost())) ? null : creds.getHost();
        String realm = (StringUtils.isBlank(creds.getRealm()) || "*".equals(creds.getRealm())) ? null : creds.getRealm();
        int port = NumberUtils.toInt(creds.getPortString(), -1);
        String scheme = creds.getScheme() != null ? creds.getScheme().getRepresentation() : null;
        AuthScope scope = new AuthScope(null, host, port, realm, scheme);
        char[] password = creds.getPassword() != null ? creds.getPassword().toCharArray() : new char[0];
        if (AuthScheme.NTLM == creds.getScheme()) {
            credentialsProvider.setCredentials(scope, new NTCredentials(creds.getUserName(), password, "tank-test", creds.getRealm()));
        } else {
            credentialsProvider.setCredentials(scope, new UsernamePasswordCredentials(creds.getUserName(), password));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intuit.tank.http.TankHttpClient#clearSession()
     */
    @Override
    public void clearSession() {
        context.getCookieStore().clear();
    }

    /**
     *
     */
    @Override
    public void setCookie(TankCookie tankCookie) {
        BasicClientCookie cookie = new BasicClientCookie(tankCookie.getName(), tankCookie.getValue());
        // Set effective domain and path attributes
        cookie.setDomain(tankCookie.getDomain());
        cookie.setPath(tankCookie.getPath());
        // Set attributes exactly as sent by the server
        cookie.setAttribute(Cookie.PATH_ATTR, tankCookie.getPath());
        cookie.setAttribute(Cookie.DOMAIN_ATTR, tankCookie.getDomain());
        context.getCookieStore().addCookie(cookie);
    }

    @Override
    @SuppressWarnings("deprecation") // per-request proxy is only available on RequestConfig
    public void setProxy(String proxyhost, int proxyport) {
        HttpHost proxy = StringUtils.isNotBlank(proxyhost) ? new HttpHost(proxyhost, proxyport) : null;
        RequestConfig requestConfig = RequestConfig.copy(context.getRequestConfig()).setProxy(proxy).build();
        context.setRequestConfig(requestConfig);
    }

    private void sendRequest(BaseRequest request, @Nonnull HttpUriRequestBase method, String requestBody) {
        if (httpclient == null) {
            httpclient = (CloseableHttpClient) createHttpClient();
        }
        long waitTime = 0L;
        String uri = request.getRequestUrl();
        if (LOG.isDebugEnabled()) LOG.debug(request.getLogUtil().getLogMessage(
                "About to " + method.getMethod() + " request to " + uri + " with requestBody  " + requestBody, LogEventType.Informational));
        List<String> cookies = new ArrayList<String>();
        if (context.getCookieStore().getCookies() != null) {
            cookies = context.getCookieStore().getCookies().stream().map(cookie -> "REQUEST COOKIE: " + cookie.toString()).collect(Collectors.toList());
        }
        request.logRequest(uri, requestBody, method.getMethod(), request.getHeaderInformation(), cookies, false);
        setHeaders(request, method, request.getHeaderInformation());
        long startTime = System.currentTimeMillis();
        request.setTimestamp(new Date(startTime));
        try {
            // The response handler always consumes the entity and closes the response,
            // which releases the connection back to the pool and prevents CLOSE-WAIT leaks
            waitTime = httpclient.execute(method, context, response -> {
                byte[] responseBody = new byte[0];
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try {
                        Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
                        String contentType = contentTypeHeader != null ? contentTypeHeader.getValue() : "";
                        if (checkContentType(contentType)) {
                            responseBody = EntityUtils.toByteArray(entity);
                        } else {
                            // Still consume the entity even if we don't keep the data
                            EntityUtils.consume(entity);
                        }
                    } catch (IOException | NullPointerException e) {
                        LOG.warn(request.getLogUtil().getLogMessage("could not get response body: " + e));
                    }
                }
                long elapsed = System.currentTimeMillis() - startTime;
                processResponse(responseBody, elapsed, request, response.getReasonPhrase(), response.getCode(), response.getHeaders());
                return elapsed;
            });
        } catch (UnknownHostException uhex) {
            LOG.error(request.getLogUtil().getLogMessage("UnknownHostException to url: " + uri + " |  error: " + uhex.toString(), LogEventType.IO), uhex);
        } catch (SocketException sex) {
            LOG.error(request.getLogUtil().getLogMessage("SocketException to url: " + uri + " |  error: " + sex.toString(), LogEventType.IO), sex);
        } catch (Exception ex) {
            LOG.error(request.getLogUtil().getLogMessage("Could not do " + method.getMethod() + " to url " + uri + " |  error: " + ex.toString(), LogEventType.IO), ex);
            throw new RuntimeException(ex);
        } finally {
            if (method.getMethod().equalsIgnoreCase("post") && request.getLogUtil().getAgentConfig().getLogPostResponse()) {
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
     * Checks content-type to filter whether to assign the response data to responseBody
     *
     * @param contentType
     */
    private boolean checkContentType(String contentType) {
        return mimeTypes.stream().anyMatch(contentType::matches);
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
                LOG.warn(request.getLogUtil().getLogMessage("Response time too slow"));
                Thread.sleep(Math.min(config.getMaxAgentWaitTime(), responseTime));
            }
        } catch (InterruptedException e) {
            LOG.warn("Interrupted", e);
        }
    }

    /**
     * Process the response data
     */
    private void processResponse(byte[] bResponse, long waitTime, BaseRequest request, String message, int httpCode, Header[] headers) {
        BaseResponse response = request.getResponse();
        try {
            if (response == null) {
                // Get response header information
                String contentType = Arrays.stream(headers).filter(
                        h -> HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(h.getName())).findFirst().map(NameValuePair::getValue).orElse("");
                response = TankHttpUtil.newResponseObject(contentType);
                request.setResponse(response);
            }

            // Get response detail information
            response.setHttpMessage(message);
            response.setHttpCode(httpCode);

            // Get response header information
            for (Header header : headers) {
                response.setHeader(header.getName(), header.getValue());
            }

            // Extract proxy response/service time header
            String proxyResponseTimeHeader = response.getHttpHeader("x-envoy-upstream-service-time");
            if (proxyResponseTimeHeader != null) {
                try {
                    long proxyResponseTime = Long.parseLong(proxyResponseTimeHeader);
                    response.setProxyResponseTime(proxyResponseTime);
                } catch (NumberFormatException e) {
                    LOG.warn("could not parse proxy service time header: " + proxyResponseTimeHeader);
                    response.setProxyResponseTime(-1);
                }
            } else {
                response.setProxyResponseTime(-1);
            }

            if (context.getCookieStore().getCookies() != null) {
                for (Cookie cookie : context.getCookieStore().getCookies()) {
                    response.setCookie(cookie.getName(), cookie.getValue());
                }
            }
            response.setResponseTime(waitTime);

            // body has already been decoded by the client's content decoders
            response.setResponseBody(bResponse);

        } catch (Exception ex) {
            LOG.warn("Unable to get response: " + ex.getMessage());
        } finally {
            if (LOG.isDebugEnabled() && response != null) {
                LOG.debug("******** RESPONSE ***********");
                LOG.debug(response.getLogMsg());
            }
        }
    }

    /**
     * Set all the header keys
     *
     * @param request
     * @param method
     * @param headerInformation
     */
    private void setHeaders(BaseRequest request, HttpUriRequestBase method, HashMap<String, String> headerInformation) {
        try {
            for (Map.Entry<String, String> mapEntry : headerInformation.entrySet()) {
                if (isMultipart(request) && HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(mapEntry.getKey())) {
                    // the multipart entity supplies Content-Type with the boundary it actually uses
                    continue;
                }
                method.setHeader(mapEntry.getKey(), mapEntry.getValue());
            }
        } catch (Exception ex) {
            LOG.warn(request.getLogUtil().getLogMessage("Unable to set header: " + ex.getMessage(), LogEventType.System));
        }
    }

    /**
     * Builds the request body, either multipart or a string entity of the request's content type
     */
    private HttpEntity buildEntity(BaseRequest request, HttpUriRequestBase method, String requestBody) {
        if (isMultipart(request)) {
            return buildParts(request);
        }
        method.setHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
        return new StringEntity(requestBody, ContentType.create(request.getContentType(), request.getContentTypeCharSet()));
    }

    private static boolean isMultipart(BaseRequest request) {
        return request.getContentType() != null
                && request.getContentType().toLowerCase().startsWith(BaseRequest.CONTENT_TYPE_MULTIPART);
    }

    private HttpEntity buildParts(BaseRequest request) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        String contentType = request.getContentType();
        String boundary = TankHttpUtil.getBoundary(contentType);
        if (boundary != null) {
            builder.setBoundary(boundary);
        }

        for (PartHolder h : TankHttpUtil.getPartsFromBody(request)) {
            ContentType partContentType = h.isContentTypeSet()
                    ? ContentType.create(h.getContentType())
                    : ContentType.DEFAULT_BINARY;

            if (h.getFileName() == null) {
                builder.addTextBody(h.getPartName(), h.getBodyAsString(), partContentType);
            } else {
                builder.addBinaryBody(h.getPartName(), h.getBody(), partContentType, h.getFileName());
            }
        }
        return builder.build();
    }
}
