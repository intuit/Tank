package com.intuit.tank.httpclient5;

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

import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.CookieSpecs;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.impl.sync.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.sync.CloseableHttpClient;
import org.apache.hc.client5.http.impl.sync.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.sync.HttpClients;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.sync.methods.HttpDelete;
import org.apache.hc.client5.http.sync.methods.HttpGet;
import org.apache.hc.client5.http.sync.methods.HttpOptions;
import org.apache.hc.client5.http.sync.methods.HttpPost;
import org.apache.hc.client5.http.sync.methods.HttpPut;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
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

    static Logger LOG = LogManager.getLogger(TankHttpClient5.class);

    private CloseableHttpClient httpclient;
    private HttpClientContext context;
    private RequestConfig requestConfig;
    private SSLConnectionSocketFactory sslsf;
    private HttpClientConnectionManager cm;
    private boolean proxyOn = false;

    /**
     * no-arg constructor for client
     */
    public TankHttpClient5() {
        sslsf = new SSLConnectionSocketFactory(SSLContexts.createDefault(), NoopHostnameVerifier.INSTANCE);
        cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslsf)
                .build();
        
        httpclient = HttpClients.custom().setConnectionManager(cm).build();
        requestConfig = RequestConfig.custom().setSocketTimeout(30, TimeUnit.SECONDS)
        		.setConnectTimeout(30, TimeUnit.SECONDS)
        		.setCircularRedirectsAllowed(true)
        		.setAuthenticationEnabled(true)
        		.setRedirectsEnabled(true)
        		.setCookieSpec(CookieSpecs.STANDARD)
                .setMaxRedirects(100).build();

        // Make sure the same context is used to execute logically related
        // requests
        context = HttpClientContext.create();
        context.setCredentialsProvider(new BasicCredentialsProvider());
        context.setCookieStore(new BasicCookieStore());
        context.setRequestConfig(requestConfig);
    }

    public void setConnectionTimeout(long connectionTimeout) {
        requestConfig = RequestConfig.custom().setSocketTimeout(30, TimeUnit.SECONDS)
        		.setConnectTimeout((int) connectionTimeout, TimeUnit.MILLISECONDS)
        		.setCircularRedirectsAllowed(true)
        		.setAuthenticationEnabled(true)
                .setRedirectsEnabled(true)
                .setCookieSpec(CookieSpecs.STANDARD)
                .setMaxRedirects(100).build();
        context.setRequestConfig(requestConfig);
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
        HttpGet httpget = new HttpGet(request.getRequestUrl());
        sendRequest(request, httpget, request.getBody());
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
        HttpPut httpput = new HttpPut(request.getRequestUrl());
        // Multiple calls can be expensive, so get it once
        String requestBody = request.getBody();
        HttpEntity entity = new StringEntity(requestBody, ContentType.create(request.getContentType(), request.getContentTypeCharSet()));
        httpput.setEntity(entity);
        sendRequest(request, httpput, requestBody);
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
     * com.intuit.tank.httpclient3.TankHttpClient#doOptions(com.intuit.tank.http.
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
     * com.intuit.tank.httpclient3.TankHttpClient#doPost(com.intuit.tank.http.
     * BaseRequest)
     */
    @Override
    public void doPost(BaseRequest request) {
        HttpPost httppost = new HttpPost(request.getRequestUrl());
        String requestBody = request.getBody();
        HttpEntity entity = null;
        if (BaseRequest.CONTENT_TYPE_MULTIPART.equalsIgnoreCase(request.getContentType())) {
            entity = buildParts(request);
        } else {
            entity = new StringEntity(requestBody, ContentType.create(request.getContentType(), request.getContentTypeCharSet()));
        }
        httppost.setEntity(entity);
        sendRequest(request, httppost, requestBody);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intuit.tank.httpclient3.TankHttpClient#addAuth(com.intuit.tank.http.
     * AuthCredentials)
     */
    @Override
    public void addAuth(AuthCredentials creds) {
        String host = (StringUtils.isBlank(creds.getHost()) || "*".equals(creds.getHost())) ? AuthScope.ANY_HOST : creds.getHost();
        String realm = (StringUtils.isBlank(creds.getRealm()) || "*".equals(creds.getRealm())) ? AuthScope.ANY_REALM : creds.getRealm();
        int port = NumberUtils.toInt(creds.getPortString(), AuthScope.ANY_PORT);
        String scheme = creds.getScheme() != null ? creds.getScheme().getRepresentation() : AuthScope.ANY_SCHEME;
        AuthScope scope = new AuthScope(host, port, realm, scheme);
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
     * @see com.intuit.tank.httpclient3.TankHttpClient#clearSession()
     */
    @Override
    public void clearSession() {
        context.getCookieStore().clear();
    }

    /**
     * 
     */
    @Override
    public void setCookie(TankCookie cookie) {
        BasicClientCookie c = new BasicClientCookie(cookie.getName(), cookie.getValue());
        c.setDomain(cookie.getDomain());
        c.setPath(cookie.getPath());
        context.getCookieStore().addCookie(c);

    }

    @Override
    public void setProxy(String proxyhost, int proxyport) {
        if (StringUtils.isNotBlank(proxyhost)) {
            HttpHost proxy = new HttpHost(proxyhost, proxyport);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            httpclient = HttpClients.custom().setConnectionManager(cm).setRoutePlanner(routePlanner).build();
            proxyOn = true;
        } else if (proxyOn){
            httpclient = HttpClients.custom().setConnectionManager(cm).build();
            proxyOn = false;
        }
    }

    private void sendRequest(BaseRequest request, @Nonnull ClassicHttpRequest method, String requestBody) {
        String uri = null;
        long waitTime = 0L;
        CloseableHttpResponse response = null;
        try {
            uri = method.getRequestUri();
            LOG.debug(request.getLogUtil().getLogMessage("About to " + method.getMethod() + " request to " + uri + " with requestBody  " + requestBody, LogEventType.Informational));
            List<String> cookies = new ArrayList<String>();
            if (context.getCookieStore().getCookies() != null) {
                for (Cookie cookie : context.getCookieStore().getCookies()) {
                    cookies.add("REQUEST COOKIE: " + cookie.toString());
                }
            }
            request.logRequest(uri, requestBody, method.getMethod(), request.getHeaderInformation(), cookies, false);
            setHeaders(request, method, request.getHeaderInformation());
            long startTime = System.currentTimeMillis();
            request.setTimestamp(new Date(startTime));
            response = httpclient.execute(method, context);

            // read response body
            byte[] responseBody = new byte[0];
            // check for no content headers
            if (response.getCode() != 203 && response.getCode() != 202 && response.getCode() != 204) {
                try {
                    responseBody = IOUtils.toByteArray(response.getEntity().getContent());
                } catch (Exception e) {
                    LOG.warn("could not get response body: " + e);
                }
            }
            waitTime = System.currentTimeMillis() - startTime;
            processResponse(responseBody, waitTime, request, response.getReasonPhrase(), response.getCode(), response.getAllHeaders());
            
        } catch (UnknownHostException uhex) {
            LOG.error(request.getLogUtil().getLogMessage("UnknownHostException to url: " + uri + " |  error: " + uhex.toString(), LogEventType.IO), uhex);
        } catch (SocketException sex) {
            LOG.error(request.getLogUtil().getLogMessage("SocketException to url: " + uri + " |  error: " + sex.toString(), LogEventType.IO), sex);
        } catch (Exception ex) {
            LOG.error(request.getLogUtil().getLogMessage("Could not do " + method.getMethod() + " to url " + uri + " |  error: " + ex.toString(), LogEventType.IO), ex);
            throw new RuntimeException(ex);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                LOG.warn("Could not release connection: " + e, e);
            }
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
    private void processResponse(byte[] bResponse, long waitTime, BaseRequest request, String message, int httpCode, Header[] headers) {
        BaseResponse response = request.getResponse();
        try {
            if (response == null) {
                // Get response header information
                String contentType = "";
                for (Header h : headers) {
                    if ("ContentType".equalsIgnoreCase(h.getName())) {
                        contentType = h.getValue();
                        break;
                    }
                }
                response = TankHttpUtil.newResponseObject(contentType);
                request.setResponse(response);
            }

            // Get response detail information
            response.setHttpMessage(message);
            response.setHttpCode(httpCode);

            // Get response header information
            for (int h = 0; h < headers.length; h++) {
                response.setHeader(headers[h].getName(), headers[h].getValue());
            }

            if (context.getCookieStore().getCookies() != null) {
                for (Cookie cookie : context.getCookieStore().getCookies()) {
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
     * Set all the header keys
     * 
     * @param connection
     */
    @SuppressWarnings("rawtypes")
    private void setHeaders(BaseRequest request, ClassicHttpRequest method, HashMap<String, String> headerInformation) {
        try {
            Set set = headerInformation.entrySet();
            Iterator iter = set.iterator();

            while (iter.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) iter.next();
                method.setHeader((String) mapEntry.getKey(), (String) mapEntry.getValue());
            }
        } catch (Exception ex) {
            LOG.warn(request.getLogUtil().getLogMessage("Unable to set header: " + ex.getMessage(), LogEventType.System));
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
