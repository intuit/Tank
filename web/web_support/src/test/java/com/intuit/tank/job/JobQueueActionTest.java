package com.intuit.tank.job;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class JobQueueActionTest {

    @InjectMocks
    private JobQueueAction action;

    @Mock
    private JobEventSender controller;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        AWSXRay.beginSegment("test-segment");
    }

    @AfterEach
    void tearDown() throws Exception {
        try {
            AWSXRay.endSegment();
        } catch (Exception ignored) {}
        closeable.close();
    }

    private ActJobNodeBean createJobNode(String jobId, String id, String status) {
        ActJobNodeBean node = mock(ActJobNodeBean.class);
        when(node.getJobId()).thenReturn(jobId);
        when(node.getId()).thenReturn(id);
        when(node.getStatus()).thenReturn(status);
        return node;
    }

    private VMNodeBean createVMNode(String jobId, String id) {
        VMNodeBean node = mock(VMNodeBean.class);
        when(node.getJobId()).thenReturn(jobId);
        when(node.getId()).thenReturn(id);
        when(node.getStatus()).thenReturn("Running");
        return node;
    }

    @Test
    public void testRun_WithCreatedActJobNode_CallsStartJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobQueueStatus.Created.toString());
        action.run(node);
        verify(controller).startJob("id1");
    }

    @Test
    public void testRun_WithPausedActJobNode_CallsRestartJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobStatus.Paused.toString());
        action.run(node);
        verify(controller).restartJob("id1");
    }

    @Test
    public void testRun_WithRampPausedActJobNode_CallsResumeRampJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobStatus.RampPaused.toString());
        action.run(node);
        verify(controller).resumeRampJob("job1");
    }

    @Test
    public void testRun_WithRunningActJobNode_CallsStartJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobStatus.Running.toString());
        action.run(node);
        verify(controller).startJob("id1");
    }

    @Test
    public void testRun_WithVMNodeBean_CallsRestartAgent() {
        VMNodeBean node = createVMNode("job1", "vmId1");
        action.run(node);
        verify(controller).restartAgent("vmId1");
    }

    @Test
    public void testStartAgents_WhenActJobNodeUseTwoStep_CallsStartAgents() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobQueueStatus.Created.toString());
        when(node.isUseTwoStep()).thenReturn(true);
        action.startAgents(node);
        verify(controller).startAgents("id1");
    }

    @Test
    public void testStartAgents_WhenActJobNodeNotTwoStep_DoesNotCallStartAgents() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobQueueStatus.Created.toString());
        when(node.isUseTwoStep()).thenReturn(false);
        action.startAgents(node);
        verify(controller, never()).startAgents(any());
    }

    @Test
    public void testStartAgents_WhenVMNodeBean_DoesNotCallStartAgents() {
        VMNodeBean node = createVMNode("job1", "vmId1");
        action.startAgents(node);
        verify(controller, never()).startAgents(any());
    }

    @Test
    public void testPause_WithActJobNode_CallsPauseJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobQueueStatus.Running.toString());
        action.pause(node);
        verify(controller).pauseJob("id1");
    }

    @Test
    public void testPause_WithVMNodeBean_CallsPauseAgent() {
        VMNodeBean node = createVMNode("job1", "vmId1");
        action.pause(node);
        verify(controller).pauseAgent("vmId1");
    }

    @Test
    public void testPauseRamp_WithActJobNode_CallsPauseRampJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobQueueStatus.Running.toString());
        action.pauseRamp(node);
        verify(controller).pauseRampJob("id1");
    }

    @Test
    public void testPauseRamp_WithVMNodeBean_CallsPauseRampInstance() {
        VMNodeBean node = createVMNode("job1", "vmId1");
        action.pauseRamp(node);
        verify(controller).pauseRampInstance("vmId1");
    }

    @Test
    public void testKill_WithActJobNode_CallsKillJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobQueueStatus.Running.toString());
        action.kill(node);
        verify(controller).killJob("id1");
    }

    @Test
    public void testKill_WithVMNodeBean_CallsKillInstance() {
        VMNodeBean node = createVMNode("job1", "vmId1");
        action.kill(node);
        verify(controller).killInstance("vmId1");
    }

    @Test
    public void testStop_WithActJobNode_CallsStopJob() {
        ActJobNodeBean node = createJobNode("job1", "id1", JobQueueStatus.Running.toString());
        action.stop(node);
        verify(controller).stopJob("id1");
    }

    @Test
    public void testStop_WithVMNodeBean_CallsStopAgent() {
        VMNodeBean node = createVMNode("job1", "vmId1");
        action.stop(node);
        verify(controller).stopAgent("vmId1");
    }
}
