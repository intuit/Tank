package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.Request;
import com.intuit.tank.project.RequestData;

/**
 * The class <code>RequestTest</code> contains tests for the class <code>{@link Request}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class RequestTest {
    /**
     * Run the Request() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testRequest_1()
        throws Exception {
        Request result = new Request();
        assertNotNull(result);
    }

    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the Set<RequestData> getData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetData_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getData();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getHostname() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetHostname_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getHostname();

        assertEquals("", result);
    }

    /**
     * Run the String getLabel() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetLabel_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getLabel();

        assertEquals("", result);
    }

    /**
     * Run the String getLoggingKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetLoggingKey_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getLoggingKey();

        assertEquals("", result);
    }

    /**
     * Run the String getMethod() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetMethod_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getMethod();

        assertEquals("", result);
    }

    /**
     * Run the String getMimetype() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetMimetype_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getMimetype();

        assertEquals("", result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getOnFail() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetOnFail_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail((String) null);
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getOnFail();

        assertEquals("abort", result);
    }

    /**
     * Run the String getOnFail() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetOnFail_2()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getOnFail();

        assertEquals("", result);
    }

    /**
     * Run the String getPayload() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPayload_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getPayload();

        assertEquals("", result);
    }

    /**
     * Run the Set<RequestData> getPostDatas() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPostDatas_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getPostDatas();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getProtocol() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProtocol_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getProtocol();

        assertEquals("", result);
    }

    /**
     * Run the Set<RequestData> getQueryStrings() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetQueryStrings_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getQueryStrings();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getReqFormat() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetReqFormat_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getReqFormat();

        assertEquals("", result);
    }

    /**
     * Run the Set<RequestData> getRequestCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRequestCookies_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getRequestCookies();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Set<RequestData> getRequestheaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRequestheaders_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getRequestheaders();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getRespFormat() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRespFormat_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getRespFormat();

        assertEquals("", result);
    }

    /**
     * Run the String getResponse() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetResponse_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getResponse();

        assertEquals("", result);
    }

    /**
     * Run the Set<RequestData> getResponseCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetResponseCookies_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getResponseCookies();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Set<RequestData> getResponseData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetResponseData_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getResponseData();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Set<RequestData> getResponseheaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetResponseheaders_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        Set<RequestData> result = fixture.getResponseheaders();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getResult() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetResult_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getResult();

        assertEquals("", result);
    }

    /**
     * Run the String getSimplePath() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSimplePath_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getSimplePath();

        assertEquals("", result);
    }

    /**
     * Run the int getStepIndex() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetStepIndex_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        int result = fixture.getStepIndex();

        assertEquals(1, result);
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getType();

        assertEquals("", result);
    }

    /**
     * Run the String getUrl() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetUrl_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getUrl();

        assertEquals("", result);
    }

    /**
     * Run the String getUuid() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetUuid_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        String result = fixture.getUuid();

        assertEquals("", result);
    }

    /**
     * Run the boolean isHasAssignments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHasAssignments_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        boolean result = fixture.isHasAssignments();

        assertEquals(false, result);
    }

    /**
     * Run the boolean isHasAssignments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHasAssignments_2()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        boolean result = fixture.isHasAssignments();

        assertEquals(false, result);
    }

    /**
     * Run the boolean isHasAssignments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHasAssignments_3()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        boolean result = fixture.isHasAssignments();

        assertEquals(false, result);
    }

    /**
     * Run the boolean isHasValidation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHasValidation_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        boolean result = fixture.isHasValidation();

        assertEquals(false, result);
    }

    /**
     * Run the boolean isHasValidation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHasValidation_2()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        boolean result = fixture.isHasValidation();

        assertEquals(false, result);
    }

    /**
     * Run the boolean isHasValidation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHasValidation_3()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");

        boolean result = fixture.isHasValidation();

        assertEquals(false, result);
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setData(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetData_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> data = new HashSet();

        fixture.setData(data);

    }

    /**
     * Run the void setHostname(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetHostname_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String hostname = "";

        fixture.setHostname(hostname);

    }

    /**
     * Run the void setLabel(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetLabel_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String label = "";

        fixture.setLabel(label);

    }

    /**
     * Run the void setLoggingKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetLoggingKey_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String loggingKey = "";

        fixture.setLoggingKey(loggingKey);

    }

    /**
     * Run the void setMethod(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetMethod_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String method = "";

        fixture.setMethod(method);

    }

    /**
     * Run the void setMimetype(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetMimetype_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String mimetype = "";

        fixture.setMimetype(mimetype);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setOnFail(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetOnFail_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String onFail = "";

        fixture.setOnFail(onFail);

    }

    /**
     * Run the void setPayload(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPayload_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String payload = "";

        fixture.setPayload(payload);

    }

    /**
     * Run the void setPostDatas(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPostDatas_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> postDatas = new HashSet();

        fixture.setPostDatas(postDatas);

    }

    /**
     * Run the void setProtocol(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetProtocol_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String protocol = "";

        fixture.setProtocol(protocol);

    }

    /**
     * Run the void setQueryStrings(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetQueryStrings_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> queryStrings = new HashSet();

        fixture.setQueryStrings(queryStrings);

    }

    /**
     * Run the void setReqFormat(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetReqFormat_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String reqFormat = "";

        fixture.setReqFormat(reqFormat);

    }

    /**
     * Run the void setRequestCookies(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRequestCookies_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> cookies = new HashSet();

        fixture.setRequestCookies(cookies);

    }

    /**
     * Run the void setRequestheaders(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRequestheaders_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> requestheaders = new HashSet();

        fixture.setRequestheaders(requestheaders);

    }

    /**
     * Run the void setRespFormat(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRespFormat_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String respFormat = "";

        fixture.setRespFormat(respFormat);

    }

    /**
     * Run the void setResponse(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetResponse_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String response = "";

        fixture.setResponse(response);

    }

    /**
     * Run the void setResponseCookies(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetResponseCookies_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> setCookies = new HashSet();

        fixture.setResponseCookies(setCookies);

    }

    /**
     * Run the void setResponseData(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetResponseData_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> responseData = new HashSet();

        fixture.setResponseData(responseData);

    }

    /**
     * Run the void setResponseheaders(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetResponseheaders_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        Set<RequestData> responseheaders = new HashSet();

        fixture.setResponseheaders(responseheaders);

    }

    /**
     * Run the void setResult(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetResult_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String result = "";

        fixture.setResult(result);

    }

    /**
     * Run the void setSimplePath(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSimplePath_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String simplePath = "";

        fixture.setSimplePath(simplePath);

    }

    /**
     * Run the void setStepIndex(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetStepIndex_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        int stepIndex = 1;

        fixture.setStepIndex(stepIndex);

    }

    /**
     * Run the void setType(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetType_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String type = "";

        fixture.setType(type);

    }

    /**
     * Run the void setUrl(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetUrl_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String url = "";

        fixture.setUrl(url);

    }

    /**
     * Run the void setUuid(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetUuid_1()
        throws Exception {
        Request fixture = new Request();
        fixture.setQueryStrings(new HashSet());
        fixture.setLoggingKey("");
        fixture.setStepIndex(1);
        fixture.setResponseCookies(new HashSet());
        fixture.setComments("");
        fixture.setType("");
        fixture.setReqFormat("");
        fixture.setResult("");
        fixture.setResponseData(new HashSet());
        fixture.setProtocol("");
        fixture.setSimplePath("");
        fixture.setUuid("");
        fixture.setResponseheaders(new HashSet());
        fixture.setLabel("");
        fixture.setRequestheaders(new HashSet());
        fixture.setData(new HashSet());
        fixture.setMimetype("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setHostname("");
        fixture.setRespFormat("");
        fixture.setUrl("");
        fixture.setMethod("");
        fixture.setPostDatas(new HashSet());
        fixture.setPayload("");
        String uuid = "";

        fixture.setUuid(uuid);

    }
}