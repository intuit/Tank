package com.intuit.tank.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * The class <code>BaseResponseTest</code> contains tests for the class <code>{@link BaseResponse}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class BaseResponseTest {
    /**
     * Run the String getBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetBody_1()
            throws Exception {
        BaseResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.cookies = new HashMap<String, String>();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap<String, String>();
        fixture.responseLogMsg = "";

        String result = fixture.getBody();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.TestResponse
        assertNotNull(result);
    }

    /**
     * Run the String getCookie(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetCookie_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.cookies = new HashMap<String, String>();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap<String, String>();
        fixture.responseLogMsg = "";
        String key = "";

        String result = fixture.getCookie(key);
    }

   

    /**
     * Run the Map<String, String> getHeaders() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHeaders_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap<String, String>();
        fixture.responseLogMsg = "";

        Map<String, String> result = fixture.getHeaders();
        assertNotNull(result);
    }

    /**
     * Run the int getHttpCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpCode_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        int result = fixture.getHttpCode();
        assertEquals(1, result);
    }

    /**
     * Run the String getHttpHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpHeader_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";
        String header = "test";
        fixture.headers.put("test", "test");
        String result = fixture.getHttpHeader(header);
        assertNotNull(result);
    }

    /**
     * Run the String getHttpMsg() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpMsg_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        String result = fixture.getHttpMsg();
        assertNotNull(result);
    }

    /**
     * Run the String getLogMsg() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetLogMsg_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        String result = fixture.getLogMsg();
        assertNotNull(result);
    }

    /**
     * Run the String getResponseBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetResponseBody_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        String result = fixture.getResponseBody();
        assertNotNull(result);
    }

    /**
     * Run the byte[] getResponseBytes() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetResponseBytes_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        byte[] result = fixture.getResponseBytes();
        assertNotNull(result);
    }

    /**
     * Run the int getResponseSize() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetResponseSize_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = null;
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        int result = fixture.getResponseSize();
        assertEquals(0, result);
    }

    /**
     * Run the int getResponseSize() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetResponseSize_2()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        int result = fixture.getResponseSize();
        assertEquals(0, result);
    }

    /**
     * Run the int getResponseSize() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetResponseSize_3()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        int result = fixture.getResponseSize();
        assertEquals(0, result);
    }

    /**
     * Run the long getResponseTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetResponseTime_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        long result = fixture.getResponseTime();
        assertEquals(1L, result);
    }

    /**
     * Run the boolean isDataType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testIsDataType_1()
            throws Exception {
        String contentType = "text/html";

        boolean result = BaseResponse.isDataType(contentType);
        assertTrue(result);
    }

    /**
     * Run the boolean isDataType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testIsDataType_2()
            throws Exception {
        String contentType = "application/pdf";

        boolean result = BaseResponse.isDataType(contentType);
        assertTrue(!result);
    }

    /**
     * Run the boolean isDataType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testIsDataType_3()
            throws Exception {
        String contentType = "text/xml";

        boolean result = BaseResponse.isDataType(contentType);
        assertTrue(result);
    }

    /**
     * Run the boolean isDataType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testIsDataType_4()
            throws Exception {
        String contentType = "image/png";

        boolean result = BaseResponse.isDataType(contentType);

        assertTrue(!result);
    }

    /**
     * Run the boolean isDataType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testIsDataType_5()
            throws Exception {
        String contentType = "";

        boolean result = BaseResponse.isDataType(contentType);
        assertTrue(!result);
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_1()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        fixture.logResponse();
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_2()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = null;
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        fixture.logResponse();
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_3()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        fixture.logResponse();
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_4()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        fixture.logResponse();
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_5()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        fixture.logResponse();
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_6()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        fixture.logResponse();
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_7()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";

        fixture.logResponse();
    }


}