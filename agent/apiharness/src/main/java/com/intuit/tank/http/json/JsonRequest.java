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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.vm.common.util.JSONBuilder;

public class JsonRequest extends BaseRequest {

    private static Logger logger = Logger.getLogger(JsonRequest.class);
    private static final String CONTENT_TYPE = "application/json";

    private JSONBuilder builder;

    public JsonRequest(HttpClient client) {
        super(client);
        builder = new JSONBuilder();
        setContentType(CONTENT_TYPE);
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public String getBody() {
        String result = body;
        if (StringUtils.isBlank(result)) {
            try {
                result = builder.toJsonString();
                return result;
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        return result != null ? result : "";
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public void setKey(String key, String value) {
        builder.add(key, value);
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public void setNamespace(String name, String value) {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getKey(String key) {
        return builder.getValue(key);
    }

}
