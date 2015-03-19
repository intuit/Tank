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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.http.binary.BinaryResponse;
import com.intuit.tank.http.json.JsonResponse;
import com.intuit.tank.http.xml.XMLResponse;
import com.intuit.tank.logging.LogEventType;

public class BaseRequestHandler {

    private static Logger logger = Logger.getLogger(BaseRequestHandler.class);

    public static URL buildUrl(String protocol, String host, int port,
            String path, Map<String, String> urlVariables)
            throws MalformedURLException {

        // no default port specified for http
        if (protocol.equalsIgnoreCase("http") && port == -1)
            port = 80;
        else if (protocol.equalsIgnoreCase("https") && port == -1)
            port = 443;

        // ensure that port 80 and 8080 requests use http and not https
        if (port == 80 || port == 8080) {
            protocol = "http";
        }

        return new URL(protocol, host, port, path
                + getQueryString(urlVariables));
    }

    public static String getQueryString(Map<String, String> urlVariables) {

        StringBuilder queryString = new StringBuilder();

        // Set the query string
        if (urlVariables != null) {
            if (!urlVariables.isEmpty()) {

                queryString.append("?");

                // Set<Map.Entry<String, String>> set = urlVariables.entrySet();
                // Iterator<Map.Entry<String, String>> iterator =
                // set.iterator();
                for (Entry<String, String> entry : urlVariables.entrySet()) {
                    try {
                        StringBuilder nvp = new StringBuilder();
                        nvp.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        if (entry.getValue() != null) {
                            nvp.append("=");
                            nvp.append(URLEncoder.encode(entry.getValue(),
                                    "UTF-8"));
                        }
                        nvp.append("&");
                        queryString.append(nvp.toString());

                    } catch (Exception ex) {
                        logger.warn(LogUtil.getLogMessage("Unable to set query string value: "
                                + ex.getMessage(), LogEventType.System));
                    }
                }
            }
        }

        // Remove the last &
        String reqQueryString = "";
        if (queryString.length() > 0) {
            if (queryString.charAt(queryString.length() - 1) == '&')
                reqQueryString = queryString.deleteCharAt(
                        queryString.length() - 1).toString();
            else
                reqQueryString = queryString.toString();
        }

        return reqQueryString;
    }

    /**
     * Set all the header keys
     * 
     * @param connection
     */
    @SuppressWarnings("rawtypes")
    public static void setHeaders(HttpMethod method,
            HashMap<String, String> headerInformation) {
        try {
            Set set = headerInformation.entrySet();
            Iterator iter = set.iterator();

            while (iter.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) iter.next();
                method.setRequestHeader((String) mapEntry.getKey(),
                        (String) mapEntry.getValue());
            }
        } catch (Exception ex) {
            logger.warn(LogUtil.getLogMessage("Unable to set header: " + ex.getMessage(), LogEventType.System));
        }
    }

    public static void handleResponse(PostMethod post, BaseResponse response,
            HttpState httpstate) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long startReadTime = System.currentTimeMillis();
        try {
            // read response body
            InputStream httpInputStream = null;
            httpInputStream = post.getResponseBodyAsStream();

            if (null != httpInputStream) {
                int curByte = httpInputStream.read();
                while (curByte >= 0) {
                    out.write(curByte);
                    curByte = httpInputStream.read();
                }

            }
            long endReadTime = System.currentTimeMillis();
            byte[] bResponse = out.toByteArray();
            processResponse(bResponse, startReadTime, endReadTime, response,
                    post.getStatusText(), post.getStatusCode(),
                    post.getResponseHeaders(), httpstate);
        } catch (Exception ex) {
            logger.error(LogUtil.getLogMessage(ex.getMessage(), LogEventType.IO), ex);
            long endReadTime = System.currentTimeMillis();
            processResponse("".getBytes(), startReadTime, endReadTime,
                    response, "ERROR", -1, new Header[0], httpstate);
        }
    }

    /**
     * Process the response data
     */
    public static void processResponse(byte[] bResponse, long startTime,
            long endTime, BaseResponse response, String message, int httpCode,
            Header[] headers, HttpState httpstate) {

        try {
            if (response == null) {
                response = newResponseObject(headers);
            }

            // Get response detail information
            response.setHttpMessage(message);
            response.setHttpCode(httpCode);

            // Get response header information
            for (int h = 0; h < headers.length; h++) {
                response.setHeader(headers[h].getName(), headers[h].getValue());
            }

            response.setCookies(httpstate.getCookies());
            response.setResponseTime(endTime - startTime);
            String contentType = response.getHttpHeader("Content-Type");
            String contentEncode = response.getHttpHeader("Content-Encoding");
            if (BaseResponse.isDataType(contentType) && contentEncode != null
                    && contentEncode.toLowerCase().contains("gzip")) {
                // decode gzip for data types
                try {
                    GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(bResponse));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(in, out);
                    bResponse = out.toByteArray();
                } catch (Exception e) {
                    logger.warn(LogUtil.getLogMessage("cannot decode gzip stream: " + e, LogEventType.System));
                }
            }
            response.setResponseBody(bResponse);

        } catch (Exception ex) {
            logger.warn("Unable to get response: " + ex.getMessage());
        } finally {
            response.logResponse();
        }
    }

    /**
     * New up a response object depending on the content type
     * 
     * @return
     */
    private static BaseResponse newResponseObject(Header[] headers) {

        String contentType = "";
        for (int h = 0; h < headers.length; h++) {
            if (headers[h].getName().equalsIgnoreCase("Content-Type"))
                contentType = headers[h].getValue();
        }
        if (contentType.contains("xml"))
            return new XMLResponse();
        else if (contentType.contains("json"))
            return new JsonResponse();
        else
            return new BinaryResponse();
    }

}
