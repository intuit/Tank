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

import org.apache.commons.httpclient.Cookie;
import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.binary.BinaryResponse;

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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        String result = fixture.getBody();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] { new Cookie() };
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        String key = "";

        String result = fixture.getCookie(key);
    }

    /**
     * Run the HashMap<String, String> getCookiesByDomain() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetCookiesByDomain_1()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        HashMap<String, String> result = fixture.getCookiesByDomain();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
        assertNotNull(result);
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        Map<String, String> result = fixture.getHeaders();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        int result = fixture.getHttpCode();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        String header = "test";
        fixture.headers.put("test", "test");
        String result = fixture.getHttpHeader(header);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        String result = fixture.getHttpMsg();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        String result = fixture.getLogMsg();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.Logger.getLogger(Logger.java:117)
        // at com.intuit.tank.http.BaseResponse.<clinit>(BaseResponse.java:18)
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        String result = fixture.getResponseBody();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        byte[] result = fixture.getResponseBytes();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = null;
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        int result = fixture.getResponseSize();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        int result = fixture.getResponseSize();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        int result = fixture.getResponseSize();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        long result = fixture.getResponseTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] { new Cookie() };
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = null;
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] { new Cookie() };
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] { new Cookie() };
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] { new Cookie() };
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_8()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] { new Cookie() };
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_9()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_10()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void logResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testLogResponse_11()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = null;
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";

        fixture.logResponse();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void setCookies(Cookie[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetCookies_1()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        Cookie[] cookies = new Cookie[] {};

        fixture.setCookies(cookies);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void setCookies(Cookie[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetCookies_2()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        Cookie[] cookies = new Cookie[] {};

        fixture.setCookies(cookies);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void setHeader(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetHeader_1()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        String key = "";
        String value = "";

        fixture.setHeader(key, value);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void setHttpCode(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetHttpCode_1()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        int code = 1;

        fixture.setHttpCode(code);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void setHttpMessage(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetHttpMessage_1()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        String msg = "";

        fixture.setHttpMessage(msg);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        String body = "";

        fixture.setResponseBody(body);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
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
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        byte[] byteArray = new byte[] {};

        fixture.setResponseBody(byteArray);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void setResponseTime(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetResponseTime_1()
            throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        fixture.responseTime = 1L;
        fixture.cookiesByDomain = new HashMap();
        fixture.httpCode = 1;
        fixture.response = "";
        fixture.responseByteArray = new byte[] {};
        fixture.cookies = new Cookie[] {};
        fixture.rspMessage = "";
        fixture.headers = new HashMap();
        fixture.responseLogMsg = "";
        long time = 1L;

        fixture.setResponseTime(time);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }
}