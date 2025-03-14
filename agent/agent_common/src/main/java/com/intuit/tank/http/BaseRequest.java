package com.intuit.tank.http;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class BaseRequest {

	static Logger LOG = LogManager.getLogger(BaseRequest.class);

	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    private static final char NEWLINE = '\n';
    
    protected BaseResponse response = null;
    private TankHttpLogger logUtil;

    protected String protocol = "https";
    protected String host = null;
    protected int port = -1;
    protected String path = "/";
    protected String contentType = "application/x-www-form-urlencoded";
    protected String contentTypeCharSet = StandardCharsets.UTF_8.toString();
    protected String requestUrl;
    private boolean async = false;

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

    private TankHttpClient httpclient;
    private Date timestamp;


    /**
     * Constructor
     */
    protected BaseRequest(TankHttpClient httpclient, TankHttpLogger logUtil) {
        this.headerInformation = new HashMap<String, String>();
        this.urlVariables = new HashMap<String, String>();
        this.httpclient = httpclient;
        this.logUtil = logUtil;
    }

    /**
     * @return the logUtil
     */
    public TankHttpLogger getLogUtil() {
        return logUtil;
    }

    public BaseResponse getResponse() {
        return response;
    }
    
    public void setResponse(BaseResponse response) {
        this.response = response;
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

    public TankHttpClient getHttpclient() {
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
     * Execute the GET. Use this to base the response off of the content type
     */
    public void doGet(BaseResponse response) {
        this.response = response;
        requestUrl = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables).toString();
        httpclient.doGet(this);
    }

    /**
     * Execute the post. Use this to force the type of response
     * 
     * @param response
     *            The response object to populate
     */
    public void doPut(BaseResponse response) {
        this.response = response;
        requestUrl = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables).toString();
        httpclient.doPut(this);
    }

    /**
     * Execute the delete request.
     * 
     * @param response
     *            The response object to populate
     */
    public void doDelete(BaseResponse response) {
        this.response = response;
        requestUrl = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables).toString();
        httpclient.doDelete(this);
    }

    /**
     * Execute the options request.
     * 
     * @param response
     *            The response object to populate
     */
    public void doOptions(BaseResponse response) {
        this.response = response;
        requestUrl = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables).toString();
        httpclient.doOptions(this);
    }
    /**
     * Execute the POST.
     */
    public void doPost(BaseResponse response) {
        this.response = response;
        requestUrl = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables).toString();
        httpclient.doPost(this);

    }

    /**
     * Execute the PATCH.
     */
    public void doPatch(BaseResponse response) {
        this.response = response;
        requestUrl = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables).toString();
        httpclient.doPatch(this);

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
        this.headerInformation.put(key.toLowerCase(), value);
    }

    public void removeHeader(String key) {
        this.headerInformation.remove(key.toLowerCase());
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
        return TankHttpUtil.getQueryString(urlVariables);
    }

    @SuppressWarnings("rawtypes")
    public void logRequest(String url, String body, String method, Map<String, String> headerInformation, List<String> cookies, boolean force) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append("REQUEST URL: " + method + " " + url).append(NEWLINE);
            sb.append("CONTENT TYPE: " + contentType).append(NEWLINE);
            // Header Information
            for (Map.Entry mapEntry : headerInformation.entrySet()) {
                sb.append("REQUEST HEADER: " + (String) mapEntry.getKey() + " = " + (String) mapEntry.getValue()).append(NEWLINE);
            }
            // Cookies Information
            if (cookies != null) {
                for (String cookie : cookies) {
                    sb.append(cookie).append(NEWLINE);
                }
            }
            if (StringUtils.isNotEmpty(body)) {
                sb.append("REQUEST SIZE: " + body.getBytes().length).append(NEWLINE);
                sb.append("REQUEST BODY: " + body).append(NEWLINE);
            }
            this.logMsg = sb.toString();
            LOG.debug("******** REQUEST *********");
            LOG.debug(this.logMsg);

        } catch (Exception ex) {
            LOG.error("Unable to log request", ex);
        }
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    public void setContentTypeCharSet(String contentTypeCharSet) {
        this.contentTypeCharSet = contentTypeCharSet;
    }
    public String getContentTypeCharSet() {
        return contentTypeCharSet;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
    public boolean getAsync() {
        return async;
    }

}
