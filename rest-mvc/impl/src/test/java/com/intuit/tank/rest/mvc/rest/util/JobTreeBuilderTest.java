/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.Project;
import com.intuit.tank.rest.mvc.rest.models.Failures;
import com.intuit.tank.rest.mvc.rest.models.JobTree;
import com.intuit.tank.rest.mvc.rest.models.JobTree.Actions;
import com.intuit.tank.rest.mvc.rest.models.JobTree.JobNode;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JobTreeBuilderTest {

    private static final Date NOW = new Date(1_800_000_000_000L);

    private final VMTracker tracker = mock(VMTracker.class);
    private final AgentWsCommandSender ws = mock(AgentWsCommandSender.class);
    private final JobQueueDao queueDao = mock(JobQueueDao.class);
    private final ProjectDao projectDao = mock(ProjectDao.class);
    private Predicate<Project> canControl = p -> p != null && "alice".equals(p.getCreator());

    private Project load;
    private JobQueue loadQueue;

    @BeforeEach
    void setUp() {
        load = ProjectFixtures.project(1, "Load", "alice");
        loadQueue = new JobQueue(1);
        loadQueue.getJobs().add(job(10, JobQueueStatus.Running, null));
        loadQueue.getJobs().add(job(11, JobQueueStatus.Created, null));
        loadQueue.getJobs().add(job(9, JobQueueStatus.Completed, new Date(NOW.getTime() - 1000)));
        loadQueue.getJobs().add(job(8, JobQueueStatus.Deleted, null));
        when(projectDao.findForIds(anyList())).thenReturn(List.of(load));
        when(queueDao.findRecent(any())).thenReturn(List.of(loadQueue));
        when(queueDao.getForProjectIds(List.of(1))).thenReturn(List.of(loadQueue));
        when(tracker.getAllJobs()).thenReturn(Set.of());

        CloudVmStatus agentA = agent("i-a", "10", VMStatus.running, JobStatus.Running, 40, 50, 7);
        agentA.getValidationFailures().addKill();
        CloudVmStatus agentB = agent("i-b", "10", VMStatus.running, JobStatus.Paused, 10, 50, 3);
        when(tracker.getVmStatusForJob("10")).thenReturn(container("10", agentA, agentB));
        when(ws.getWsState("i-a")).thenReturn("connected");
        when(ws.getLastSeen("i-a")).thenReturn(123L);
    }

    private JobTreeBuilder builder() {
        return new JobTreeBuilder(tracker, ws, canControl, queueDao, projectDao);
    }

    private static JobInstance job(int id, JobQueueStatus status, Date end) {
        JobInstance job = new JobInstance();
        job.setId(id);
        job.setName("job" + id);
        job.setStatus(status);
        job.setEndTime(end);
        job.setTotalVirtualUsers(100);
        return job;
    }

    private static CloudVmStatus agent(String instanceId, String jobId, VMStatus vmStatus, JobStatus jobStatus,
                                       int current, int total, int tps) {
        CloudVmStatus vm = new CloudVmStatus(instanceId, jobId, "sg", jobStatus, VMImageType.AGENT, VMRegion.US_EAST,
                vmStatus, new ValidationStatus(), total, current, null, null);
        vm.setTotalTps(tps);
        return vm;
    }

    private static CloudVmStatusContainer container(String jobId, CloudVmStatus... agents) {
        CloudVmStatusContainer container = new CloudVmStatusContainer(new HashSet<>(List.of(agents)));
        container.setJobId(jobId);
        container.setStatus(JobQueueStatus.Running);
        return container;
    }

    @Test
    void groupsUnfinishedJobsByProjectNewestFirst() {
        JobTree tree = builder().build(null, false, NOW);
        assertEquals(1, tree.projects().size());
        JobTree.ProjectJobs project = tree.projects().get(0);
        assertEquals("Load", project.name());
        assertEquals(List.of("11", "10"), project.jobs().stream().map(JobNode::jobId).toList(),
                "deleted and finished jobs are hidden, newest first");
        assertEquals(NOW, tree.generatedAt());
        verify(queueDao).findRecent(new Date(NOW.getTime() - JobTreeBuilder.RECENT_MS));
    }

    @Test
    void includeFinishedShowsEndedJobsButNeverDeleted() {
        JobTree tree = builder().build(null, true, NOW);
        assertEquals(List.of("11", "10", "9"), tree.projects().get(0).jobs().stream().map(JobNode::jobId).toList());
    }

    @Test
    void sumsLiveAgentFigures() {
        JobTree.ProjectJobs project = builder().build(null, false, NOW).projects().get(0);
        JobNode running = project.jobs().get(1);
        assertEquals(50, running.activeUsers());
        assertEquals(100, running.totalUsers(), "a job's total is its configured users");
        assertEquals(10, running.tps());
        assertEquals(1, running.failures().kills());
        assertEquals(2, running.agents().size());
        JobTree.AgentNode agentA = running.agents().get(0);
        assertEquals("i-a", agentA.instanceId());
        assertEquals("US_EAST", agentA.region());
        assertEquals("connected", agentA.wsState());
        assertEquals(123L, agentA.lastSeenMs());
        assertEquals(50, project.activeUsers());
        assertEquals(100, project.totalUsers(), "a project's total is its agents' totals");
        assertEquals(10, project.tps());
    }

    @Test
    void oneProjectUsesItsQueueWithoutCreatingOne() {
        JobTree tree = builder().build(1, false, NOW);
        assertEquals(1, tree.projects().size());
        verify(queueDao, never()).findOrCreateForProjectId(anyInt());
        verify(queueDao, never()).findRecent(any());
    }

    @Test
    void actionsDependOnStatusAndControl() {
        List<JobNode> jobs = builder().build(null, false, NOW).projects().get(0).jobs();
        JobNode created = jobs.get(0);
        assertTrue(created.actions().start());
        assertTrue(created.actions().delete());
        assertFalse(created.actions().kill());
        JobNode running = jobs.get(1);
        assertTrue(running.actions().stop());
        assertTrue(running.actions().kill());
        assertTrue(running.actions().pause());
        assertFalse(running.actions().start());
        // the second agent is paused: it can be resumed, not paused
        assertTrue(running.agents().get(1).actions().resume());
        assertFalse(running.agents().get(1).actions().pause());

        canControl = p -> false;
        JobNode uncontrolled = builder().build(null, false, NOW).projects().get(0).jobs().get(0);
        assertFalse(uncontrolled.actions().control());
        assertFalse(uncontrolled.actions().start());
        assertFalse(uncontrolled.actions().delete());
    }

    @Test
    void trackedJobsWithoutAQueueEntry() {
        CloudVmStatus stray = agent("i-z", "77", VMStatus.running, JobStatus.Running, 5, 5, 1);
        CloudVmStatusContainer strayContainer = container("77", stray);
        CloudVmStatusContainer known = container("10");
        when(tracker.getAllJobs()).thenReturn(Set.of(strayContainer, known));
        when(tracker.getVmStatusForJob("77")).thenReturn(strayContainer);
        when(queueDao.findForJobId(77)).thenReturn(null);

        JobTree tree = builder().build(null, false, NOW);

        assertEquals(1, tree.otherJobs().size(), "job 10 is in a queue, so only 77 is other");
        JobNode other = tree.otherJobs().get(0);
        assertEquals("77", other.jobId());
        assertEquals("Running", other.status());
        assertEquals(5, other.totalUsers());
        assertFalse(other.actions().control(), "no project, so only CONTROL_JOB holders may control it");
    }

    @Test
    void trackedJobOfAKnownProjectJoinsIt() {
        CloudVmStatusContainer strayContainer = container("12", agent("i-y", "12", VMStatus.running, JobStatus.Running, 1, 1, 0));
        when(tracker.getAllJobs()).thenReturn(Set.of(strayContainer));
        when(tracker.getVmStatusForJob("12")).thenReturn(strayContainer);
        when(queueDao.findForJobId(12)).thenReturn(loadQueue);

        JobTree tree = builder().build(null, false, NOW);
        assertEquals(List.of("11", "10", "12"), tree.projects().get(0).jobs().stream().map(JobNode::jobId).toList());
        assertTrue(tree.projects().get(0).jobs().get(2).actions().control());
    }

    @Test
    void noWebSocketSender() {
        JobTree tree = new JobTreeBuilder(tracker, null, canControl, queueDao, projectDao).build(null, false, NOW);
        assertNull(tree.projects().get(0).jobs().get(1).agents().get(0).wsState());
    }

    @Test
    void jobActionRules() {
        Actions twoStepStarting = JobTreeBuilder.jobActions("Starting", true, true);
        assertTrue(twoStepStarting.startLoad());
        assertFalse(JobTreeBuilder.jobActions("Starting", false, true).startLoad(), "start-load is for two-step jobs");
        assertTrue(JobTreeBuilder.jobActions("Paused", false, true).resume());
        assertTrue(JobTreeBuilder.jobActions("RampPaused", false, true).resumeRamp());
        assertFalse(JobTreeBuilder.jobActions("Completed", false, true).kill());
        assertFalse(JobTreeBuilder.jobActions("", false, true).control() && JobTreeBuilder.jobActions("", false, true).start());
    }

    @Test
    void agentActionRules() {
        Actions rampPaused = JobTreeBuilder.agentActions("rampPaused", "RampPaused", true);
        assertTrue(rampPaused.resumeRamp());
        assertTrue(rampPaused.stop());
        assertFalse(rampPaused.start());
        assertFalse(rampPaused.delete());
        assertFalse(JobTreeBuilder.agentActions("terminated", "Completed", true).kill());
    }

    @Test
    void failuresAddUp() {
        Failures a = new Failures(3, 1, 0, 2, 0, 0, 0);
        assertEquals(new Failures(6, 2, 0, 4, 0, 0, 0), a.plus(a));
        assertEquals(Failures.NONE, Failures.of(null));
    }
}
