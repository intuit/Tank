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

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.intuit.tank.http.json.JsonResponse;

public class JsonResponseTest {

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

    }

    /**
     * Run the JsonResponse() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testJsonResponse_1()
            throws Exception {

        JsonResponse result = new JsonResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.Logger.getLogger(Logger.java:117)
        // at com.intuit.tank.http.BaseResponse.<clinit>(BaseResponse.java:18)
        assertNotNull(result);
    }

    /**
     * Run the JsonResponse(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testJsonResponse_2()
            throws Exception {
        String resp = "";

        JsonResponse result = new JsonResponse(resp);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "0";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_2()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "0";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_3()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "0";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_4()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_5()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String key = "";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
        assertNotNull(result);
    }

    /**
     * Run the void setResponseBody(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetResponseBody_1()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        String body = "";

        fixture.setResponseBody(body);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
    }

    /**
     * Run the void setResponseBody(byte[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetResponseBody_2()
            throws Exception {
        JsonResponse fixture = new JsonResponse();
        fixture.setResponseBody(new byte[] {});
        byte[] byteArray = new byte[] {};

        fixture.setResponseBody(byteArray);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.json.JsonResponse
    }
}
