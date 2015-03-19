package com.intuit.tank.http.binary;

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

import com.intuit.tank.http.BaseRequest;

public class BinaryRequest extends BaseRequest {

    private String body;

    public BinaryRequest(HttpClient client) {
        super(client);
        body = "";
        binary = true;
    }

    @Override
    public String getKey(String key) {
        return body;
    }

    @Override
    public void setKey(String key, String value) {
        body = body + value;
    }

    @Override
    public void setNamespace(String name, String value) {

    }

    public void setConvertData() {
        byte[] bytes = body.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
    }

}
