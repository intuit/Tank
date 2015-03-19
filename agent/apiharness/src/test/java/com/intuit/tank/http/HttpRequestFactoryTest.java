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
import org.junit.*;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.HttpRequestFactory;
import com.intuit.tank.script.ScriptConstants;

import static org.junit.Assert.*;

/**
 * The class <code>HttpRequestFactoryTest</code> contains tests for the class <code>{@link HttpRequestFactory}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class HttpRequestFactoryTest {
    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_1()
            throws Exception {
        String format = ScriptConstants.XML_TYPE;
        HttpClient httpclient = new HttpClient();

        BaseRequest result = HttpRequestFactory.getHttpRequest(format, httpclient);
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_2()
            throws Exception {
        String format = ScriptConstants.JSON_TYPE;
        HttpClient httpclient = new HttpClient();

        BaseRequest result = HttpRequestFactory.getHttpRequest(format, httpclient);
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_3()
            throws Exception {
        String format = ScriptConstants.PLAIN_TEXT_TYPE;
        HttpClient httpclient = new HttpClient();

        BaseRequest result = HttpRequestFactory.getHttpRequest(format, httpclient);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_4()
            throws Exception {
        String format = ScriptConstants.MULTI_PART_TYPE;
        HttpClient httpclient = new HttpClient();

        BaseRequest result = HttpRequestFactory.getHttpRequest(format, httpclient);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getHttpRequest(String,HttpClient) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpRequest_5()
            throws Exception {
        String format = ScriptConstants.NVP_TYPE;
        HttpClient httpclient = new HttpClient();

        BaseRequest result = HttpRequestFactory.getHttpRequest(format, httpclient);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpClient
        assertNotNull(result);
    }

}