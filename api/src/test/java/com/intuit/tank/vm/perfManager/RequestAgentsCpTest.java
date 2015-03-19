package com.intuit.tank.vm.perfManager;

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

import static org.junit.Assert.*;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.perfManager.RequestAgents;

/**
 * The class <code>RequestAgentsCpTest</code> contains tests for the class <code>{@link RequestAgents}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class RequestAgentsCpTest {
    /**
     * Run the RequestAgents(String,String,String,VMRegion,int,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testRequestAgents_1()
            throws Exception {
        String jobId = "";
        String reportingMode = "";
        String loggingProfile = "";
        VMRegion region = VMRegion.ASIA_1;
        int numberOfUsers = 1;
        String stopBehavior = "";

        RequestAgents result = new RequestAgents(jobId, reportingMode, loggingProfile, region, numberOfUsers,
                stopBehavior);

        assertNotNull(result);
        assertEquals("", result.getStopBehavior());
        assertEquals("", result.getReportingMode());
        assertEquals("", result.getJobId());
        assertEquals(1, result.getNumberOfUsers());
        assertEquals("", result.getLoggingProfile());
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
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the String getLoggingProfile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetLoggingProfile_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");

        String result = fixture.getLoggingProfile();

        assertEquals("", result);
    }

    /**
     * Run the int getNumberOfUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNumberOfUsers_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");

        int result = fixture.getNumberOfUsers();

        assertEquals(1, result);
    }

    /**
     * Run the int getNumberOfUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNumberOfUsers_2()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");

        int result = fixture.getNumberOfUsers();

        assertEquals(1, result);
    }

    /**
     * Run the VMRegion getRegion() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRegion_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");

        VMRegion result = fixture.getRegion();

        assertNotNull(result);
        assertEquals("Asia Pacific (Singapore)", result.getDescription());
        assertEquals("Asia Pacific (Singapore)", result.toString());
        assertEquals("ec2.ap-southeast-1.amazonaws.com", result.getEndpoint());
        assertEquals("ASIA_1", result.name());
        assertEquals(4, result.ordinal());
    }

    /**
     * Run the String getReportingMode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetReportingMode_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");

        String result = fixture.getReportingMode();

        assertEquals("", result);
    }

    /**
     * Run the String getStopBehavior() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetStopBehavior_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");

        String result = fixture.getStopBehavior();

        assertEquals("", result);
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
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");
        String jobId = "";

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setLoggingProfile(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetLoggingProfile_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");
        String data = "";

        fixture.setLoggingProfile(data);

    }

    /**
     * Run the void setNumberOfUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetNumberOfUsers_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");
        int data = 1;

        fixture.setNumberOfUsers(data);

    }

    /**
     * Run the void setRegion(VMRegion) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetRegion_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");
        VMRegion region = VMRegion.ASIA_1;

        fixture.setRegion(region);

    }

    /**
     * Run the void setReportingMode(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetReportingMode_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");
        String reportingMode = "";

        fixture.setReportingMode(reportingMode);

    }

    /**
     * Run the void setStopBehavior(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetStopBehavior_1()
            throws Exception {
        RequestAgents fixture = new RequestAgents("", "", "", VMRegion.ASIA_1, 1, "");
        String data = "";

        fixture.setStopBehavior(data);

    }
}