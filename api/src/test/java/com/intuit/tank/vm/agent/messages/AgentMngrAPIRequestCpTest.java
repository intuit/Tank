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

import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>AgentMngrAPIRequestCpTest</code> contains tests for the class
 * <code>{@link AgentMngrAPIRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AgentMngrAPIRequestCpTest {
    /**
     * Run the AgentMngrAPIRequest(String,UserRequest[]) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentMngrAPIRequest_1()
            throws Exception {
        String jobId = "";

        AgentMngrAPIRequest result = new AgentMngrAPIRequest(jobId);

        assertNotNull(result);
        assertEquals(0, result.totalNumberVirtualUsers());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the AgentMngrAPIRequest(String,UserRequest[]) constructor test.
     * 
     * @throws Exception 
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentMngrAPIRequest_2()
            throws Exception {
        String jobId = "";

        AgentMngrAPIRequest result = new AgentMngrAPIRequest(jobId);

        assertNotNull(result);
        assertEquals(0, result.totalNumberVirtualUsers());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the AgentMngrAPIRequest(String,UserRequest[]) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentMngrAPIRequest_3()
            throws Exception {
        String jobId = "";

        AgentMngrAPIRequest result = new AgentMngrAPIRequest(jobId);

        assertNotNull(result);
        assertEquals(0, result.totalNumberVirtualUsers());
        assertEquals("", result.getJobId());
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
        AgentMngrAPIRequest fixture = new AgentMngrAPIRequest("", new AgentMngrAPIRequest.UserRequest[] {});

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the int getNumberVirtualUsers(VMRegion) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNumberVirtualUsers_1()
            throws Exception {
        AgentMngrAPIRequest fixture = new AgentMngrAPIRequest("", new AgentMngrAPIRequest.UserRequest(VMRegion.ASIA_1,
                1000));
        VMRegion region = VMRegion.ASIA_1;

        int result = fixture.getNumberVirtualUsers(region);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NullPointerException
        // at
        // com.intuit.tank.agent.messages.AgentMngrAPIRequest.getNumberVirtualUsers(AgentMngrAPIRequest.java:32)
        assertEquals(1000, result);
    }

    /**
     * Run the int getNumberVirtualUsers(VMRegion) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNumberVirtualUsers_2()
            throws Exception {
        AgentMngrAPIRequest fixture = new AgentMngrAPIRequest("", new AgentMngrAPIRequest.UserRequest[] {});
        VMRegion region = null;

        int result = fixture.getNumberVirtualUsers(region);

        assertEquals(0, result);
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
        AgentMngrAPIRequest fixture = new AgentMngrAPIRequest("", new AgentMngrAPIRequest.UserRequest[] {});

        String result = fixture.toString();

        assertNotNull(result);
    }

    /**
     * Run the int totalNumberVirtualUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testTotalNumberVirtualUsers_1()
            throws Exception {
        AgentMngrAPIRequest fixture = new AgentMngrAPIRequest("", new AgentMngrAPIRequest.UserRequest[] {});

        int result = fixture.totalNumberVirtualUsers();

        assertEquals(0, result);
    }
}