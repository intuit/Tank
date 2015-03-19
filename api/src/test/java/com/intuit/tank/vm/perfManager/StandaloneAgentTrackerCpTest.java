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

import java.util.Collection;
import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;

/**
 * The class <code>StandaloneAgentTrackerCpTest</code> contains tests for the class
 * <code>{@link StandaloneAgentTracker}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class StandaloneAgentTrackerCpTest {
    /**
     * Run the StandaloneAgentTracker() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testStandaloneAgentTracker_1()
            throws Exception {
        StandaloneAgentTracker result = new StandaloneAgentTracker();
        assertNotNull(result);
    }

    /**
     * Run the void addAvailability(AgentAvailability) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAddAvailability_1()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();
        AgentAvailability availability = new AgentAvailability("", "", 1, AgentAvailabilityStatus.AVAILABLE);

        fixture.addAvailability(availability);

    }

    /**
     * Run the List<AgentAvailability> getAgents(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAgents_1()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();
        int numUsers = 1;

        List<AgentAvailability> result = fixture.getAgents(numUsers);

        assertEquals(null, result);
    }

    /**
     * Run the List<AgentAvailability> getAgents(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAgents_2()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();
        int numUsers = 1;

        List<AgentAvailability> result = fixture.getAgents(numUsers);

        assertEquals(null, result);
    }

    /**
     * Run the List<AgentAvailability> getAgents(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAgents_3()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();
        int numUsers = 0;

        List<AgentAvailability> result = fixture.getAgents(numUsers);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<AgentAvailability> getAgents(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAgents_4()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();
        int numUsers = 1;

        List<AgentAvailability> result = fixture.getAgents(numUsers);

        assertEquals(null, result);
    }

    /**
     * Run the List<AgentAvailability> getAgents(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAgents_5()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();
        int numUsers = 0;

        List<AgentAvailability> result = fixture.getAgents(numUsers);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Collection<AgentAvailability> getAvailabilities() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAvailabilities_1()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();

        Collection<AgentAvailability> result = fixture.getAvailabilities();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the AgentAvailability getAvailabilityForAgent(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAvailabilityForAgent_1()
            throws Exception {
        StandaloneAgentTracker fixture = new StandaloneAgentTracker();
        String instanceId = "";

        AgentAvailability result = fixture.getAvailabilityForAgent(instanceId);

        assertEquals(null, result);
    }
}