package com.intuit.tank.http;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.*;

/**
 * The class <code>BaseRequestHandlerTest</code> contains tests for the class <code>{@link TankHttpUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class TankHttpUtilTest {
    /**
     * Run the BaseRequestHandler() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBaseRequestHandler_1()
        throws Exception {
        TankHttpUtil result = new TankHttpUtil();
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_1()
        throws Exception {
        String protocol = "";
        String host = "";
        int port = 8080;
        String path = "";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_2()
        throws Exception {
        String protocol = "http";
        String host = "www.testdomain.com";
        int port = 80;
        String path = "/index.html";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_3()
        throws Exception {
        String protocol = "https";
        String host = "www.testdomain.com";
        int port = 443;
        String path = "/index.html";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_4()
        throws Exception {
        String protocol = "http";
        String host = "www.testdomain.com";
        int port = 8080;
        String path = "/index.html";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_1()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_2()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_3()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_4()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_5()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_6()
        throws Exception {
        Map<String, String> urlVariables = null;

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

   


    
}