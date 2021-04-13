package com.intuit.tank.http.json;

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

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.intuit.tank.http.BaseResponse;

public class JsonResponse extends BaseResponse {

    static protected Logger logger = LogManager.getLogger(JsonResponse.class);
    @SuppressWarnings("rawtypes")
    private Map jsonMap = null;

    public JsonResponse() {
        this("");
    }

    public JsonResponse(String resp) {
        this.setResponseBody(resp);
    }

    @Override
    public void setResponseBody(String body) {
        this.response = this.cleanString(body);
    }

    @Override
    public void setResponseBody(byte[] byteArray) {
        this.responseByteArray = byteArray;
        this.response = this.cleanString(new String(byteArray));
    }

    @Override
    public String getValue(String key) {

        try {
            if (NumberUtils.isDigits(key)) {
                Integer.parseInt(key);
                JSONObject jsonResponse = new JSONObject(this.response);
                return (String) jsonResponse.get(key);
            }
        } catch (Exception e) { }
        try {
            if (this.jsonMap == null) {
                initialize();
            }
            String keyTrans = key.replace("@", "");
            // note that indexing is 1 based not zero based
            JXPathContext context = JXPathContext.newContext(this.jsonMap);
            String output = URLDecoder.decode(String.valueOf(context.getValue(keyTrans)), StandardCharsets.UTF_8);
            if (output.equalsIgnoreCase("null"))
                return "";
            return output;
        } catch (Exception ex) {
            return "";
        }
    }

    private String cleanString(String input) {
        try {
            String output = StringUtils.remove(input.trim(), "@");
            return StringUtils.remove(output,"(\r\n)+");
        } catch (Exception ex) {
            return input;
        }
    }

    private void initialize() {
        try {
            if (!StringUtils.isEmpty(this.response)) {
                this.jsonMap = new ObjectMapper().readValue(this.response, HashMap.class);
            } else {
                this.jsonMap = new HashMap();
            }
        } catch (IOException ex) {
            logger.warn("Unable to parse the response string as a JSON object: " + this.response, ex);
        }
    }

}
