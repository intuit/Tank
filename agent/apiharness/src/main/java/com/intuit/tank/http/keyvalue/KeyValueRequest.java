/**
 * 
 */
package com.intuit.tank.http.keyvalue;

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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.http.BaseRequest;

/**
 * @author ahernandez1
 * 
 */
public class KeyValueRequest extends BaseRequest {

    protected HashMap<String, String> postData = null;
    static protected Logger logger = Logger.getLogger(KeyValueRequest.class);

    public KeyValueRequest(HttpClient client) {
        super(client);
        this.postData = new HashMap<String, String>();
        this.headerInformation = new HashMap<String, String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.common.http.BaseRequest#setkey(java.lang.String, java.lang.String)
     */
    @Override
    public void setKey(String key, String value) {
        this.postData.put(key, value);
        this.body = this.getBody();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.common.http.BaseRequest#getBody()
     */
    @SuppressWarnings("rawtypes")
    public String getBody() {
        Set set = this.postData.entrySet();
        Iterator iter = set.iterator();
        String body = "";

        while (iter.hasNext()) {
            try {
                Map.Entry mapEntry = (Map.Entry) iter.next();
                String key = mapEntry.getKey() != null ? URLEncoder.encode((String) mapEntry.getKey(), "UTF-8") : "";
                String value = mapEntry.getValue() != null ? URLEncoder.encode((String) mapEntry.getValue(), "UTF-8")
                        : "";

                if (StringUtils.isEmpty(body)) {
                    body = key + "=" + value;
                } else {
                    body = body + "&" + key + "=" + value;
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return body;
    }

    @Override
    public String getKey(String key) {
        return this.postData.get(key);
    }

    public String removeKey(String key) {
        return this.postData.remove(key);
    }

    public void setNamespace(String name, String value) {
    }

}
