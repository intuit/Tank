package com.intuit.tank.http.xml;

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

public class XMLRequest extends BaseRequest {

    private static final String CONTENT_TYPE = "application/xml";
    protected GenericXMLHandler handler = null;

    /**
     * Initializes the XML to an empty string
     */
    public XMLRequest(HttpClient client) {
        super(client);
        this.handler = new GenericXMLHandler();
        setContentType(CONTENT_TYPE);
    }

    /**
     * Constructor which will initialize its XML handler to the XML passed in
     * 
     * @param xml
     *            - The XML to initialize the response to.
     */
    public XMLRequest(HttpClient client, String xml) {
        super(client);
        this.handler = new GenericXMLHandler(xml);
        setContentType(CONTENT_TYPE);
    }

    // @Override
    // public String getBody() {
    // return this.handler.toString();
    // }

    /**
     * Finds the node for the XPath expression and sets it to the value
     * 
     * @param key
     *            - The XPath expression to change
     * @param value
     *            - The value to change it to
     */
    public void setKey(String key, String value) {
        // TODO Auto-generated method stub
        handler.SetElementText(key, value);
        this.body = this.handler.toString();
    }

    /**
     * This is used for testing if setkey worked
     * 
     * @param key
     * @return The value associated with the key
     */
    public String getKey(String key) {
        return handler.GetElementText(key);
    }

    public void setNamespace(String name, String value) {
        this.handler.setNamespace(name, value);
    }

}
