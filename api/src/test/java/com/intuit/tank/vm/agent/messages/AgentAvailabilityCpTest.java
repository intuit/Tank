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

import java.text.DateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>AgentAvailabilityCpTest</code> contains tests for the class <code>{@link AgentAvailability}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AgentAvailabilityCpTest {
    /**
     * Run the AgentAvailability() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentAvailability_1()
            throws Exception {

        AgentAvailability result = new AgentAvailability();

        assertNotNull(result);
        assertEquals(null, result.getInstanceId());
        assertEquals(null, result.getTimestamp());
        assertEquals(null, result.getInstanceUrl());
        assertEquals(0, result.getCapacity());
    }

    /**
     * Run the AgentAvailability(String,String,int,AgentAvailabilityStatus) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentAvailability_2()
            throws Exception {
        String instanceId = "";
        String instanceUrl = "";
        int capacity = 1;
        AgentAvailabilityStatus status = AgentAvailabilityStatus.AVAILABLE;

        AgentAvailability result = new AgentAvailability(instanceId, instanceUrl, capacity, status);

        assertNotNull(result);
        assertEquals("", result.getInstanceId());
        assertEquals("", result.getInstanceUrl());
        assertEquals(1, result.getCapacity());
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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);
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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);
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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);
        Object obj = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);
        Object obj = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the AgentAvailabilityStatus getAvailabilityStatus() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAvailabilityStatus_1()
            throws Exception {
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

        AgentAvailabilityStatus result = fixture.getAvailabilityStatus();

        assertNotNull(result);
        assertEquals("AVAILABLE", result.name());
        assertEquals("AVAILABLE", result.toString());
        assertEquals(0, result.ordinal());
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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

        String result = fixture.getInstanceUrl();

        assertEquals("", result);
    }

    /**
     * Run the Date getTimestamp() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetTimestamp_1()
            throws Exception {
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

        Date result = fixture.getTimestamp();

        assertNotNull(result);
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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

        int result = fixture.hashCode();

        assertEquals(525, result);
    }

    /**
     * Run the void setAvailabilityStatus(AgentAvailabilityStatus) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetAvailabilityStatus_1()
            throws Exception {
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);
        AgentAvailabilityStatus availabilityStatus = AgentAvailabilityStatus.AVAILABLE;

        fixture.setAvailabilityStatus(availabilityStatus);

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
        AgentAvailability fixture = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

        String result = fixture.toString();

        assertNotNull(result);
    }
}