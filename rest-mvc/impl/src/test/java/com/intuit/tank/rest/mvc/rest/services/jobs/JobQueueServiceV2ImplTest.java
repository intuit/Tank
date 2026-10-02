/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.jobs;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.Project;
import com.intuit.tank.reporting.api.ResultsReader;
import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.JobQueueEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.JobActionResult;
import com.intuit.tank.rest.mvc.rest.models.JobDetails;
import com.intuit.tank.rest.mvc.rest.models.JobLaunchRequest;
import com.intuit.tank.rest.mvc.rest.models.JobPreview;
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation;
import com.intuit.tank.rest.mvc.rest.models.QueuedJob;
import com.intuit.tank.rest.mvc.rest.models.Timeseries;
import com.intuit.tank.rest.mvc.rest.util.JobDetailFormatter;
import com.intuit.tank.rest.mvc.rest.util.JobInstanceFactory;
import com.intuit.tank.rest.mvc.rest.util.JobValidator;
import com.intuit.tank.rest.mvc.rest.util.ProjectFixtures;
import com.intuit.tank.rest.mvc.rest.util.ProjectValidator;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class JobQueueServiceV2ImplTest {

    @InjectMocks
    private JobQueueServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;
    private final List<AutoCloseable> statics = new ArrayList<>();
    private final VMTracker tracker = mock(VMTracker.class);
    private final JobEventSender events = mock(JobEventSender.class);
    private final JobQueueEventSender queueEvents = mock(JobQueueEventSender.class);
    private final Map<Integer, JobInstance> jobs = new HashMap<>();
    private final List<JobInstance> savedJobs = new ArrayList<>();
    private Project project;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        useConfig(true, Map.of(AccessRight.CONTROL_JOB, List.of("operators")));
        actAs(user("olivia", "operators"));
        project = ProjectFixtures.project(1, "Load", "alice");
        jobs.put(10, job(10, JobQueueStatus.Running, false));
        jobs.put(11, job(11, JobQueueStatus.Created, false));
        jobs.put(12, job(12, JobQueueStatus.Starting, true));
        jobs.put(13, job(13, JobQueueStatus.Deleted, false));

        statics.add(Mockito.mockConstruction(ProjectDao.class, (dao, ctx) -> {
            when(dao.findByIdEager(1)).thenReturn(project);
            when(dao.findById(1)).thenReturn(project);
        }));
        statics.add(Mockito.mockConstruction(JobInstanceDao.class, (dao, ctx) -> {
            when(dao.findById(anyInt())).thenAnswer(i -> jobs.get(i.<Integer>getArgument(0)));
            when(dao.saveOrUpdate(any())).thenAnswer(i -> {
                savedJobs.add(i.getArgument(0));
                return i.getArgument(0);
            });
        }));
        // JobAuthorization finds a job's project through its queue
        statics.add(Mockito.mockConstruction(JobQueueDao.class, (dao, ctx) -> {
            JobQueue queue = new JobQueue(1);
            when(dao.findForJobId(anyInt())).thenReturn(queue);
        }));
        statics.add(Mockito.mockConstruction(ServletInjector.class, (injector, ctx) -> {
            when(injector.getManagedBean(eq(servletContext), eq(VMTracker.class))).thenReturn(tracker);
            when(injector.getManagedBean(eq(servletContext), eq(JobEventSender.class))).thenReturn(events);
            when(injector.getManagedBean(eq(servletContext), eq(JobQueueEventSender.class))).thenReturn(queueEvents);
        }));
    }

    @AfterEach
    void tearDown() throws Exception {
        for (AutoCloseable c : statics) {
            c.close();
        }
        mocks.close();
        reset();
    }

    private static JobInstance job(int id, JobQueueStatus status, boolean twoStep) {
        JobInstance job = new JobInstance();
        job.setId(id);
        job.setName("job" + id);
        job.setStatus(status);
        job.setUseTwoStep(twoStep);
        job.setTotalVirtualUsers(100);
        job.setJobDetails("<b>details</b>");
        return job;
    }

    private static CloudVmStatus agent(String id, String jobId, VMStatus vmStatus, JobStatus jobStatus) {
        CloudVmStatus vm = new CloudVmStatus(id, jobId, "sg", jobStatus, VMImageType.AGENT, VMRegion.US_EAST, vmStatus,
                new ValidationStatus(), 50, 20, null, null);
        return vm;
    }

    // preview and queue

    private MockedStatic<JobInstanceFactory> stubFactory(List<String> errors) {
        JobInstance proposed = job(0, JobQueueStatus.Created, false);
        proposed.setName("Load_100_users_x");
        JobInstanceFactory.Proposal proposal = new JobInstanceFactory.Proposal(proposed, mock(JobValidator.class));
        MockedStatic<JobInstanceFactory> factory = Mockito.mockStatic(JobInstanceFactory.class);
        factory.when(() -> JobInstanceFactory.propose(any(), eq("Load"), any(), anyString())).thenReturn(proposal);
        factory.when(() -> JobInstanceFactory.queue(eq(1), any(), any())).thenAnswer(i -> {
            JobInstance j = i.getArgument(2);
            j.setId(500);
            return new JobInstanceFactory.Queued(j, new JobQueue(1));
        });
        MockedStatic<ProjectValidator> validator = Mockito.mockStatic(ProjectValidator.class);
        validator.when(() -> ProjectValidator.validate(eq(project), any(JobValidator.class))).thenReturn(
                new ProjectValidation(errors.isEmpty(), errors, List.of("Variable 'x' is set but never used."), 100,
                        1_800_000, 300_000, List.of(), List.of(), List.of("x"), List.of()));
        MockedStatic<JobDetailFormatter> details = Mockito.mockStatic(JobDetailFormatter.class);
        details.when(() -> JobDetailFormatter.createJobDetails(any(JobValidator.class), any(), any())).thenReturn("<p>job</p>");
        statics.add(validator);
        statics.add(details);
        return factory;
    }

    @Test
    void preview_describesTheJobWithoutQueueing() {
        try (MockedStatic<JobInstanceFactory> factory = stubFactory(List.of())) {
            JobPreview preview = service.previewJob(1, null);
            assertEquals("Load_100_users_x", preview.name());
            assertTrue(preview.valid());
            assertEquals(List.of("Variable 'x' is set but never used."), preview.warnings());
            assertEquals("<p>job</p>", preview.detailsHtml());
            factory.verify(() -> JobInstanceFactory.propose(any(), eq("Load"), isNull(), eq("olivia")));
            factory.verify(() -> JobInstanceFactory.queue(anyInt(), any(), any()), never());
        }
    }

    @Test
    void preview_validatesName() {
        try (MockedStatic<JobInstanceFactory> factory = stubFactory(List.of())) {
            assertThrows(GenericServiceBadRequestException.class, () -> service.previewJob(1, new JobLaunchRequest(" ")));
            assertThrows(GenericServiceBadRequestException.class,
                    () -> service.previewJob(1, new JobLaunchRequest("x".repeat(256))));
            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.previewJob(404, null));
        }
    }

    @Test
    void queue_savesJobAndAnnouncesIt() {
        try (MockedStatic<JobInstanceFactory> factory = stubFactory(List.of())) {
            QueuedJob queued = service.queueJob(1, new JobLaunchRequest(" nightly "));
            assertEquals(500, queued.jobId());
            assertEquals("Created", queued.status());
            factory.verify(() -> JobInstanceFactory.propose(any(), eq("Load"), eq("nightly"), eq("olivia")));
            verify(queueEvents).jobQueued(500);
        }
    }

    @Test
    void queue_refusedWhenProjectFailsValidation() {
        try (MockedStatic<JobInstanceFactory> factory = stubFactory(List.of("No users defined.", "No scripts defined."))) {
            GenericServiceBadRequestException e = assertThrows(GenericServiceBadRequestException.class,
                    () -> service.queueJob(1, null));
            assertTrue(e.getMessage().contains("No users defined.; No scripts defined."), e.getMessage());
            factory.verify(() -> JobInstanceFactory.queue(anyInt(), any(), any()), never());
            verifyNoInteractions(queueEvents);
        }
    }

    @Test
    void queue_needsControlRightOrOwnership() {
        try (MockedStatic<JobInstanceFactory> factory = stubFactory(List.of())) {
            actAs(user("bob"));
            assertThrows(GenericServiceForbiddenAccessException.class, () -> service.queueJob(1, null));
            actAs(user("alice"));
            assertEquals(500, service.queueJob(1, null).jobId(), "the project owner may queue");
        }
    }

    @Test
    void everythingNeedsAUser() {
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.previewJob(1, null));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.getJobTree(null, false));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.controlJob(10, "stop"));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.getJobDetails(10));
    }

    // job control

    @Test
    void controlJob_dispatchesEachAction() {
        assertEquals(new JobActionResult("10", "stop", "Running"), service.controlJob(10, "stop"));
        verify(events).stopJob("10");
        service.controlJob(10, "kill");
        verify(events).killJob("10");
        service.controlJob(10, "pause");
        verify(events).pauseJob("10");
        service.controlJob(10, "pause-ramp");
        verify(events).pauseRampJob("10");
        service.controlJob(11, "start");
        verify(events).startJob("11");
        service.controlJob(12, "start-load");
        verify(events).startAgents("12");
        jobs.get(10).setStatus(JobQueueStatus.Paused);
        service.controlJob(10, "resume");
        verify(events).restartJob("10");
        jobs.get(10).setStatus(JobQueueStatus.RampPaused);
        service.controlJob(10, "resume-ramp");
        verify(events).resumeRampJob("10");
    }

    @Test
    void controlJob_rejectsActionsTheStatusDoesNotAllow() {
        assertThrows(GenericServiceConflictException.class, () -> service.controlJob(11, "kill"));
        assertThrows(GenericServiceConflictException.class, () -> service.controlJob(10, "start"));
        assertThrows(GenericServiceConflictException.class, () -> service.controlJob(10, "resume"));
        jobs.get(12).setUseTwoStep(false);
        assertThrows(GenericServiceConflictException.class, () -> service.controlJob(12, "start-load"),
                "start-load is for two-step jobs");
        verifyNoInteractions(events);
    }

    @Test
    void controlJob_unknownActionAndJob() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.controlJob(10, "explode"));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.controlJob(404, "stop"));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.controlJob(13, "start"),
                "deleted jobs are not found");
    }

    @Test
    void controlJob_needsControlRightOrProjectOwnership() {
        actAs(user("bob"));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.controlJob(10, "stop"));
        actAs(user("alice"));
        assertDoesNotThrow(() -> service.controlJob(10, "stop"));
    }

    // agent control

    @Test
    void controlAgent_dispatchesAndChecksStatus() {
        when(tracker.getStatus("i-1")).thenReturn(agent("i-1", "10", VMStatus.running, JobStatus.Running));
        service.controlAgent("i-1", "pause");
        verify(events).pauseAgent("i-1");
        service.controlAgent("i-1", "stop");
        verify(events).stopAgent("i-1");
        service.controlAgent("i-1", "kill");
        verify(events).killInstance("i-1");
        service.controlAgent("i-1", "pause-ramp");
        verify(events).pauseRampInstance("i-1");
        assertThrows(GenericServiceConflictException.class, () -> service.controlAgent("i-1", "resume"));

        when(tracker.getStatus("i-2")).thenReturn(agent("i-2", "10", VMStatus.running, JobStatus.Paused));
        service.controlAgent("i-2", "resume");
        verify(events).restartAgent("i-2");
        when(tracker.getStatus("i-3")).thenReturn(agent("i-3", "10", VMStatus.rampPaused, JobStatus.RampPaused));
        service.controlAgent("i-3", "resume-ramp");
        verify(events).resumeRampInstance("i-3");
    }

    @Test
    void controlAgent_rejections() {
        when(tracker.getStatus("i-1")).thenReturn(agent("i-1", "10", VMStatus.running, JobStatus.Running));
        assertThrows(GenericServiceBadRequestException.class, () -> service.controlAgent("i-1", "start"));
        assertThrows(GenericServiceBadRequestException.class, () -> service.controlAgent("i-1", "start-load"));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.controlAgent("i-missing", "stop"));
        actAs(user("bob"));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.controlAgent("i-1", "stop"));
        verifyNoInteractions(events);
    }

    // delete

    @Test
    void deleteJob_onlyBeforeItStarts() {
        service.deleteJob(11);
        assertEquals(JobQueueStatus.Deleted, jobs.get(11).getStatus());
        assertEquals(List.of(jobs.get(11)), savedJobs);
        assertThrows(GenericServiceConflictException.class, () -> service.deleteJob(10));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.deleteJob(11), "already deleted");
        actAs(user("bob"));
        jobs.put(14, job(14, JobQueueStatus.Created, false));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.deleteJob(14));
    }

    // details and charts

    @Test
    void details_combineStoredAndLiveFigures() {
        CloudVmStatus a = agent("i-1", "10", VMStatus.running, JobStatus.Running);
        a.getValidationFailures().addAbort();
        when(tracker.getVmStatusForJob("10")).thenReturn(new CloudVmStatusContainer(new HashSet<>(Set.of(a))));
        JobDetails details = service.getJobDetails(10);
        assertEquals("job10", details.name());
        assertEquals("Running", details.status());
        assertEquals(20, details.activeUsers());
        assertEquals(100, details.totalUsers());
        assertEquals(1, details.failures().aborts());
        assertEquals("<b>details</b>", details.detailsHtml());
    }

    @Test
    void userSeriesWithoutLiveData() {
        assertEquals(new Timeseries(List.of(), List.of()), service.getUserSeries(10));
    }

    @Test
    void tpsSeries() {
        ResultsReader reader = mock(ResultsReader.class);
        Date t = new Date(5000);
        when(reader.getTpsMapForJob(new Date(4000), "10")).thenReturn(Map.of(t, Map.of("GET /", new TPSInfo(t, "GET /", 20, 10))));
        when(reader.getTpsMapForInstance(null, "10", "i-1")).thenReturn(Map.of());
        try (MockedStatic<ReportingFactory> factory = Mockito.mockStatic(ReportingFactory.class)) {
            factory.when(ReportingFactory::getResultsReader).thenReturn(reader);
            Timeseries series = service.getTpsSeries(10, null, 4000L);
            assertEquals(List.of(t), series.times());
            assertEquals(List.of(2), series.series().get(0).values());
            assertEquals(List.of(), service.getTpsSeries(10, "i-1", null).times());

            factory.when(ReportingFactory::getResultsReader).thenReturn(null);
            assertEquals(new Timeseries(List.of(), List.of()), service.getTpsSeries(10, null, null),
                    "no reader configured gives no data, not an error");
        }
    }

    @Test
    void tree() {
        when(tracker.getAllJobs()).thenReturn(Set.of());
        assertNotNull(service.getJobTree(1, false));
    }

    @Test
    void jobActionPaths() {
        assertEquals(JobAction.START_LOAD, JobAction.fromPath("start-load").orElseThrow());
        assertTrue(JobAction.fromPath("START").isEmpty());
        assertFalse(JobAction.START.isForAgents());
        assertTrue(JobAction.KILL.isForAgents());
    }
}
