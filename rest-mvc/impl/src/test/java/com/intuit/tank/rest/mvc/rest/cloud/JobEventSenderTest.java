package com.intuit.tank.rest.mvc.rest.cloud;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.perfManager.AgentChannel;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import com.intuit.tank.vm.vmManager.VMTerminator;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

import jakarta.enterprise.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JobEventSender
 */
@ExtendWith(MockitoExtension.class)
public class JobEventSenderTest {

    @Mock
    private VMTracker vmTracker;

    @Mock
    private VMTerminator terminator;

    @Mock
    private JobManager jobManager;

    @Mock
    private AgentChannel agentChannel;

    @Mock
    private Event<JobEvent> jobEventProducer;

    @InjectMocks
    private JobEventSender jobEventSender;

    private CloudVmStatusContainer container;

    @BeforeEach
    void setUp() {
        container = new CloudVmStatusContainer();
        container.setJobId("123");
    }

    // Helper method to create CloudVmStatus
    private CloudVmStatus createStatus(String instanceId, JobStatus jobStatus, VMStatus vmStatus) {
        return new CloudVmStatus(
                instanceId,
                "123",
                "sg-test",
                jobStatus,
                VMImageType.AGENT,
                VMRegion.US_WEST_2,
                vmStatus,
                new ValidationStatus(),
                100,
                50,
                new Date(),
                null
        );
    }

    /**
     * Use reflection to test the private getInstancesForJob method.
     */
    private List<String> invokeGetInstancesForJob(String jobId) throws Exception {
        Method method = JobEventSender.class.getDeclaredMethod("getInstancesForJob", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) method.invoke(jobEventSender, jobId);
        return result;
    }

    // =====================================================================
    // getInstancesForJob tests (existing)
    // =====================================================================

    @Test
    @DisplayName("getInstancesForJob excludes terminated instances (no longer exist on AWS)")
    void getInstancesForJob_excludesTerminatedInstances() throws Exception {
        // Given: Mix of running and terminated instances
        container.getStatuses().add(createStatus("i-running1", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-terminated", JobStatus.Stopped, VMStatus.terminated));
        container.getStatuses().add(createStatus("i-running2", JobStatus.Running, VMStatus.running));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: Terminated instances excluded (no longer exist on AWS)
        assertEquals(2, instances.size());
        assertTrue(instances.contains("i-running1"));
        assertTrue(instances.contains("i-running2"));
        assertFalse(instances.contains("i-terminated"));
    }

    @Test
    @DisplayName("getInstancesForJob includes stopped instances (EC2 still exists)")
    void getInstancesForJob_includesStoppedInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-stopped", JobStatus.Stopped, VMStatus.stopped));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: stopped instances are included (EC2 still exists, needs termination)
        assertEquals(2, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-stopped"));
    }

    @Test
    @DisplayName("getInstancesForJob includes stopping instances (EC2 still exists)")
    void getInstancesForJob_includesStoppingInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-stopping", JobStatus.Stopped, VMStatus.stopping));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: stopping instances are included (EC2 still exists)
        assertEquals(2, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-stopping"));
    }

    @Test
    @DisplayName("getInstancesForJob includes shutting_down instances (EC2 still exists)")
    void getInstancesForJob_includesShuttingDownInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-shuttingdown", JobStatus.Stopped, VMStatus.shutting_down));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: shutting_down instances are included (EC2 still exists)
        assertEquals(2, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-shuttingdown"));
    }

    @Test
    @DisplayName("getInstancesForJob includes pending instances (agents starting)")
    void getInstancesForJob_includesPendingInstances() throws Exception {
        // Given: Pending instances are still reachable
        container.getStatuses().add(createStatus("i-pending", JobStatus.Starting, VMStatus.pending));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: Pending instances should be included
        assertEquals(1, instances.size());
        assertTrue(instances.contains("i-pending"));
    }

    @Test
    @DisplayName("getInstancesForJob includes starting instances")
    void getInstancesForJob_includesStartingInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-starting", JobStatus.Starting, VMStatus.starting));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then
        assertEquals(1, instances.size());
        assertTrue(instances.contains("i-starting"));
    }

    @Test
    @DisplayName("getInstancesForJob returns empty list for non-existent job")
    void getInstancesForJob_returnsEmptyForNonExistent() throws Exception {
        // Given
        when(vmTracker.getVmStatusForJob("999")).thenReturn(null);

        // When
        List<String> instances = invokeGetInstancesForJob("999");

        // Then
        assertNotNull(instances);
        assertTrue(instances.isEmpty());
    }

    @Test
    @DisplayName("getInstancesForJob excludes replaced instances (can't receive HTTP commands)")
    void getInstancesForJob_excludesReplacedInstances() throws Exception {
        // Given: VMStatus.replaced is set by AgentWatchdog when an agent fails and is replaced
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-replaced", JobStatus.Starting, VMStatus.replaced));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: Replaced instances filtered out (they can't receive HTTP commands)
        assertEquals(1, instances.size());
        assertTrue(instances.contains("i-running"));
        assertFalse(instances.contains("i-replaced"));
    }

    @Test
    @DisplayName("getInstancesForJob excludes replaced instances in AgentWatchdog scenario")
    void getInstancesForJob_agentWatchdogScenario() throws Exception {
        // Given: Real-world scenario where AgentWatchdog replaced some agents
        container.getStatuses().add(createStatus("i-healthy1", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-healthy2", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-healthy3", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-replaced1", JobStatus.Starting, VMStatus.replaced));
        container.getStatuses().add(createStatus("i-replaced2", JobStatus.Starting, VMStatus.replaced));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When: User tries to kill the job
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: Only healthy instances returned (replaced no longer exist on AWS)
        assertEquals(3, instances.size());
        assertTrue(instances.contains("i-healthy1"));
        assertTrue(instances.contains("i-healthy2"));
        assertTrue(instances.contains("i-healthy3"));
        assertFalse(instances.contains("i-replaced1"));
        assertFalse(instances.contains("i-replaced2"));
    }

    @Test
    @DisplayName("getInstancesForJob excludes replaced and terminated, keeps other statuses")
    void getInstancesForJob_excludesReplacedAndTerminated() throws Exception {
        // Given: One instance of each state
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-terminated", JobStatus.Completed, VMStatus.terminated));
        container.getStatuses().add(createStatus("i-replaced", JobStatus.Starting, VMStatus.replaced));
        container.getStatuses().add(createStatus("i-stopped", JobStatus.Stopped, VMStatus.stopped));
        container.getStatuses().add(createStatus("i-stopping", JobStatus.Stopped, VMStatus.stopping));
        container.getStatuses().add(createStatus("i-shutting-down", JobStatus.Stopped, VMStatus.shutting_down));

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        // When
        List<String> instances = invokeGetInstancesForJob("123");

        // Then: Excludes replaced and terminated (no longer exist on AWS)
        assertEquals(4, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-stopped"));
        assertTrue(instances.contains("i-stopping"));
        assertTrue(instances.contains("i-shutting-down"));
        assertFalse(instances.contains("i-terminated"));
        assertFalse(instances.contains("i-replaced"));
    }

    @Test
    @DisplayName("getVmStatus delegates to vmTracker")
    void getVmStatus_delegatesToVmTracker() {
        String instanceId = "i-test123";
        CloudVmStatus expectedStatus = createStatus(instanceId, JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus(instanceId)).thenReturn(expectedStatus);

        CloudVmStatus result = jobEventSender.getVmStatus(instanceId);

        assertEquals(expectedStatus, result);
        verify(vmTracker).getStatus(instanceId);
    }

    @Test
    @DisplayName("getVmStatusForJob delegates to vmTracker")
    void getVmStatusForJob_delegatesToVmTracker() {
        String jobId = "123";
        when(vmTracker.getVmStatusForJob(jobId)).thenReturn(container);

        CloudVmStatusContainer result = jobEventSender.getVmStatusForJob(jobId);

        assertEquals(container, result);
        verify(vmTracker).getVmStatusForJob(jobId);
    }

    // =====================================================================
    // startJob tests
    // =====================================================================

    @Test
    @DisplayName("startJob with Created status starts the job")
    void startJob_createdStatus_startsJob() {
        JobInstance job = new JobInstance();
        job.setId(123);
        job.setStatus(JobQueueStatus.Created);

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);

        try (MockedStatic<AWSXRay> xray = Mockito.mockStatic(AWSXRay.class);
             MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> {
                         when(mock.findById(123)).thenReturn(job);
                         when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                     });
             MockedStatic<ProjectDaoUtil> projectDaoUtil = Mockito.mockStatic(ProjectDaoUtil.class)) {

            Subsegment mockSubsegment = mock(Subsegment.class);
            xray.when(() -> AWSXRay.beginSubsegment(anyString())).thenReturn(mockSubsegment);

            projectDaoUtil.when(() -> ProjectDaoUtil.getScriptFile("123")).thenReturn(mockFile);

            String result = jobEventSender.startJob("123");

            assertEquals("123", result);
            assertEquals(JobQueueStatus.Starting, job.getStatus());
            verify(vmTracker).removeStatusForJob("123");
            verify(jobManager).startJob(123);
            verify(jobEventProducer).fire(any(JobEvent.class));
            xray.verify(() -> AWSXRay.beginSubsegment("Start.Job.123"));
            xray.verify(AWSXRay::endSubsegment);
        }
    }

    @Test
    @DisplayName("startJob with Created status creates script file when it does not exist")
    void startJob_createdStatus_createsScriptFile() {
        JobInstance job = new JobInstance();
        job.setId(123);
        job.setStatus(JobQueueStatus.Created);

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);

        Workload mockWorkload = mock(Workload.class);
        HDWorkload mockHdWorkload = mock(HDWorkload.class);

        try (MockedStatic<AWSXRay> xray = Mockito.mockStatic(AWSXRay.class);
             MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> {
                         when(mock.findById(123)).thenReturn(job);
                         when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                     });
             MockedConstruction<WorkloadDao> workloadDaoConstruction = Mockito.mockConstruction(WorkloadDao.class,
                     (mock, context) -> {
                         when(mock.findById(anyInt())).thenReturn(mockWorkload);
                     });
             MockedStatic<ProjectDaoUtil> projectDaoUtil = Mockito.mockStatic(ProjectDaoUtil.class);
             MockedStatic<ConverterUtil> converterUtil = Mockito.mockStatic(ConverterUtil.class)) {

            Subsegment mockSubsegment = mock(Subsegment.class);
            xray.when(() -> AWSXRay.beginSubsegment(anyString())).thenReturn(mockSubsegment);

            projectDaoUtil.when(() -> ProjectDaoUtil.getScriptFile("123")).thenReturn(mockFile);
            projectDaoUtil.when(() -> ProjectDaoUtil.storeScriptFile(anyString(), any())).thenAnswer(inv -> null);

            converterUtil.when(() -> ConverterUtil.convertWorkload(any(Workload.class), any(JobInstance.class)))
                    .thenReturn(mockHdWorkload);
            converterUtil.when(() -> ConverterUtil.getWorkloadXML(any(HDWorkload.class)))
                    .thenReturn("<xml>script</xml>");

            String result = jobEventSender.startJob("123");

            assertEquals("123", result);
            projectDaoUtil.verify(() -> ProjectDaoUtil.storeScriptFile(eq("123"), eq("<xml>script</xml>")));
        }
    }

    @Test
    @DisplayName("startJob with non-Created status does not start")
    void startJob_nonCreatedStatus_doesNotStart() {
        JobInstance job = new JobInstance();
        job.setId(123);
        job.setStatus(JobQueueStatus.Running);

        try (MockedStatic<AWSXRay> xray = Mockito.mockStatic(AWSXRay.class);
             MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.findById(123)).thenReturn(job))) {

            Subsegment mockSubsegment = mock(Subsegment.class);
            xray.when(() -> AWSXRay.beginSubsegment(anyString())).thenReturn(mockSubsegment);

            String result = jobEventSender.startJob("123");

            assertEquals("123", result);
            verify(jobManager, never()).startJob(anyInt());
            verify(jobEventProducer, never()).fire(any(JobEvent.class));
        }
    }

    // =====================================================================
    // startAgents tests
    // =====================================================================

    @Test
    @DisplayName("startAgents with Starting status starts agents")
    void startAgents_startingStatus_startsAgents() {
        JobInstance job = new JobInstance();
        job.setId(123);
        job.setStatus(JobQueueStatus.Starting);

        try (MockedStatic<AWSXRay> xray = Mockito.mockStatic(AWSXRay.class);
             MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.findById(123)).thenReturn(job))) {

            Subsegment mockSubsegment = mock(Subsegment.class);
            xray.when(() -> AWSXRay.beginSubsegment(anyString())).thenReturn(mockSubsegment);

            String result = jobEventSender.startAgents("123");

            assertEquals("123", result);
            verify(jobManager).startAgents("123");
            verify(jobEventProducer).fire(any(JobEvent.class));
            xray.verify(() -> AWSXRay.beginSubsegment("Start.Agents.123"));
            xray.verify(AWSXRay::endSubsegment);
        }
    }

    @Test
    @DisplayName("startAgents with non-Starting status does not start agents but still fires event")
    void startAgents_nonStartingStatus_firesEventOnly() {
        JobInstance job = new JobInstance();
        job.setId(123);
        job.setStatus(JobQueueStatus.Running);

        try (MockedStatic<AWSXRay> xray = Mockito.mockStatic(AWSXRay.class);
             MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.findById(123)).thenReturn(job))) {

            Subsegment mockSubsegment = mock(Subsegment.class);
            xray.when(() -> AWSXRay.beginSubsegment(anyString())).thenReturn(mockSubsegment);

            String result = jobEventSender.startAgents("123");

            assertEquals("123", result);
            verify(jobManager, never()).startAgents(anyString());
            // Event still fires outside the if block
            verify(jobEventProducer).fire(any(JobEvent.class));
        }
    }

    // =====================================================================
    // killJob tests
    // =====================================================================

    @Test
    @DisplayName("killJob with empty instances sets job to Completed")
    void killJob_emptyInstances_setsCompleted() {
        // No instances for job
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        JobInstance job = new JobInstance();
        job.setId(123);
        job.setStatus(JobQueueStatus.Running);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(job);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                })) {

            jobEventSender.killJob("123", true);

            verify(vmTracker).stopJob("123");
            assertEquals(JobQueueStatus.Completed, job.getStatus());
            assertNotNull(job.getEndTime());
            verify(jobEventProducer).fire(any(JobEvent.class));
        }
    }

    @Test
    @DisplayName("killJob with null job from DAO does not throw")
    void killJob_nullJob_doesNotThrow() {
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> when(mock.findById(123)).thenReturn(null))) {

            jobEventSender.killJob("123", true);

            verify(vmTracker).stopJob("123");
            verify(jobEventProducer).fire(any(JobEvent.class));
        }
    }

    @Test
    @DisplayName("killJob with fireEvent=false does not fire event")
    void killJob_fireEventFalse_noEvent() {
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        JobInstance job = new JobInstance();
        job.setId(123);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(job);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                })) {

            jobEventSender.killJob("123", false);

            verify(vmTracker).stopJob("123");
            verify(jobEventProducer, never()).fire(any(JobEvent.class));
        }
    }

    @Test
    @DisplayName("killJob with instances calls killInstances instead of setting Completed")
    void killJob_withInstances_callsKillInstances() {
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        // For killInstances: need to mock isDevMode
        when(vmTracker.isDevMode()).thenReturn(true);
        CloudVmStatus existingStatus = createStatus("i-running", JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus("i-running")).thenReturn(existingStatus);

        jobEventSender.killJob("123", true);

        verify(vmTracker).stopJob("123");
        verify(agentChannel).killAgents(anyList());
        verify(jobEventProducer).fire(any(JobEvent.class));
    }

    @Test
    @DisplayName("killJob(jobId) delegates to killJob(jobId, true)")
    void killJob_singleArg_delegatesWithFireEvent() {
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        JobInstance job = new JobInstance();
        job.setId(123);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(job);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                })) {

            jobEventSender.killJob("123");

            verify(vmTracker).stopJob("123");
            // fire event is called because killJob(jobId) calls killJob(jobId, true)
            verify(jobEventProducer).fire(any(JobEvent.class));
        }
    }

    // =====================================================================
    // killAllJobs tests
    // =====================================================================

    @Test
    @DisplayName("killAllJobs kills all tracked jobs")
    void killAllJobs_killsAllTrackedJobs() {
        CloudVmStatusContainer job1 = new CloudVmStatusContainer();
        job1.setJobId("100");
        CloudVmStatusContainer job2 = new CloudVmStatusContainer();
        job2.setJobId("200");

        Set<CloudVmStatusContainer> allJobs = new LinkedHashSet<>(Arrays.asList(job1, job2));
        when(vmTracker.getAllJobs()).thenReturn(allJobs);
        // getInstancesForJob returns empty for both
        when(vmTracker.getVmStatusForJob("100")).thenReturn(null);
        when(vmTracker.getVmStatusForJob("200")).thenReturn(null);

        JobInstance ji1 = new JobInstance();
        ji1.setId(100);
        JobInstance ji2 = new JobInstance();
        ji2.setId(200);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(100)).thenReturn(ji1);
                    when(mock.findById(200)).thenReturn(ji2);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                })) {

            Set<CloudVmStatusContainer> result = jobEventSender.killAllJobs();

            assertEquals(2, result.size());
            verify(vmTracker).stopJob("100");
            verify(vmTracker).stopJob("200");
            verify(jobEventProducer, times(2)).fire(any(JobEvent.class));
        }
    }

    @Test
    @DisplayName("killAllJobs with no jobs returns empty set")
    void killAllJobs_noJobs_returnsEmptySet() {
        when(vmTracker.getAllJobs()).thenReturn(Collections.emptySet());

        Set<CloudVmStatusContainer> result = jobEventSender.killAllJobs();

        assertTrue(result.isEmpty());
        verify(vmTracker, never()).stopJob(anyString());
    }

    // =====================================================================
    // killInstance / killInstances tests
    // =====================================================================

    @Test
    @DisplayName("killInstance delegates to killInstances with singleton list")
    void killInstance_delegatesToKillInstances() {
        when(vmTracker.isDevMode()).thenReturn(true);
        CloudVmStatus existingStatus = createStatus("i-test", JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus("i-test")).thenReturn(existingStatus);
        // checkJobStatus will be called
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        jobEventSender.killInstance("i-test");

        verify(agentChannel).killAgents(Collections.singletonList("i-test"));
    }

    @Test
    @DisplayName("killInstances in dev mode skips AWS termination")
    void killInstances_devMode_skipsAwsTermination() {
        when(vmTracker.isDevMode()).thenReturn(true);
        CloudVmStatus existingStatus = createStatus("i-dev", JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus("i-dev")).thenReturn(existingStatus);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        jobEventSender.killInstances(Collections.singletonList("i-dev"));

        verify(agentChannel).killAgents(Collections.singletonList("i-dev"));
        verify(vmTracker).setStatus(argThat(status ->
                status.getVmStatus() == VMStatus.terminated &&
                status.getJobStatus() == JobStatus.Completed &&
                status.getCurrentUsers() == 0 &&
                status.getEndTime() != null
        ));
    }

    @Test
    @DisplayName("killInstances in non-dev mode terminates on AWS")
    void killInstances_nonDevMode_terminatesOnAws() {
        when(vmTracker.isDevMode()).thenReturn(false);
        CloudVmStatus existingStatus = createStatus("i-prod", JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus("i-prod")).thenReturn(existingStatus);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        VmManagerConfig mockVmConfig = mock(VmManagerConfig.class);
        when(mockVmConfig.getRegions()).thenReturn(Collections.singleton(VMRegion.US_WEST_2));

        try (MockedConstruction<TankConfig> tankConfigConstruction = Mockito.mockConstruction(TankConfig.class,
                     (mock, context) -> when(mock.getVmManagerConfig()).thenReturn(mockVmConfig));
             MockedConstruction<AmazonInstance> amazonConstruction = Mockito.mockConstruction(AmazonInstance.class)) {

            jobEventSender.killInstances(Collections.singletonList("i-prod"));

            verify(agentChannel).killAgents(Collections.singletonList("i-prod"));
            // Verify AmazonInstance was constructed and killInstances called
            assertEquals(1, amazonConstruction.constructed().size());
            verify(amazonConstruction.constructed().get(0)).killInstances(Collections.singletonList("i-prod"));
        }
    }

    @Test
    @DisplayName("killInstances with null existing status skips status update")
    void killInstances_nullExistingStatus_skipsUpdate() {
        when(vmTracker.isDevMode()).thenReturn(true);
        when(vmTracker.getStatus("i-gone")).thenReturn(null);

        jobEventSender.killInstances(Collections.singletonList("i-gone"));

        verify(agentChannel).killAgents(Collections.singletonList("i-gone"));
        verify(vmTracker, never()).setStatus(any(CloudVmStatus.class));
    }

    @Test
    @DisplayName("killInstances with multiple instances updates all statuses")
    void killInstances_multipleInstances_updatesAll() {
        when(vmTracker.isDevMode()).thenReturn(true);
        CloudVmStatus status1 = createStatus("i-1", JobStatus.Running, VMStatus.running);
        CloudVmStatus status2 = createStatus("i-2", JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus("i-1")).thenReturn(status1);
        when(vmTracker.getStatus("i-2")).thenReturn(status2);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        jobEventSender.killInstances(Arrays.asList("i-1", "i-2"));

        verify(agentChannel).killAgents(Arrays.asList("i-1", "i-2"));
        verify(vmTracker, times(2)).setStatus(any(CloudVmStatus.class));
    }

    @Test
    @DisplayName("killInstances with multiple regions terminates in each region")
    void killInstances_multipleRegions_terminatesInEach() {
        when(vmTracker.isDevMode()).thenReturn(false);
        CloudVmStatus existingStatus = createStatus("i-multi", JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus("i-multi")).thenReturn(existingStatus);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        VmManagerConfig mockVmConfig = mock(VmManagerConfig.class);
        Set<VMRegion> regions = new LinkedHashSet<>();
        regions.add(VMRegion.US_WEST_2);
        regions.add(VMRegion.US_EAST);
        when(mockVmConfig.getRegions()).thenReturn(regions);

        try (MockedConstruction<TankConfig> tankConfigConstruction = Mockito.mockConstruction(TankConfig.class,
                     (mock, context) -> when(mock.getVmManagerConfig()).thenReturn(mockVmConfig));
             MockedConstruction<AmazonInstance> amazonConstruction = Mockito.mockConstruction(AmazonInstance.class)) {

            jobEventSender.killInstances(Collections.singletonList("i-multi"));

            // AmazonInstance constructed once per region
            assertEquals(2, amazonConstruction.constructed().size());
            for (AmazonInstance ai : amazonConstruction.constructed()) {
                verify(ai).killInstances(Collections.singletonList("i-multi"));
            }
        }
    }

    // =====================================================================
    // stopAllJobs / stopJob / stopAgent / stopAgents tests
    // =====================================================================

    @Test
    @DisplayName("stopAllJobs stops all tracked jobs")
    void stopAllJobs_stopsAllTrackedJobs() {
        CloudVmStatusContainer job1 = new CloudVmStatusContainer();
        job1.setJobId("100");
        job1.getStatuses().add(createStatus("i-1", JobStatus.Running, VMStatus.running));
        CloudVmStatusContainer job2 = new CloudVmStatusContainer();
        job2.setJobId("200");
        job2.getStatuses().add(createStatus("i-2", JobStatus.Running, VMStatus.running));

        Set<CloudVmStatusContainer> allJobs = new LinkedHashSet<>(Arrays.asList(job1, job2));
        when(vmTracker.getAllJobs()).thenReturn(allJobs);
        when(vmTracker.getVmStatusForJob("100")).thenReturn(job1);
        when(vmTracker.getVmStatusForJob("200")).thenReturn(job2);

        Set<CloudVmStatusContainer> result = jobEventSender.stopAllJobs();

        assertEquals(2, result.size());
        verify(vmTracker).stopJob("100");
        verify(vmTracker).stopJob("200");
        verify(agentChannel).stopAgents(Collections.singletonList("i-1"));
        verify(agentChannel).stopAgents(Collections.singletonList("i-2"));
        verify(jobEventProducer, times(2)).fire(any(JobEvent.class));
    }

    @Test
    @DisplayName("stopAllJobs with no jobs returns empty set")
    void stopAllJobs_noJobs_returnsEmptySet() {
        when(vmTracker.getAllJobs()).thenReturn(Collections.emptySet());

        Set<CloudVmStatusContainer> result = jobEventSender.stopAllJobs();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("stopJob stops agents and fires event")
    void stopJob_stopsAgentsAndFiresEvent() {
        container.getStatuses().add(createStatus("i-agent", JobStatus.Running, VMStatus.running));
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        jobEventSender.stopJob("123");

        verify(vmTracker).stopJob("123");
        verify(agentChannel).stopAgents(Collections.singletonList("i-agent"));
        verify(jobEventProducer).fire(any(JobEvent.class));
    }

    @Test
    @DisplayName("stopAgent delegates to stopAgents with singleton list")
    void stopAgent_delegatesToStopAgents() {
        jobEventSender.stopAgent("i-single");

        verify(agentChannel).stopAgents(Collections.singletonList("i-single"));
    }

    @Test
    @DisplayName("stopAgents calls agentChannel.stopAgents")
    void stopAgents_callsAgentChannel() {
        List<String> ids = Arrays.asList("i-1", "i-2");

        jobEventSender.stopAgents(ids);

        verify(agentChannel).stopAgents(ids);
    }

    // =====================================================================
    // pauseJob / pauseAgent / pauseAgents tests
    // =====================================================================

    @Test
    @DisplayName("pauseJob pauses agents and fires event")
    void pauseJob_pausesAgentsAndFiresEvent() {
        container.getStatuses().add(createStatus("i-agent", JobStatus.Running, VMStatus.running));
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        jobEventSender.pauseJob("123");

        verify(agentChannel).pauseAgents(Collections.singletonList("i-agent"));
        verify(jobEventProducer).fire(any(JobEvent.class));
    }

    @Test
    @DisplayName("pauseAgent delegates to pauseAgents with singleton list")
    void pauseAgent_delegatesToPauseAgents() {
        jobEventSender.pauseAgent("i-single");

        verify(agentChannel).pauseAgents(Collections.singletonList("i-single"));
    }

    @Test
    @DisplayName("pauseAgents calls agentChannel.pauseAgents")
    void pauseAgents_callsAgentChannel() {
        List<String> ids = Arrays.asList("i-1", "i-2");

        jobEventSender.pauseAgents(ids);

        verify(agentChannel).pauseAgents(ids);
    }

    // =====================================================================
    // restartJob / restartAgent / restartAgents tests
    // =====================================================================

    @Test
    @DisplayName("restartJob restarts agents and fires event")
    void restartJob_restartsAgentsAndFiresEvent() {
        container.getStatuses().add(createStatus("i-agent", JobStatus.Running, VMStatus.running));
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        jobEventSender.restartJob("123");

        verify(agentChannel).restartAgents(Collections.singletonList("i-agent"));
        verify(jobEventProducer).fire(any(JobEvent.class));
    }

    @Test
    @DisplayName("restartAgent delegates to restartAgents with singleton list")
    void restartAgent_delegatesToRestartAgents() {
        jobEventSender.restartAgent("i-single");

        verify(agentChannel).restartAgents(Collections.singletonList("i-single"));
    }

    @Test
    @DisplayName("restartAgents calls agentChannel.restartAgents")
    void restartAgents_callsAgentChannel() {
        List<String> ids = Arrays.asList("i-1", "i-2");

        jobEventSender.restartAgents(ids);

        verify(agentChannel).restartAgents(ids);
    }

    // =====================================================================
    // pauseRampJob / pauseRampInstance / pauseRampInstances tests
    // =====================================================================

    @Test
    @DisplayName("pauseRampJob pauses ramp and fires event")
    void pauseRampJob_pausesRampAndFiresEvent() {
        container.getStatuses().add(createStatus("i-agent", JobStatus.Running, VMStatus.running));
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        jobEventSender.pauseRampJob("123");

        verify(agentChannel).pauseRamp(Collections.singletonList("i-agent"));
        verify(jobEventProducer).fire(any(JobEvent.class));
    }

    @Test
    @DisplayName("pauseRampInstance delegates to pauseRampInstances with singleton list")
    void pauseRampInstance_delegatesToPauseRampInstances() {
        jobEventSender.pauseRampInstance("i-single");

        verify(agentChannel).pauseRamp(Collections.singletonList("i-single"));
    }

    @Test
    @DisplayName("pauseRampInstances calls agentChannel.pauseRamp")
    void pauseRampInstances_callsAgentChannel() {
        List<String> ids = Arrays.asList("i-1", "i-2");

        jobEventSender.pauseRampInstances(ids);

        verify(agentChannel).pauseRamp(ids);
    }

    // =====================================================================
    // resumeRampJob / resumeRampInstance / resumeRampInstances tests
    // =====================================================================

    @Test
    @DisplayName("resumeRampJob resumes ramp and fires event")
    void resumeRampJob_resumesRampAndFiresEvent() {
        container.getStatuses().add(createStatus("i-agent", JobStatus.Running, VMStatus.running));
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        jobEventSender.resumeRampJob("123");

        verify(agentChannel).resumeRamp(Collections.singletonList("i-agent"));
        verify(jobEventProducer).fire(any(JobEvent.class));
    }

    @Test
    @DisplayName("resumeRampInstance delegates to resumeRampInstances with singleton list")
    void resumeRampInstance_delegatesToResumeRampInstances() {
        jobEventSender.resumeRampInstance("i-single");

        verify(agentChannel).resumeRamp(Collections.singletonList("i-single"));
    }

    @Test
    @DisplayName("resumeRampInstances calls agentChannel.resumeRamp")
    void resumeRampInstances_callsAgentChannel() {
        List<String> ids = Arrays.asList("i-1", "i-2");

        jobEventSender.resumeRampInstances(ids);

        verify(agentChannel).resumeRamp(ids);
    }

    // =====================================================================
    // setVmStatus tests
    // =====================================================================

    @Test
    @DisplayName("setVmStatus with running status sets status without termination")
    void setVmStatus_runningStatus_noTermination() {
        CloudVmStatus status = createStatus("i-running", JobStatus.Running, VMStatus.running);

        jobEventSender.setVmStatus("i-running", status);

        verify(vmTracker).setStatus(status);
        verify(terminator, never()).terminate(anyString());
    }

    @Test
    @DisplayName("setVmStatus with Completed job status triggers termination and checkJobStatus")
    void setVmStatus_completedJobStatus_triggersTermination() {
        CloudVmStatus status = createStatus("i-done", JobStatus.Completed, VMStatus.running);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        jobEventSender.setVmStatus("i-done", status);

        verify(vmTracker).setStatus(status);
        verify(terminator).terminate("i-done");
        // checkJobStatus is also called
        verify(vmTracker).getVmStatusForJob("123");
    }

    @Test
    @DisplayName("setVmStatus with terminated vm status triggers termination")
    void setVmStatus_terminatedVmStatus_triggersTermination() {
        CloudVmStatus status = createStatus("i-term", JobStatus.Running, VMStatus.terminated);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        jobEventSender.setVmStatus("i-term", status);

        verify(vmTracker).setStatus(status);
        verify(terminator).terminate("i-term");
    }

    @Test
    @DisplayName("setVmStatus with replaced vm status triggers termination")
    void setVmStatus_replacedVmStatus_triggersTermination() {
        CloudVmStatus status = createStatus("i-repl", JobStatus.Running, VMStatus.replaced);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        jobEventSender.setVmStatus("i-repl", status);

        verify(vmTracker).setStatus(status);
        verify(terminator).terminate("i-repl");
    }

    // =====================================================================
    // checkJobStatus tests
    // =====================================================================

    @Test
    @DisplayName("checkJobStatus with null container does nothing")
    void checkJobStatus_nullContainer_doesNothing() {
        when(vmTracker.getVmStatusForJob("123")).thenReturn(null);

        jobEventSender.checkJobStatus("123");

        // Just verifies it does not throw
        verify(vmTracker).getVmStatusForJob("123");
    }

    @Test
    @DisplayName("checkJobStatus with no end time does not complete job")
    void checkJobStatus_noEndTime_doesNotComplete() {
        container.setEndTime(null);
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

        jobEventSender.checkJobStatus("123");

        // Container exists but has no end time - the else branch logs and returns
        verify(vmTracker).getVmStatusForJob("123");
    }

    @Test
    @DisplayName("checkJobStatus with end time marks job as Completed")
    void checkJobStatus_withEndTime_marksJobCompleted() {
        container.setEndTime(new Date());

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        when(vmTracker.getAllJobs()).thenReturn(Collections.emptySet());

        JobInstance finishedJob = new JobInstance();
        finishedJob.setId(123);
        finishedJob.setEndTime(null); // no end time yet

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(finishedJob);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                    when(mock.getForStatus(anyList())).thenReturn(Collections.emptyList());
                })) {

            jobEventSender.checkJobStatus("123");

            assertEquals(JobQueueStatus.Completed, finishedJob.getStatus());
            assertNotNull(finishedJob.getEndTime());
        }
    }

    @Test
    @DisplayName("checkJobStatus with end time but job already has end time does not update")
    void checkJobStatus_jobAlreadyHasEndTime_doesNotUpdate() {
        container.setEndTime(new Date());
        Date existingEndTime = new Date(System.currentTimeMillis() - 60000);

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        when(vmTracker.getAllJobs()).thenReturn(Collections.emptySet());

        JobInstance finishedJob = new JobInstance();
        finishedJob.setId(123);
        finishedJob.setEndTime(existingEndTime);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(finishedJob);
                    when(mock.getForStatus(anyList())).thenReturn(Collections.emptyList());
                })) {

            jobEventSender.checkJobStatus("123");

            // End time should not change
            assertEquals(existingEndTime, finishedJob.getEndTime());
            verify(daoConstruction.constructed().get(0), never()).saveOrUpdate(any(JobInstance.class));
        }
    }

    @Test
    @DisplayName("checkJobStatus with running instances checks their status")
    void checkJobStatus_withRunningInstances_checksStatus() {
        container.setEndTime(new Date());

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        when(vmTracker.getAllJobs()).thenReturn(Collections.emptySet());

        JobInstance finishedJob = new JobInstance();
        finishedJob.setId(123);
        finishedJob.setEndTime(null);

        JobInstance runningJob = new JobInstance();
        runningJob.setId(456);

        CloudVmStatusContainer runningContainer = new CloudVmStatusContainer();
        runningContainer.setJobId("456");
        runningContainer.setEndTime(null); // not finished

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(finishedJob);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                    when(mock.getForStatus(anyList())).thenReturn(Collections.singletonList(runningJob));
                })) {

            when(vmTracker.getVmStatusForJob("456")).thenReturn(runningContainer);

            jobEventSender.checkJobStatus("123");

            assertEquals(JobQueueStatus.Completed, finishedJob.getStatus());
            // Verifies the running job check happened
            verify(vmTracker).getVmStatusForJob("456");
        }
    }

    @Test
    @DisplayName("checkJobStatus with automation job prevents killing")
    void checkJobStatus_automationJob_preventsKilling() {
        container.setEndTime(new Date());

        CloudVmStatusContainer automationJob = new CloudVmStatusContainer();
        automationJob.setJobId("automation-abc"); // non-numeric = automation job
        automationJob.setEndTime(null); // still running

        Set<CloudVmStatusContainer> allJobs = new HashSet<>();
        allJobs.add(automationJob);

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        when(vmTracker.getAllJobs()).thenReturn(allJobs);

        JobInstance finishedJob = new JobInstance();
        finishedJob.setId(123);
        finishedJob.setEndTime(null);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(finishedJob);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                    when(mock.getForStatus(anyList())).thenReturn(Collections.emptyList());
                })) {

            jobEventSender.checkJobStatus("123");

            // The automation job should prevent killing
            verify(vmTracker).getAllJobs();
        }
    }

    @Test
    @DisplayName("checkJobStatus with same job id in running instances skips it")
    void checkJobStatus_sameJobInRunningInstances_skips() {
        container.setEndTime(new Date());

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        when(vmTracker.getAllJobs()).thenReturn(Collections.emptySet());

        JobInstance finishedJob = new JobInstance();
        finishedJob.setId(123);
        finishedJob.setEndTime(null);

        // The same job appears in the running instances list
        JobInstance sameJob = new JobInstance();
        sameJob.setId(123);

        CloudVmStatusContainer sameJobContainer = new CloudVmStatusContainer();
        sameJobContainer.setJobId("123");
        sameJobContainer.setEndTime(null);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(finishedJob);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                    when(mock.getForStatus(anyList())).thenReturn(Collections.singletonList(sameJob));
                })) {

            // For the same job id, getVmStatusForJob is called once for the main check
            // and again for the running instances loop - but since it equals jobId, the "found another job" branch is not taken
            when(vmTracker.getVmStatusForJob("123")).thenReturn(container);

            jobEventSender.checkJobStatus("123");

            assertEquals(JobQueueStatus.Completed, finishedJob.getStatus());
        }
    }

    @Test
    @DisplayName("checkJobStatus with all completed automation jobs allows killing")
    void checkJobStatus_completedAutomationJobs_allowsKilling() {
        container.setEndTime(new Date());

        CloudVmStatusContainer completedAutomation = new CloudVmStatusContainer();
        completedAutomation.setJobId("automation-xyz");
        completedAutomation.setEndTime(new Date()); // already finished

        Set<CloudVmStatusContainer> allJobs = new HashSet<>();
        allJobs.add(completedAutomation);

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        when(vmTracker.getAllJobs()).thenReturn(allJobs);

        JobInstance finishedJob = new JobInstance();
        finishedJob.setId(123);
        finishedJob.setEndTime(null);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(finishedJob);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                    when(mock.getForStatus(anyList())).thenReturn(Collections.emptyList());
                })) {

            jobEventSender.checkJobStatus("123");

            // Completed automation jobs don't prevent killing
            assertEquals(JobQueueStatus.Completed, finishedJob.getStatus());
        }
    }

    @Test
    @DisplayName("checkJobStatus with numeric job in allJobs that has end time does not block")
    void checkJobStatus_numericJobWithEndTime_doesNotBlock() {
        container.setEndTime(new Date());

        CloudVmStatusContainer numericJob = new CloudVmStatusContainer();
        numericJob.setJobId("999"); // numeric, so isCreatable returns true - does not block
        numericJob.setEndTime(null);

        Set<CloudVmStatusContainer> allJobs = new HashSet<>();
        allJobs.add(numericJob);

        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        when(vmTracker.getAllJobs()).thenReturn(allJobs);

        JobInstance finishedJob = new JobInstance();
        finishedJob.setId(123);
        finishedJob.setEndTime(null);

        try (MockedConstruction<JobInstanceDao> daoConstruction = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, context) -> {
                    when(mock.findById(123)).thenReturn(finishedJob);
                    when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> inv.getArgument(0));
                    when(mock.getForStatus(anyList())).thenReturn(Collections.emptyList());
                })) {

            jobEventSender.checkJobStatus("123");

            // Numeric job IDs don't block - only non-numeric (automation) job IDs block
            assertEquals(JobQueueStatus.Completed, finishedJob.getStatus());
        }
    }
}
