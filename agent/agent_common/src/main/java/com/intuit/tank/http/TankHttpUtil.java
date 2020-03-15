package com.intuit.tank.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.http.binary.BinaryResponse;
import com.intuit.tank.http.json.JsonResponse;
import com.intuit.tank.http.xml.XMLResponse;

import static java.util.stream.Collectors.joining;

/**
 * utitly methods for tank http clients
 * @author denisa
 *
 */
public class TankHttpUtil {
    private static Logger LOG = LogManager.getLogger(TankHttpUtil.class);

    public static URL buildUrl(String protocol, String host, int port, String path, Map<String, String> urlVariables) {
        try {
            // ensure that port 80 and 8080 requests use http and not https
            if (port == 80 || port == 8080) {
                protocol = "http";
            }

            // no default port specified for http
            return (port == -1) ?
                    new URL(protocol, host, path + getQueryString(urlVariables)) :
                    new URL(protocol, host, port, path + getQueryString(urlVariables));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getQueryString(Map<String, String> urlVariables) {
        if (urlVariables != null && !urlVariables.isEmpty()) {
            return "?" + urlVariables.entrySet().stream()
                    .map(entry -> {
                        try {
                            return URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name())
                                    + "="
                                    + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException ex) {
                            LOG.warn("Unable to set query string value: " + ex.getMessage());
                        }
                        return "";
                    })
                    .collect(joining("&"));
        }
        return "";
    }
    
    /**
     * New up a response object depending on the content type
     * 
     * @return
     */
    public static BaseResponse newResponseObject(String contentTypeHeader) {
        String contentType = StringUtils.isNotBlank(contentTypeHeader) ? contentTypeHeader : "";
        return contentType.contains("xml") ?
                new XMLResponse() :
                contentType.contains("json") ?
                        new JsonResponse() :
                        new BinaryResponse();
    }
    
    public static List<PartHolder> getPartsFromBody(BaseRequest request) {
    	List<PartHolder> parameters = new ArrayList<PartHolder>();
        String s = new String(Base64.decodeBase64(request.getBody()));
        if (StringUtils.isNotBlank(s)) {
	        String boundary = StringUtils.substringBefore(s, "\r\n").substring(2);
	        request.setBody(s);
	        try {
				MultipartStream multipartStream = new MultipartStream(new ByteArrayInputStream(s.getBytes()), boundary.getBytes(), 4096, null);
	            boolean nextPart = multipartStream.skipPreamble();
	            while (nextPart) {
	                String header = multipartStream.readHeaders();
	                ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                multipartStream.readBodyData(bos);
	                PartHolder p = new PartHolder(bos.toByteArray(), header);
	                parameters.add(p);
	                nextPart = multipartStream.readBoundary();
	            }
	        } catch(MultipartStream.MalformedStreamException e) {
	        	LOG.error("the multipart stream failed to follow required syntax");
	        	LOG.error(e.toString(), e);
	        } catch (Exception e) {
	            LOG.error(e.toString(), e);
	            // a read or write error occurred
	        }
        }
        return parameters;
    }

    public static class PartHolder {
        private byte[] body;
        private String header;
        private Map<String, String> headerMap = new HashMap<String, String>();
        private Map<String, String> dispositionMap = new HashMap<String, String>();

        public PartHolder(byte[] body, String header) {
            super();
            this.body = body;
            this.header = header;
            String[] headers = StringUtils.splitByWholeSeparator(this.header, "\r\n");
            for (String s : headers) {
                if (StringUtils.isNotBlank(s) && s.indexOf(':') != -1) {
                    String key = StringUtils.substringBefore(s, ":").trim();
                    String value = StringUtils.substringAfter(s, ":").trim();
                    headerMap.put(key, value);
                }
            }
            String[] dispositions = StringUtils.split(getContentDisposition(), ';');
            for (String s : dispositions) {
                if (StringUtils.isNotBlank(s) && s.indexOf('=') != -1) {
                    String key = removeQuotes(StringUtils.substringBefore(s, "=").trim());
                    String value = removeQuotes(StringUtils.substringAfter(s, "=").trim());
                    dispositionMap.put(key, value);
                }
            }
        }

        /**
         * @return the body
         */
        public byte[] getBody() {
            return body;
        }

        /**
         * @return the body as a string
         */
        public String getBodyAsString() {
            return new String(body, StandardCharsets.UTF_8);
        }

        // Content-Disposition: form-data; name="uploadname1";
        // filename="diamond-sword.png"
        public String getPartName() {
            return dispositionMap.get("name");
        }

        // Content-Disposition: form-data; name="uploadname1";
        // filename="diamond-sword.png"
        public String getFileName() {
            return dispositionMap.get("filename");
        }

        // Content-Disposition: form-data; name="uploadname1";
        // filename="diamond-sword.png"
        public String getContentType() {
            String ct = headerMap.get("Content-Type");
            return ct == null ?
                    "text/plain" :
                    ct;
        }

        // Content-Disposition: form-data; name="uploadname1";
        // filename="diamond-sword.png"
        public String getContentDisposition() {
            String ct = headerMap.get("Content-Disposition");
            return ct == null ?
                    "form-data" :
                    ct;
        }

        /**
         * 
         * @return
         */
        public boolean isContentTypeSet() {
            return headerMap.get("Content-Type") != null;
        }

        /**
         * 
         * @param s
         * @return
         */
        private String removeQuotes(String s) {
            s = StringUtils.removeEnd(s, "\"");
            s = StringUtils.removeStart(s, "\"");
            return s;
        }

    }

    
}
