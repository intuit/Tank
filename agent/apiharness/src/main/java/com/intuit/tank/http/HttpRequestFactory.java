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

import org.apache.commons.httpclient.HttpClient;

import com.intuit.tank.http.json.JsonRequest;
import com.intuit.tank.http.json.PlainTextRequest;
import com.intuit.tank.http.keyvalue.KeyValueRequest;
import com.intuit.tank.http.multipart.MultiPartRequest;
import com.intuit.tank.http.xml.XMLRequest;
import com.intuit.tank.script.ScriptConstants;

public class HttpRequestFactory {

    public static BaseRequest getHttpRequest(String format, HttpClient httpclient)
            throws IllegalArgumentException {
        if (format.equalsIgnoreCase(ScriptConstants.XML_TYPE)) {
            return new XMLRequest(httpclient);
        } else if (format.equalsIgnoreCase(ScriptConstants.NVP_TYPE)) {
            return new KeyValueRequest(httpclient);
        } else if (format.equalsIgnoreCase(ScriptConstants.MULTI_PART_TYPE)) {
            return new MultiPartRequest(httpclient);
        } else if (format.equalsIgnoreCase(ScriptConstants.JSON_TYPE)) {
            return new JsonRequest(httpclient);
        } else if (format.equalsIgnoreCase(ScriptConstants.PLAIN_TEXT_TYPE)) {
            return new PlainTextRequest(httpclient);
        } else {
            throw new IllegalArgumentException("unknow request format - " + format);
        }
    }
}
