package com.intuit.tank.okhttpclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Challenge;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

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
/**
 * Implements the OkHttpclient to support Http2.
 * 
 * @author alfredom
 *
 */
public class TankOkHttpClient implements TankHttpClient {

    static Logger LOG = LogManager.getLogger(TankOkHttpClient.class);

    private OkHttpClient okHttpClient = new OkHttpClient();
    private CookieManager cookieManager = new CookieManager();
    private SSLSocketFactory sslSocketFactory;

    /**
     * no-arg constructor for OkHttp client
     */
    public TankOkHttpClient() {
        try {

            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            } };

            // Setup SSL to accept all certs
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            sslSocketFactory = sslContext.getSocketFactory();

            // Setup Cookie manager
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);
            okHttpClient.setCookieHandler(cookieManager);

            okHttpClient.setConnectTimeout(30000, TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(30000, TimeUnit.MILLISECONDS); // Socket-timeout
            okHttpClient.setFollowRedirects(true);
            okHttpClient.setFollowSslRedirects(true);

            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

        } catch (Exception e) {
            LOG.error("Error setting accept all: " + e, e);
        }
    }

    public void setConnectionTimeout(long connectionTimeout) {
        okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.okhttpclient.TankHttpClient#doGet(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doGet(BaseRequest request) {
        Builder builder = new Request.Builder();
        builder.get();
        builder.url(request.getRequestUrl());
        sendRequest(request, builder, request.getBody(), "get");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.okhttpclient.TankHttpClient#doPut(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPut(BaseRequest request) {
        MediaType contentType = MediaType.parse(request.getContentType());
        RequestBody requtestBody = RequestBody.create(contentType, request.getBody());

        Builder builder = new Request.Builder().url(request.getRequestUrl()).put(requtestBody);

        sendRequest(request, builder, request.getBody(), "put");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.okhttpclient.TankHttpClient#doDelete(com.intuit.tank.
     * http. BaseRequest)
     */
    @Override
    public void doDelete(BaseRequest request) {

        Builder builder = new Request.Builder().delete().url(request.getRequestUrl());
        String type = request.getKey("Content-Type");

        if (StringUtils.isBlank(type)) {
            request.setKey("Content-Type", "application/x-www-form-urlencoded");
        }
        sendRequest(request, builder, request.getBody(), "delete");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.okhttpclient.TankHttpClient#addAuth(com.intuit.tank.http.
     * AuthCredentials)
     */
    @Override
    public void addAuth(final AuthCredentials creds) {
        okHttpClient.setAuthenticator(new InternalTankAuthenticator(creds));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.okhttpclient.TankHttpClient#clearSession()
     */
    @Override
    public void clearSession() {
        cookieManager.getCookieStore().removeAll();
    }

    /**
    * 
    */
    @Override
    public void setCookie(TankCookie cookie) {

        HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
        httpCookie.setDomain(cookie.getDomain());
        httpCookie.setPath(cookie.getPath());
        try {
            ((CookieManager) okHttpClient.getCookieHandler()).getCookieStore().add(null, httpCookie);
        } catch (Exception e) {
            LOG.error("Error setting cookie: " + e, e);
        }

    }

    @Override
    public void setProxy(String proxyhost, int proxyport) {
        if (StringUtils.isNotBlank(proxyhost)) {
            okHttpClient.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyhost, proxyport)));
            okHttpClient.setSslSocketFactory(sslSocketFactory);
        } else {
            // Set proxy to direct
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setProxy(Proxy.NO_PROXY);
        }
    }

    String getProxyInfo() {
        return okHttpClient.getProxy().toString();
    }

    private void sendRequest(BaseRequest request, @Nonnull Request.Builder builder, String requestBody, String method) {
        String uri = null;
        long waitTime = 0L;
        try {
            setHeaders(request, builder, request.getHeaderInformation());

            List<String> cookies = new ArrayList<String>();
            if (((CookieManager) okHttpClient.getCookieHandler()).getCookieStore().getCookies() != null) {
                for (HttpCookie httpCookie : ((CookieManager) okHttpClient.getCookieHandler()).getCookieStore().getCookies()) {
                    cookies.add("REQUEST COOKIE: " + httpCookie.toString());
                }
            }

            Request okRequest = builder.build();
            uri = okRequest.uri().toString();
            LOG.debug(request.getLogUtil().getLogMessage("About to " + okRequest.method() + " request to " + uri + " with requestBody  " + requestBody, LogEventType.Informational));
            request.logRequest(uri, requestBody, okRequest.method(), request.getHeaderInformation(), cookies, false);

            Response response = okHttpClient.newCall(okRequest).execute();
            long startTime = Long.parseLong(response.header("OkHttp-Sent-Millis"));
            long endTime = Long.parseLong(response.header("OkHttp-Received-Millis"));

            // read response body
            byte[] responseBody = new byte[0];
            // check for no content headers
            if (response.code() != 203 && response.code() != 202 && response.code() != 204) {
                try {
                    responseBody = response.body().bytes();
                } catch (Exception e) {
                    LOG.warn("could not get response body: " + e);
                }
            }
            waitTime = endTime - startTime;
            processResponse(responseBody, waitTime, request, response.message(), response.code(), response.headers());
            
        } catch (Exception ex) {
            LOG.error(request.getLogUtil().getLogMessage("Could not do " + method + " to url " + uri + " |  error: " + ex.toString(), LogEventType.IO), ex);
            throw new RuntimeException(ex);
        } finally {
            if (method.equalsIgnoreCase("post") && request.getLogUtil().getAgentConfig().getLogPostResponse()) {
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
    private void processResponse(byte[] bResponse, long waitTime, BaseRequest request, String message, int httpCode, Headers headers) {
        BaseResponse response = request.getResponse();
        try {
            if (response == null) {
                // Get response header information
                String contentType = headers.get("ContentType");
                if (StringUtils.isBlank(contentType)) {
                    contentType = "";
                }
                response = TankHttpUtil.newResponseObject(contentType);
                request.setResponse(response);
            }

            // Get response detail information
            response.setHttpMessage(message);
            response.setHttpCode(httpCode);

            // Get response header information
            for (String key : headers.names()) {
                response.setHeader(key, headers.get(key));
            }

            if (((CookieManager) okHttpClient.getCookieHandler()).getCookieStore().getCookies() != null) {
                for (HttpCookie cookie : ((CookieManager) okHttpClient.getCookieHandler()).getCookieStore().getCookies()) {
                    // System.out.println("in processResponse-getCookies");
                    // System.out.println(cookie.toString());
                    response.setCookie(cookie.getName(), cookie.getValue());
                }
            }

            response.setResponseTime(waitTime);
            String contentType = response.getHttpHeader("Content-Type");
            String contentEncode = response.getHttpHeader("Content-Encoding");
            if (BaseResponse.isDataType(contentType) && contentEncode != null && contentEncode.toLowerCase().contains("gzip")) {
                // decode gzip for data types
                try {
                    GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(bResponse));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(in, out);
                    bResponse = out.toByteArray();
                } catch (Exception e) {
                    LOG.warn(request.getLogUtil().getLogMessage("cannot decode gzip stream: " + e, LogEventType.System));
                }
            }
            response.setResponseBody(bResponse);

        } catch (Exception ex) {
            LOG.warn("Unable to get response: " + ex.getMessage());
        } finally {
            response.logResponse();
        }
    }

    /**
     * Set all the header keys defined by user
     *
     * @param connection
     */
    @SuppressWarnings("rawtypes")
    private void setHeaders(BaseRequest request, Builder builder, HashMap<String, String> headerInformation) {
        try {
            Set set = headerInformation.entrySet();
            Iterator iter = set.iterator();

            while (iter.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) iter.next();
                builder.addHeader((String) mapEntry.getKey(), (String) mapEntry.getValue());
            }
        } catch (Exception ex) {
            LOG.warn(request.getLogUtil().getLogMessage("Unable to set header: " + ex.getMessage(), LogEventType.System));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.okhttpclient.TankHttpClient#doPost(com.intuit.tank.http
     * .BaseRequest)
     */
    @Override
    public void doPost(BaseRequest request) {

        MediaType contentType = MediaType.parse(request.getContentType() + ";" + request.getContentTypeCharSet());
        Builder requestBuilder = new Request.Builder().url(request.getRequestUrl());
        RequestBody requestBodyEntity = null;

        if (BaseRequest.CONTENT_TYPE_MULTIPART.equalsIgnoreCase(request.getContentType())) {
            requestBodyEntity = buildParts(request);
        } else {
            requestBodyEntity = RequestBody.create(contentType, request.getBody());
        }

        if (requestBodyEntity != null) {
            requestBuilder.post(requestBodyEntity);
        }

        sendRequest(request, requestBuilder, request.getBody(), "post");

    }

    private RequestBody buildParts(BaseRequest request) {

        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

        for (PartHolder h : TankHttpUtil.getPartsFromBody(request)) {
            if (h.getFileName() == null) {
                if (h.isContentTypeSet()) {
                    multipartBuilder.addFormDataPart(h.getPartName(), null, RequestBody.create(MediaType.parse(request.getContentType()), h.getBody()));
                } else {
                    multipartBuilder.addFormDataPart(h.getPartName(), h.getBodyAsString());
                }
            } else {
                if (h.isContentTypeSet()) {
                    multipartBuilder.addFormDataPart(h.getPartName(), h.getFileName(), RequestBody.create(MediaType.parse(request.getContentType()), h.getBody()));
                } else {
                    multipartBuilder.addFormDataPart(h.getPartName(), h.getFileName(), RequestBody.create(null, h.getBody()));
                }
            }
        }
        return multipartBuilder.build();
    }

    private static final class InternalTankAuthenticator implements Authenticator {
        private AuthCredentials creds;

        public InternalTankAuthenticator(AuthCredentials creds) {
            this.creds = creds;
        }

        @Override
        public Request authenticate(Proxy proxy, Response response) throws IOException {
            List<Challenge> challenges = response.challenges();
            for (Challenge challenge : challenges) {
                if (!matchRealm(challenge.getRealm()) || !matchScheme(challenge.getScheme())) {
                    return null;
                }
            }
            String credential = Credentials.basic(creds.getUserName(), creds.getPassword());
            if (credential.equals(response.request().header("Authorization"))) {
                // If we already failed with these credentials, don't retry.
                LOG.warn("Credentials already tried. not trying again");
                return null;
            }
            return response.request().newBuilder().header("Authorization", credential).build();
        }

        @Override
        public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
            return null;
        }

        private boolean matchRealm(String realm) {
            boolean ret = true;
            if (StringUtils.isNotBlank(creds.getRealm())) {
                ret = creds.getRealm().equalsIgnoreCase(realm);
            }
            return ret;
        }

        private boolean matchScheme(String scheme) {
            boolean ret = true;
            if (!scheme.equalsIgnoreCase(AuthScheme.Basic.getRepresentation())) {
                ret = false;
            } else {
                if (creds.getScheme().getRepresentation() != null) {
                    ret = creds.getScheme().getRepresentation().equalsIgnoreCase(scheme);
                }
            }
            return ret;
        }
    }
}
