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

import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>StandaloneAgentRequestCpTest</code> contains tests for the class
 * <code>{@link StandaloneAgentRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class StandaloneAgentRequestCpTest {
    /**
     * Run the StandaloneAgentRequest() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testStandaloneAgentRequest_1()
            throws Exception {

        StandaloneAgentRequest result = new StandaloneAgentRequest();

        assertNotNull(result);
        assertEquals(0, result.getUsers());
        assertEquals(null, result.getInstanceId());
        assertEquals("END_OF_SCRIPT_GROUP", result.getStopBehavior());
        assertEquals(null, result.getJobId());
    }

    /**
     * Run the StandaloneAgentRequest(String,String,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testStandaloneAgentRequest_2()
            throws Exception {
        String jobId = "";
        String instanceId = "";
        int users = 1;

        StandaloneAgentRequest result = new StandaloneAgentRequest(jobId, instanceId, users);

        assertNotNull(result);
        assertEquals(1, result.getUsers());
        assertEquals("", result.getInstanceId());
        assertEquals("END_OF_SCRIPT_GROUP", result.getStopBehavior());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");
        Object obj = null;

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");
        Object obj = new StandaloneAgentRequest("", "", 1);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testEquals_4()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");
        Object obj = new StandaloneAgentRequest("", "", 1);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getInstanceId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetInstanceId_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");

        String result = fixture.getInstanceId();

        assertEquals("", result);
    }

    /**
     * Run the String getJobId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetJobId_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the String getStopBehavior() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetStopBehavior_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");

        String result = fixture.getStopBehavior();

        assertEquals("", result);
    }

    /**
     * Run the int getUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetUsers_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");

        int result = fixture.getUsers();

        assertEquals(1, result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");

        int result = fixture.hashCode();

        assertEquals(525, result);
    }

    /**
     * Run the void setStopBehavior(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetStopBehavior_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");
        String stopBehavior = "";

        fixture.setStopBehavior(stopBehavior);

    }

    /**
     * Run the void setUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetUsers_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");
        int users = 1;

        fixture.setUsers(users);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        StandaloneAgentRequest fixture = new StandaloneAgentRequest("", "", 1);
        fixture.setStopBehavior("");

        String result = fixture.toString();

        assertNotNull(result);
    }
}