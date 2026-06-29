package com.intuit.tank.rest.mvc.rest.services.jobs;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.jobs.models.CreateJobRegion;
import com.intuit.tank.jobs.models.CreateJobRequest;
import com.intuit.tank.jobs.models.JobContainer;
import com.intuit.tank.jobs.models.JobTO;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.util.JobDetailFormatter;
import com.intuit.tank.rest.mvc.rest.util.JobServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.JobValidator;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.util.TestParameterContainer;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.ServletContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JobServiceV2ImplTest {

    @InjectMocks
    private JobServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;
    private MockedStatic<AWSXRay> xrayMock;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        xrayMock = Mockito.mockStatic(AWSXRay.class);
        Segment mockSegment = mock(Segment.class);
        xrayMock.when(AWSXRay::getCurrentSegment).thenReturn(mockSegment);
    }

    @AfterEach
    void tearDown() throws Exception {
        xrayMock.close();
        mocks.close();
    }

    // =====================================================================
    // ping
    // =====================================================================

    @Test
    void ping_returnsPong() {
        String result = service.ping();
        assertTrue(result.startsWith("PONG"));
        assertTrue(result.contains("JobServiceV2"));
    }

    // =====================================================================
    // getJob
    // =====================================================================

    @Test
    void getJob_found_returnsJobTO() {
        JobInstance job = createJobInstance(42, "test-job", JobQueueStatus.Running);

        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findById(42)).thenReturn(job))) {

            JobTO result = service.getJob(42);

            assertNotNull(result);
            assertEquals(42, result.getId());
            assertEquals("test-job", result.getName());
        }
    }

    @Test
    void getJob_notFound_returnsNull() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findById(99)).thenReturn(null))) {

            JobTO result = service.getJob(99);

            assertNull(result);
        }
    }

    @Test
    void getJob_error_throwsException() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("db error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJob(1));
        }
    }

    // =====================================================================
    // getJobsByProject
    // =====================================================================

    @Test
    void getJobsByProject_projectFound_returnsJobs() {
        Project project = new Project();
        project.setId(10);
        project.setName("proj");

        JobInstance job1 = createJobInstance(1, "job1", JobQueueStatus.Created);
        JobInstance job2 = createJobInstance(2, "job2", JobQueueStatus.Running);
        JobQueue queue = new JobQueue(10);
        queue.addJob(job1);
        queue.addJob(job2);

        try (MockedConstruction<ProjectDao> projDaoMock = Mockito.mockConstruction(ProjectDao.class,
                     (mock, ctx) -> when(mock.findByIdEager(10)).thenReturn(project));
             MockedConstruction<JobQueueDao> queueDaoMock = Mockito.mockConstruction(JobQueueDao.class,
                     (mock, ctx) -> when(mock.findOrCreateForProjectId(10)).thenReturn(queue))) {

            JobContainer result = service.getJobsByProject(10);

            assertNotNull(result);
            assertEquals(2, result.getJobs().size());
        }
    }

    @Test
    void getJobsByProject_projectNotFound_returnsNull() {
        try (MockedConstruction<ProjectDao> projDaoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(99)).thenReturn(null))) {

            JobContainer result = service.getJobsByProject(99);

            assertNull(result);
        }
    }

    @Test
    void getJobsByProject_error_throwsException() {
        try (MockedConstruction<ProjectDao> projDaoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJobsByProject(1));
        }
    }

    // =====================================================================
    // getAllJobs
    // =====================================================================

    @Test
    void getAllJobs_nonEmpty_returnsContainer() {
        JobInstance job = createJobInstance(1, "job1", JobQueueStatus.Running);
        List<JobInstance> jobs = new ArrayList<>(List.of(job));

        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(jobs))) {

            JobContainer result = service.getAllJobs();

            assertNotNull(result);
            assertEquals(1, result.getJobs().size());
        }
    }

    @Test
    void getAllJobs_empty_returnsNull() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(new ArrayList<>()))) {

            JobContainer result = service.getAllJobs();

            assertNull(result);
        }
    }

    @Test
    void getAllJobs_error_throwsException() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findAll()).thenThrow(new RuntimeException("db error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getAllJobs());
        }
    }

    // =====================================================================
    // createJob
    // =====================================================================

    @Test
    void createJob_success_returnsJobIdAndStatus() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "projectId", 7);

        Script script = createScript("s", 1);
        Workload workload = createWorkload("wl", 10, script);
        Project project = createProject(workload);
        JobQueue queue = new JobQueue(project.getId());

        try (MockedStatic<JobDetailFormatter> jdf = Mockito.mockStatic(JobDetailFormatter.class);
             MockedStatic<TestParamUtil> tpu = Mockito.mockStatic(TestParamUtil.class);
             MockedConstruction<ProjectDao> projDaoMock = Mockito.mockConstruction(ProjectDao.class,
                     (mock, ctx) -> {
                         when(mock.findByIdEager(7)).thenReturn(project);
                         when(mock.saveOrUpdateProject(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));
                     });
             MockedConstruction<JobValidator> valMock = Mockito.mockConstruction(JobValidator.class,
                     (mock, ctx) -> when(mock.getDurationMs(anyString())).thenReturn(0L));
             MockedConstruction<JobQueueDao> queueDaoMock = Mockito.mockConstruction(JobQueueDao.class,
                     (mock, ctx) -> {
                         when(mock.findOrCreateForProjectId(anyInt())).thenReturn(queue);
                         when(mock.saveOrUpdate(any(JobQueue.class))).thenReturn(queue);
                     });
             MockedConstruction<WorkloadDao> wlDaoMock = Mockito.mockConstruction(WorkloadDao.class,
                     (mock, ctx) -> when(mock.saveOrUpdate(any(Workload.class))).thenAnswer(inv -> inv.getArgument(0)));
             MockedConstruction<JobInstanceDao> jiDaoMock = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(inv -> {
                         JobInstance ji = inv.getArgument(0);
                         ji.setId(555);
                         return ji;
                     }));
             MockedConstruction<DataFileDao> dfDaoMock = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> jnDaoMock = Mockito.mockConstruction(JobNotificationDao.class)) {

            jdf.when(() -> JobDetailFormatter.createJobDetails(any(), any(), any())).thenReturn("details");
            tpu.when(() -> TestParamUtil.evaluateTestTimes(anyLong(), anyString(), anyString()))
                    .thenReturn(TestParameterContainer.builder().withRampTime(0L).withSimulationTime(0L).build());

            Map<String, String> result = service.createJob(request);

            assertEquals("555", result.get("JobId"));
            assertEquals("created", result.get("status"));
        }
    }

    @Test
    void createJob_error_throwsException() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "projectId", 7);

        try (MockedConstruction<ProjectDao> projDaoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.createJob(request));
        }
    }

    @Test
    void createJob_nullProjectId_returnsEmptyMap() throws Exception {
        CreateJobRequest request = createRequest();
        // projectId is null by default

        Map<String, String> result = service.createJob(request);

        assertTrue(result.isEmpty());
    }

    // =====================================================================
    // getJobStatus
    // =====================================================================

    @Test
    void getJobStatus_found_returnsStatusName() {
        JobInstance job = createJobInstance(42, "j", JobQueueStatus.Running);

        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findById(42)).thenReturn(job))) {

            String result = service.getJobStatus(42);

            assertEquals("Running", result);
        }
    }

    @Test
    void getJobStatus_notFound_returnsNull() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findById(99)).thenReturn(null))) {

            String result = service.getJobStatus(99);

            assertNull(result);
        }
    }

    @Test
    void getJobStatus_error_throwsException() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJobStatus(1));
        }
    }

    // =====================================================================
    // getJobVMStatus
    // =====================================================================

    @Test
    void getJobVMStatus_success_returnsContainer() {
        CloudVmStatusContainer container = mock(CloudVmStatusContainer.class);
        JobEventSender mockSender = mock(JobEventSender.class);
        when(mockSender.getVmStatusForJob("42")).thenReturn(container);

        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenReturn(mockSender))) {

            CloudVmStatusContainer result = service.getJobVMStatus("42");

            assertNotNull(result);
            assertSame(container, result);
        }
    }

    @Test
    void getJobVMStatus_error_throwsException() {
        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJobVMStatus("42"));
        }
    }

    // =====================================================================
    // getAllJobStatus
    // =====================================================================

    @Test
    void getAllJobStatus_withJobs_returnsStatusList() {
        JobInstance job1 = createJobInstance(1, "j1", JobQueueStatus.Running);
        JobInstance job2 = createJobInstance(2, "j2", JobQueueStatus.Completed);
        List<JobInstance> jobs = List.of(job1, job2);

        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(jobs))) {

            List<Map<String, String>> result = service.getAllJobStatus();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("1", result.get(0).get("jobId"));
            assertEquals("Running", result.get(0).get("status"));
            assertEquals("2", result.get(1).get("jobId"));
            assertEquals("Completed", result.get(1).get("status"));
        }
    }

    @Test
    void getAllJobStatus_empty_returnsEmptyList() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(new ArrayList<>()))) {

            List<Map<String, String>> result = service.getAllJobStatus();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getAllJobStatus_error_throwsException() {
        try (MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                (mock, ctx) -> when(mock.findAll()).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getAllJobStatus());
        }
    }

    // =====================================================================
    // startJob
    // =====================================================================

    @Test
    void startJob_success_returnsStatus() {
        JobEventSender mockSender = mock(JobEventSender.class);
        JobInstance job = createJobInstance(42, "j", JobQueueStatus.Starting);

        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                     (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                             .thenReturn(mockSender));
             MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.findById(42)).thenReturn(job))) {

            String result = service.startJob(42);

            assertEquals("Starting", result);
            verify(mockSender).startJob("42");
        }
    }

    @Test
    void startJob_error_throwsException() {
        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.startJob(1));
        }
    }

    // =====================================================================
    // stopJob
    // =====================================================================

    @Test
    void stopJob_success_returnsStatus() {
        JobEventSender mockSender = mock(JobEventSender.class);
        JobInstance job = createJobInstance(42, "j", JobQueueStatus.Stopped);

        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                     (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                             .thenReturn(mockSender));
             MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.findById(42)).thenReturn(job))) {

            String result = service.stopJob(42);

            assertEquals("Stopped", result);
            verify(mockSender).stopJob("42");
        }
    }

    @Test
    void stopJob_error_throwsException() {
        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.stopJob(1));
        }
    }

    // =====================================================================
    // pauseJob
    // =====================================================================

    @Test
    void pauseJob_success_returnsStatus() {
        JobEventSender mockSender = mock(JobEventSender.class);
        JobInstance job = createJobInstance(42, "j", JobQueueStatus.RampPaused);

        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                     (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                             .thenReturn(mockSender));
             MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.findById(42)).thenReturn(job))) {

            String result = service.pauseJob(42);

            assertEquals("RampPaused", result);
            verify(mockSender).pauseRampJob("42");
        }
    }

    @Test
    void pauseJob_error_throwsException() {
        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.pauseJob(1));
        }
    }

    // =====================================================================
    // resumeJob
    // =====================================================================

    @Test
    void resumeJob_success_returnsStatus() {
        JobEventSender mockSender = mock(JobEventSender.class);
        JobInstance job = createJobInstance(42, "j", JobQueueStatus.Running);

        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                     (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                             .thenReturn(mockSender));
             MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.findById(42)).thenReturn(job))) {

            String result = service.resumeJob(42);

            assertEquals("Running", result);
            verify(mockSender).resumeRampJob("42");
        }
    }

    @Test
    void resumeJob_error_throwsException() {
        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.resumeJob(1));
        }
    }

    // =====================================================================
    // killJob
    // =====================================================================

    @Test
    void killJob_success_returnsStatus() {
        JobEventSender mockSender = mock(JobEventSender.class);
        JobInstance job = createJobInstance(42, "j", JobQueueStatus.Completed);

        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                     (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                             .thenReturn(mockSender));
             MockedConstruction<JobInstanceDao> daoMock = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, ctx) -> when(mock.findById(42)).thenReturn(job))) {

            String result = service.killJob(42);

            assertEquals("Completed", result);
            verify(mockSender).killJob("42");
        }
    }

    @Test
    void killJob_error_throwsException() {
        try (MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.killJob(1));
        }
    }

    // =====================================================================
    // buildJobConfiguration (static method)
    // =====================================================================

    @Test
    void buildJobConfiguration_nullJobConfig_throwsException() throws Exception {
        CreateJobRequest request = createRequest();
        Project project = new Project();
        project.setName("proj");
        // project has no workloads, so jobConfiguration will be null

        assertThrows(GenericServiceCreateOrUpdateException.class,
                () -> JobServiceV2Impl.buildJobConfiguration(request, project));
    }

    @Test
    void buildJobConfiguration_nullProject_throwsException() throws Exception {
        CreateJobRequest request = createRequest();

        assertThrows(GenericServiceCreateOrUpdateException.class,
                () -> JobServiceV2Impl.buildJobConfiguration(request, null));
    }

    @Test
    void buildJobConfiguration_setsRampTime() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "rampTime", "300");

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals("300", jc.getRampTimeExpression());
    }

    @Test
    void buildJobConfiguration_setsSimulationTime() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "simulationTime", "600");

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals("600", jc.getSimulationTimeExpression());
    }

    @Test
    void buildJobConfiguration_setsWorkloadType() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "workloadType", IncrementStrategy.increasing);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(IncrementStrategy.increasing, jc.getIncrementStrategy());
    }

    @Test
    void buildJobConfiguration_setsStopBehavior_whenProvided() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "stopBehavior", StopBehavior.END_OF_TEST.name());

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(StopBehavior.END_OF_TEST.name(), jc.getStopBehavior());
    }

    @Test
    void buildJobConfiguration_setsStopBehavior_defaultWhenEmpty() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "stopBehavior", "");

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(StopBehavior.END_OF_SCRIPT_GROUP.name(), jc.getStopBehavior());
    }

    @Test
    void buildJobConfiguration_setsStopBehavior_defaultWhenNull() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "stopBehavior", null);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(StopBehavior.END_OF_SCRIPT_GROUP.name(), jc.getStopBehavior());
    }

    @Test
    void buildJobConfiguration_setsVmInstance() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "vmInstance", "c5.xlarge");

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals("c5.xlarge", jc.getVmInstanceType());
    }

    @Test
    void buildJobConfiguration_setsNumUsersPerAgent() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "numUsersPerAgent", 50);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(50, jc.getNumUsersPerAgent());
    }

    @Test
    void buildJobConfiguration_terminationPolicy_timeWhenSimTime() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "simulationTime", "600");

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(TerminationPolicy.time, jc.getTerminationPolicy());
    }

    @Test
    void buildJobConfiguration_terminationPolicy_scriptWhenNoSimTime() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "simulationTime", "0");

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.setSimulationTimeExpression("0");

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(TerminationPolicy.script, jc.getTerminationPolicy());
    }

    @Test
    void buildJobConfiguration_setsJobRegions() throws Exception {
        CreateJobRequest request = createRequest();
        Set<CreateJobRegion> regions = new HashSet<>();
        regions.add(new CreateJobRegion("us-west-2a", "100", "50"));
        setField(request, "jobRegions", regions);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        // Ensure jobRegions is initialized
        jc.getJobRegions().add(new JobRegion(VMRegion.US_EAST, "10", "100"));

        try (MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class,
                (mock, ctx) -> when(mock.saveOrUpdate(any(JobRegion.class))).thenAnswer(inv -> inv.getArgument(0)))) {

            JobServiceV2Impl.buildJobConfiguration(request, project);

            assertEquals(1, jc.getJobRegions().size());
        }
    }

    @Test
    void buildJobConfiguration_jobRegions_nullUsers_defaultsToZero() throws Exception {
        CreateJobRequest request = createRequest();
        Set<CreateJobRegion> regions = new HashSet<>();
        regions.add(new CreateJobRegion("us-east-1b", null, null));
        setField(request, "jobRegions", regions);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.getJobRegions().add(new JobRegion(VMRegion.US_EAST, "10", "100"));

        try (MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class,
                (mock, ctx) -> when(mock.saveOrUpdate(any(JobRegion.class))).thenAnswer(inv -> {
                    JobRegion jr = inv.getArgument(0);
                    assertEquals("0", jr.getUsers());
                    assertEquals("0", jr.getPercentage());
                    return jr;
                }))) {

            JobServiceV2Impl.buildJobConfiguration(request, project);
        }
    }

    @Test
    void buildJobConfiguration_jobRegions_emptyRegionName_skipped() throws Exception {
        CreateJobRequest request = createRequest();
        Set<CreateJobRegion> regions = new HashSet<>();
        regions.add(new CreateJobRegion("", "100", "50"));
        setField(request, "jobRegions", regions);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.getJobRegions().add(new JobRegion(VMRegion.US_EAST, "10", "100"));

        try (MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class)) {

            JobServiceV2Impl.buildJobConfiguration(request, project);

            // empty region should be skipped - the old ones were cleared but nothing new added
            assertTrue(jc.getJobRegions().isEmpty());
        }
    }

    @Test
    void buildJobConfiguration_nullJobRegions_skipsRegionSetting() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "jobRegions", null);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.getJobRegions().add(new JobRegion(VMRegion.US_EAST, "10", "100"));

        JobServiceV2Impl.buildJobConfiguration(request, project);

        // Original region should remain since jobRegions is null
        assertEquals(1, jc.getJobRegions().size());
    }

    @Test
    void buildJobConfiguration_setsUserIntervalAndRates() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "userIntervalIncrement", 5);
        setField(request, "targetRampRate", 2.5);
        setField(request, "targetRatePerAgent", 1.5);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(5, jc.getUserIntervalIncrement());
        assertEquals(2.5, jc.getTargetRampRate(), 0.01);
        assertEquals(1.5, jc.getTargetRatePerAgent(), 0.01);
    }

    @Test
    void buildJobConfiguration_noRampTime_doesNotOverride() throws Exception {
        CreateJobRequest request = createRequest();
        // rampTime is null by default

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.setRampTimeExpression("existing");

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals("existing", jc.getRampTimeExpression());
    }

    @Test
    void buildJobConfiguration_noSimulationTime_doesNotOverride() throws Exception {
        CreateJobRequest request = createRequest();
        // simulationTime is null by default

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.setSimulationTimeExpression("existing");

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals("existing", jc.getSimulationTimeExpression());
    }

    @Test
    void buildJobConfiguration_noVmInstance_doesNotOverride() throws Exception {
        CreateJobRequest request = createRequest();
        // vmInstance is null by default

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.setVmInstanceType("original");

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals("original", jc.getVmInstanceType());
    }

    @Test
    void buildJobConfiguration_nullWorkloadType_doesNotOverride() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "workloadType", null);

        Project project = createProjectWithJobConfig();
        JobConfiguration jc = project.getWorkloads().get(0).getJobConfiguration();
        jc.setIncrementStrategy(IncrementStrategy.increasing);

        JobServiceV2Impl.buildJobConfiguration(request, project);

        assertEquals(IncrementStrategy.increasing, jc.getIncrementStrategy());
    }

    // =====================================================================
    // buildJobInstanceName (tested via addJobToQueue)
    // =====================================================================

    @Test
    void addJobToQueue_customJobInstanceName_usesCustomName() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "jobInstanceName", "my-custom-job-name");

        JobInstance createdJob = runAddJobToQueue(request);

        assertEquals("my-custom-job-name", createdJob.getName());
    }

    @Test
    void addJobToQueue_increasingWorkloadType_usesUsersInName() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "workloadType", IncrementStrategy.increasing);

        JobInstance createdJob = runAddJobToQueue(request);

        assertTrue(createdJob.getName().contains("_users_"));
        assertTrue(createdJob.getName().startsWith("project-name"));
    }

    @Test
    void addJobToQueue_standardWorkloadType_usesNonlinearInName() throws Exception {
        CreateJobRequest request = createRequest();
        setField(request, "workloadType", IncrementStrategy.standard);
        setField(request, "projectName", null);

        JobInstance createdJob = runAddJobToQueue(request);

        assertTrue(createdJob.getName().contains("_nonlinear_"));
        assertTrue(createdJob.getName().startsWith("project-name"));
    }

    @Test
    void addJobToQueue_requestProjectName_overridesProjectName() throws Exception {
        CreateJobRequest request = new CreateJobRequest("custom-project-name");
        setField(request, "workloadType", IncrementStrategy.standard);

        JobInstance createdJob = runAddJobToQueue(request);

        assertTrue(createdJob.getName().startsWith("custom-project-name"));
    }

    // =====================================================================
    // ORIGINAL TESTS (preserved)
    // =====================================================================

    @Test
    void addJobToQueue_doesNotCallStoreScript() throws Exception {
        Script loadedScript = createScript("loaded-script", 11);
        Workload loadedWorkload = createWorkload("loaded-workload", 111, loadedScript);
        Project project = createProject(loadedWorkload);
        CreateJobRequest request = createRequest();
        JobQueue queue = new JobQueue(project.getId());

        try (MockedStatic<ResponseUtil> responseUtil = Mockito.mockStatic(ResponseUtil.class);
             MockedStatic<JobDetailFormatter> jobDetailFormatter = Mockito.mockStatic(JobDetailFormatter.class);
             MockedStatic<TestParamUtil> testParamUtil = Mockito.mockStatic(TestParamUtil.class);
             MockedConstruction<JobValidator> ignoredValidator = Mockito.mockConstruction(JobValidator.class,
                     (mock, context) -> when(mock.getDurationMs(anyString())).thenReturn(0L));
             MockedConstruction<JobQueueDao> ignoredJobQueueDao = Mockito.mockConstruction(JobQueueDao.class,
                     (mock, context) -> {
                         when(mock.findOrCreateForProjectId(anyInt())).thenReturn(queue);
                         when(mock.saveOrUpdate(any(JobQueue.class))).thenReturn(queue);
                     });
             MockedConstruction<WorkloadDao> ignoredWorkloadDao = Mockito.mockConstruction(WorkloadDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(Workload.class))).thenAnswer(invocation -> invocation.getArgument(0)));
             MockedConstruction<JobInstanceDao> ignoredJobInstanceDao = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(invocation -> {
                         JobInstance savedJob = invocation.getArgument(0);
                         savedJob.setId(123);
                         return savedJob;
                     }));
             MockedConstruction<DataFileDao> ignoredDataFileDao = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> ignoredJobNotificationDao = Mockito.mockConstruction(JobNotificationDao.class)) {

            jobDetailFormatter.when(() -> JobDetailFormatter.createJobDetails(any(JobValidator.class), any(Workload.class), any(JobInstance.class)))
                    .thenReturn("job-details");
            testParamUtil.when(() -> TestParamUtil.evaluateTestTimes(anyLong(), anyString(), anyString()))
                    .thenReturn(TestParameterContainer.builder().withRampTime(0L).withSimulationTime(0L).build());

            JobInstance createdJob = JobServiceV2Impl.addJobToQueue(project, request);

            assertEquals(123, createdJob.getId());
            assertTrue(loadedScript.getScriptSteps().isEmpty());
            responseUtil.verifyNoInteractions();
        }
    }

    @Test
    void addJobToQueue_clearsScriptStepsAfterJobDetails() throws Exception {
        Script loadedScript = createScript("loaded-script", 11);
        Workload loadedWorkload = createWorkload("loaded-workload", 111, loadedScript);
        Project project = createProject(loadedWorkload);
        CreateJobRequest request = createRequest();
        JobQueue queue = new JobQueue(project.getId());

        try (MockedStatic<ResponseUtil> responseUtil = Mockito.mockStatic(ResponseUtil.class);
             MockedStatic<JobDetailFormatter> jobDetailFormatter = Mockito.mockStatic(JobDetailFormatter.class);
             MockedStatic<TestParamUtil> testParamUtil = Mockito.mockStatic(TestParamUtil.class);
             MockedConstruction<JobValidator> ignoredValidator = Mockito.mockConstruction(JobValidator.class,
                     (mock, context) -> when(mock.getDurationMs(anyString())).thenReturn(0L));
             MockedConstruction<JobQueueDao> ignoredJobQueueDao = Mockito.mockConstruction(JobQueueDao.class,
                     (mock, context) -> {
                         when(mock.findOrCreateForProjectId(anyInt())).thenReturn(queue);
                         when(mock.saveOrUpdate(any(JobQueue.class))).thenReturn(queue);
                     });
             MockedConstruction<WorkloadDao> ignoredWorkloadDao = Mockito.mockConstruction(WorkloadDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(Workload.class))).thenAnswer(invocation -> {
                         Workload persistedWorkload = invocation.getArgument(0);
                         assertTrue(getOnlyScript(persistedWorkload).getScriptSteps().isEmpty());
                         return persistedWorkload;
                     }));
             MockedConstruction<JobInstanceDao> ignoredJobInstanceDao = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(invocation -> {
                         JobInstance savedJob = invocation.getArgument(0);
                         savedJob.setId(123);
                         return savedJob;
                     }));
             MockedConstruction<DataFileDao> ignoredDataFileDao = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> ignoredJobNotificationDao = Mockito.mockConstruction(JobNotificationDao.class)) {

            jobDetailFormatter.when(() -> JobDetailFormatter.createJobDetails(any(JobValidator.class), any(Workload.class), any(JobInstance.class)))
                    .thenAnswer(invocation -> {
                        Workload formatterWorkload = invocation.getArgument(1);
                        assertFalse(getOnlyScript(formatterWorkload).getScriptSteps().isEmpty());
                        return "job-details";
                    });
            testParamUtil.when(() -> TestParamUtil.evaluateTestTimes(anyLong(), anyString(), anyString()))
                    .thenReturn(TestParameterContainer.builder().withRampTime(0L).withSimulationTime(0L).build());

            JobServiceV2Impl.addJobToQueue(project, request);

            assertTrue(loadedScript.getScriptSteps().isEmpty());
            responseUtil.verifyNoInteractions();
        }
    }

    // =====================================================================
    // Helper methods
    // =====================================================================

    private static JobInstance createJobInstance(int id, String name, JobQueueStatus status) {
        JobInstance job = new JobInstance();
        job.setId(id);
        job.setName(name);
        job.setStatus(status);
        job.setCreated(new Date());
        return job;
    }

    private static Project createProject(Workload workload) {
        Project project = new Project();
        project.setId(7);
        project.setName("project-name");
        project.addWorkload(workload);
        return project;
    }

    private static Project createProjectWithJobConfig() {
        JobConfiguration jobConfiguration = new JobConfiguration();
        jobConfiguration.setLocation("us-west-2");
        jobConfiguration.setLoggingProfile("standard");
        jobConfiguration.setStopBehavior("stop");
        jobConfiguration.setVmInstanceType("m5.large");
        jobConfiguration.setNumUsersPerAgent(1);
        jobConfiguration.setReportingMode("standard");
        jobConfiguration.setRampTimeExpression("0");
        jobConfiguration.setSimulationTimeExpression("0");

        Workload workload = new Workload();
        workload.setId(10);
        workload.setName("wl");
        workload.setJobConfiguration(jobConfiguration);
        jobConfiguration.setParent(workload);

        Project project = new Project();
        project.setId(7);
        project.setName("project-name");
        project.addWorkload(workload);
        return project;
    }

    private static Workload createWorkload(String name, int id, Script script) {
        JobConfiguration jobConfiguration = new JobConfiguration();
        jobConfiguration.setLocation("us-west-2");
        jobConfiguration.setLoggingProfile("standard");
        jobConfiguration.setStopBehavior("stop");
        jobConfiguration.setVmInstanceType("m5.large");
        jobConfiguration.setNumUsersPerAgent(1);
        jobConfiguration.setReportingMode("standard");
        jobConfiguration.setRampTimeExpression("0");
        jobConfiguration.setSimulationTimeExpression("0");

        ScriptGroupStep groupStep = new ScriptGroupStep();
        groupStep.setScript(script);

        ScriptGroup group = new ScriptGroup();
        group.setName(name + "-group");
        group.addScriptGroupStep(groupStep);

        TestPlan testPlan = new TestPlan();
        testPlan.setName(name + "-plan");
        testPlan.addScriptGroup(group);

        Workload workload = new Workload();
        workload.setId(id);
        workload.setName(name);
        workload.setJobConfiguration(jobConfiguration);
        jobConfiguration.setParent(workload);
        workload.addTestPlan(testPlan);
        return workload;
    }

    private static Script createScript(String name, int id) {
        Script script = new Script();
        script.setId(id);
        script.setName(name);
        script.setScriptSteps(new ArrayList<>(List.of(new ScriptStep(), new ScriptStep())));
        return script;
    }

    private static Script getOnlyScript(Workload workload) {
        return workload.getTestPlans().get(0).getScriptGroups().get(0).getScriptGroupSteps().get(0).getScript();
    }

    private static JobInstance runAddJobToQueue(CreateJobRequest request) {
        Script loadedScript = createScript("loaded-script", 11);
        Workload loadedWorkload = createWorkload("loaded-workload", 111, loadedScript);
        Project project = createProject(loadedWorkload);
        JobQueue queue = new JobQueue(project.getId());

        try (MockedStatic<ResponseUtil> responseUtil = Mockito.mockStatic(ResponseUtil.class);
             MockedStatic<JobDetailFormatter> jobDetailFormatter = Mockito.mockStatic(JobDetailFormatter.class);
             MockedStatic<TestParamUtil> testParamUtil = Mockito.mockStatic(TestParamUtil.class);
             MockedConstruction<JobValidator> ignoredValidator = Mockito.mockConstruction(JobValidator.class,
                     (mock, context) -> when(mock.getDurationMs(anyString())).thenReturn(0L));
             MockedConstruction<JobQueueDao> ignoredJobQueueDao = Mockito.mockConstruction(JobQueueDao.class,
                     (mock, context) -> {
                         when(mock.findOrCreateForProjectId(anyInt())).thenReturn(queue);
                         when(mock.saveOrUpdate(any(JobQueue.class))).thenReturn(queue);
                     });
             MockedConstruction<WorkloadDao> ignoredWorkloadDao = Mockito.mockConstruction(WorkloadDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(Workload.class))).thenAnswer(invocation -> invocation.getArgument(0)));
             MockedConstruction<JobInstanceDao> ignoredJobInstanceDao = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(invocation -> {
                         JobInstance savedJob = invocation.getArgument(0);
                         savedJob.setId(123);
                         return savedJob;
                     }));
             MockedConstruction<DataFileDao> ignoredDataFileDao = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> ignoredJobNotificationDao = Mockito.mockConstruction(JobNotificationDao.class)) {

            jobDetailFormatter.when(() -> JobDetailFormatter.createJobDetails(any(JobValidator.class), any(Workload.class), any(JobInstance.class)))
                    .thenReturn("job-details");
            testParamUtil.when(() -> TestParamUtil.evaluateTestTimes(anyLong(), anyString(), anyString()))
                    .thenReturn(TestParameterContainer.builder().withRampTime(0L).withSimulationTime(0L).build());

            return JobServiceV2Impl.addJobToQueue(project, request);
        }
    }

    private static CreateJobRequest createRequest() throws Exception {
        CreateJobRequest request = new CreateJobRequest("project-name");
        setField(request, "workloadType", IncrementStrategy.standard);
        return request;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
