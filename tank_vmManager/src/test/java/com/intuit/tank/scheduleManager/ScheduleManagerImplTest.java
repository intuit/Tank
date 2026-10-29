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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.scheduleManager.ScheduleManagerImpl;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIResponse;
import com.intuit.tank.vm.scheduleManager.JobRequestData;

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

    /**
     * Run the getJob(String) method test after adding a job and an agent for it.
     */
    @Test
    public void testGetJob_withAgent()
            throws Exception {
        ScheduleManagerImpl fixture = new ScheduleManagerImpl();
        AgentMngrAPIRequest request = new AgentMngrAPIRequest("schedule-job-1",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {});
        fixture.addJob(request);

        AgentMngrAPIResponse agent = new AgentMngrAPIResponse();
        agent.setJobId("schedule-job-1");
        fixture.addAgent(agent);

        JobRequestData data = fixture.getJob("schedule-job-1");
        assertNotNull(data);
        assertSame(request, data.getRequest());
        assertEquals(1, data.getAgents().size());
        assertNull(fixture.getJob("schedule-job-unknown"));
    }

    /**
     * Run the addAgent(AgentMngrAPIResponse) method test for a job that was never added.
     */
    @Test
    public void testAddAgent_unknownJob()
            throws Exception {
        ScheduleManagerImpl fixture = new ScheduleManagerImpl();
        AgentMngrAPIResponse agent = new AgentMngrAPIResponse();
        agent.setJobId("schedule-job-never-added");

        fixture.addAgent(agent);

        assertNull(fixture.getJob("schedule-job-never-added"));
    }
}