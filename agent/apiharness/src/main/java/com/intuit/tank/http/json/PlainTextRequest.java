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

public class PlainTextRequest extends BaseRequest {

    private static final String CONTENT_TYPE = "text/plain";

    public PlainTextRequest(HttpClient client) {
        super(client);
        setContentType(CONTENT_TYPE);
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public String getBody() {
        String result = body;

        return result != null ? result : "";
    }

    @Override
    public void setKey(String key, String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getKey(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNamespace(String name, String value) {
        // TODO Auto-generated method stub

    }

}
