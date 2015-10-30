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

import com.intuit.tank.http.BaseResponse;

public class XMLResponse extends BaseResponse {

    private GenericXMLHandler handler = null;

    /**
     * Initializes the XML to an empty string, empty header.
     */
    public XMLResponse()
    {
        this("");
    }

    /**
     * Constructor which will initialize its handler to the XML passed in
     * 
     * @param xml
     *            - The XML to initialize the response to.
     */
    public XMLResponse(String body)
    {
        response = body;
    }

    /**
     * Returns a String representing all XML included in this response, including the standard XML header <?xml
     * version="1.0" encoding="UTF-8"?>
     */
    public String getResponseBody()
    {
        if (null == handler) {
            handler = new GenericXMLHandler(response);
        }
        return handler.toString();
    }

    /**
     * Gets the value in this response with the associated XPath expression
     * 
     * @param key
     *            - The XPath expression to look up
     * @return A string which is the value in the XML
     */
    public String getValue(String key)
    {
        if (null == handler) {
            handler = new GenericXMLHandler(response);
        }
        return handler.GetElementText(key);
    }

}
