package com.intuit.tank.agent;

/*
 * #%L
 * JSF Support Beans
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

import com.intuit.tank.agent.AgentStatusReporter;

import static org.junit.Assert.*;

/**
 * The class <code>AgentStatusReporterTest</code> contains tests for the class <code>{@link AgentStatusReporter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class AgentStatusReporterTest {
    /**
     * Run the AgentStatusReporter() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testAgentStatusReporter_1()
        throws Exception {

        AgentStatusReporter result = new AgentStatusReporter();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:22)
        assertNotNull(result);
    }

    /**
     * Run the AgentStatusReporter(String,String,String,String,String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testAgentStatusReporter_2()
        throws Exception {
        String agentStatus = "";
        String jobStatus = "";
        String role = "";
        String region = "";
        String activeUsers = "";
        String totalUsers = "";
        String usersChange = "";
        String startTime = "";
        String endTime = "";
        String totalTime = "";
        String jobId = "";
        String instanceId = "";

        AgentStatusReporter result = new AgentStatusReporter(agentStatus, jobStatus, role, region, activeUsers, totalUsers, usersChange, startTime, endTime, totalTime, jobId, instanceId);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getActiveUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetActiveUsers_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getActiveUsers();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getAgentStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetAgentStatus_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getAgentStatus();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getEndTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetEndTime_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getEndTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getInstanceId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetInstanceId_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getInstanceId();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getJobId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetJobId_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getJobId();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getJobStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetJobStatus_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getJobStatus();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getRegion() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetRegion_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getRegion();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getRole() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetRole_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getRole();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getStartTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetStartTime_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getStartTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getTotalTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetTotalTime_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getTotalTime();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getTotalUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetTotalUsers_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getTotalUsers();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the String getUsersChange() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetUsersChange_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");

        String result = fixture.getUsersChange();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
        assertNotNull(result);
    }

    /**
     * Run the void setActiveUsers(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetActiveUsers_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String activeUsers = "";

        fixture.setActiveUsers(activeUsers);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setAgentStatus(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetAgentStatus_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String agentStatus = "";

        fixture.setAgentStatus(agentStatus);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setEndTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetEndTime_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String endTime = "";

        fixture.setEndTime(endTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setInstanceId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetInstanceId_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String instanceId = "";

        fixture.setInstanceId(instanceId);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setJobId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetJobId_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String jobId = "";

        fixture.setJobId(jobId);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setJobStatus(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetJobStatus_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String jobStatus = "";

        fixture.setJobStatus(jobStatus);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setRegion(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetRegion_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String region = "";

        fixture.setRegion(region);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setRole(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetRole_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String role = "";

        fixture.setRole(role);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setStartTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetStartTime_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String startTime = "";

        fixture.setStartTime(startTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setTotalTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetTotalTime_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String totalTime = "";

        fixture.setTotalTime(totalTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setTotalUsers(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetTotalUsers_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String totalUsers = "";

        fixture.setTotalUsers(totalUsers);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }

    /**
     * Run the void setUsersChange(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetUsersChange_1()
        throws Exception {
        AgentStatusReporter fixture = new AgentStatusReporter("", "", "", "", "", "", "", "", "", "", "", "");
        String usersChange = "";

        fixture.setUsersChange(usersChange);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.agent.AgentStatusReporter.<init>(AgentStatusReporter.java:53)
    }
}