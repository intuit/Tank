package com.intuit.tank.perfManager.workLoads;

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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.AgentCommand;

/**
 * The class <code>JobManagerTest</code> contains tests for the class <code>{@link JobManager}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class JobManagerTest {
    /**
     * Run the JobManager() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testJobManager_1()
            throws Exception {
        JobManager result = new JobManager();
        assertNotNull(result);
    }

    /**
     * Run the AgentTestStartData registerAgentForJob(AgentData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testRegisterAgentForJob_1()
            throws Exception {
        JobManager fixture = new JobManager();
        AgentData agent = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");

        AgentTestStartData result = fixture.registerAgentForJob(agent);

        assertEquals(null, result);
    }

    /**
     * Run the AgentTestStartData registerAgentForJob(AgentData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testRegisterAgentForJob_2()
            throws Exception {
        JobManager fixture = new JobManager();
        AgentData agent = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");

        AgentTestStartData result = fixture.registerAgentForJob(agent);

        assertEquals(null, result);
    }

    /**
     * Run the AgentTestStartData registerAgentForJob(AgentData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testRegisterAgentForJob_3()
            throws Exception {
        JobManager fixture = new JobManager();
        AgentData agent = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");

        AgentTestStartData result = fixture.registerAgentForJob(agent);

        assertEquals(null, result);
    }

    /**
     * Run the FutureTask<AgentData> sendCommand(String,WatsAgentCommand) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testSendCommand_1() {
        JobManager fixture = new JobManager();
        List<String> instanceId = Collections.singletonList("");
        AgentCommand cmd = AgentCommand.kill;

        CompletableFuture<?>[] result = fixture.sendCommand(instanceId, cmd);

        assertEquals(0, result.length);
    }

    @Test
    public void testSendCommand_2() {
        JobManager fixture = new JobManager();
        List<String> instanceId = new LinkedList<>();
        AgentCommand cmd = AgentCommand.kill;

        CompletableFuture<?>[] result = fixture.sendCommand(instanceId, cmd);

        assertEquals(0, result.length);
    }

    @Test
    public void testGetInstanceUrl() {
        JobManager fixture = new JobManager();
        List<String> instanceId = Collections.singletonList("");

        List<String> result = fixture.getInstanceUrl(instanceId);

        assertTrue(result.isEmpty());
    }
}