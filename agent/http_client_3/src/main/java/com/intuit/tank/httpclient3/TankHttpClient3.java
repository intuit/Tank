package com.intuit.tank.httpclient3;

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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

public class TankHttpClient3 implements TankHttpClient {

    static Logger LOG = LogManager.getLogger(TankHttpClient3.class);

    private HttpClient httpclient;

    /**
     * no-arg constructor for client
     */
    public TankHttpClient3() {
        httpclient = new HttpClient();
        httpclient.getParams().setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.BROWSER_COMPATIBILITY);
        httpclient.getParams().setBooleanParameter("http.protocol.single-cookie-header", true);
        httpclient.getParams().setBooleanParameter("http.protocol.allow-circular-redirects", true);
        httpclient.getParams().setIntParameter("http.protocol.max-redirects", 100);
        httpclient.setState(new HttpState());
        @SuppressWarnings("deprecation")
        Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", easyhttps);
    }

    public void setConnectionTimeout(long connectionTimeout) {
        httpclient.getParams().setConnectionManagerTimeout(connectionTimeout);
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
        GetMethod httpget = new GetMethod(request.getRequestUrl());
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
        try {
            PutMethod httpput = new PutMethod(request.getRequestUrl());
            // Multiple calls can be expensive, so get it once
            String requestBody = request.getBody();
            StringRequestEntity entity = new StringRequestEntity(requestBody, request.getContentType(), request.getContentTypeCharSet());
            httpput.setRequestEntity(entity);
            sendRequest(request, httpput, requestBody);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     */
    @Override
    public void setCookie(TankCookie cookie) {
        Cookie c = new Cookie(cookie.getDomain(), cookie.getName(), cookie.getValue());
        c.setPath(cookie.getPath());
        httpclient.getState().addCookie(c);

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
        DeleteMethod httpdelete = new DeleteMethod(request.getRequestUrl());
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
    	OptionsMethod httpoptions = new OptionsMethod(request.getRequestUrl());
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
        try {
            PostMethod httppost = new PostMethod(request.getRequestUrl());
            String requestBody = request.getBody();
            RequestEntity entity = null;
            if (BaseRequest.CONTENT_TYPE_MULTIPART.equalsIgnoreCase(request.getContentType())) {
                List<Part> parts = buildParts(request);

                entity = new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), httppost.getParams());
            } else {
                entity = new StringRequestEntity(requestBody, request.getContentType(), request.getContentTypeCharSet());
            }
            httppost.setRequestEntity(entity);
            sendRequest(request, httppost, requestBody);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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

        Credentials defaultcreds = new UsernamePasswordCredentials(creds.getUserName(), creds.getPassword());
        httpclient.getState().setCredentials(new AuthScope(host, port, realm, scheme), defaultcreds);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.httpclient3.TankHttpClient#clearSession()
     */
    @Override
    public void clearSession() {
        httpclient.setState(new HttpState());
        httpclient.getHttpConnectionManager().closeIdleConnections(0);
    }

    @Override
    public void setProxy(String proxyhost, int proxyport) {
        if (StringUtils.isNotBlank(proxyhost)) {
            httpclient.getHostConfiguration().setProxy(proxyhost, proxyport);
        } else {
            httpclient.getHostConfiguration().setProxyHost(null);
        }
    }

    private void sendRequest(BaseRequest request, @Nonnull HttpMethod method, String requestBody) {
        String uri = null;
        long waitTime = 0L;

        try {
            uri = method.getURI().toString();
            LOG.debug(request.getLogUtil().getLogMessage("About to " + method.getName() + " request to " + uri + " with requestBody  " + requestBody, LogEventType.Informational));
            List<String> cookies = new ArrayList<String>();
            if (httpclient != null && httpclient.getState() != null && httpclient.getState().getCookies() != null) {
                cookies = Arrays.stream(httpclient.getState().getCookies()).map(cookie -> "REQUEST COOKIE: " + cookie.toExternalForm() + " (domain=" + cookie.getDomain() + " : path=" + cookie.getPath() + ")").collect(Collectors.toList());
            }
            request.logRequest(uri, requestBody, method.getName(), request.getHeaderInformation(), cookies, false);
            setHeaders(request, method, request.getHeaderInformation());
            long startTime = System.currentTimeMillis();
            request.setTimestamp(new Date(startTime));
            httpclient.executeMethod(method);

            // read response body
            byte[] responseBody = new byte[0];
            // check for no content headers
            if (method.getStatusCode() != 203 && method.getStatusCode() != 202 && method.getStatusCode() != 204) {
                try {
                    InputStream httpInputStream = method.getResponseBodyAsStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int curByte = httpInputStream.read();
                    while (curByte >= 0) {
                        out.write(curByte);
                        curByte = httpInputStream.read();
                    }
                    responseBody = out.toByteArray();
                } catch (Exception e) {
                    LOG.warn("could not get response body: " + e);
                }
            }
            waitTime = System.currentTimeMillis() - startTime;
            processResponse(responseBody, waitTime, request, method.getStatusText(), method.getStatusCode(), method.getResponseHeaders(), httpclient.getState());
        } catch (UnknownHostException uhex) {
            LOG.error(request.getLogUtil().getLogMessage("UnknownHostException to url: " + uri + " |  error: " + uhex.toString(), LogEventType.IO), uhex);
        } catch (SocketException sex) {
            LOG.error(request.getLogUtil().getLogMessage("SocketException to url: " + uri + " |  error: " + sex.toString(), LogEventType.IO), sex);
        } catch (Exception ex) {
            LOG.error(request.getLogUtil().getLogMessage("Could not do " + method.getName() + " to url " + uri + " |  error: " + ex.toString(), LogEventType.IO), ex);
            throw new RuntimeException(ex);
        } finally {
            try {
                method.releaseConnection();
            } catch (Exception e) {
                LOG.warn("Could not release connection: " + e, e);
            }
            if (method.getName().equalsIgnoreCase("post") && request.getLogUtil().getAgentConfig().getLogPostResponse()) {
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
    private void processResponse(byte[] bResponse, long waitTime, BaseRequest request, String message, int httpCode, Header[] headers, HttpState httpstate) {
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

            Cookie[] cookies = httpstate.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
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
     * @param request
     * @param method
     * @param headerInformation
     */
    @SuppressWarnings("rawtypes")
    private void setHeaders(BaseRequest request, HttpMethod method, HashMap<String, String> headerInformation) {
        try {
            Set set = headerInformation.entrySet();

            for (Object aSet : set) {
                Map.Entry mapEntry = (Map.Entry) aSet;
                method.setRequestHeader((String) mapEntry.getKey(), (String) mapEntry.getValue());
            }
        } catch (Exception ex) {
            LOG.warn(request.getLogUtil().getLogMessage("Unable to set header: " + ex.getMessage(), LogEventType.System));
        }
    }

    

    private List<Part> buildParts(BaseRequest request) {
        List<Part> parts = new ArrayList<Part>();
        for (PartHolder h : TankHttpUtil.getPartsFromBody(request)) {
            if (h.getFileName() == null) {
                StringPart stringPart = new StringPart(h.getPartName(), new String(h.getBodyAsString()));
                if (h.isContentTypeSet()) {
                    stringPart.setContentType(h.getContentType());
                }
                parts.add(stringPart);
            } else {
                PartSource partSource = new ByteArrayPartSource(h.getFileName(), h.getBody());
                FilePart p = new FilePart(h.getPartName(), partSource);
                if (h.isContentTypeSet()) {
                    p.setContentType(h.getContentType());
                }
                parts.add(p);
            }
        }
        return parts;
    }

   

}
