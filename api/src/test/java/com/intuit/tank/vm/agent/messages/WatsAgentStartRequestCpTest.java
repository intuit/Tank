package com.intuit.tank.vm.agent.messages;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.agent.messages.WatsAgentStartRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>WatsAgentStartRequestCpTest</code> contains tests for the class
 * <code>{@link WatsAgentStartRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class WatsAgentStartRequestCpTest {
    /**
     * Run the WatsAgentStartRequest() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testWatsAgentStartRequest_1()
            throws Exception {

        WatsAgentStartRequest result = new WatsAgentStartRequest();

        assertNotNull(result);
        assertEquals(0L, result.getRampTime());
        assertEquals(1, result.getUserIntervalIncrement());
        assertEquals(0, result.getTotalAgents());
        assertEquals(0, result.getConcurrentUsers());
        assertEquals(null, result.getScriptUrl());
        assertEquals(0, result.getStartUsers());
        assertEquals(0, result.getAgentInstanceNum());
        assertEquals(0L, result.getSimulationTime());
        assertEquals(null, result.getJobId());
    }

    /**
     * Run the WatsAgentStartRequest(WatsAgentStartRequest) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testWatsAgentStartRequest_2()
            throws Exception {
        WatsAgentStartRequest copy = new WatsAgentStartRequest("", 1, 1L);
        copy.setAgentInstanceNum(1);
        copy.setTotalAgents(1);
        copy.setStartUsers(1);
        copy.setUserIntervalIncrement(1);
        copy.setSimulationTime(1L);
        copy.setJobId("");

        WatsAgentStartRequest result = new WatsAgentStartRequest(copy);

        assertNotNull(result);
        assertEquals(1L, result.getRampTime());
        assertEquals(1, result.getUserIntervalIncrement());
        assertEquals(1, result.getTotalAgents());
        assertEquals(1, result.getConcurrentUsers());
        assertEquals("", result.getScriptUrl());
        assertEquals(1, result.getStartUsers());
        assertEquals(1, result.getAgentInstanceNum());
        assertEquals(1L, result.getSimulationTime());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the WatsAgentStartRequest(String,int,long) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testWatsAgentStartRequest_3()
            throws Exception {
        String scriptUrl = "";
        int users = 1;
        long ramp = 1L;

        WatsAgentStartRequest result = new WatsAgentStartRequest(scriptUrl, users, ramp);

        assertNotNull(result);
        assertEquals(1L, result.getRampTime());
        assertEquals(1, result.getUserIntervalIncrement());
        assertEquals(0, result.getTotalAgents());
        assertEquals(1, result.getConcurrentUsers());
        assertEquals("", result.getScriptUrl());
        assertEquals(0, result.getStartUsers());
        assertEquals(0, result.getAgentInstanceNum());
        assertEquals(0L, result.getSimulationTime());
        assertEquals(null, result.getJobId());
    }

    /**
     * Run the int getAgentInstanceNum() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAgentInstanceNum_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        int result = fixture.getAgentInstanceNum();

        assertEquals(1, result);
    }

    /**
     * Run the int getConcurrentUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetConcurrentUsers_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        int result = fixture.getConcurrentUsers();

        assertEquals(1, result);
    }

    /**
     * Run the String getJobId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetJobId_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the long getRampTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRampTime_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        long result = fixture.getRampTime();

        assertEquals(1L, result);
    }

    /**
     * Run the String getScriptUrl() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetScriptUrl_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        String result = fixture.getScriptUrl();

        assertEquals("", result);
    }

    /**
     * Run the long getSimulationTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSimulationTime_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        long result = fixture.getSimulationTime();

        assertEquals(1L, result);
    }

    /**
     * Run the int getStartUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetStartUsers_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        int result = fixture.getStartUsers();

        assertEquals(1, result);
    }

    /**
     * Run the int getTotalAgents() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetTotalAgents_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        int result = fixture.getTotalAgents();

        assertEquals(1, result);
    }

    /**
     * Run the int getUserIntervalIncrement() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetUserIntervalIncrement_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(0);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        int result = fixture.getUserIntervalIncrement();

        assertEquals(1, result);
    }

    /**
     * Run the int getUserIntervalIncrement() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetUserIntervalIncrement_2()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        int result = fixture.getUserIntervalIncrement();

        assertEquals(1, result);
    }

    /**
     * Run the void setAgentInstanceNum(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetAgentInstanceNum_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");
        int agentInstanceNum = 1;

        fixture.setAgentInstanceNum(agentInstanceNum);

    }

    /**
     * Run the void setJobId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetJobId_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");
        String jobId = "";

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setScriptUrl(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetScriptUrl_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");
        String scriptUrl = "";

        fixture.setScriptUrl(scriptUrl);

    }

    /**
     * Run the void setSimulationTime(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetSimulationTime_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");
        long simulationTime = 1L;

        fixture.setSimulationTime(simulationTime);

    }

    /**
     * Run the void setStartUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetStartUsers_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");
        int start = 1;

        fixture.setStartUsers(start);

    }

    /**
     * Run the void setTotalAgents(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetTotalAgents_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");
        int totalAgents = 1;

        fixture.setTotalAgents(totalAgents);

    }

    /**
     * Run the void setUserIntervalIncrement(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetUserIntervalIncrement_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");
        int userIntervalIncrement = 1;

        fixture.setUserIntervalIncrement(userIntervalIncrement);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        WatsAgentStartRequest fixture = new WatsAgentStartRequest("", 1, 1L);
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setStartUsers(1);
        fixture.setUserIntervalIncrement(1);
        fixture.setSimulationTime(1L);
        fixture.setJobId("");

        String result = fixture.toString();

        assertNotNull(result);
    }
}