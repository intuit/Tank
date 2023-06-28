package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.script.ScriptStepTO;
import com.intuit.tank.api.model.v1.script.StepDataTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ScriptStepTOTest</code> contains tests for the class <code>{@link ScriptStepTO}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class ScriptStepTOTest {
    /**
     * Run the ScriptStepTO() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptStepTO_1()
        throws Exception {

        ScriptStepTO result = new ScriptStepTO();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(null, result.getMethod());
        assertEquals(null, result.getType());
        assertEquals(null, result.getResult());
        assertEquals(null, result.getProtocol());
        assertEquals(null, result.getUrl());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getUuid());
        assertEquals(null, result.getHostname());
        assertEquals(0, result.getStepIndex());
        assertEquals(null, result.getLoggingKey());
        assertEquals(null, result.getRespFormat());
        assertEquals(null, result.getReqFormat());
        assertEquals(null, result.getMimetype());
        assertEquals(null, result.getOnFail());
        assertEquals(null, result.getSimplePath());
        assertEquals(null, result.getScriptGroupName());
        assertEquals(null, result.getPayload());
        assertEquals(null, result.getLabel());
        assertEquals(null, result.getResponse());
        assertEquals(null, result.getComments());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the Date getCreated() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetCreated_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Date result = fixture.getCreated();

        assertNotNull(result);
    }

    /**
     * Run the Set<StepDataTO> getData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetData_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getData();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getHostname() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetHostname_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getHostname();

        assertEquals("", result);
    }

    /**
     * Run the String getLabel() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetLabel_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getLabel();

        assertEquals("", result);
    }

    /**
     * Run the String getLoggingKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetLoggingKey_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getLoggingKey();

        assertEquals("", result);
    }

    /**
     * Run the String getMethod() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetMethod_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getMethod();

        assertEquals("", result);
    }

    /**
     * Run the String getMimetype() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetMimetype_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getMimetype();

        assertEquals("", result);
    }

    /**
     * Run the Date getModified() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Date result = fixture.getModified();

        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getOnFail() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetOnFail_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getOnFail();

        assertEquals("", result);
    }

    /**
     * Run the String getPayload() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetPayload_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getPayload();

        assertEquals("", result);
    }

    /**
     * Run the Set<StepDataTO> getPostDatas() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetPostDatas_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getPostDatas();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getProtocol() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetProtocol_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getProtocol();

        assertEquals("", result);
    }

    /**
     * Run the Set<StepDataTO> getQueryStrings() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetQueryStrings_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getQueryStrings();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getReqFormat() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetReqFormat_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getReqFormat();

        assertEquals("", result);
    }

    /**
     * Run the Set<StepDataTO> getRequestCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetRequestCookies_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getRequestCookies();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Set<StepDataTO> getRequestheaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetRequestheaders_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getRequestheaders();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getRespFormat() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetRespFormat_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getRespFormat();

        assertEquals("", result);
    }

    /**
     * Run the String getResponse() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetResponse_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getResponse();

        assertEquals("", result);
    }

    /**
     * Run the Set<StepDataTO> getResponseCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetResponseCookies_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getResponseCookies();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Set<StepDataTO> getResponseData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetResponseData_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getResponseData();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Set<StepDataTO> getResponseheaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetResponseheaders_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        Set<StepDataTO> result = fixture.getResponseheaders();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getResult() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetResult_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getResult();

        assertEquals("", result);
    }

    /**
     * Run the String getScriptGroupName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetScriptGroupName_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getScriptGroupName();

        assertEquals("", result);
    }

    /**
     * Run the String getSimplePath() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetSimplePath_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getSimplePath();

        assertEquals("", result);
    }

    /**
     * Run the int getStepIndex() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetStepIndex_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        int result = fixture.getStepIndex();

        assertEquals(1, result);
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getType();

        assertEquals("", result);
    }

    /**
     * Run the String getUrl() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetUrl_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getUrl();

        assertEquals("", result);
    }

    /**
     * Run the String getUuid() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetUuid_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());

        String result = fixture.getUuid();

        assertEquals("", result);
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setCreated(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetCreated_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Date created = new Date();

        fixture.setCreated(created);

    }

    /**
     * Run the void setData(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetData_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> data = new HashSet();

        fixture.setData(data);

    }

    /**
     * Run the void setHostname(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetHostname_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String hostname = "";

        fixture.setHostname(hostname);

    }

    /**
     * Run the void setLabel(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetLabel_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String label = "";

        fixture.setLabel(label);

    }

    /**
     * Run the void setLoggingKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetLoggingKey_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String loggingKey = "";

        fixture.setLoggingKey(loggingKey);

    }

    /**
     * Run the void setMethod(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetMethod_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String method = "";

        fixture.setMethod(method);

    }

    /**
     * Run the void setMimetype(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetMimetype_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String mimetype = "";

        fixture.setMimetype(mimetype);

    }

    /**
     * Run the void setModified(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetModified_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Date modified = new Date();

        fixture.setModified(modified);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setOnFail(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetOnFail_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String onFail = "";

        fixture.setOnFail(onFail);

    }

    /**
     * Run the void setPayload(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetPayload_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String payload = "";

        fixture.setPayload(payload);

    }

    /**
     * Run the void setPostDatas(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetPostDatas_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> postDatas = new HashSet();

        fixture.setPostDatas(postDatas);

    }

    /**
     * Run the void setProtocol(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetProtocol_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String protocol = "";

        fixture.setProtocol(protocol);

    }

    /**
     * Run the void setQueryStrings(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetQueryStrings_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> queryStrings = new HashSet();

        fixture.setQueryStrings(queryStrings);

    }

    /**
     * Run the void setReqFormat(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetReqFormat_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String reqFormat = "";

        fixture.setReqFormat(reqFormat);

    }

    /**
     * Run the void setRequestCookies(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetRequestCookies_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> requestCookies = new HashSet();

        fixture.setRequestCookies(requestCookies);

    }

    /**
     * Run the void setRequestheaders(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetRequestheaders_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> requestheaders = new HashSet();

        fixture.setRequestheaders(requestheaders);

    }

    /**
     * Run the void setRespFormat(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetRespFormat_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String respFormat = "";

        fixture.setRespFormat(respFormat);

    }

    /**
     * Run the void setResponse(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetResponse_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String response = "";

        fixture.setResponse(response);

    }

    /**
     * Run the void setResponseCookies(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetResponseCookies_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> responseCookies = new HashSet();

        fixture.setResponseCookies(responseCookies);

    }

    /**
     * Run the void setResponseData(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetResponseData_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> responseData = new HashSet();

        fixture.setResponseData(responseData);

    }

    /**
     * Run the void setResponseheaders(Set<StepDataTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetResponseheaders_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        Set<StepDataTO> responseheaders = new HashSet();

        fixture.setResponseheaders(responseheaders);

    }

    /**
     * Run the void setResult(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetResult_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String result = "";

        fixture.setResult(result);

    }

    /**
     * Run the void setScriptGroupName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetScriptGroupName_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String scriptGroupName = "";

        fixture.setScriptGroupName(scriptGroupName);

    }

    /**
     * Run the void setSimplePath(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetSimplePath_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String simplePath = "";

        fixture.setSimplePath(simplePath);

    }

    /**
     * Run the void setStepIndex(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetStepIndex_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        int stepIndex = 1;

        fixture.setStepIndex(stepIndex);

    }

    /**
     * Run the void setType(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetType_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String type = "";

        fixture.setType(type);

    }

    /**
     * Run the void setUrl(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetUrl_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String url = "";

        fixture.setUrl(url);

    }

    /**
     * Run the void setUuid(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetUuid_1()
        throws Exception {
        ScriptStepTO fixture = new ScriptStepTO();
        fixture.setName("");
        fixture.setProtocol("");
        fixture.setHostname("");
        fixture.setLoggingKey("");
        fixture.setResponseheaders(new HashSet());
        fixture.setRequestheaders(new HashSet());
        fixture.setModified(new Date());
        fixture.setPayload("");
        fixture.setResult("");
        fixture.setResponseCookies(new HashSet());
        fixture.setOnFail("");
        fixture.setRespFormat("");
        fixture.setResponse("");
        fixture.setRequestCookies(new HashSet());
        fixture.setData(new HashSet());
        fixture.setCreated(new Date());
        fixture.setStepIndex(1);
        fixture.setMimetype("");
        fixture.setReqFormat("");
        fixture.setType("");
        fixture.setLabel("");
        fixture.setUuid("");
        fixture.setComments("");
        fixture.setPostDatas(new HashSet());
        fixture.setUrl("");
        fixture.setSimplePath("");
        fixture.setQueryStrings(new HashSet());
        fixture.setScriptGroupName("");
        fixture.setMethod("");
        fixture.setResponseData(new HashSet());
        String uuid = "";

        fixture.setUuid(uuid);

    }
}