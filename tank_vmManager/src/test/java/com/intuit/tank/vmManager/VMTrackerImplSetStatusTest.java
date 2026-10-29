package com.intuit.tank.vmManager;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests for the status-update path of VMTrackerImpl (setStatusThread / addStatusToJobContainer).
 * The private setStatusThread is invoked directly so the tests stay synchronous; the CDI
 * dependencies are mocks injected by reflection.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class VMTrackerImplSetStatusTest {

    private static final String JOB_ID = "42";

    @Mock
    private Event<JobEvent> jobEventProducer;
    @Mock
    private Instance<JobInstanceDao> jobInstanceDaoInstance;
    @Mock
    private Instance<WorkloadDao> workloadDaoInstance;
    @Mock
    private JobInstanceDao jobInstanceDao;
    @Mock
    private WorkloadDao workloadDao;

    private VMTrackerImpl vmTracker;

    @BeforeEach
    void setUp() throws Exception {
        vmTracker = new VMTrackerImpl();
        setField("jobEventProducer", jobEventProducer);
        setField("jobInstanceDao", jobInstanceDaoInstance);
        setField("workloadDaoInstance", workloadDaoInstance);
        when(jobInstanceDaoInstance.get()).thenReturn(jobInstanceDao);
        when(workloadDaoInstance.get()).thenReturn(workloadDao);
    }

    // ============ setStatusThread: new job container ============

    @Test
    @DisplayName("First running report creates the job and project containers and marks the job Running")
    void setStatus_firstRunningReport_createsContainersAndStartsJob() throws Exception {
        JobInstance job = job(JobQueueStatus.Starting);
        when(jobInstanceDao.findById(42)).thenReturn(job);
        mockProject(job, 7);

        CloudVmStatus status = new CloudVmStatus("i-1", JOB_ID, "sg-test", JobStatus.Running, VMImageType.AGENT,
                VMRegion.US_WEST_2, VMStatus.running, new ValidationStatus(), 10, 5, new Date(1000), null);

        try (MockedConstruction<JobInstanceDao> daoConstruction = mockConstruction(JobInstanceDao.class)) {
            invokeSetStatusThread(status);
            // the queue status changed from Starting to Running, so the new job is saved via a fresh dao
            verify(daoConstruction.constructed().get(0)).saveOrUpdate(job);
        }

        assertSame(status, vmTracker.getStatus("i-1"));
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(JOB_ID);
        assertNotNull(container);
        assertEquals(JobQueueStatus.Running, container.getStatus());
        assertTrue(container.getStatuses().contains(status));
        assertNotNull(container.getReportTime());
        assertTrue(vmTracker.getAllJobs().contains(container));

        verify(job).setStartTime(status.getStartTime());
        verify(job, atLeastOnce()).setStatus(JobQueueStatus.Running);
        verify(jobInstanceDao).saveOrUpdate(job);
        assertEquals(JobLifecycleEvent.LOAD_STARTED, firedEvents().get(0).getEvent());

        assertNotNull(vmTracker.getProjectStatusContainer("7"));
    }

    @Test
    @DisplayName("Unknown job id with an unmappable job status keeps the container's previous status")
    void setStatus_jobNotFound_unknownJobStatus_keepsOldQueueStatus() throws Exception {
        when(jobInstanceDao.findById(anyInt())).thenReturn(null);

        CloudVmStatus status = status("i-1", VMStatus.starting, JobStatus.Unknown);
        invokeSetStatusThread(status);

        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(JOB_ID);
        assertNotNull(container);
        // JobStatus.Unknown has no JobQueueStatus counterpart, so the container keeps its default status
        assertEquals(JobQueueStatus.Created, container.getStatus());
        // project lookup fails (job is null) and is cached as "no project"
        assertTrue(vmTracker.getAllJobs().contains(container));
        verify(jobEventProducer, never()).fire(any());

        // second report for the same job reuses the container and the cached (empty) project id
        invokeSetStatusThread(status("i-2", VMStatus.starting, JobStatus.Starting));
        assertEquals(2, vmTracker.getVmStatusForJob(JOB_ID).getStatuses().size());
        verify(workloadDao, never()).findById(any());
    }

    @Test
    @DisplayName("Existing job whose queue status already matches is not re-saved by a new dao")
    void setStatus_jobStatusUnchanged_noExtraSave() throws Exception {
        JobInstance job = job(JobQueueStatus.Starting);
        when(jobInstanceDao.findById(42)).thenReturn(job);
        when(workloadDao.findById(any())).thenThrow(new RuntimeException("db down"));

        try (MockedConstruction<JobInstanceDao> daoConstruction = mockConstruction(JobInstanceDao.class)) {
            invokeSetStatusThread(status("i-1", VMStatus.starting, JobStatus.Starting));
            assertTrue(daoConstruction.constructed().isEmpty());
        }
        assertEquals(JobQueueStatus.Starting, vmTracker.getVmStatusForJob(JOB_ID).getStatus());
        // project lookup failed, so no project container exists
        assertNull(vmTracker.getProjectStatusContainer("7"));
    }

    // ============ setStatusThread: terminal guard and completed instances ============

    @Test
    @DisplayName("Update for an instance already in a terminal state is skipped")
    void setStatus_currentTerminated_skipsStatusUpdate() throws Exception {
        when(jobInstanceDao.findById(anyInt())).thenReturn(null);
        CloudVmStatus terminated = status("i-1", VMStatus.terminated, JobStatus.Completed);
        invokeSetStatusThread(terminated);

        CloudVmStatus late = status("i-1", VMStatus.running, JobStatus.Running);
        invokeSetStatusThread(late);

        assertSame(terminated, vmTracker.getStatus("i-1"));
    }

    @Test
    @DisplayName("Completed job on a running VM kills the instance and finishes the job")
    void setStatus_completedOnRunningVm_killsInstanceAndFinishesJob() throws Exception {
        JobInstance job = job(JobQueueStatus.Running);
        when(jobInstanceDao.findById(42)).thenReturn(job);
        mockProject(job, 7);

        CloudVmStatus status = status("i-1", VMStatus.running, JobStatus.Completed);
        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class);
             MockedConstruction<JobInstanceDao> ignored = mockConstruction(JobInstanceDao.class)) {
            invokeSetStatusThread(status);
            verify(amazon.constructed().get(0)).killInstances(Collections.singletonList("i-1"));
        }

        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(JOB_ID);
        assertNotNull(container.getEndTime());
        assertEquals(JobLifecycleEvent.JOB_FINISHED, firedEvents().get(0).getEvent());
        verify(job, atLeastOnce()).setStatus(JobQueueStatus.Completed);
        assertFalse(vmTracker.isRunning(JOB_ID));

        // a second completed report does not fire JOB_FINISHED again
        try (MockedConstruction<AmazonInstance> ignored = mockConstruction(AmazonInstance.class)) {
            invokeSetStatusThread(status("i-2", VMStatus.running, JobStatus.Completed));
        }
        verify(jobEventProducer, times(1)).fire(any());
    }

    @Test
    @DisplayName("Completed job in dev mode does not kill the instance")
    void setStatus_completedInDevMode_doesNotKill() throws Exception {
        setField("devMode", true);
        when(jobInstanceDao.findById(anyInt())).thenReturn(null);

        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class)) {
            invokeSetStatusThread(status("i-1", VMStatus.running, JobStatus.Completed));
            assertTrue(amazon.constructed().isEmpty());
        }
        assertTrue(vmTracker.isDevMode());
    }

    // ============ addStatusToJobContainer: aggregate job status ============

    @Test
    @DisplayName("All agents paused marks the job Paused")
    void setStatus_allPaused_jobPaused() throws Exception {
        assertAggregateStatus(JobStatus.Paused, JobQueueStatus.Paused);
    }

    @Test
    @DisplayName("All agents ramp-paused marks the job RampPaused")
    void setStatus_allRampPaused_jobRampPaused() throws Exception {
        assertAggregateStatus(JobStatus.RampPaused, JobQueueStatus.RampPaused);
    }

    @Test
    @DisplayName("All agents stopped marks the job Stopped")
    void setStatus_allStopped_jobStopped() throws Exception {
        assertAggregateStatus(JobStatus.Stopped, JobQueueStatus.Stopped);
    }

    @Test
    @DisplayName("Mixed agent statuses leave the job's queue status unchanged")
    void setStatus_mixedStatuses_jobStatusUnchanged() throws Exception {
        JobInstance job = job(JobQueueStatus.Starting);
        when(jobInstanceDao.findById(42)).thenReturn(job);
        prepopulateContainer(null);

        invokeSetStatusThread(status("i-1", VMStatus.running, JobStatus.Running));
        invokeSetStatusThread(status("i-2", VMStatus.starting, JobStatus.Starting));

        // first report: the only agent is running; second report: agents disagree so the job keeps its status
        verify(job).setStatus(JobQueueStatus.Running);
        verify(job).setStatus(JobQueueStatus.Starting);
    }

    @Test
    @DisplayName("Report for a replaced agent with no other active agents skips the status calculation")
    void setStatus_onlyReplacedAgents_skipsCalculation() throws Exception {
        JobInstance job = job(JobQueueStatus.Running);
        when(jobInstanceDao.findById(42)).thenReturn(job);
        prepopulateContainer(null);

        invokeSetStatusThread(status("i-1", VMStatus.replaced, JobStatus.Stopped));

        verify(job, never()).setStatus(any());
        verify(jobInstanceDao, never()).saveOrUpdate(any());
    }

    @Test
    @DisplayName("An earlier job start time overrides the container start time")
    void setStatus_jobStartedEarlier_updatesContainerStartTime() throws Exception {
        JobInstance job = job(JobQueueStatus.Running);
        Date jobStart = new Date(1_000);
        when(job.getStartTime()).thenReturn(jobStart);
        when(jobInstanceDao.findById(42)).thenReturn(job);
        CloudVmStatusContainer container = prepopulateContainer(new Date(5_000));

        invokeSetStatusThread(status("i-1", VMStatus.running, JobStatus.Running));

        assertEquals(jobStart, container.getStartTime());
        // the job already has a start time, so LOAD_STARTED is not fired again
        verify(jobEventProducer, never()).fire(any());
    }

    @Test
    @DisplayName("A later job start time leaves the container start time alone")
    void setStatus_jobStartedLater_keepsContainerStartTime() throws Exception {
        JobInstance job = job(JobQueueStatus.Running);
        when(job.getStartTime()).thenReturn(new Date(9_000));
        when(jobInstanceDao.findById(42)).thenReturn(job);
        Date containerStart = new Date(5_000);
        CloudVmStatusContainer container = prepopulateContainer(containerStart);

        invokeSetStatusThread(status("i-1", VMStatus.running, JobStatus.Running));

        assertEquals(containerStart, container.getStartTime());
    }

    // ============ async setStatus, publishEvent, removeStatusForJob, init ============

    @Test
    @DisplayName("setStatus applies the update on the background executor")
    void setStatus_async_updatesStatusMap() throws Exception {
        when(jobInstanceDao.findById(anyInt())).thenReturn(null);
        CloudVmStatus status = status("i-async", VMStatus.starting, JobStatus.Starting);

        vmTracker.setStatus(status);

        long deadline = System.currentTimeMillis() + 5_000;
        while (vmTracker.getStatus("i-async") == null && System.currentTimeMillis() < deadline) {
            Thread.sleep(10);
        }
        assertSame(status, vmTracker.getStatus("i-async"));
    }

    @Test
    @DisplayName("publishEvent fires the event and swallows producer failures")
    void publishEvent_firesAndSwallowsErrors() {
        JobEvent event = new JobEvent(JOB_ID, "msg", JobLifecycleEvent.AGENT_STARTED);
        vmTracker.publishEvent(event);
        verify(jobEventProducer).fire(event);

        doThrow(new IllegalStateException("no observers")).when(jobEventProducer).fire(any());
        assertDoesNotThrow(() -> vmTracker.publishEvent(event));
    }

    @Test
    @DisplayName("removeStatusForJob removes the job container and all its instance statuses")
    void removeStatusForJob_removesInstancesAndJob() throws Exception {
        when(jobInstanceDao.findById(anyInt())).thenReturn(null);
        invokeSetStatusThread(status("i-1", VMStatus.starting, JobStatus.Starting));
        invokeSetStatusThread(status("i-2", VMStatus.starting, JobStatus.Starting));

        vmTracker.removeStatusForJob(JOB_ID);

        assertNull(vmTracker.getVmStatusForJob(JOB_ID));
        assertNull(vmTracker.getStatus("i-1"));
        assertNull(vmTracker.getStatus("i-2"));
    }

    @Test
    @DisplayName("init reads dev mode from the Tank configuration")
    void init_readsStandaloneFlag() {
        vmTracker.init();
        // the test settings are not standalone
        assertFalse(vmTracker.isDevMode());
    }

    // ============ helpers ============

    private void assertAggregateStatus(JobStatus agentStatus, JobQueueStatus expected) throws Exception {
        JobInstance job = job(JobQueueStatus.Running);
        when(jobInstanceDao.findById(42)).thenReturn(job);
        prepopulateContainer(null);

        invokeSetStatusThread(status("i-1", VMStatus.running, agentStatus));
        invokeSetStatusThread(status("i-2", VMStatus.running, agentStatus));

        verify(job, atLeastOnce()).setStatus(expected);
        verify(jobInstanceDao, atLeastOnce()).saveOrUpdate(job);
    }

    private JobInstance job(JobQueueStatus initial) {
        JobInstance job = mock(JobInstance.class);
        when(job.getId()).thenReturn(42);
        when(job.getStatus()).thenReturn(initial);
        when(job.getWorkloadId()).thenReturn(3);
        return job;
    }

    private void mockProject(JobInstance job, int projectId) {
        Workload workload = mock(Workload.class);
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(projectId);
        when(workload.getProject()).thenReturn(project);
        when(workloadDao.findById(job.getWorkloadId())).thenReturn(workload);
    }

    @SuppressWarnings("unchecked")
    private CloudVmStatusContainer prepopulateContainer(Date startTime) throws Exception {
        CloudVmStatusContainer container = new CloudVmStatusContainer();
        container.setJobId(JOB_ID);
        container.setStartTime(startTime);
        ((Map<String, CloudVmStatusContainer>) getField("jobMap")).put(JOB_ID, container);
        return container;
    }

    private List<JobEvent> firedEvents() {
        ArgumentCaptor<JobEvent> captor = ArgumentCaptor.forClass(JobEvent.class);
        verify(jobEventProducer, atLeastOnce()).fire(captor.capture());
        return captor.getAllValues();
    }

    private static CloudVmStatus status(String instanceId, VMStatus vmStatus, JobStatus jobStatus) {
        return new CloudVmStatus(instanceId, JOB_ID, "sg-test", jobStatus, VMImageType.AGENT,
                VMRegion.US_WEST_2, vmStatus, new ValidationStatus(), 10, 5, null, null);
    }

    private void invokeSetStatusThread(CloudVmStatus status) throws Exception {
        Method method = VMTrackerImpl.class.getDeclaredMethod("setStatusThread", CloudVmStatus.class);
        method.setAccessible(true);
        try {
            method.invoke(vmTracker, status);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private void setField(String name, Object value) throws Exception {
        Field field = VMTrackerImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(vmTracker, value);
    }

    private Object getField(String name) throws Exception {
        Field field = VMTrackerImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(vmTracker);
    }
}
