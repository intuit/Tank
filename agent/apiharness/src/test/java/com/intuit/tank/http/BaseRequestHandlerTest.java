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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.ConnectMethod;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.http.BaseRequestHandler;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.binary.BinaryResponse;

/**
 * The class <code>BaseRequestHandlerTest</code> contains tests for the class <code>{@link BaseRequestHandler}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class BaseRequestHandlerTest {
    /**
     * Run the BaseRequestHandler() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBaseRequestHandler_1()
        throws Exception {
        BaseRequestHandler result = new BaseRequestHandler();
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

        URL result = BaseRequestHandler.buildUrl(protocol, host, port, path, urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.Logger.getLogger(Logger.java:117)
        //       at com.intuit.tank.http.BaseRequestHandler.<clinit>(BaseRequestHandler.java:31)
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

        URL result = BaseRequestHandler.buildUrl(protocol, host, port, path, urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        URL result = BaseRequestHandler.buildUrl(protocol, host, port, path, urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        URL result = BaseRequestHandler.buildUrl(protocol, host, port, path, urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        String result = BaseRequestHandler.getQueryString(urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        String result = BaseRequestHandler.getQueryString(urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        String result = BaseRequestHandler.getQueryString(urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        String result = BaseRequestHandler.getQueryString(urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        String result = BaseRequestHandler.getQueryString(urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
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

        String result = BaseRequestHandler.getQueryString(urlVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.BaseRequestHandler
        assertNotNull(result);
    }

    /**
     * Run the void handleResponse(PostMethod,BaseResponse,HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testHandleResponse_1()
        throws Exception {
        PostMethod post = new PostMethod();
        BaseResponse response = new BinaryResponse();
        HttpState httpstate = new HttpState();

        BaseRequestHandler.handleResponse(post, response, httpstate);
    }

    /**
     * Run the void handleResponse(PostMethod,BaseResponse,HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testHandleResponse_2()
        throws Exception {
        PostMethod post = new PostMethod();
        BaseResponse response = new BinaryResponse();
        HttpState httpstate = new HttpState();

        BaseRequestHandler.handleResponse(post, response, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: org.apache.commons.httpclient.HttpMethodBase
        //       at java.lang.Class.getDeclaredConstructors0(Native Method)
        //       at java.lang.Class.privateGetDeclaredConstructors(Class.java:2663)
        //       at java.lang.Class.getConstructor0(Class.java:3067)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2170)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.findConstructor(InstanceCreationExpression.java:572)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:452)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:568)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
    }

    /**
     * Run the void handleResponse(PostMethod,BaseResponse,HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testHandleResponse_3()
        throws Exception {
        PostMethod post = new PostMethod();
        BaseResponse response = new BinaryResponse();
        HttpState httpstate = new HttpState();

        BaseRequestHandler.handleResponse(post, response, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: org.apache.commons.httpclient.HttpMethodBase
        //       at java.lang.Class.getDeclaredConstructors0(Native Method)
        //       at java.lang.Class.privateGetDeclaredConstructors(Class.java:2663)
        //       at java.lang.Class.getConstructor0(Class.java:3067)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2170)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.findConstructor(InstanceCreationExpression.java:572)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:452)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:568)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
    }

    /**
     * Run the void handleResponse(PostMethod,BaseResponse,HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testHandleResponse_4()
        throws Exception {
        PostMethod post = new PostMethod();
        BaseResponse response = new BinaryResponse();
        HttpState httpstate = new HttpState();

        BaseRequestHandler.handleResponse(post, response, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: org.apache.commons.httpclient.HttpMethodBase
        //       at java.lang.Class.getDeclaredConstructors0(Native Method)
        //       at java.lang.Class.privateGetDeclaredConstructors(Class.java:2663)
        //       at java.lang.Class.getConstructor0(Class.java:3067)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2170)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.findConstructor(InstanceCreationExpression.java:572)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:452)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:568)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
    }

    /**
     * Run the void handleResponse(PostMethod,BaseResponse,HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testHandleResponse_5()
        throws Exception {
        PostMethod post = new PostMethod();
        BaseResponse response = new BinaryResponse();
        HttpState httpstate = new HttpState();

        BaseRequestHandler.handleResponse(post, response, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: org.apache.commons.httpclient.HttpMethodBase
        //       at java.lang.Class.getDeclaredConstructors0(Native Method)
        //       at java.lang.Class.privateGetDeclaredConstructors(Class.java:2663)
        //       at java.lang.Class.getConstructor0(Class.java:3067)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2170)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.findConstructor(InstanceCreationExpression.java:572)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:452)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:568)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
    }

    /**
     * Run the void handleResponse(PostMethod,BaseResponse,HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testHandleResponse_6()
        throws Exception {
        PostMethod post = new PostMethod();
        BaseResponse response = new BinaryResponse();
        HttpState httpstate = new HttpState();

        BaseRequestHandler.handleResponse(post, response, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: org.apache.commons.httpclient.HttpMethodBase
        //       at java.lang.Class.getDeclaredConstructors0(Native Method)
        //       at java.lang.Class.privateGetDeclaredConstructors(Class.java:2663)
        //       at java.lang.Class.getConstructor0(Class.java:3067)
        //       at java.lang.Class.getDeclaredConstructor(Class.java:2170)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.findConstructor(InstanceCreationExpression.java:572)
        //       at com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:452)
        //       at com.instantiations.eclipse.analysis.expression.model.MethodInvocationExpression.execute(MethodInvocationExpression.java:568)
        //       at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        //       at com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        //       at java.lang.Thread.run(Thread.java:745)
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_1()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {new Header()};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.log4j.LogManager
        //       at org.apache.log4j.Logger.getLogger(Logger.java:117)
        //       at com.intuit.tank.http.BaseResponse.<clinit>(BaseResponse.java:18)
    }

  

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_3()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = null;
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpState
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_4()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

  

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_6()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {new Header()};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_7()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = null;
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpState
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_8()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

  

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_10()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {new Header()};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_11()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = null;
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpState
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_12()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

   

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_14()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {new Header()};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_15()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = null;
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.HttpState
    }

    /**
     * Run the void processResponse(byte[],long,long,BaseResponse,String,int,Header[],HttpState) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testProcessResponse_16()
        throws Exception {
        byte[] bResponse = new byte[] {};
        long startTime = 1L;
        long endTime = 1L;
        BaseResponse response = new BinaryResponse();
        String message = "";
        int httpCode = 1;
        Header[] headers = new Header[] {};
        HttpState httpstate = new HttpState();

        BaseRequestHandler.processResponse(bResponse, startTime, endTime, response, message, httpCode, headers, httpstate);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.binary.BinaryResponse
    }

    /**
     * Run the void setHeaders(HttpMethod,HashMap<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetHeaders_1()
        throws Exception {
        HttpMethod method = new ConnectMethod();
        HashMap<String, String> headerInformation = new HashMap();

        BaseRequestHandler.setHeaders(method, headerInformation);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
    }

    /**
     * Run the void setHeaders(HttpMethod,HashMap<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testSetHeaders_2()
        throws Exception {
        HttpMethod method = new ConnectMethod();
        HashMap<String, String> headerInformation = new HashMap();

        BaseRequestHandler.setHeaders(method, headerInformation);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class org.apache.commons.httpclient.ConnectMethod
    }
}