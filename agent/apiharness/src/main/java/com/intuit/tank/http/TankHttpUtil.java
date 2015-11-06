package com.intuit.tank.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.http.binary.BinaryResponse;
import com.intuit.tank.http.json.JsonResponse;
import com.intuit.tank.http.xml.XMLResponse;
import com.intuit.tank.logging.LogEventType;

public class TankHttpUtil {

    private static Logger logger = Logger.getLogger(TankHttpUtil.class);

    public static URL buildUrl(String protocol, String host, int port, String path, Map<String, String> urlVariables) {

        try {
            // no default port specified for http
            if (protocol.equalsIgnoreCase("http") && port == -1) {
                port = 80;
            } else if (protocol.equalsIgnoreCase("https") && port == -1) {
                port = 443;
            }
            // ensure that port 80 and 8080 requests use http and not https
            if (port == 80 || port == 8080) {
                protocol = "http";
            }

            return new URL(protocol, host, port, path + getQueryString(urlVariables));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
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
                            nvp.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                        }
                        nvp.append("&");
                        queryString.append(nvp.toString());

                    } catch (Exception ex) {
                        logger.warn(LogUtil.getLogMessage("Unable to set query string value: " + ex.getMessage(), LogEventType.System));
                    }
                }
            }
        }

        // Remove the last &
        String reqQueryString = "";
        if (queryString.length() > 0) {
            if (queryString.charAt(queryString.length() - 1) == '&')
                reqQueryString = queryString.deleteCharAt(queryString.length() - 1).toString();
            else
                reqQueryString = queryString.toString();
        }

        return reqQueryString;
    }

    /**
     * New up a response object depending on the content type
     * 
     * @return
     */
    public static BaseResponse newResponseObject(String contentTypeHeader) {
        String contentType = StringUtils.isNotBlank(contentTypeHeader) ? contentTypeHeader : "";
        if (contentType.contains("xml")) {
            return new XMLResponse();
        } else if (contentType.contains("json")) {
            return new JsonResponse();
        } else {
            return new BinaryResponse();
        }
    }

}
