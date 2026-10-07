/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.common.util.ReportUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JobInstanceFactoryTest {

    private final List<MockedConstruction<?>> constructions = new ArrayList<>();

    @BeforeEach
    void setUp() {
        constructions.add(Mockito.mockConstruction(JobValidator.class,
                (mock, ctx) -> when(mock.getDurationMs(anyString())).thenReturn(120_000L)));
        // each kind of entity has its own head revision so the test can tell them apart
        constructions.add(Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.getHeadRevisionNumber(anyInt())).thenReturn(11)));
        constructions.add(Mockito.mockConstruction(JobNotificationDao.class,
                (mock, ctx) -> when(mock.getHeadRevisionNumber(anyInt())).thenReturn(22)));
        constructions.add(Mockito.mockConstruction(JobRegionDao.class,
                (mock, ctx) -> when(mock.getHeadRevisionNumber(anyInt())).thenReturn(33)));
    }

    @AfterEach
    void tearDown() {
        constructions.forEach(MockedConstruction::close);
    }

    private static Workload workload(Project project) {
        Workload workload = project.getWorkloads().get(0);
        workload.getJobConfiguration().getNotifications().iterator().next().setId(301);
        return workload;
    }

    @Test
    void propose_snapshotsTheSavedProject() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        JobInstanceFactory.Proposal proposal = JobInstanceFactory.propose(workload(project), "Load", "nightly", "bob");
        JobInstance job = proposal.job();

        assertEquals(0, job.getId(), "not saved");
        assertEquals("nightly", job.getName());
        assertEquals("bob", job.getCreator());
        assertNotNull(job.getScheduledTime());
        assertEquals(Map.of("host", "example.com"), job.getVariables());
        assertTrue(job.isAllowOverride());
        assertEquals("c5.large", job.getVmInstanceType(), "settings come from the project");
        assertEquals(120_000L, job.getExecutionTime());
        assertEquals(5 * 60_000L, job.getRampTime());
        assertEquals(30 * 60_000L, job.getSimulationTime());
        assertEquals(100, job.getTotalVirtualUsers());
        assertEquals(Set.of(new EntityVersion(7, 11, DataFile.class)), job.getDataFileVersions());
        assertEquals(Set.of(new EntityVersion(301, 22, JobNotification.class)), job.getNotificationVersions());
        assertEquals(Set.of(new EntityVersion(501, 33, JobRegion.class)), job.getJobRegionVersions(),
                "regions are recorded by their current revision, not 0");
        assertNotNull(proposal.validator());
    }

    @Test
    void propose_evaluatesUserExpressions() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        project.getWorkloads().get(0).getJobConfiguration().getJobRegions().iterator().next().setUsers("10 * 5");
        assertEquals(50, JobInstanceFactory.propose(workload(project), "Load", null, "bob").job().getTotalVirtualUsers());
    }

    @Test
    void propose_defaultName() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        String name = JobInstanceFactory.propose(workload(project), "Load", null, "bob").job().getName();
        assertTrue(name.startsWith("Load_100_users_"), name);
    }

    @Test
    void defaultName() {
        Date when = new Date(0);
        assertEquals("P_42_users_" + ReportUtil.getTimestamp(when),
                JobInstanceFactory.defaultName("P", IncrementStrategy.increasing, 42, when));
        assertEquals("P_nonlinear_" + ReportUtil.getTimestamp(when),
                JobInstanceFactory.defaultName("P", IncrementStrategy.standard, 42, when));
    }

    @Test
    void queue_savesJobBeforeAddingItToTheQueueAndStoresScript() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        JobInstance job = JobInstanceFactory.propose(workload(project), "Load", "nightly", "bob").job();
        JobQueue queue = new JobQueue(1);
        try (MockedConstruction<JobInstanceDao> jobDao = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.saveOrUpdate(any())).thenAnswer(i -> {
                         JobInstance j = i.getArgument(0);
                         assertTrue(queue.getJobs().isEmpty(), "the job must be saved before it joins the queue");
                         j.setId(900);
                         return j;
                     }));
             MockedConstruction<JobQueueDao> queueDao = Mockito.mockConstruction(JobQueueDao.class, (mock, ctx) -> {
                 when(mock.findOrCreateForProjectId(1)).thenReturn(queue);
                 when(mock.saveOrUpdate(any())).thenAnswer(i -> i.getArgument(0));
             });
             MockedConstruction<WorkloadDao> workloadDao = Mockito.mockConstruction(WorkloadDao.class);
             MockedStatic<ConverterUtil> converter = Mockito.mockStatic(ConverterUtil.class);
             MockedStatic<ProjectDaoUtil> scripts = Mockito.mockStatic(ProjectDaoUtil.class)) {
            converter.when(() -> ConverterUtil.getWorkloadXML(any())).thenReturn("<workload/>");

            JobInstanceFactory.Queued queued = JobInstanceFactory.queue(1, project.getWorkloads().get(0), job);

            assertEquals(900, queued.job().getId());
            assertSame(queue, queued.queue());
            assertTrue(queue.getJobs().contains(queued.job()));
            scripts.verify(() -> ProjectDaoUtil.storeScriptFile("900", "<workload/>"));
            verify(workloadDao.constructed().get(0)).loadScriptsForWorkload(project.getWorkloads().get(0));
        }
    }

    @Test
    void queue_scriptFailureDoesNotFailTheQueue() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        JobInstance job = JobInstanceFactory.propose(workload(project), "Load", "nightly", "bob").job();
        try (MockedConstruction<JobInstanceDao> jobDao = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.saveOrUpdate(any())).thenAnswer(i -> {
                         JobInstance j = i.getArgument(0);
                         j.setId(901);
                         return j;
                     }));
             MockedConstruction<JobQueueDao> queueDao = Mockito.mockConstruction(JobQueueDao.class, (mock, ctx) -> {
                 when(mock.findOrCreateForProjectId(1)).thenReturn(new JobQueue(1));
                 when(mock.saveOrUpdate(any())).thenAnswer(i -> i.getArgument(0));
             });
             MockedConstruction<WorkloadDao> workloadDao = Mockito.mockConstruction(WorkloadDao.class);
             MockedStatic<ConverterUtil> converter = Mockito.mockStatic(ConverterUtil.class);
             MockedStatic<ProjectDaoUtil> scripts = Mockito.mockStatic(ProjectDaoUtil.class)) {
            converter.when(() -> ConverterUtil.convertWorkload(any(), any())).thenThrow(new NullPointerException("header"));

            JobInstanceFactory.Queued queued = JobInstanceFactory.queue(1, project.getWorkloads().get(0), job);

            assertEquals(901, queued.job().getId(), "the job stays queued; starting it generates the script");
            scripts.verifyNoInteractions();
        }
    }
}
