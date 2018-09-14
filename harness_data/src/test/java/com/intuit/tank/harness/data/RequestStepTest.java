package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
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

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.HDResponse;
import com.intuit.tank.harness.data.RequestStep;

/**
 * The class <code>RequestStepTest</code> contains tests for the class <code>{@link RequestStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class RequestStepTest {
    /**
     * Run the String getComments() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetComments_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the String getInfo() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetInfo_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals(null, result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getOnFail() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetOnFail_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getOnFail();

        assertEquals("", result);
    }

    /**
     * Run the HDRequest getRequest() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetRequest_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        HDRequest result = fixture.getRequest();

        assertNotNull(result);
        assertEquals(null, result.getQueryString());
        assertEquals(null, result.getMethod());
        assertEquals(null, result.getPath());
        assertEquals(null, result.getProtocol());
        assertEquals(null, result.getHost());
        assertEquals(null, result.getPort());
        assertEquals(null, result.getLabel());
        assertEquals(null, result.getPayload());
        assertEquals(null, result.getRequestHeaders());
        assertEquals(null, result.getLoggingKey());
        assertEquals(null, result.getPostDatas());
        assertEquals(null, result.getReqFormat());
    }

    /**
     * Run the HDResponse getResponse() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetResponse_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        HDResponse result = fixture.getResponse();

        assertNotNull(result);
        assertEquals(null, result.getRespFormat());
    }

    /**
     * Run the String getScriptGroupName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetScriptGroupName_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getScriptGroupName();

        assertEquals("", result);
    }

    /**
     * Run the void setComments(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetComments_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setOnFail(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetOnFail_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String onFail = "";

        fixture.setOnFail(onFail);

    }

    /**
     * Run the void setRequest(HDRequest) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetRequest_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        HDRequest request = new HDRequest();

        fixture.setRequest(request);

    }

    /**
     * Run the void setResponse(HDResponse) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetResponse_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        HDResponse response = new HDResponse();

        fixture.setResponse(response);

    }

    /**
     * Run the void setScriptGroupName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetScriptGroupName_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String scriptGroupName = "";

        fixture.setScriptGroupName(scriptGroupName);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        RequestStep fixture = new RequestStep();
        fixture.setName("");
        fixture.setComments("");
        fixture.setOnFail("");
        fixture.setResponse(new HDResponse());
        fixture.setRequest(new HDRequest());
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("RequestStep : ", result);
    }
}