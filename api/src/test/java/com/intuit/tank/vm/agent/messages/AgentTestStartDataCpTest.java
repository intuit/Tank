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

import org.junit.*;

import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.DataFileRequest;

import static org.junit.Assert.*;

/**
 * The class <code>AgentTestStartDataCpTest</code> contains tests for the class <code>{@link AgentTestStartData}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AgentTestStartDataCpTest {
    /**
     * Run the AgentTestStartData() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentTestStartData_1()
            throws Exception {

        AgentTestStartData result = new AgentTestStartData();

        assertNotNull(result);
        assertEquals(0L, result.getRampTime());
        assertEquals(1, result.getUserIntervalIncrement());
        assertEquals(0, result.getTotalAgents());
        assertEquals(0, result.getConcurrentUsers());
        assertEquals(null, result.getScriptUrl());
        assertEquals(null, result.getDataFiles());
        assertEquals(0, result.getStartUsers());
        assertEquals(0, result.getAgentInstanceNum());
        assertEquals(0L, result.getSimulationTime());
        assertEquals(null, result.getJobId());
    }

    /**
     * Run the AgentTestStartData(AgentTestStartData) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentTestStartData_2()
            throws Exception {
        AgentTestStartData copy = new AgentTestStartData("", 1, 1L);
        copy.setUserIntervalIncrement(1);
        copy.setAgentInstanceNum(1);
        copy.setTotalAgents(1);
        copy.setJobId("");
        copy.setStartUsers(1);
        copy.setSimulationTime(1L);

        AgentTestStartData result = new AgentTestStartData(copy);

        assertNotNull(result);
        assertEquals(1L, result.getRampTime());
        assertEquals(1, result.getUserIntervalIncrement());
        assertEquals(1, result.getTotalAgents());
        assertEquals(1, result.getConcurrentUsers());
        assertEquals("", result.getScriptUrl());
        assertEquals(null, result.getDataFiles());
        assertEquals(1, result.getStartUsers());
        assertEquals(1, result.getAgentInstanceNum());
        assertEquals(1L, result.getSimulationTime());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the AgentTestStartData(String,int,long) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentTestStartData_3()
            throws Exception {
        String scriptUrl = "";
        int users = 1;
        long ramp = 1L;

        AgentTestStartData result = new AgentTestStartData(scriptUrl, users, ramp);

        assertNotNull(result);
        assertEquals(1L, result.getRampTime());
        assertEquals(1, result.getUserIntervalIncrement());
        assertEquals(0, result.getTotalAgents());
        assertEquals(1, result.getConcurrentUsers());
        assertEquals("", result.getScriptUrl());
        assertEquals(null, result.getDataFiles());
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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

        int result = fixture.getConcurrentUsers();

        assertEquals(1, result);
    }

    /**
     * Run the DataFileRequest[] getDataFiles() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDataFiles_1()
            throws Exception {
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

        DataFileRequest[] result = fixture.getDataFiles();

        assertNotNull(result);
        assertEquals(0, result.length);
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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(0);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
        int agentInstanceNum = 1;

        fixture.setAgentInstanceNum(agentInstanceNum);

    }

    /**
     * Run the void setConcurrentUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetConcurrentUsers_1()
            throws Exception {
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
        int concurrentUsers = 1;

        fixture.setConcurrentUsers(concurrentUsers);

    }

    /**
     * Run the void setDataFiles(DataFileRequest[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetDataFiles_1()
            throws Exception {
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
        DataFileRequest[] dataFiles = new DataFileRequest[] {};

        fixture.setDataFiles(dataFiles);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
        String jobId = "";

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setRampTime(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetRampTime_1()
            throws Exception {
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
        long rampTime = 1L;

        fixture.setRampTime(rampTime);

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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);
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
        AgentTestStartData fixture = new AgentTestStartData("", 1, 1L);
        fixture.setUserIntervalIncrement(1);
        fixture.setDataFiles(new DataFileRequest[] {});
        fixture.setAgentInstanceNum(1);
        fixture.setTotalAgents(1);
        fixture.setJobId("");
        fixture.setStartUsers(1);
        fixture.setSimulationTime(1L);

        String result = fixture.toString();

        assertNotNull(result);
    }
}