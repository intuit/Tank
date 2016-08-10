package com.intuit.tank.harness;

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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.harness.AgentRunData;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;

/**
 * The class <code>AgentRunDataTest</code> contains tests for the class <code>{@link AgentRunData}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class AgentRunDataTest {
    /**
     * Run the AgentRunData() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testAgentRunData_1()
            throws Exception {
        AgentRunData result = new AgentRunData();
        assertNotNull(result);
    }

    /**
     * Run the LoggingProfile getActiveProfile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetActiveProfile_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        LoggingProfile result = fixture.getActiveProfile();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the int getAgentInstanceNum() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetAgentInstanceNum_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        int result = fixture.getAgentInstanceNum();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertEquals(1, result);
    }

    /**
     * Run the String getInstanceId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetInstanceId_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        String result = fixture.getInstanceId();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the String getJobId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetJobId_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        String result = fixture.getJobId();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the String getMachineName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetMachineName_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        String result = fixture.getMachineName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the int getNumStartUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetNumStartUsers_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        int result = fixture.getNumStartUsers();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertEquals(1, result);
    }

    /**
     * Run the int getNumUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetNumUsers_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        int result = fixture.getNumUsers();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.AgentRunData.<clinit>(AgentRunData.java:7)
        assertEquals(1, result);
    }

    /**
     * Run the String getProjectName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetProjectName_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        String result = fixture.getProjectName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the long getRampTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetRampTime_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        long result = fixture.getRampTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertEquals(1L, result);
    }

    /**
     * Run the String getReportingMode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetReportingMode_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        String result = fixture.getReportingMode();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the long getSimulationTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetSimulationTime_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        long result = fixture.getSimulationTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertEquals(1L, result);
    }

    /**
     * Run the StopBehavior getStopBehavior() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetStopBehavior_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        StopBehavior result = fixture.getStopBehavior();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the String getTestPlans() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetTestPlans_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        String result = fixture.getTestPlans();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertNotNull(result);
    }

    /**
     * Run the int getTotalAgents() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetTotalAgents_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        int result = fixture.getTotalAgents();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertEquals(1, result);
    }

    /**
     * Run the int getUserInterval() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetUserInterval_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");

        int result = fixture.getUserInterval();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
        assertEquals(1, result);
    }

    /**
     * Run the void setActiveProfile(LoggingProfile) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetActiveProfile_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        LoggingProfile activeProfile = LoggingProfile.STANDARD;

        fixture.setActiveProfile(activeProfile);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setAgentInstanceNum(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetAgentInstanceNum_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        int agentInstanceNum = 1;

        fixture.setAgentInstanceNum(agentInstanceNum);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setInstanceId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetInstanceId_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        String instanceId = "";

        fixture.setInstanceId(instanceId);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setJobId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetJobId_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        String jobId = "";

        fixture.setJobId(jobId);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setMachineName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetMachineName_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        String machineName = "";

        fixture.setMachineName(machineName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setNumStartUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetNumStartUsers_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        int numStartUsers = 1;

        fixture.setNumStartUsers(numStartUsers);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setNumUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetNumUsers_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        int numUsers = 1;

        fixture.setNumUsers(numUsers);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setProjectName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetProjectName_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        String projectName = "";

        fixture.setProjectName(projectName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setRampTime(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetRampTime_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        long rampTime = 1L;

        fixture.setRampTime(rampTime);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setReportingMode(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetReportingMode_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        String reportingMode = "";

        fixture.setReportingMode(reportingMode);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setSimulationTime(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetSimulationTime_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        long simulationTime = 1L;

        fixture.setSimulationTime(simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setStopBehavior(StopBehavior) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetStopBehavior_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        StopBehavior stopBehavior = StopBehavior.END_OF_SCRIPT;

        fixture.setStopBehavior(stopBehavior);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setTestPlans(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetTestPlans_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        String testPlans = "";

        fixture.setTestPlans(testPlans);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setTotalAgents(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetTotalAgents_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        int totalAgents = 1;

        fixture.setTotalAgents(totalAgents);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setUserInterval(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetUserInterval_1()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        int userInterval = 1;

        fixture.setUserInterval(userInterval);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }

    /**
     * Run the void setUserInterval(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetUserInterval_2()
            throws Exception {
        AgentRunData fixture = new AgentRunData();
        fixture.setSimulationTime(1L);
        fixture.setRampTime(1L);
        fixture.setNumStartUsers(1);
        fixture.setJobId("");
        fixture.setProjectName("");
        fixture.setNumUsers(1);
        fixture.setTotalAgents(1);
        fixture.setMachineName("");
        fixture.setStopBehavior(StopBehavior.END_OF_SCRIPT);
        fixture.setAgentInstanceNum(1);
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setReportingMode("");
        fixture.setInstanceId("");
        fixture.setTestPlans("");
        int userInterval = 0;

        fixture.setUserInterval(userInterval);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.AgentRunData
    }
}