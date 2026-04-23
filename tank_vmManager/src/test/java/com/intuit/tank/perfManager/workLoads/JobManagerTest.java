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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.JobRequest;

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
    public void testRegisterAgentForJob()
            throws Exception {
        JobManager fixture = new JobManager();
        AgentData agent = new AgentData("", "", "", 1, VMRegion.ASIA_1, "");
        AgentTestStartData result = fixture.registerAgentForJob(agent);

        assertNull(result);
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

        List<CompletableFuture<?>> result = fixture.sendCommand(instanceId, cmd);

        assertEquals(0, result.size());
    }

    @Test
    public void testSendCommand_2() {
        JobManager fixture = new JobManager();
        List<String> instanceId = new LinkedList<>();
        AgentCommand cmd = AgentCommand.kill;

        List<CompletableFuture<?>> result = fixture.sendCommand(instanceId, cmd);

        assertEquals(0, result.size());
    }

    @Test
    public void testGetInstanceUrl() {
        JobManager fixture = new JobManager();
        List<String> instanceId = Collections.singletonList("");

        List<String> result = fixture.getInstanceUrl(instanceId);

        assertTrue(result.isEmpty());
    }

    /**
     * When WS sender is not injected (null), sendCommand should use HTTP path only.
     * Uses empty string instanceId (same pattern as existing tests) to avoid
     * hitting findAgent which requires tankConfig.
     */
    @Test
    public void testSendCommandWithNoWsSender() {
        JobManager fixture = new JobManager();
        // wsCommandSenderInstance is null (not injected) — should fall through to HTTP
        // Empty string is filtered out by StringUtils.isNotEmpty in getInstanceUrl
        List<String> instanceIds = Collections.singletonList("");
        AgentCommand cmd = AgentCommand.stop;

        // Should not throw, should return empty (no resolvable URL)
        List<CompletableFuture<?>> result = fixture.sendCommand(instanceIds, cmd);
        assertEquals(0, result.size());
    }

    /**
     * When WS sender is not injected, empty instanceId list should return empty results.
     */
    @Test
    public void testSendCommandEmptyListWithNoWsSender() {
        JobManager fixture = new JobManager();
        List<CompletableFuture<?>> result = fixture.sendCommand(Collections.emptyList(), AgentCommand.start);
        assertEquals(0, result.size());
    }

    @Test
    public void testSendCommandUsesControllerInitiatedWsWhenEnabled() throws Exception {
        JobManager fixture = new JobManager();
        String jobId = "job-1";
        String instanceId = "i-12345";
        String instanceUrl = "http://127.0.0.1:8090";
        seedJobInfoCache(fixture, jobId, instanceId, instanceUrl);

        AgentConfig agentConfig = mock(AgentConfig.class);
        when(agentConfig.isCommandWsEnabled()).thenReturn(false);
        when(agentConfig.isControllerInitiatedWsEnabled()).thenReturn(true);
        when(agentConfig.isCommandWsHttpFallbackEnabled()).thenReturn(false);
        when(agentConfig.getCommandWsAckTimeoutMillis()).thenReturn(1000L);

        TankConfig tankConfig = mock(TankConfig.class);
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        setField(fixture, "tankConfig", tankConfig);

        ControllerInitiatedAgentWsClient wsClient = mock(ControllerInitiatedAgentWsClient.class);
        when(wsClient.hasSession(instanceId)).thenReturn(true);
        when(wsClient.sendCommand(instanceId, jobId, AgentCommand.stop.name(), 1000L)).thenReturn(true);

        @SuppressWarnings("unchecked")
        Instance<ControllerInitiatedAgentWsClient> wsClientInstance = mock(Instance.class);
        when(wsClientInstance.isResolvable()).thenReturn(true);
        when(wsClientInstance.get()).thenReturn(wsClient);
        setField(fixture, "controllerInitiatedWsClientInstance", wsClientInstance);

        List<CompletableFuture<?>> result = fixture.sendCommand(Collections.singletonList(instanceId), AgentCommand.stop);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isDone());
        assertNull(result.get(0).join());
        verify(wsClient).sendCommand(eq(instanceId), eq(jobId), eq(AgentCommand.stop.name()), eq(1000L));
    }

    @Test
    public void testSendCommandSkipsWhenNoControllerWsSessionAndNoFallback() throws Exception {
        JobManager fixture = new JobManager();
        String jobId = "job-2";
        String instanceId = "i-54321";
        String instanceUrl = "http://127.0.0.1:8090";
        seedJobInfoCache(fixture, jobId, instanceId, instanceUrl);

        AgentConfig agentConfig = mock(AgentConfig.class);
        when(agentConfig.isCommandWsEnabled()).thenReturn(false);
        when(agentConfig.isControllerInitiatedWsEnabled()).thenReturn(true);
        when(agentConfig.isCommandWsHttpFallbackEnabled()).thenReturn(false);
        when(agentConfig.getCommandWsAckTimeoutMillis()).thenReturn(1000L);

        TankConfig tankConfig = mock(TankConfig.class);
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        setField(fixture, "tankConfig", tankConfig);

        ControllerInitiatedAgentWsClient wsClient = mock(ControllerInitiatedAgentWsClient.class);
        when(wsClient.hasSession(instanceId)).thenReturn(false);

        @SuppressWarnings("unchecked")
        Instance<ControllerInitiatedAgentWsClient> wsClientInstance = mock(Instance.class);
        when(wsClientInstance.isResolvable()).thenReturn(true);
        when(wsClientInstance.get()).thenReturn(wsClient);
        setField(fixture, "controllerInitiatedWsClientInstance", wsClientInstance);

        List<CompletableFuture<?>> result = fixture.sendCommand(Collections.singletonList(instanceId), AgentCommand.stop);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isDone());
        assertNull(result.get(0).join());
        verify(wsClient, never()).sendCommand(anyString(), anyString(), anyString(), anyLong());
    }

    private void seedJobInfoCache(JobManager fixture, String jobId, String instanceId, String instanceUrl) throws Exception {
        JobRequest jobRequest = mock(JobRequest.class);
        when(jobRequest.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        when(jobRequest.getRegions()).thenReturn(Collections.emptySet());
        when(jobRequest.getScriptsXmlUrl()).thenReturn("script.xml");
        when(jobRequest.getId()).thenReturn(jobId);
        when(jobRequest.getNumUsersPerAgent()).thenReturn(1);

        Class<?> jobInfoClass = Class.forName("com.intuit.tank.perfManager.workLoads.JobManager$JobInfo");
        Constructor<?> ctor = jobInfoClass.getDeclaredConstructor(JobRequest.class);
        ctor.setAccessible(true);
        Object jobInfo = ctor.newInstance(jobRequest);

        Field agentDataField = jobInfoClass.getDeclaredField("agentData");
        agentDataField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<AgentData> agentData = (Set<AgentData>) agentDataField.get(jobInfo);
        agentData.add(new AgentData(jobId, instanceId, instanceUrl, 1, VMRegion.US_EAST_2, "zone-a"));

        Map<String, Object> cache = new HashMap<>();
        cache.put(jobId, jobInfo);
        setField(fixture, "jobInfoMapLocalCache", cache);
    }

    private void setField(JobManager fixture, String fieldName, Object value) throws Exception {
        Field field = JobManager.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(fixture, value);
    }
}