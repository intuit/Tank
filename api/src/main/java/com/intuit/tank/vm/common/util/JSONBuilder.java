/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common.util;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSONBuilder
 * 
 * @author dangleton
 * 
 */
public class JSONBuilder {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JSONBuilder.class);

    JSONObject json = new JSONObject();
    Map<String, String> valueMap = new HashMap<String, String>();

    public static void main(String[] args) {
        JSONBuilder builder = new JSONBuilder();
        builder.add("/1/2/[0]/TransactionId", "myTransactionId");
        builder.add("/1/2/[0]/Name", "myName");
        String jsonString = builder.toJsonString(2);
        System.out.println(jsonString);
    }

    public String getValue(String key) {
        return valueMap.get(key);
    }

    public void add(String keySequence, String value) {
        valueMap.put(keySequence, value);
        // parse the keys
        try {
            String[] keys = keySequence.split("/");
            JSONObject currentObject = json;
            JSONArray currentArray = null;
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                if (key.startsWith("[")) {
                    try {
                        currentObject = currentArray.getJSONObject(parseIndex(key));
                    } catch (JSONException e) {
                        currentObject = new JSONObject();
                        currentArray.put(parseIndex(key), currentObject);
                    }
                    continue;
                }
                JSONObject obj = null;
                try {
                    obj = currentObject.getJSONObject(keys[i]);
                } catch (JSONException e) {
                    // thrown if key is not found
                }
                if (obj == null) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = currentObject.getJSONArray(key);
                    } catch (JSONException e) {
                        // thrown if key is not found
                    }
                    if (jsonArray == null && keys.length > i + 1 && keys[i + 1].startsWith("[")) {
                        jsonArray = new JSONArray();
                        currentObject.put(key, jsonArray);
                        currentArray = jsonArray;
                        currentObject = new JSONObject();
                        currentArray.put(parseIndex(keys[i + 1]), currentObject);
                        continue;
                    } else if (jsonArray != null) {
                        currentArray = jsonArray;
                        continue;
                    }
                }
                if (obj != null) {
                    currentObject = obj;
                } else {
                    // create the obj and add to json
                    if (i < keys.length - 1) {
                        JSONObject newObj = new JSONObject();
                        currentObject.put(key, newObj);
                        currentObject = newObj;
                    } else {
                        currentObject.put(key, findValueOfType(value));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Object findValueOfType(String value) {
        if (StringUtils.isEmpty(value) || "null".equalsIgnoreCase(value)) {
            return JSONObject.NULL;
        }
        try {
            if (!value.startsWith("0")) {
                if (value.matches("^\\d*$")) {
                    return Long.valueOf(value);
                }
                if (value.matches("^\\d*\\.\\d+")) {
                    return Double.valueOf(value);
                }

                if (value.equalsIgnoreCase("true")) {
                    return Boolean.TRUE;
                }
                if (value.equalsIgnoreCase("false")) {
                    return Boolean.FALSE;
                }
            }
        } catch (NumberFormatException e) {
            LOG.warn("Error trying to parse a number value: " + e);
        }
        // strip leading and trainling quotes
        value = StringUtils.stripEnd(value, "\"");
        value = StringUtils.stripStart(value, "\"");
        return value;
    }

    private int parseIndex(String key) {
        return Integer.valueOf(key.substring(1, key.length() - 1));
    }

    /**
     * @return the json
     */
    public JSONObject getJson() {
        return json;
    }

    /**
     * @{inheritDoc
     */
    public String toJsonString(int indent) {
        try {
            return json.toString(indent);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return toJsonString();
    }

    /**
     * @return
     */
    public String toJsonString() {
        return json.toString();
    }
}
