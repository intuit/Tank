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

import static org.junit.Assert.*;

import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>AgentDataCpTest</code> contains tests for the class <code>{@link AgentData}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AgentDataCpTest {
    /**
     * Run the AgentData() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentData_1()
            throws Exception {

        AgentData result = new AgentData();

        assertNotNull(result);
        assertEquals(0, result.getUsers());
        assertEquals(null, result.getZone());
        assertEquals(null, result.getInstanceId());
        assertEquals(null, result.getRegion());
        assertEquals(null, result.getInstanceUrl());
        assertEquals(0, result.getCapacity());
        assertEquals(null, result.getJobId());
    }

    /**
     * Run the AgentData(String,String,String,int,VMRegion,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentData_2()
            throws Exception {
        String jobId = "";
        String instanceId = "";
        String instanceUrl = "";
        int capacity = 1;
        VMRegion region = VMRegion.ASIA_1;
        String zone = "";

        AgentData result = new AgentData(jobId, instanceId, instanceUrl, capacity, region, zone);

        assertNotNull(result);
        assertEquals(0, result.getUsers());
        assertEquals("", result.getZone());
        assertEquals("", result.getInstanceId());
        assertEquals("", result.getInstanceUrl());
        assertEquals(1, result.getCapacity());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);
        Object obj = null;

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);
        Object obj = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEquals_4()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);
        Object obj = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the int getCapacity() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetCapacity_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        int result = fixture.getCapacity();

        assertEquals(1, result);
    }

    /**
     * Run the String getInstanceId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetInstanceId_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        String result = fixture.getInstanceId();

        assertEquals("", result);
    }

    /**
     * Run the String getInstanceUrl() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetInstanceUrl_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        String result = fixture.getInstanceUrl();

        assertEquals("", result);
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
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        String result = fixture.getJobId();

        assertEquals("", result);
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
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        VMRegion result = fixture.getRegion();

        assertNotNull(result);
        assertEquals("Asia Pacific (Singapore)", result.getDescription());
        assertEquals("Asia Pacific (Singapore)", result.toString());
        assertEquals("ec2.ap-southeast-1.amazonaws.com", result.getEndpoint());
        assertEquals("ASIA_1", result.name());
    }

    /**
     * Run the int getUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetUsers_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        int result = fixture.getUsers();

        assertEquals(1, result);
    }

    /**
     * Run the String getZone() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetZone_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        String result = fixture.getZone();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        int result = fixture.hashCode();

        assertEquals(525, result);
    }

    /**
     * Run the void setUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetUsers_1()
            throws Exception {
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);
        int users = 1;

        fixture.setUsers(users);

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
        AgentData fixture = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        fixture.setUsers(1);

        String result = fixture.toString();

        assertNotNull(result);
    }
}