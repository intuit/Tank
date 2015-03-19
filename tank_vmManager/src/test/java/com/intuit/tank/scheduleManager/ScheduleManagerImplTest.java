package com.intuit.tank.scheduleManager;

/*
 * #%L
 * VmManager
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

import com.intuit.tank.scheduleManager.ScheduleManagerImpl;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIResponse;

/**
 * The class <code>ScheduleManagerImplTest</code> contains tests for the class <code>{@link ScheduleManagerImpl}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:42 AM
 */
public class ScheduleManagerImplTest {
    /**
     * Run the ScheduleManagerImpl() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:42 AM
     */
    @Test
    public void testScheduleManagerImpl_1()
            throws Exception {
        ScheduleManagerImpl result = new ScheduleManagerImpl();
        assertNotNull(result);
    }

    /**
     * Run the void addAgent(AgentMngrAPIResponse) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:42 AM
     */
    @Test
    public void testAddAgent_1()
            throws Exception {
        ScheduleManagerImpl fixture = new ScheduleManagerImpl();
        AgentMngrAPIResponse agent = new AgentMngrAPIResponse();
        agent.setJobId("");

        fixture.addAgent(agent);

    }

    /**
     * Run the void addAgent(AgentMngrAPIResponse) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:42 AM
     */
    @Test
    public void testAddAgent_2()
            throws Exception {
        ScheduleManagerImpl fixture = new ScheduleManagerImpl();
        AgentMngrAPIResponse agent = new AgentMngrAPIResponse();
        agent.setJobId("");

        fixture.addAgent(agent);

    }

    /**
     * Run the void addJob(AgentMngrAPIRequest) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:42 AM
     */
    @Test
    public void testAddJob_1()
            throws Exception {
        ScheduleManagerImpl fixture = new ScheduleManagerImpl();
        AgentMngrAPIRequest request = new AgentMngrAPIRequest("",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {});

        fixture.addJob(request);

    }
}