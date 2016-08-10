/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.conversation.Cookie;
import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Protocol;
import com.intuit.tank.conversation.Request;
import com.intuit.tank.conversation.Response;

/**
 * HeaderParseUtil
 * 
 * @author dangleton
 * 
 */
public class HeaderParser {

    private static final Logger LOG = LogManager.getLogger(HeaderParser.class);
    // HTTP/1.0 200 OK
    private static final int RESPONSE_STATUS_CODE_INDEX = 1;
    private static final int RESPONSE_STATUS_MSG_INDEX = 2;
    private static final int RESPONSE_PROTOCOL_INDEX = 0;

    // GET /path/to/file/index.html HTTP/1.0
    private static final int REQUEST_PROTOCOL_INDEX = 2;
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;

    private Map<String, List<String>> headerMap = new HashMap<String, List<String>>();

    private Collection<Header> headers;

    private HeaderType headerType;
    private String[] firstLine;
    private DateFormat expiresFormat = new SimpleDateFormat("EE, dd-MMM-yyyy HH:mm:ss z");
    private DateFormat responseDateFormat = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss z");

    public HeaderParser(Request request) {
        this(HeaderType.Request, request.getFirstLine(), request.getHeaders());
    }

    public HeaderParser(Response response) {
        this(HeaderType.Response, response.getFirstLine(), response.getHeaders());
    }

    public HeaderParser(HeaderType type, String firstLine, Collection<Header> headers) {
        this.headerType = type;
        this.headers = headers;
        this.firstLine = firstLine != null ? firstLine.split("\\s") : new String[0];

        for (Header header : headers) {
            List<String> list = headerMap.get(header.getKey().toLowerCase());
            if (list == null) {
                list = new ArrayList<String>();
                headerMap.put(header.getKey().toLowerCase(), list);
            }
            String value = header.getValue();
            if (this.headerType == HeaderType.Request && header.getKey().equalsIgnoreCase("Authorization")) {
                int index = value.indexOf(':');
                if (index != -1) {
                    value = value.substring(0, index) + " tank removed auth info";
                }
            }
            list.add(value);
        }
    }

    /**
     * 
     * @param line
     * @return
     */
    public static String extractPath(String line) {
        String ret = null;
        String[] lines = line.split("\\s");
        if (lines.length > REQUEST_PATH_INDEX) {
            ret = lines[REQUEST_PATH_INDEX];
            if (ret.indexOf('?') != -1) {
                ret = ret.substring(0, ret.indexOf('?'));
            }
        }
        return ret;
    }

    /**
     * 
     * @param line
     * @return
     */
    public static String extractLocation(String line) {
        String ret = null;
        String[] lines = line.split("\\s");
        if (lines.length > REQUEST_PATH_INDEX) {
            ret = lines[REQUEST_PATH_INDEX];
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public static int extractStatusCode(String line) {
        String[] lines = line.split("\\s");
        if (lines.length > RESPONSE_STATUS_CODE_INDEX) {
            return Integer.parseInt(lines[RESPONSE_STATUS_CODE_INDEX]);
        }
        return 0;
    }

    /**
     * @return
     */
    public List<Cookie> getCookies() {
        if (headerType == HeaderType.Request) {
            return getRequestCookies();
        }
        return getResponseCookies();
    }

    /**
     * 
     * @return
     */
    public int getStatusCode() {
        if (headerType == HeaderType.Response && firstLine.length > RESPONSE_STATUS_CODE_INDEX) {
            return Integer.parseInt(firstLine[RESPONSE_STATUS_CODE_INDEX]);
        }
        return 0;
    }

    public String getUrl(Protocol protocol) {
        String port = getPort();
        return protocol.name() + "://" + getHost() + (StringUtils.isEmpty(port) ? "" : ":" + port) + getFullPath();
    }

    public List<Header> getPassThroughHeaders() {
        ArrayList<Header> ret = new ArrayList<Header>(headers.size());
        for (Header h : headers) {
            String s = h.getKey().toLowerCase();
            if (!s.contains("cookie"))
                ret.add(h);
        }
        ret.addAll(this.extractFirstLineHeaders());
        return ret;
    }

    public String getRedirectLocation() {
        return getSingleValue("Location", null);

    }

    /**
     * 
     * @return
     */
    public String getContentType() {
        return getSingleValue("Content-Type", "");
    }

    /**
     * 
     * @return
     */
    public String getHost() {
        String ret = getSingleValue("Host");
        if (!StringUtils.isEmpty(ret)) {
            int ind = ret.indexOf(':');
            if (ind != -1) {
                ret = ret.substring(0, ind);
            }
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public String getPort() {
        String ret = getSingleValue("Host");
        if (!StringUtils.isEmpty(ret)) {
            int ind = ret.indexOf(':');
            if (ind != -1) {
                ret = ret.substring(ind + 1);
            } else {
                ret = "";
            }
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public int getContentLength() {
        String contentLength = getSingleValue("Content-Length");
        if (NumberUtils.isNumber(contentLength)) {
            return Integer.parseInt(contentLength);
        }
        return 0;
    }

    /**
     * 
     * @return
     */
    public List<KeyValuePair> getQueryParams() {
        String path = firstLine[REQUEST_PATH_INDEX];
        List<KeyValuePair> ret = new ArrayList<KeyValuePair>();
        if (!StringUtils.isEmpty(path) && path.indexOf('?') != -1) {
            path = path.substring(path.indexOf('?') + 1);
            String delim = "\\;";
            if (path.indexOf('&') != -1 || path.indexOf(';') == -1) {
                delim = "\\&";
            }
            String[] split = path.split(delim);
            for (String keyPair : split) {
                if (keyPair.indexOf('=') == -1) {
                    if (!StringUtils.isEmpty(keyPair)) {
                        ret.add(new KeyValuePair(keyPair.trim()));
                    }
                } else {
                    String[] pair = keyPair.split("=");
                    String value = pair.length > 1 ? pair[1].trim() : "";
                    ret.add(new KeyValuePair(pair[0].trim(), value));
                }
            }
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public List<KeyValuePair> getPostParameters(String body) {
        List<KeyValuePair> ret = new ArrayList<KeyValuePair>();
        if (headerType == HeaderType.Request && !StringUtils.isEmpty(body)
                && getContentType().toLowerCase().contains("www-form-urlencoded")) {
            String[] split = body.split("\\&");
            for (String keyPair : split) {
                if (keyPair.indexOf('=') == -1) {
                    ret.add(new KeyValuePair(keyPair.trim()));
                } else {
                    String[] pair = keyPair.split("=");
                    String value = pair.length > 1 ? pair[1].trim() : "";
                    ret.add(new KeyValuePair(pair[0].trim(), value));
                }
            }
        }
        return ret;
    }

    /*
     * 
     */
    public String getStatusMessage() {
        if (headerType == HeaderType.Response && firstLine.length > RESPONSE_STATUS_MSG_INDEX) {
            return firstLine[RESPONSE_STATUS_MSG_INDEX];
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public String getHttpVersion() {
        if (headerType == HeaderType.Response && firstLine.length > RESPONSE_PROTOCOL_INDEX) {
            return firstLine[RESPONSE_STATUS_MSG_INDEX];
        } else if (headerType == HeaderType.Request && firstLine.length > REQUEST_PROTOCOL_INDEX) {
            return firstLine[REQUEST_PROTOCOL_INDEX];
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public String getPath() {
        String ret = null;
        if (headerType == HeaderType.Request && firstLine.length > REQUEST_PATH_INDEX) {
            ret = firstLine[REQUEST_PATH_INDEX];
            if (ret.indexOf('?') != -1) {
                ret = ret.substring(0, ret.indexOf('?'));
            }
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public String getFullPath() {
        String ret = null;
        if (headerType == HeaderType.Request && firstLine.length > REQUEST_PATH_INDEX) {
            ret = firstLine[REQUEST_PATH_INDEX];
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public String getMethod() {
        if (headerType == HeaderType.Request && firstLine.length > REQUEST_METHOD_INDEX) {
            return firstLine[REQUEST_METHOD_INDEX];
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public Date getResponseDate() {
        Date ret = null;
        if (headerType == HeaderType.Response) {
            String dateStr = getSingleValue("date");
            parseDate(dateStr);
        }
        return ret;
    }

    /**
     * @param dateStr
     */
    private Date parseDate(String dateStr) {
        Date ret = null;
        if (dateStr != null) {
            try {
                ret = responseDateFormat.parse(dateStr);
            } catch (ParseException e) {
                try {
                    ret = expiresFormat.parse(dateStr);
                } catch (ParseException e1) {
                    LOG.warn("Cannot parse date " + dateStr);
                }
            }
        }
        return ret;
    }

    /**
     * 
     * @param key
     * @return
     */
    private String getSingleValue(String key) {
        String ret = null;
        List<String> list = headerMap.get(key.toLowerCase());
        if (list != null && list.size() == 1) {
            ret = list.get(0);
        }
        return ret;
    }

    /**
     * 
     * @param key
     * @return
     */
    private String getSingleValue(String key, String defaultValue) {
        String ret = getSingleValue(key);
        return ret != null ? ret : defaultValue;
    }

    /**
     * @return
     */
    private List<Cookie> getResponseCookies() {
        List<Cookie> ret = new ArrayList<Cookie>();
        List<String> list = headerMap.get("set-cookie");

        if (list != null) {
            for (String cookieValue : list) {
                Cookie c = new Cookie();
                ret.add(c);
                String[] attributePairs = cookieValue.split(";");
                boolean isFirst = true;
                for (String pair : attributePairs) {
                    if (!StringUtils.isEmpty(pair)) {
                        String[] cookie = pair.split("=");
                        if (cookie.length > 0 && !StringUtils.isEmpty(cookie[0])) {
                            String key = cookie[0].trim();
                            String value = "";
                            if (cookie.length > 1) {
                                value = cookie[1].trim();
                            }
                            if (isFirst) {
                                c.setKey(key);
                                c.setValue(value);
                                isFirst = false;
                            } else if (key.equalsIgnoreCase("Expires")) {
                                c.setExpires(parseDate(value));
                            } else if (key.equalsIgnoreCase("Max-Age") && NumberUtils.isNumber(value)) {
                                c.setMaxAge(value);
                            } else if (key.equalsIgnoreCase("Path") && !StringUtils.isEmpty(value)) {
                                c.setPath(value);
                            } else if (key.equalsIgnoreCase("Domain") && !StringUtils.isEmpty(value)) {
                                c.setDomain(value);
                            } else if (key.equalsIgnoreCase("Secure")) {
                                c.setSecuredOnly(true);
                            } else if (key.equalsIgnoreCase("HttpOnly")) {
                                c.setHttpOnly(true);
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    private List<Cookie> getRequestCookies() {
        List<Cookie> ret = new ArrayList<Cookie>();
        List<String> list = headerMap.get("cookie");
        if (list != null) {
            for (String cookieValue : list) {
                String[] cookiePairs = cookieValue.split(";");
                for (String pair : cookiePairs) {
                    if (!StringUtils.isEmpty(pair)) {
                        String[] cookie = pair.split("=");
                        if (cookie.length > 0 && !StringUtils.isEmpty(cookie[0])) {
                            String key = cookie[0].trim();
                            String value = "";
                            if (cookie.length > 1) {
                                value = cookie[1].trim();
                            }
                            ret.add(new Cookie(key, value));

                        }
                    }
                }
            }
        }
        return ret;
    }

    public List<Header> extractFirstLineHeaders() {
        List<Header> ret = new ArrayList<Header>();
        if (this.headerType.equals(HeaderType.Response)) {
            ret.add(new Header("X-HTTP_PROTOCOL", this.firstLine[0]));
            ret.add(new Header("X-HTTP_STATUS_CODE", this.firstLine[1]));
            ret.add(new Header("X-HTTP_STATUS_MESSAGE",
                    StringUtils.join(Arrays.copyOfRange(this.firstLine, 2, this.firstLine.length))));
        }
        return ret;
    }

    public static enum HeaderType {
        Request,
        Response
    }
}
