package com.intuit.tank.http.multipart;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseRequestHandler;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.logging.LogEventType;

public class MultiPartRequest extends BaseRequest {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MultiPartRequest.class);

    private static final String CONTENT_TYPE = "multipart/form-data";
    private List<PartHolder> parameters = new ArrayList<MultiPartRequest.PartHolder>();

    public MultiPartRequest(HttpClient client) {
        super(client);
        setContentType(CONTENT_TYPE);
    }

    @Override
    public void setBody(String bodyEncoded) {
        String s = new String(Base64.decodeBase64(bodyEncoded));
        String boundary = StringUtils.substringBefore(s, "\r\n").substring(2);
        super.setBody(s);
        try {
            // s = getBody();
            @SuppressWarnings("deprecation") MultipartStream multipartStream = new MultipartStream(
                    new ByteArrayInputStream(Base64.decodeBase64(bodyEncoded)),
                    boundary.getBytes());
            boolean nextPart = multipartStream.skipPreamble();
            while (nextPart) {
                String header = multipartStream.readHeaders();
                // process headers
                // create some output stream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                multipartStream.readBodyData(bos);
                PartHolder p = new PartHolder(bos.toByteArray(), header);
                parameters.add(p);
                nextPart = multipartStream.readBoundary();
            }
        } catch (MultipartStream.MalformedStreamException e) {
            LOG.error(e.toString(), e);
            // the stream failed to follow required syntax
        } catch (IOException e) {
            LOG.error(e.toString(), e);
            // a read or write error occurred
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

            List<Part> parts = buildParts();

            httppost.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), httppost
                    .getParams()));

            sendRequest(response, httppost, requestBody);
        } catch (MalformedURLException e) {
            LOG.error(
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
                LOG.info(LogUtil.getLogMessage("Response from POST to " + theUrl
                        + " got status code " + response.getHttpCode() + " BODY { "
                        + response.getResponseBody() + " }", LogEventType.Informational));
            }
        }

    }

    protected List<Part> buildParts() {
        List<Part> parts = new ArrayList<Part>();
        for (PartHolder h : parameters) {
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

    @Override
    public void setKey(String key, String value) {

    }

    @Override
    public String getKey(String key) {
        return null;
    }

    @Override
    public void setNamespace(String name, String value) {

    }

    private static class PartHolder {
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
            return new String(body, Charset.forName("UTF-8"));
        }

        // Content-Disposition: form-data; name="uploadname1"; filename="diamond-sword.png"
        public String getPartName() {
            return dispositionMap.get("name");
        }

        // Content-Disposition: form-data; name="uploadname1"; filename="diamond-sword.png"
        public String getFileName() {
            return dispositionMap.get("filename");
        }

        // Content-Disposition: form-data; name="uploadname1"; filename="diamond-sword.png"
        public String getContentType() {
            String ct = headerMap.get("Content-Type");
            if (ct == null) {
                ct = "text/plain";
            }
            return ct;
        }

        // Content-Disposition: form-data; name="uploadname1"; filename="diamond-sword.png"
        public String getContentDisposition() {
            String ct = headerMap.get("Content-Disposition");
            if (ct == null) {
                ct = "form-data";
            }
            return ct;
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
