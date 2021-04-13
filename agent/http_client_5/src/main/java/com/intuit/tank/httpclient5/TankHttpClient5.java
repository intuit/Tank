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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.http.cookie.ClientCookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private CloseableHttpAsyncClient httpclient;
    private HttpClientContext context;

    /**
     * no-arg constructor for client
     */
    public TankHttpClient5() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(30L, TimeUnit.SECONDS)
        		.setConnectTimeout(30L, TimeUnit.SECONDS)
        		.setCircularRedirectsAllowed(true)
        		.setAuthenticationEnabled(true)
        		.setRedirectsEnabled(true)
        		.setCookieSpec(StandardCookieSpec.RELAXED)
                .setMaxRedirects(100)
                .build();

        // Make sure the same context is used to execute logically related
        // requests
        context = HttpClientContext.create();
        context.setCredentialsProvider(new BasicCredentialsProvider());
        context.setUserToken(UUID.randomUUID());
        context.setCookieStore(new BasicCookieStore());
        context.setRequestConfig(requestConfig);
    }

    public Object createHttpClient() {
        // default this implementation will create no more than than 2 concurrent connections per given route and no more 20 connections in total
        PoolingAsyncClientConnectionManager cm =
                PoolingAsyncClientConnectionManagerBuilder.create()
                        .setMaxConnPerRoute(10240)
                        .setMaxConnTotal(20480)
                        .build();
        UserTokenHandler userTokenHandler = (httpRoute, httpContext) -> httpContext.getAttribute(HttpClientContext.USER_TOKEN);
        return HttpAsyncClients.custom()
                .setConnectionManager(cm)
                .setUserTokenHandler(userTokenHandler)
                .build();
    }

    public void setHttpClient(Object httpClient) {
        if (httpClient instanceof CloseableHttpAsyncClient) {
            this.httpclient = (CloseableHttpAsyncClient) httpClient;
        } else {
            this.httpclient = (CloseableHttpAsyncClient) createHttpClient();
        }
        this.httpclient.start();
    }

    public void setConnectionTimeout(long connectionTimeout) {
        RequestConfig requestConfig =
                context.getRequestConfig().custom().setConnectTimeout((int) connectionTimeout, TimeUnit.MILLISECONDS).build();
        context.setRequestConfig(requestConfig);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient5.TankHttpClient#doGet(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doGet(BaseRequest request) {
        SimpleHttpRequest httpget = SimpleHttpRequests.get(request.getRequestUrl());
        sendRequest(request, httpget);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient5.TankHttpClient#doPut(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPut(BaseRequest request) {
        SimpleHttpRequest httpput = SimpleHttpRequests.put(request.getRequestUrl());
        httpput.setBody(request.getBody(), ContentType.create(request.getContentType(), request.getContentTypeCharSet()));
        sendRequest(request, httpput);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient5.TankHttpClient#doDelete(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doDelete(BaseRequest request) {
        SimpleHttpRequest httpdelete = SimpleHttpRequests.delete(request.getRequestUrl());
        sendRequest(request, httpdelete);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient5.TankHttpClient#doOptions(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doOptions(BaseRequest request) {
        SimpleHttpRequest httpoptions = SimpleHttpRequests.options(request.getRequestUrl());
        sendRequest(request, httpoptions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient5.TankHttpClient#doPost(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPost(BaseRequest request) {
        SimpleHttpRequest httppost = SimpleHttpRequests.post(request.getRequestUrl());
        String requestBody = request.getBody();
        if (BaseRequest.CONTENT_TYPE_MULTIPART.equalsIgnoreCase(request.getContentType())) {
            HttpEntity entity = buildParts(request);
            try (ByteArrayOutputStream baoStream = new ByteArrayOutputStream() ) {
                entity.writeTo(baoStream);
                httppost.setBody(baoStream.toByteArray(), ContentType.create(request.getContentType(), request.getContentTypeCharSet()));
            } catch (IOException e) {
                LOG.error("Failure to write multipart POST payload.");
            }
        } else {
            httppost.setBody(requestBody, ContentType.create(request.getContentType(), request.getContentTypeCharSet()));
        }
        sendRequest(request, httppost);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient5.TankHttpClient#addAuth(com.intuit.tank.http.
     * AuthCredentials)
     */
    @Override
    public void addAuth(AuthCredentials creds) {
        String protocol = null;
        String host = (StringUtils.isBlank(creds.getHost()) || "*".equals(creds.getHost())) ? null : creds.getHost();
        String realm = (StringUtils.isBlank(creds.getRealm()) || "*".equals(creds.getRealm())) ? null : creds.getRealm();
        int port = NumberUtils.toInt(creds.getPortString(), -1);
        String scheme = creds.getScheme() != null ? creds.getScheme().getRepresentation() : null;
        AuthScope scope = new AuthScope(protocol, host, port, realm, scheme);
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (AuthScheme.NTLM == creds.getScheme()) {
            credentialsProvider.setCredentials(scope, new NTCredentials(creds.getUserName(), creds.getPassword().toCharArray(), "tank-test", creds.getRealm()));
        } else {
        	credentialsProvider.setCredentials(scope, new UsernamePasswordCredentials(creds.getUserName(), creds.getPassword().toCharArray()));
        }
        context.setCredentialsProvider(credentialsProvider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.httpclient5.TankHttpClient#clearSession()
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
        cookie.setAttribute(ClientCookie.PATH_ATTR, tankCookie.getPath());
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, tankCookie.getDomain());
        context.getCookieStore().addCookie(cookie);
    }

    @Override
    public void setProxy(String proxyhost, int proxyport) {
        if (StringUtils.isNotBlank(proxyhost)) {
            HttpHost proxy = new HttpHost(proxyhost, proxyport);
            RequestConfig requestConfig =
                    context.getRequestConfig().custom().setProxy(proxy).build();
            context.setRequestConfig(requestConfig);
        } else {
            RequestConfig requestConfig =
                    context.getRequestConfig().custom().setProxy(null).build();
            context.setRequestConfig(requestConfig);
        }
    }

    private void sendRequest(BaseRequest request, @Nonnull SimpleHttpRequest method) {
        LOG.debug(request.getLogUtil().getLogMessage("About to " + method.getMethod() + " request to " + method.getRequestUri() + " with requestBody  " + method.getBody(), LogEventType.Informational));
        List<String> cookies = new ArrayList<String>();
        if (context.getCookieStore().getCookies() != null) {
            cookies = context.getCookieStore().getCookies().stream()
                    .map(cookie -> "REQUEST COOKIE: " + cookie.toString())
                    .collect(Collectors.toList());
        }
        request.logRequest(method.getRequestUri(), method.getBodyText(), method.getMethod(), request.getHeaderInformation(), cookies, false);
        setHeaders(method, request.getHeaderInformation());
        long startTime = System.currentTimeMillis();
        request.setTimestamp(new Date(startTime));
        final Future<SimpleHttpResponse> future = httpclient.execute(method, context,
            new FutureCallback<SimpleHttpResponse>() {

                @Override
                public void completed(final SimpleHttpResponse response) {
                    if (request.getAsync() && response.getCode() != 203 && response.getCode() != 202 && response.getCode() != 204) {
                        long waitTime = System.currentTimeMillis() - startTime;
                        processResponse(response.getBodyBytes(), waitTime, request, response.getReasonPhrase(), response.getCode(), response.getHeaders());
                        if (waitTime != 0) {
                            doWaitDueToLongResponse(request, waitTime, method.getRequestUri());
                        }
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    if (ex instanceof UnknownHostException) {
                        LOG.error(request.getLogUtil()
                                .getLogMessage("UnknownHostException to url: " + method.getRequestUri() + " |  error: " + ex.toString(), LogEventType.IO), ex);
                    }
                    else if (ex instanceof SocketException) {
                        LOG.error(request.getLogUtil()
                                .getLogMessage("SocketException to url: " + method.getRequestUri() + " |  error: " + ex.toString(), LogEventType.IO), ex);
                    }
                    else if (ex instanceof Exception) {
                        LOG.error(request.getLogUtil()
                                .getLogMessage("Could not do " + method.getMethod() + " to url " + method.getRequestUri() + " |  error: " + ex.toString(), LogEventType.IO), ex);
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public void cancelled() {
                    System.out.println(method.getMethod() + " cancelled");
                }

            });
        try {
            SimpleHttpResponse response = future.get();
            if (!request.getAsync() && response.getCode() != 203 && response.getCode() != 202 && response.getCode() != 204) {
                long waitTime = System.currentTimeMillis() - startTime;
                processResponse(response.getBodyBytes(), waitTime, request, response.getReasonPhrase(), response.getCode(), response.getHeaders());
                if (waitTime != 0) {
                    doWaitDueToLongResponse(request, waitTime, method.getRequestUri());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(request.getLogUtil().getLogMessage("Execution Interrupted: " + e.getMessage()), e);
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
            LOG.warn(request.getLogUtil().getLogMessage("Interrupted"), e);
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
                        h -> "ContentType".equalsIgnoreCase(h.getName())).findFirst().map(NameValuePair::getValue).orElse("");
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

            if (context.getCookieStore().getCookies() != null) {
                for (Cookie cookie : context.getCookieStore().getCookies()) {
                    response.setCookie(cookie.getName(), cookie.getValue());
                }
            }
            response.setResponseTime(waitTime);

            String contentEncoding = response.getHttpHeader("content-ecncoding");
            bResponse = StringUtils.equalsIgnoreCase(contentEncoding, "gzip") ?
                    IOUtils.toByteArray(new GZIPInputStream(new ByteArrayInputStream(bResponse))) :
                    bResponse;

            response.setResponseBody(bResponse);

        } catch (Exception ex) {
            LOG.warn(request.getLogUtil().getLogMessage("Unable to get response: " + ex.getMessage()));
        } finally {
            response.logResponse();
        }
    }

    /**
     * Set all the header keys
     *
     * @param method
     * @param headerInformation
     */
    private void setHeaders(SimpleHttpRequest method, HashMap<String, String> headerInformation) {
        for (Object aSet : headerInformation.entrySet()) {
            Map.Entry mapEntry = (Map.Entry) aSet;
            method.setHeader((String) mapEntry.getKey(), (String) mapEntry.getValue());
        }
    }

    private HttpEntity buildParts(BaseRequest request) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (PartHolder h : TankHttpUtil.getPartsFromBody(request)) {
            if (h.getFileName() == null) {
                if (h.isContentTypeSet()) {
                    builder.addTextBody(h.getPartName(), new String(h.getBodyAsString()), ContentType.create(h.getContentType()));
                } else {
                    builder.addTextBody(h.getPartName(), new String(h.getBodyAsString()));
                }
            } else {
                if (h.isContentTypeSet()) {
                    builder.addBinaryBody(h.getPartName(), h.getBody(), ContentType.create(h.getContentType()), h.getFileName());
                } else {
                    builder.addBinaryBody(h.getFileName(), h.getBody());
                }
            }
        }
        return builder.build();
    }
}
