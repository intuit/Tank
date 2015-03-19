package com.intuit.tank.http;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.settings.AgentConfig;
import com.sun.jersey.api.client.ClientResponse.Status;

public abstract class BaseRequest {

    static Logger logger = Logger.getLogger(BaseRequest.class);

    private static final char NEWLINE = '\n';

    protected BaseResponse response = null;

    protected String protocol = "https";
    protected String host = null;
    protected int port = -1;
    protected String path = "/";
    protected String contentType = "application/x-www-form-urlencoded";
    protected String contentTypeCharSet = "UTF-8";
    protected String requestUrl;

    protected HashMap<String, String> headerInformation = null;
    protected HashMap<String, String> urlVariables = null;

    protected String proxyHost = "";
    protected int proxyPort = 80;

    protected String body = "";
    protected boolean binary = false;

    protected long connectionTime = 0;
    protected long writeTime = 0;

    protected SSLContext sslContext = null;
    protected String logMsg;

    private HttpClient httpclient;
    private Date timestamp;

    public BaseResponse getResponse() {
        return response;
    }

    public void setResponse(BaseResponse response) {
        this.response = response;
    }

    /**
     * Constructor
     */
    protected BaseRequest(HttpClient httpclient) {
        this.headerInformation = new HashMap<String, String>();
        this.urlVariables = new HashMap<String, String>();
        this.httpclient = httpclient;
    }

    /**
     * @return the responseLogMsg
     */
    public String getLogMsg() {
        return logMsg;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Execute the GET. Use this to base the response off of the content type
     */
    public void doGet(BaseResponse response) {
        GetMethod httpget = null;
        long waitTime = 0L;
        InputStream httpInputStream = null;
        try {
            this.response = response;
            URL url = BaseRequestHandler.buildUrl(protocol, host, port, path,
                    urlVariables);
            requestUrl = url.toString();
            httpget = new GetMethod(requestUrl);
            logRequest(requestUrl, null, httpget.getName(), headerInformation,
                    httpclient, false);
            // FlowController flowController =
            // APITestHarness.getInstance().getFlowController(Thread.currentThread().getId());
            BaseRequestHandler.setHeaders(httpget, headerInformation);
            long startTime = System.currentTimeMillis();
            timestamp = new Date(startTime);

            httpclient.executeMethod(httpget);

            // read response body
            byte[] responseBody = new byte[0];
            httpInputStream = httpget.getResponseBodyAsStream();
            if (httpInputStream != null) {
                responseBody = IOUtils.toByteArray(httpInputStream);
            }
            long endTime = System.currentTimeMillis();
            BaseRequestHandler.processResponse(responseBody, startTime,
                    endTime, response, httpget.getStatusText(),
                    httpget.getStatusCode(), httpget.getResponseHeaders(),
                    httpclient.getState());
            waitTime = endTime - startTime;
        } catch (Exception ex) {
            logger.error(
                    LogUtil.getLogMessage("Could not do " + httpget.getName()
                            + " to url " + requestUrl + " |  error: "
                            + ex.toString(), LogEventType.IO), ex);
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(httpInputStream);
            if (null != httpget) {
                httpget.releaseConnection();
            }
        }
        if (waitTime != 0) {
            doWaitDueToLongResponse(waitTime, requestUrl);
        }
    }

    public HttpClient getHttpclient() {
        return httpclient;
    }

    public HashMap<String, String> getHeaderInformation() {
        return headerInformation;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * @return the startRequestTime
     */
    public Date getTimeStamp() {
        return timestamp;
    }

    /**
     * Execute the post. Use this to force the type of response
     * 
     * @param newResponse
     *            The response object to populate
     */
    public void doPut(BaseResponse response) {
        PutMethod httpput = null;
        try {
            URL url = BaseRequestHandler.buildUrl(protocol, host, port, path,
                    urlVariables);
            httpput = new PutMethod(url.toString());
            String requestBody = this.getBody(); // Multiple calls can be
                                                 // expensive, so get it once
            StringRequestEntity entity;

            entity = new StringRequestEntity(requestBody, getContentType(),
                    contentTypeCharSet);
            httpput.setRequestEntity(entity);
            sendRequest(response, httpput, requestBody);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (null != httpput)
                httpput.releaseConnection();
        }
    }

    /**
     * Execute the delete request.
     * 
     * @param response
     *            The response object to populate
     */
    public void doDelete(BaseResponse response) {
        DeleteMethod httpdelete = null;
        try {
            URL url = BaseRequestHandler.buildUrl(protocol, host, port, path,
                    urlVariables);
            httpdelete = new DeleteMethod(url.toString());
            String requestBody = this.getBody(); // Multiple calls can be
                                                 // expensive, so get it once
            String type = headerInformation.get("Content-Type");
            if (StringUtils.isBlank(type)) {
                headerInformation.put("Content-Type", "application/x-www-form-urlencoded");
            }
            sendRequest(response, httpdelete, requestBody);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (null != httpdelete)
                httpdelete.releaseConnection();
        }
    }

    /**
     * Execute the POST.
     */
    public void doPost(BaseResponse response) {
        PostMethod httppost = null;
        String theUrl = null;
        try {
            URL url = BaseRequestHandler.buildUrl(protocol, host, port, path,
                    urlVariables);
            theUrl = url.toExternalForm();
            httppost = new PostMethod(url.toString());
            String requestBody = getBody();
            StringRequestEntity entity = new StringRequestEntity(requestBody,
                    getContentType(), contentTypeCharSet);
            httppost.setRequestEntity(entity);

            sendRequest(response, httppost, requestBody);
        } catch (MalformedURLException e) {
            logger.error(
                    LogUtil.getLogMessage("Malformed URL Exception: "
                            + e.toString(), LogEventType.IO), e);
            // swallowing error. validatin will check if there is no response
            // and take appropriate action
            throw new RuntimeException(e);
        } catch (Exception ex) {
            // logger.error(LogUtil.getLogMessage(ex.toString()), ex);
            // swallowing error. validatin will check if there is no response
            // and take appropriate action
            throw new RuntimeException(ex);
        } finally {
            if (null != httppost) {
                httppost.releaseConnection();
            }
            if (APITestHarness.getInstance().getTankConfig().getAgentConfig()
                    .getLogPostResponse()) {
                logger.info(LogUtil.getLogMessage("Response from POST to " + theUrl
                        + " got status code " + response.httpCode + " BODY { "
                        + response.response + " }", LogEventType.Informational));
            }
        }

    }

    public void sendRequest(BaseResponse response, @Nonnull HttpMethod method,
            String requestBody) {
        String uri = null;
        long waitTime = 0L;
        try {
            this.response = response;
            uri = method.getURI().toString();
            logger.debug(LogUtil.getLogMessage("About to POST request to " + uri
                    + " with requestBody  " + requestBody, LogEventType.Informational));
            logRequest(uri, requestBody, method.getName(), headerInformation,
                    httpclient, false);
            BaseRequestHandler.setHeaders(method, headerInformation);
            long startTime = System.currentTimeMillis();
            timestamp = new Date(startTime);
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
                    logger.warn("could not get response body: " + e);
                }
            }
            long endTime = System.currentTimeMillis();
            BaseRequestHandler.processResponse(responseBody, startTime,
                    endTime, response, method.getStatusText(),
                    method.getStatusCode(), method.getResponseHeaders(),
                    httpclient.getState());
            waitTime = endTime - startTime;
        } catch (Exception ex) {
            logger.error(
                    LogUtil.getLogMessage("Could not do " + method.getName()
                            + " to url " + uri + " |  error: " + ex.toString(), LogEventType.IO),
                    ex);
            throw new RuntimeException(ex);
        } finally {
            method.releaseConnection();
        }
        if (waitTime != 0) {
            doWaitDueToLongResponse(waitTime, uri);
        }
    }

    /**
     * Wait for the amount of time it took to get a response from the system if the response time is over some threshold
     * specified in the properties file. This will ensure users don't bunch up together after a blip on the system under
     * test
     * 
     * @param responseTime
     *            - response time of the request; this will also be the time to sleep
     * @param uri
     */
    private void doWaitDueToLongResponse(long responseTime, String uri) {
        try {
            AgentConfig config = APITestHarness.getInstance().getTankConfig()
                    .getAgentConfig();
            long maxAgentResponseTime = config.getMaxAgentResponseTime();
            if (maxAgentResponseTime < responseTime) {
                long waitTime = Math.min(config.getMaxAgentWaitTime(),
                        responseTime);
                logger.warn(LogUtil
                        .getLogMessage("Response time to slow | delaying "
                                + waitTime + " ms | url --> " + uri, LogEventType.Script));
                Thread.sleep(waitTime);
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted", e);
        }
    }

    /**
     * Set as value in the request
     * 
     * @param key
     *            The name of the key
     * @param value
     *            The value of the key
     */
    public abstract void setKey(String key, String value);

    /**
     * Get a key value from the request
     * 
     * @param key
     *            The name of the key
     * @return The value of the requested key
     */
    public abstract String getKey(String key);

    public abstract void setNamespace(String name, String value);

    /**
     * Sets the URL variables (GET variables) for this Request
     * 
     * @param variables
     *            HashMapped variables to set
     */
    public void setURLVariables(HashMap<String, String> variables) {
        urlVariables = variables;
    }

    /**
     * Adds the variable to this Request's URL variables
     * 
     * @param name
     *            - variable name
     * @param value
     *            - variable value
     */
    public void addURLVariable(String name, String value) {
        urlVariables.put(name, value);
    }

    public void addHeader(String key, String value) {
        this.headerInformation.put(key, value);
    }

    public void removeHeader(String key) {
        this.headerInformation.remove(key);
    }

    /**
     * Removes the variable from this Request's URL variables
     * 
     * @param name
     *            - Name of variable to remove
     */
    public void removeURLVariable(String name) {
        urlVariables.remove(name);
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPort(String port) {
        this.port = Integer.parseInt(port);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getBody() {
        return body != null ? body : "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getConnectionTime() {
        return connectionTime;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public String getReqQueryString() {
        return BaseRequestHandler.getQueryString(urlVariables);
    }

    @SuppressWarnings("rawtypes")
    protected void logRequest(String url, String body, String method,
            HashMap<String, String> headerInformation, HttpClient httpclient,
            boolean force) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append("REQUEST URL: " + method + " " + url).append(NEWLINE);
            // Header Information
            for (Map.Entry mapEntry : headerInformation.entrySet()) {
                sb.append("REQUEST HEADER: " + (String) mapEntry.getKey()
                        + " = " + (String) mapEntry.getValue()).append(NEWLINE);
            }
            // Cookies Information
            if (httpclient != null && httpclient.getState() != null
                    && httpclient.getState().getCookies() != null) {
                for (Cookie cookie : httpclient.getState().getCookies()) {
                    sb.append("REQUEST COOKIE: " + cookie.toExternalForm()
                            + " (domain=" + cookie.getDomain() + " : path=" + cookie.getPath() + ")").append(NEWLINE);
                }
            }
            if (null != body) {
                sb.append("REQUEST SIZE: " + body.getBytes().length).append(NEWLINE);
                sb.append("REQUEST BODY: " + body).append(NEWLINE);
            }
            this.logMsg = sb.toString();
            if (APITestHarness.getInstance().isDebug()) {
                System.out.println("******** REQUEST *********");
                System.out.println(this.logMsg);
            }
            logger.debug("******** REQUEST *********");
            logger.debug(this.logMsg);

        } catch (Exception ex) {
            logger.error("Unable to log request", ex);
        }
    }

}
