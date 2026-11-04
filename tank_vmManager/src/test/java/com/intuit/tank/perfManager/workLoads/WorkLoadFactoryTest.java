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

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.scheduleManager.AgentDispatcher;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vm.vmManager.VMChannel;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The class <code>WorkLoadFactoryTest</code> contains tests for the class <code>{@link WorkLoadFactory}</code>.
 *
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class WorkLoadFactoryTest {

    private static WorkLoadFactory fixture;

    @BeforeAll
    public static void constructor() {
        fixture = new WorkLoadFactory();
    }
    /**
     * Run the WorkLoadFactory() constructor test.
     *
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testWorkLoadFactory()
            throws Exception {
        assertNotNull(fixture);
    }

    /**
     * Run the String buildScriptXml(String,Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testBuildScriptXml()
            throws Exception {
        String jobId = "";
        Workload workload = new Workload();
        String result = fixture.buildScriptXml(jobId, workload);

        assertNotNull(result);
    }

    /**
     * getModelRunner builds the JobRequest from the job's region, data file and notification revisions.
     * Revisions that cannot be loaded fall back to the current region; duplicate regions are collapsed.
     */
    @Test
    public void testGetModelRunner_resolvesRevisions() throws Exception {
        JobInstance job = job(77, IncrementStrategy.increasing, TerminationPolicy.script);
        when(job.getJobRegionVersions()).thenReturn(versions(
                new EntityVersion(1, 1, JobRegion.class),   // revision found
                new EntityVersion(2, 1, JobRegion.class),   // revision found, duplicate region, expression users
                new EntityVersion(3, 1, JobRegion.class),   // revision lookup throws, current region found
                new EntityVersion(0, 4, JobRegion.class))); // no revision id, current region found (duplicate)
        when(job.getDataFileVersions()).thenReturn(versions(
                new EntityVersion(11, 2, DataFile.class),
                new EntityVersion(12, 2, DataFile.class)));
        when(job.getNotificationVersions()).thenReturn(versions(
                new EntityVersion(21, 3, JobNotification.class),
                new EntityVersion(22, 3, JobNotification.class)));
        DataFile dataFile = mock(DataFile.class);
        when(dataFile.getId()).thenReturn(11);
        JobNotification notification = mock(JobNotification.class);

        JobRequest request;
        try (MockedConstruction<JobRegionDao> regionDao = mockConstruction(JobRegionDao.class, (mock, context) -> {
                 when(mock.findRevision(1, 1)).thenReturn(new JobRegion(VMRegion.US_EAST, "10"));
                 when(mock.findRevision(2, 1)).thenReturn(new JobRegion(VMRegion.US_EAST, "2*5"));
                 when(mock.findRevision(3, 1)).thenThrow(new IllegalStateException("audit table missing"));
                 when(mock.findById(3)).thenReturn(new JobRegion(VMRegion.US_WEST_2, "5"));
                 when(mock.findById(0)).thenReturn(new JobRegion(VMRegion.US_WEST_2, "6"));
             });
             MockedConstruction<DataFileDao> dataFileDao = mockConstruction(DataFileDao.class,
                     (mock, context) -> when(mock.findRevision(11, 2)).thenReturn(dataFile));
             MockedConstruction<JobNotificationDao> notificationDao = mockConstruction(JobNotificationDao.class,
                     (mock, context) -> when(mock.findRevision(21, 3)).thenReturn(notification));
             MockedConstruction<WorkloadDao> workloadDao = mockConstruction(WorkloadDao.class)) {
            request = runnerFor(job).getJob();
        }

        Map<VMRegion, String> usersByRegion = usersByRegion(request);
        assertEquals(Set.of(VMRegion.US_EAST, VMRegion.US_WEST_2), usersByRegion.keySet());
        assertEquals("10", usersByRegion.get(VMRegion.US_EAST));
        assertEquals(Set.of(11), request.getDataFileIds());
        assertEquals(1, request.getNotifications().size());
        assertSame(notification, request.getNotifications().iterator().next());
        assertEquals("77", request.getId());
        // script-terminated linear jobs run until the scripts finish, not for a simulation time
        assertEquals(0, request.getSimulationTime());
        assertEquals(2, request.getNumAgents());
        assertEquals(5.0, request.getTargetRatePerAgent());
        assertTrue(request.getScriptsXmlUrl().contains("77"));
    }

    /**
     * When a region revision and its current row are both gone, the workload's current regions are used.
     */
    @Test
    public void testGetModelRunner_fallsBackToWorkloadRegions() throws Exception {
        JobInstance job = job(78, IncrementStrategy.increasing, TerminationPolicy.time);
        when(job.getWorkloadId()).thenReturn(5);
        when(job.getSimulationTime()).thenReturn(300L);
        when(job.getJobRegionVersions()).thenReturn(versions(
                new EntityVersion(9, 1, JobRegion.class),
                new EntityVersion(10, 1, JobRegion.class)));
        Set<JobRegion> currentRegions = new LinkedHashSet<>(List.of(
                new JobRegion(VMRegion.US_EAST, "3+2"), new JobRegion(VMRegion.US_WEST_2, "4")));
        JobConfiguration jobConfiguration = mock(JobConfiguration.class);
        when(jobConfiguration.getJobRegions()).thenReturn(currentRegions);
        Workload workload = mock(Workload.class);
        when(workload.getJobConfiguration()).thenReturn(jobConfiguration);

        JobRequest request;
        try (MockedConstruction<JobRegionDao> regionDao = mockConstruction(JobRegionDao.class);
             MockedConstruction<DataFileDao> dataFileDao = mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> notificationDao = mockConstruction(JobNotificationDao.class);
             MockedConstruction<WorkloadDao> workloadDao = mockConstruction(WorkloadDao.class,
                     (mock, context) -> when(mock.findById(5)).thenReturn(workload))) {
            request = runnerFor(job).getJob();
            // the first missing region stops the scan, so the second version is never looked up
            verify(regionDao.constructed().get(0), never()).findById(10);
        }

        Map<VMRegion, String> usersByRegion = usersByRegion(request);
        assertEquals("5", usersByRegion.get(VMRegion.US_EAST));
        assertEquals("4", usersByRegion.get(VMRegion.US_WEST_2));
        assertTrue(request.getDataFileIds().isEmpty());
        assertTrue(request.getNotifications().isEmpty());
        assertEquals(300L, request.getSimulationTime());
    }

    /**
     * Nonlinear script-terminated jobs keep their simulation time.
     */
    @Test
    public void testGetModelRunner_nonlinearScriptJobKeepsSimulationTime() throws Exception {
        JobInstance job = job(79, IncrementStrategy.standard, TerminationPolicy.script);
        when(job.getSimulationTime()).thenReturn(600L);

        JobRequest request;
        try (MockedConstruction<JobRegionDao> regionDao = mockConstruction(JobRegionDao.class);
             MockedConstruction<DataFileDao> dataFileDao = mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> notificationDao = mockConstruction(JobNotificationDao.class);
             MockedConstruction<WorkloadDao> workloadDao = mockConstruction(WorkloadDao.class)) {
            request = runnerFor(job).getJob();
        }

        assertEquals(600L, request.getSimulationTime());
        assertTrue(request.getRegions().isEmpty());
    }

    private IncreasingWorkLoad runnerFor(JobInstance job) throws Exception {
        WorkLoadFactory factory = new WorkLoadFactory();
        JobInstanceDao jobInstanceDao = mock(JobInstanceDao.class);
        when(jobInstanceDao.findById(job.getId())).thenReturn(job);
        setField(factory, "jobInstanceDao", jobInstanceDao);
        setField(factory, "agentDispatcher", mock(AgentDispatcher.class));
        setField(factory, "channel", mock(VMChannel.class));
        IncreasingWorkLoad runner = factory.getModelRunner(job.getId());
        assertNotNull(runner);
        return runner;
    }

    private JobInstance job(int id, IncrementStrategy strategy, TerminationPolicy terminationPolicy) {
        JobInstance job = mock(JobInstance.class);
        when(job.getId()).thenReturn(id);
        when(job.getIncrementStrategy()).thenReturn(strategy);
        when(job.getTerminationPolicy()).thenReturn(terminationPolicy);
        when(job.getExecutionTime()).thenReturn(0L);
        when(job.getTargetRampRate()).thenReturn(10.0);
        when(job.getTargetRatePerAgent()).thenReturn(5.0);
        return job;
    }

    private static Set<EntityVersion> versions(EntityVersion... versions) {
        return new LinkedHashSet<>(List.of(versions));
    }

    private static Map<VMRegion, String> usersByRegion(JobRequest request) {
        return request.getRegions().stream()
                .sorted(Comparator.comparing(RegionRequest::getRegion))
                .collect(Collectors.toMap(RegionRequest::getRegion, RegionRequest::getUsers));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
