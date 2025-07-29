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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

        import static org.junit.jupiter.api.Assertions.*;

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
        fixture.cookies = new HashMap<>();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap<>();
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
        fixture.cookies = new HashMap<>();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap<>();
        fixture.responseLogMsg = "";
        String key = "";

        String result = fixture.getCookie(key);
        assertNull(result);
    }
    @Test
    public void testGetCookie_2() {
        MockResponse fixture = new MockResponse();
        String key = "testKey";
        fixture.setCookie(key, "testValue");
        String result = fixture.getCookie(key);
        assertEquals("testValue", result);
    }
    @Test
    public void testGetCookie_3() {
        MockResponse fixture = new MockResponse();
        Map<String, String> expected = new HashMap<>();
        expected.put("testKey", "testValue");
        fixture.cookies.put("testKey", "testValue");
        Map<String, String> result = fixture.getCookies();
        assertEquals(expected, result);
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
    @Test
    public void testGetHeaders_2() {
        MockResponse fixture = new MockResponse();
        fixture.setHeader("testKey", "testValue");

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

    @Test
    public void testGetHttpCode_2() {
        MockResponse fixture = new MockResponse();
        fixture.setHttpCode(4);

        int result = fixture.getHttpCode();
        assertEquals(4, result);
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

    @Test
    public void testGetHttpMsg_2()
            throws Exception {
        MockResponse fixture = new MockResponse();
        fixture.setHttpMessage("testMsg");

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

    @Test
    public void testGetResponseBody_2() {
        MockResponse fixture = new MockResponse();
        fixture.setResponseBody("testResponse");

        String result = fixture.getResponseBody();
        assertNotNull(result);
    }

    @Test
    public void testGetResponseBody_3() {
        MockResponse fixture = new MockResponse();
        byte[] responseByteArray = new byte[] {'T', 'E', 'S', 'T'};
        fixture.setResponseBody(responseByteArray);

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

    @Test
    public void testGetResponseTime_2() {
        MockResponse fixture = new MockResponse();
        fixture.setResponseTime(15L);

        long result = fixture.getResponseTime();
        assertEquals(15L, result);
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
        assertFalse(result);
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

        assertFalse(result);
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
        assertFalse(result);
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
        BaseResponse fixture = new MockResponse();
        fixture.responseTime = 1L;
        fixture.proxyResponseTime = 1L;
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.rspMessage = "";
        fixture.responseLogMsg = "";
        assertEquals("RESPONSE HTTP CODE: 1\n" +
                "RESPONSE HTTP MSG: \n" +
                "RESPONSE TIME: 1\n" +
                "PROXY RESPONSE TIME: 1\n" +
                "RESPONSE SIZE: 0\n" +
                "RESPONSE BODY: not logged because null is not a content-type.\n", fixture.getLogMsg());
    }

    @Test
    public void testLogResponse_2() {
        BaseResponse fixture = new MockResponse();
        fixture.headers = new HashMap<String, String>();
        fixture.headers.put("content-type", "text/html");
        fixture.cookies = new HashMap<String, String>();
        fixture.cookies.put("testHeadersKey", "testHeadersValue");
        fixture.response = "testResponse";
        assertEquals("RESPONSE HTTP CODE: -1\n" +
                "RESPONSE HTTP MSG: \n" +
                "RESPONSE TIME: -1\n" +
                "PROXY RESPONSE TIME: -1\n" +
                "RESPONSE SIZE: 12\n" +
                "RESPONSE HEADER: content-type = text/html\n" +
                "RESPONSE COOKIE: testHeadersKey = testHeadersValue\n" +
                "RESPONSE BODY: testResponse\n", fixture.getLogMsg());
    }

}
