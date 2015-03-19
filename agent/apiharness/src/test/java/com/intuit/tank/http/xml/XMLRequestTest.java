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
import org.junit.*;

import com.intuit.tank.http.xml.GenericXMLHandler;
import com.intuit.tank.http.xml.XMLRequest;

import static org.junit.Assert.*;

/**
 * The class <code>XMLRequestTest</code> contains tests for the class <code>{@link XMLRequest}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 4:29 PM
 */
public class XMLRequestTest {
    /**
     * Run the XMLRequest(HttpClient) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testXMLRequest_1()
            throws Exception {
        HttpClient client = new HttpClient();

        XMLRequest result = new XMLRequest(client);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        assertNotNull(result);
    }

    /**
     * Run the XMLRequest(HttpClient,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testXMLRequest_2()
            throws Exception {
        HttpClient client = new HttpClient();
        String xml = "";

        XMLRequest result = new XMLRequest(client, xml);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

    /**
     * Run the String getKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetKey_1()
            throws Exception {
        XMLRequest fixture = new XMLRequest(new HttpClient());
        fixture.handler = new GenericXMLHandler();
        String key = "";

        String result = fixture.getKey(key);
        assertNotNull(result);
    }

    /**
     * Run the void setNamespace(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSetNamespace_1()
            throws Exception {
        XMLRequest fixture = new XMLRequest(new HttpClient());
        fixture.handler = new GenericXMLHandler();
        String name = "";
        String value = "";

        fixture.setNamespace(name, value);
    }
}