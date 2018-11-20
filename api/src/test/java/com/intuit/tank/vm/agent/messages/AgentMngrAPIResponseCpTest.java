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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.agent.messages.AgentMngrAPIResponse;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>AgentMngrAPIResponseCpTest</code> contains tests for the class
 * <code>{@link AgentMngrAPIResponse}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AgentMngrAPIResponseCpTest {
    /**
     * Run the AgentMngrAPIResponse() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentMngrAPIResponse_1()
            throws Exception {
        AgentMngrAPIResponse result = new AgentMngrAPIResponse();
        assertNotNull(result);
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
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");

        String result = fixture.getInstanceId();

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
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the int getNumberVirtualUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNumberVirtualUsers_1()
            throws Exception {
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");

        int result = fixture.getNumberVirtualUsers();

        assertEquals(1, result);
    }

    /**
     * Run the String getQueueName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetQueueName_1()
            throws Exception {
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");

        String result = fixture.getQueueName();

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
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");

        VMRegion result = fixture.getRegion();

        assertNotNull(result);
        assertEquals("Asia Pacific (Singapore)", result.getDescription());
        assertEquals("Asia Pacific (Singapore)", result.toString());
        assertEquals("ec2.ap-southeast-1.amazonaws.com", result.getEndpoint());
        assertEquals("ASIA_1", result.name());
    }

    /**
     * Run the void setInstanceId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetInstanceId_1()
            throws Exception {
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");
        String instanceId = "";

        fixture.setInstanceId(instanceId);

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
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");
        String jobId = "";

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setNumberVirtualUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetNumberVirtualUsers_1()
            throws Exception {
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");
        int numberVirtualUsers = 1;

        fixture.setNumberVirtualUsers(numberVirtualUsers);

    }

    /**
     * Run the void setQueueName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetQueueName_1()
            throws Exception {
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");
        String queueName = "";

        fixture.setQueueName(queueName);

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
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");
        VMRegion region = VMRegion.ASIA_1;

        fixture.setRegion(region);

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
        AgentMngrAPIResponse fixture = new AgentMngrAPIResponse();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setNumberVirtualUsers(1);
        fixture.setInstanceId("");
        fixture.setQueueName("");
        fixture.setJobId("");

        String result = fixture.toString();
        assertNotNull(result);
    }
}