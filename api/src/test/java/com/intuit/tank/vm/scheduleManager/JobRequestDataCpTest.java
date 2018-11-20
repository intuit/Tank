package com.intuit.tank.vm.scheduleManager;

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

import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIResponse;
import com.intuit.tank.vm.scheduleManager.JobRequestData;

/**
 * The class <code>JobRequestDataCpTest</code> contains tests for the class <code>{@link JobRequestData}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class JobRequestDataCpTest {
    /**
     * Run the JobRequestData(AgentMngrAPIRequest) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testJobRequestData_1()
            throws Exception {
        AgentMngrAPIRequest request = new AgentMngrAPIRequest("",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {});

        JobRequestData result = new JobRequestData(request);

        assertNotNull(result);
        assertEquals(true, result.isFullfilled());
    }

    /**
     * Run the void addAgent(AgentMngrAPIResponse) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAddAgent_1()
            throws Exception {
        JobRequestData fixture = new JobRequestData(new AgentMngrAPIRequest("",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {}));
        fixture.addAgent(new AgentMngrAPIResponse());
        AgentMngrAPIResponse agent = new AgentMngrAPIResponse();
        agent.setNumberVirtualUsers(1);

        fixture.addAgent(agent);

    }

    /**
     * Run the List<AgentMngrAPIResponse> getAgents() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetAgents_1()
            throws Exception {
        JobRequestData fixture = new JobRequestData(new AgentMngrAPIRequest("",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {}));
        fixture.addAgent(new AgentMngrAPIResponse());

        List<AgentMngrAPIResponse> result = fixture.getAgents();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the AgentMngrAPIRequest getRequest() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRequest_1()
            throws Exception {
        JobRequestData fixture = new JobRequestData(new AgentMngrAPIRequest("",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {}));
        fixture.addAgent(new AgentMngrAPIResponse());

        AgentMngrAPIRequest result = fixture.getRequest();

        assertNotNull(result);
        assertEquals(0, result.totalNumberVirtualUsers());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the boolean isFullfilled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsFullfilled_1()
            throws Exception {
        JobRequestData fixture = new JobRequestData(new AgentMngrAPIRequest("",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {}));
        fixture.addAgent(new AgentMngrAPIResponse());

        boolean result = fixture.isFullfilled();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isFullfilled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsFullfilled_2()
            throws Exception {
        JobRequestData fixture = new JobRequestData(new AgentMngrAPIRequest("",
                new com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest.UserRequest[] {}));
        fixture.addAgent(new AgentMngrAPIResponse());

        boolean result = fixture.isFullfilled();

        assertEquals(true, result);
    }
}