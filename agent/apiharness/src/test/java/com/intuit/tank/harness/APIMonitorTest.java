package com.intuit.tank.harness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.intuit.tank.reporting.ResultsReporter;
import com.intuit.tank.reporting.models.TPSInfoContainer;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.*;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.vmManager.models.UserDetail;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APIMonitorTest {

    @Mock private APITestHarness mockApiTestHarnessInstance;
    @Mock private TankConfig mockTankConfig;
    @Mock private AgentConfig mockAgentConfig;
    @Mock private WatsAgentStatusResponse mockAgentStatusResponse;
    @Mock private CloudVmStatus mockInitialVmStatus;
    @Mock private AgentRunData mockAgentRunData;
    @Mock private LoggingProfile mockLoggingProfile;
    @Mock private ResultsReporter mockResultsReporter;
    @Mock private TPSInfoContainer mockTpsInfoContainer;
    @Mock private CloudVmStatus mockStaticStatus;
    @Mock private UserTracker mockUserTracker;
    @Mock private List<UserDetail> mockUserSnapshot;
    @Mock private TPSMonitor mockTpsMonitor;


    private MockedStatic<APITestHarness> mockedApiHarness;
    private APIMonitor apiMonitorInstance;

    private static final long DEFAULT_REPORT_INTERVAL = 15000L;
    private static final int MIN_REPORT_TIME_CONST = 15000;
    private static Field statusField;
    private static Object originalStaticStatusValue;
    private static Field reportIntervalField;
    private static final ObjectWriter testObjectWriter = new ObjectMapper().writerFor(CloudVmStatus.class).withDefaultPrettyPrinter();

    @BeforeEach
    void setUp() throws Exception {
        resetStaticStatus();
        mockedApiHarness = Mockito.mockStatic(APITestHarness.class);
        mockedApiHarness.when(APITestHarness::getInstance).thenReturn(mockApiTestHarnessInstance);

        lenient().when(mockApiTestHarnessInstance.getAgentRunData()).thenReturn(mockAgentRunData);
        lenient().when(mockAgentRunData.getActiveProfile()).thenReturn(mockLoggingProfile);
        lenient().when(mockApiTestHarnessInstance.getResultsReporter()).thenReturn(mockResultsReporter);
        lenient().when(mockApiTestHarnessInstance.getStatus()).thenReturn(mockAgentStatusResponse);
        lenient().when(mockApiTestHarnessInstance.getUserTracker()).thenReturn(mockUserTracker);
        lenient().when(mockUserTracker.getSnapshot()).thenReturn(mockUserSnapshot);
        lenient().when(mockAgentConfig.getAgentToken()).thenReturn("dummy-token-123");
        lenient().when(mockTankConfig.getControllerBase()).thenReturn("http://dummy.controller:8080");
        lenient().when(mockApiTestHarnessInstance.getTPSMonitor()).thenReturn(mockTpsMonitor);
        lenient().when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
        lenient().doNothing().when(mockApiTestHarnessInstance).checkAgentThreads();
        lenient().when(mockAgentRunData.getJobId()).thenReturn("4141");
        lenient().when(mockAgentRunData.getInstanceId()).thenReturn("i-4214219841");
        when(mockApiTestHarnessInstance.getTankConfig()).thenReturn(mockTankConfig);
        when(mockTankConfig.getAgentConfig()).thenReturn(mockAgentConfig);
        lenient().when(mockAgentConfig.getStatusReportIntervalMilis(anyLong()))
                .thenReturn(DEFAULT_REPORT_INTERVAL);

        lenient().when(mockApiTestHarnessInstance.getResultsReporter()).thenReturn(mockResultsReporter);

        APIMonitor.setDoMonitor(true);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockedApiHarness.close();
        restoreStaticStatus();
        APIMonitor.setDoMonitor(true);
    }

    private void invokeUpdateInstanceStatus(APIMonitor instance) throws Exception {
        Method method = APIMonitor.class.getDeclaredMethod("updateInstanceStatus");
        method.setAccessible(true);
        method.invoke(instance);
    }

    private JobStatus invokeCalculateJobStatus(WatsAgentStatusResponse agentStatus, JobStatus currentStatus) throws Exception {
        Method method = APIMonitor.class.getDeclaredMethod("calculateJobStatus", WatsAgentStatusResponse.class, JobStatus.class);
        method.setAccessible(true);
        return (JobStatus) method.invoke(apiMonitorInstance, agentStatus, currentStatus);
    }

    private void invokeSendTps(TPSInfoContainer tpsInfo) throws Exception {
        Method method = APIMonitor.class.getDeclaredMethod("sendTps", TPSInfoContainer.class);
        method.setAccessible(true);
        method.invoke(apiMonitorInstance, tpsInfo);
    }

    private CloudVmStatus invokeCreateStatus(WatsAgentStatusResponse agentStatus) throws Exception {
        Method method = APIMonitor.class.getDeclaredMethod("createStatus", WatsAgentStatusResponse.class);
        method.setAccessible(true);
        return (CloudVmStatus) method.invoke(apiMonitorInstance, agentStatus);
    }

    private long getReportInterval(APIMonitor instance) throws Exception {
        if (reportIntervalField == null) {
            reportIntervalField = APIMonitor.class.getDeclaredField("reportInterval");
            reportIntervalField.setAccessible(true);
        }
        return (long) reportIntervalField.get(instance);
    }

    private void setReportInterval(APIMonitor instance, long interval) throws Exception {
        if (reportIntervalField == null) {
            reportIntervalField = APIMonitor.class.getDeclaredField("reportInterval");
            reportIntervalField.setAccessible(true);
        }
        reportIntervalField.set(instance, interval);
    }

    private static void setStaticStatus(CloudVmStatus newStatus) throws Exception {
        if (statusField == null) {
            statusField = APIMonitor.class.getDeclaredField("status");
            statusField.setAccessible(true);
            originalStaticStatusValue = statusField.get(null);
        }
        statusField.set(null, newStatus);
    }

    private static void resetStaticStatus() throws Exception {
        if (statusField != null) {
            statusField.set(null, null);
        }

        statusField = null;
        originalStaticStatusValue = null;
    }

    private static void restoreStaticStatus() throws Exception {
        if (statusField != null) {
            statusField.set(null, originalStaticStatusValue);
        }
        statusField = null;
        originalStaticStatusValue = null;
    }

    private static CloudVmStatus getStaticStatus() throws Exception {
        if (statusField == null) {
            statusField = APIMonitor.class.getDeclaredField("status");
            statusField.setAccessible(true);
        }
        return (CloudVmStatus) statusField.get(null);
    }

    private static void invokeSetInstanceStatus(String instanceId, CloudVmStatus vmStatus) throws Exception {
        Method method = APIMonitor.class.getDeclaredMethod("setInstanceStatus", String.class, CloudVmStatus.class);
        method.setAccessible(true);
        method.invoke(null, instanceId, vmStatus);
    }

    @Test
    @DisplayName("Constructor should handle exceptions during report interval fetching")
    void constructor_HandlesConfigException() throws Exception {
        RuntimeException configException = new RuntimeException("Simulated interval fetch error");

        when(mockAgentConfig.getStatusReportIntervalMilis(anyLong()))
                .thenThrow(configException);

        APIMonitor instance = null;
        try {
            instance = new APIMonitor(false, mockInitialVmStatus);
        } catch (Exception e) {
            fail("Constructor should have caught the exception, but it was thrown.", e);
        }

        assertNotNull(instance, "Instance should be created even if config fetch fails");
        assertEquals(MIN_REPORT_TIME_CONST, getReportInterval(instance),
                "reportInterval should retain MIN_REPORT_TIME if fetching fails");
    }

    @Nested
    @DisplayName("run Method Tests")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class RunTests {

        private AtomicInteger getStatusCount;
        private AtomicInteger checkThreadsCount;

        @BeforeEach
        void setupRunTests() throws Exception {
            getStatusCount = new AtomicInteger(0);
            checkThreadsCount = new AtomicInteger(0);

            apiMonitorInstance = new APIMonitor(true, mockInitialVmStatus);
            setReportInterval(apiMonitorInstance, 1L);
            assertEquals(1L, getReportInterval(apiMonitorInstance), "Verify interval is set short");

            when(mockStaticStatus.getInstanceId()).thenReturn("i-run-test-local");
            when(mockStaticStatus.getJobId()).thenReturn("job-run-test-local");
            when(mockStaticStatus.getSecurityGroup()).thenReturn("sg-run-test");
            when(mockStaticStatus.getJobStatus()).thenReturn(JobStatus.Running);
            when(mockStaticStatus.getRole()).thenReturn(VMImageType.AGENT);
            when(mockStaticStatus.getVmRegion()).thenReturn(VMRegion.US_EAST_2);
            when(mockStaticStatus.getVmStatus()).thenReturn(VMStatus.running);
            when(mockStaticStatus.getStartTime()).thenReturn(new Date());
            when(mockStaticStatus.getEndTime()).thenReturn(null);
            setStaticStatus(mockStaticStatus);

            when(mockAgentStatusResponse.getKills()).thenReturn(0);
            when(mockAgentStatusResponse.getAborts()).thenReturn(0);
            when(mockAgentStatusResponse.getGotos()).thenReturn(0);
            when(mockAgentStatusResponse.getSkips()).thenReturn(0);
            when(mockAgentStatusResponse.getSkipGroups()).thenReturn(0);
            when(mockAgentStatusResponse.getRestarts()).thenReturn(0);
            when(mockAgentStatusResponse.getMaxVirtualUsers()).thenReturn(100);
            when(mockAgentStatusResponse.getCurrentNumberUsers()).thenReturn(50);

            when(mockTpsMonitor.getTPSInfo()).thenReturn(null);

            lenient().when(mockApiTestHarnessInstance.getStatus()).thenAnswer(invocation -> {
                getStatusCount.incrementAndGet();
                return mockAgentStatusResponse;
            });
            lenient().doAnswer(invocation -> {
                checkThreadsCount.incrementAndGet();
                return null;
            }).when(mockApiTestHarnessInstance).checkAgentThreads();
        }

        @Test
        @DisplayName("Should perform one loop iteration and final update when doMonitor becomes false")
        void run_PerformsOneLoopAndFinalUpdate() throws Exception {
            APIMonitor.setDoMonitor(true);
            AtomicBoolean stopped = new AtomicBoolean(false);
            
            doAnswer(invocation -> {
                checkThreadsCount.incrementAndGet();
                if (stopped.compareAndSet(false, true)) {
                    APIMonitor.setDoMonitor(false);
                }
                return null;
            }).when(mockApiTestHarnessInstance).checkAgentThreads();

            apiMonitorInstance.run();

            assertEquals(2, getStatusCount.get(), "getStatus should be called twice");
            assertEquals(2, checkThreadsCount.get(), "checkAgentThreads should be called twice");
        }

        @Test
        @DisplayName("Should run only final updateInstanceStatus if doMonitor starts false (isLocal=true)")
        void run_DoesNotLoopIfDoMonitorIsFalseInitially() throws Exception {
            APIMonitor.setDoMonitor(false);
            getStatusCount.set(0);
            checkThreadsCount.set(0);

            apiMonitorInstance.run();

            assertEquals(1, getStatusCount.get(), "getStatus should be called once");
            assertEquals(1, checkThreadsCount.get(), "checkAgentThreads should be called once");
        }
    }

    @Nested
    @DisplayName("calculateJobStatus Tests")
    class CalculateJobStatusTests {
        @BeforeEach
        void createInstance() {
            apiMonitorInstance = new APIMonitor(false, mockInitialVmStatus);
        }

        @Test
        @DisplayName("Should return Paused when command is pause")
        void calculateJobStatus_CmdPause_ReturnsPaused() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.pause);
            JobStatus currentStatus = JobStatus.Running;

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(JobStatus.Paused, newStatus);
        }

        @Test
        @DisplayName("Should return Stopped when command is stop")
        void calculateJobStatus_CmdStop_ReturnsStopped() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.stop);
            JobStatus currentStatus = JobStatus.Running;

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(JobStatus.Stopped, newStatus);
        }

        @Test
        @DisplayName("Should return RampPaused when command is pause_ramp")
        void calculateJobStatus_CmdPauseRamp_ReturnsRampPaused() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.pause_ramp);
            JobStatus currentStatus = JobStatus.Running;

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(JobStatus.RampPaused, newStatus);
        }

        @Test
        @DisplayName("Should return Running when command is other and current status is Unknown")
        void calculateJobStatus_CmdOther_CurrentUnknown_ReturnsRunning() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
            JobStatus currentStatus = JobStatus.Unknown;

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(JobStatus.Running, newStatus);
        }

        @Test
        @DisplayName("Should return Running when command is other, current status is Starting, and users > 0")
        void calculateJobStatus_CmdOther_CurrentStarting_UsersExist_ReturnsRunning() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
            JobStatus currentStatus = JobStatus.Starting;
            when(mockAgentStatusResponse.getCurrentNumberUsers()).thenReturn(10);

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(JobStatus.Running, newStatus);
        }

        @Test
        @DisplayName("Should return Starting when command is other, current status is Starting, and users = 0")
        void calculateJobStatus_CmdOther_CurrentStarting_NoUsers_ReturnsStarting() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
            JobStatus currentStatus = JobStatus.Starting;
            when(mockAgentStatusResponse.getCurrentNumberUsers()).thenReturn(0);

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(JobStatus.Starting, newStatus, "Should remain Starting if no users are running yet");
        }

        @Test
        @DisplayName("Should return current status when command is other and current status is not Unknown or Starting")
        void calculateJobStatus_CmdOther_CurrentStable_ReturnsCurrent() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
            JobStatus currentStatus = JobStatus.Running;

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(currentStatus, newStatus, "Should return the existing status if it's stable and cmd isn't overriding");
        }

        @Test
        @DisplayName("Should return Paused when command is other and current status is Paused")
        void calculateJobStatus_CmdOther_CurrentPaused_ReturnsPaused() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
            JobStatus currentStatus = JobStatus.Paused;

            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(currentStatus, newStatus);
        }

        @Test
        @DisplayName("Should return Completed when command is other and current status is Completed")
        void calculateJobStatus_CmdOther_CurrentCompleted_ReturnsCompleted() throws Exception {
            when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
            JobStatus currentStatus = JobStatus.Completed;
            JobStatus newStatus = invokeCalculateJobStatus(mockAgentStatusResponse, currentStatus);
            assertEquals(currentStatus, newStatus);
        }
    }

    @Test
    @DisplayName("sendTps should call resultsReporter with correct arguments")
    void sendTps_CallsReporterCorrectly() throws Exception {
        apiMonitorInstance = new APIMonitor(false, mockInitialVmStatus);
        String expectedJobId = "job-123";
        String expectedInstanceId = "i-abcde";

        when(mockAgentRunData.getJobId()).thenReturn(expectedJobId);
        when(mockAgentRunData.getInstanceId()).thenReturn(expectedInstanceId);

        invokeSendTps(mockTpsInfoContainer);

        verify(mockResultsReporter, times(1)).sendTpsResults(
                eq(expectedJobId),
                eq(expectedInstanceId),
                eq(mockTpsInfoContainer),
                eq(true)
        );
    }

    @Test
    @DisplayName("createStatus should correctly populate CloudVmStatus")
    void createStatus_PopulatesFieldsCorrectly() throws Exception {
        apiMonitorInstance = new APIMonitor(false, mockInitialVmStatus);
        String expectedInstanceId = "i-static-123";
        String expectedJobId = "job-static-456";
        String expectedSecurityGroup = "sg-static";
        JobStatus initialJobStatus = JobStatus.Starting;
        VMImageType expectedRole = VMImageType.AGENT;
        VMRegion expectedRegion = VMRegion.US_WEST_2;
        VMStatus expectedVmStatus = VMStatus.running;
        Date expectedStartTime = new Date(System.currentTimeMillis() - 100000);
        Date expectedEndTime = new Date(System.currentTimeMillis() - 200000);

        when(mockStaticStatus.getInstanceId()).thenReturn(expectedInstanceId);
        when(mockStaticStatus.getJobId()).thenReturn(expectedJobId);
        when(mockStaticStatus.getSecurityGroup()).thenReturn(expectedSecurityGroup);
        when(mockStaticStatus.getJobStatus()).thenReturn(initialJobStatus);
        when(mockStaticStatus.getRole()).thenReturn(expectedRole);
        when(mockStaticStatus.getVmRegion()).thenReturn(expectedRegion);
        when(mockStaticStatus.getVmStatus()).thenReturn(expectedVmStatus);
        when(mockStaticStatus.getStartTime()).thenReturn(expectedStartTime);
        when(mockStaticStatus.getEndTime()).thenReturn(expectedEndTime);

        setStaticStatus(mockStaticStatus);

        int expectedKills = 1;
        int expectedAborts = 2;
        int expectedGotos = 3;
        int expectedSkips = 4;
        int expectedSkipGroups = 5;
        int expectedRestarts = 6;
        int expectedMaxVirtualUsers = 100;
        int expectedCurrentVirtualUsers = 50;

        when(mockAgentStatusResponse.getKills()).thenReturn(expectedKills);
        when(mockAgentStatusResponse.getAborts()).thenReturn(expectedAborts);
        when(mockAgentStatusResponse.getGotos()).thenReturn(expectedGotos);
        when(mockAgentStatusResponse.getSkips()).thenReturn(expectedSkips);
        when(mockAgentStatusResponse.getSkipGroups()).thenReturn(expectedSkipGroups);
        when(mockAgentStatusResponse.getRestarts()).thenReturn(expectedRestarts);
        when(mockAgentStatusResponse.getMaxVirtualUsers()).thenReturn(expectedMaxVirtualUsers);
        when(mockAgentStatusResponse.getCurrentNumberUsers()).thenReturn(expectedCurrentVirtualUsers);

        when(mockApiTestHarnessInstance.getCmd()).thenReturn(AgentCommand.run);
        JobStatus expectedCalculatedJobStatus = JobStatus.Running;

        CloudVmStatus resultStatus = invokeCreateStatus(mockAgentStatusResponse);

        assertNotNull(resultStatus);
        assertEquals(expectedInstanceId, resultStatus.getInstanceId());
        assertEquals(expectedJobId, resultStatus.getJobId());
        assertEquals(expectedSecurityGroup, resultStatus.getSecurityGroup());
        assertEquals(expectedCalculatedJobStatus, resultStatus.getJobStatus());
        assertEquals(expectedRole, resultStatus.getRole());
        assertEquals(expectedRegion, resultStatus.getVmRegion());
        assertEquals(expectedVmStatus, resultStatus.getVmStatus());
        assertEquals(expectedStartTime, resultStatus.getStartTime());
        assertEquals(expectedEndTime, resultStatus.getEndTime());
        assertEquals(expectedMaxVirtualUsers, resultStatus.getTotalUsers());
        assertEquals(expectedCurrentVirtualUsers, resultStatus.getCurrentUsers());

        ValidationStatus validationStatus = resultStatus.getValidationFailures();
        assertNotNull(validationStatus);
        assertEquals(expectedKills, validationStatus.getValidationKills());
        assertEquals(expectedAborts, validationStatus.getValidationAborts());
        assertEquals(expectedGotos, validationStatus.getValidationGotos());
        assertEquals(expectedSkips, validationStatus.getValidationSkips());
        assertEquals(expectedSkipGroups, validationStatus.getValidationSkipGroups());
        assertEquals(expectedRestarts, validationStatus.getValidationRestarts());
    }


    @Nested
    @DisplayName("setJobStatus Tests")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class SetJobStatusTests {
        private MockedStatic<APIMonitor> mockedApiMonitor;

        @BeforeEach
        void setupInitialStatusAndMocks() throws Exception {
            mockedApiMonitor = Mockito.mockStatic(APIMonitor.class);
            mockedApiMonitor.when(() -> APIMonitor.setJobStatus(any(JobStatus.class))).thenCallRealMethod();
            mockedApiMonitor.when(() -> APIMonitor.setDoMonitor(anyBoolean())).thenCallRealMethod();

            mockedApiMonitor.when(() -> APIMonitor.setInstanceStatus(anyString(), any(CloudVmStatus.class)))
                    .thenAnswer(invocation -> null);

            when(mockStaticStatus.getJobStatus()).thenReturn(JobStatus.Starting);
            when(mockStaticStatus.getInstanceId()).thenReturn("i-56789");
            when(mockStaticStatus.getJobId()).thenReturn("12345");
            when(mockStaticStatus.getSecurityGroup()).thenReturn("sg-123");
            when(mockStaticStatus.getRole()).thenReturn(VMImageType.AGENT);
            when(mockStaticStatus.getVmRegion()).thenReturn(VMRegion.US_WEST_2);
            when(mockStaticStatus.getVmStatus()).thenReturn(VMStatus.starting);
            when(mockStaticStatus.getStartTime()).thenReturn(new Date(System.currentTimeMillis() - 5000));
            when(mockStaticStatus.getEndTime()).thenReturn(null);
            setStaticStatus(mockStaticStatus);

            when(mockAgentStatusResponse.getKills()).thenReturn(1);
            when(mockAgentStatusResponse.getAborts()).thenReturn(2);
            when(mockAgentStatusResponse.getGotos()).thenReturn(3);
            when(mockAgentStatusResponse.getSkips()).thenReturn(4);
            when(mockAgentStatusResponse.getSkipGroups()).thenReturn(5);
            when(mockAgentStatusResponse.getRestarts()).thenReturn(6);
            when(mockAgentStatusResponse.getMaxVirtualUsers()).thenReturn(100);
            when(mockAgentStatusResponse.getCurrentNumberUsers()).thenReturn(10);
        }

        @AfterEach
        void tearDownNested() {
            if (mockedApiMonitor != null) mockedApiMonitor.close();
        }

        @Test
        @DisplayName("Should do nothing if initial static status is null")
        void setJobStatus_StaticStatusNull_DoesNothing() throws Exception {
            setStaticStatus(null);
            APIMonitor.setJobStatus(JobStatus.Running);
            assertNull(getStaticStatus());
            mockedApiMonitor.verify(() -> APIMonitor.setInstanceStatus(anyString(), any()), never());
        }

        @Test
        @DisplayName("Should do nothing if initial static status is Completed")
        void setJobStatus_StaticStatusCompleted_DoesNothing() throws Exception {
            when(mockStaticStatus.getJobStatus()).thenReturn(JobStatus.Completed);
            setStaticStatus(mockStaticStatus);
            CloudVmStatus initialStatusSnapshot = getStaticStatus();

            APIMonitor.setJobStatus(JobStatus.Running);

            assertEquals(initialStatusSnapshot, getStaticStatus());
            mockedApiMonitor.verify(() -> APIMonitor.setInstanceStatus(anyString(), any()), never());
        }

        @Test
        @DisplayName("Should update status to Running")
        void setJobStatus_ToRunning_UpdatesState() throws Exception {
            APIMonitor.setJobStatus(JobStatus.Running);

            CloudVmStatus finalStatus = getStaticStatus();
            assertNotNull(finalStatus);
            assertEquals(JobStatus.Running, finalStatus.getJobStatus());
            assertEquals(VMStatus.running, finalStatus.getVmStatus());
            assertNull(finalStatus.getEndTime());
            assertEquals(mockUserSnapshot, finalStatus.getUserDetails());

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockUserTracker).getSnapshot();
            ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<CloudVmStatus> statusCaptor = ArgumentCaptor.forClass(CloudVmStatus.class);
            mockedApiMonitor.verify(() -> APIMonitor.setInstanceStatus(idCaptor.capture(), statusCaptor.capture()), times(1));

            assertEquals("i-56789", idCaptor.getValue());
            assertNotNull(statusCaptor.getValue());
            assertEquals(JobStatus.Running, statusCaptor.getValue().getJobStatus());
            assertEquals(mockUserSnapshot, statusCaptor.getValue().getUserDetails());
        }

        @Test
        @DisplayName("Should update status to Stopped")
        void setJobStatus_ToStopped_UpdatesState() throws Exception {
            APIMonitor.setJobStatus(JobStatus.Stopped);

            CloudVmStatus finalStatus = getStaticStatus();
            assertNotNull(finalStatus);
            assertEquals(JobStatus.Stopped, finalStatus.getJobStatus());
            assertEquals(VMStatus.stopping, finalStatus.getVmStatus());
            assertNull(finalStatus.getEndTime());
            assertEquals(mockUserSnapshot, finalStatus.getUserDetails());

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockUserTracker).getSnapshot();
            ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<CloudVmStatus> statusCaptor = ArgumentCaptor.forClass(CloudVmStatus.class);
            mockedApiMonitor.verify(() -> APIMonitor.setInstanceStatus(idCaptor.capture(), statusCaptor.capture()), times(1));

            assertEquals("i-56789", idCaptor.getValue());
            assertNotNull(statusCaptor.getValue());
            assertEquals(JobStatus.Stopped, statusCaptor.getValue().getJobStatus());
            assertEquals(mockUserSnapshot, statusCaptor.getValue().getUserDetails());
        }

        @Test
        @DisplayName("Should update status to Completed and set EndTime")
        void setJobStatus_ToCompleted_UpdatesStateSetsEndTime() throws Exception {
            long timeBefore = System.currentTimeMillis();

            APIMonitor.setJobStatus(JobStatus.Completed);
            CloudVmStatus finalStatus = getStaticStatus();
            assertNotNull(finalStatus);
            assertEquals(JobStatus.Completed, finalStatus.getJobStatus());
            assertEquals(VMStatus.terminated, finalStatus.getVmStatus());
            assertNotNull(finalStatus.getEndTime());
            assertTrue(finalStatus.getEndTime().getTime() >= timeBefore);
            assertEquals(mockUserSnapshot, finalStatus.getUserDetails());

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockUserTracker).getSnapshot();
            ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<CloudVmStatus> statusCaptor = ArgumentCaptor.forClass(CloudVmStatus.class);
            mockedApiMonitor.verify(() -> APIMonitor.setInstanceStatus(idCaptor.capture(), statusCaptor.capture()), times(1));

            assertEquals("i-56789", idCaptor.getValue());
            assertNotNull(statusCaptor.getValue());
            assertEquals(JobStatus.Completed, statusCaptor.getValue().getJobStatus());
            assertEquals(mockUserSnapshot, statusCaptor.getValue().getUserDetails());
        }

        @Test
        @DisplayName("Should handle exception during processing gracefully")
        void setJobStatus_HandlesInternalException() throws Exception {
            RuntimeException internalException = new RuntimeException("Failed getting agent status");
            when(mockApiTestHarnessInstance.getStatus()).thenThrow(internalException);
            CloudVmStatus statusBefore = getStaticStatus();

            assertDoesNotThrow(
                    () -> APIMonitor.setJobStatus(JobStatus.Running),
                    "setJobStatus should catch internal exceptions"
            );

            assertEquals(statusBefore, getStaticStatus());
            mockedApiMonitor.verify(() -> APIMonitor.setInstanceStatus(anyString(), any()), never());
        }
    }

    @Nested
    @DisplayName("setInstanceStatus Tests")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class SetInstanceStatusTests {

        private final String DEFAULT_INSTANCE_ID = "i-567890231";

        @BeforeEach
        void setupSetInstanceStatusMocks() {
            when(mockAgentConfig.getAgentToken()).thenReturn("test-token-123");
            when(mockTankConfig.getControllerBase()).thenReturn("http://test.controller:9090");
        }

        @Test
        @DisplayName("Should build and send correct HTTP PUT request")
        void setInstanceStatus_HappyPath_BuildsAndSendsRequest() throws Exception {
            CloudVmStatus statusToSend = new CloudVmStatus(DEFAULT_INSTANCE_ID, "4215", "sg-abc",
                    JobStatus.Running, VMImageType.AGENT, VMRegion.US_EAST_2, VMStatus.running,
                    new ValidationStatus(0,0,0,0,0,0), 10, 5, new Date(), null);

            UserDetail userDetail = new UserDetail("testScript", 994);
            List <UserDetail> userDetails = List.of(userDetail);
            statusToSend.setUserDetails(userDetails);
            assertDoesNotThrow(() -> {
                invokeSetInstanceStatus(DEFAULT_INSTANCE_ID, statusToSend);
            }, "Invocation should proceed without error until potentially HttpClient call");

            verify(mockAgentConfig).getAgentToken();
            verify(mockTankConfig).getControllerBase();
        }

        @Test
        @DisplayName("Should throw URISyntaxException for invalid base URL")
        void setInstanceStatus_InvalidBaseUrl_ThrowsURISyntaxException() {
            String invalidUrl = "http://bad url spaces:9090";
            when(mockTankConfig.getControllerBase()).thenReturn(invalidUrl);
            CloudVmStatus statusToSend = new CloudVmStatus(DEFAULT_INSTANCE_ID, null, null, null, null, null, null, null, 0, 0, null, null);

            InvocationTargetException wrapperException = assertThrows(InvocationTargetException.class, () -> {
                invokeSetInstanceStatus(DEFAULT_INSTANCE_ID, statusToSend);
            }, "Reflection call should wrap the target exception");

            Throwable cause = wrapperException.getCause();
            assertNotNull(cause, "InvocationTargetException should have a cause");
            assertInstanceOf(URISyntaxException.class, cause, "Cause should be URISyntaxException");

            assertTrue(cause.getMessage().contains("Illegal character in authority") || cause.getMessage().contains("Illegal character in URL"),
                    "Cause message should indicate the syntax error. Actual: " + cause.getMessage());
        }
    }

    @Nested
    @DisplayName("updateInstanceStatus Tests")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class UpdateInstanceStatusTests {

        private MockedStatic<APIMonitor> mockedApiMonitor;
        private volatile boolean setInstanceStatusIntercepted;
        private volatile String interceptedInstanceId;
        private volatile CloudVmStatus interceptedStatus;
        private APIMonitor apiMonitorInstance;

        @BeforeEach
        void setupUpdateStatusTests() throws Exception {
            setInstanceStatusIntercepted = false;
            interceptedInstanceId = null;
            interceptedStatus = null;

            apiMonitorInstance = new APIMonitor(false, mockInitialVmStatus);

            mockedApiMonitor = Mockito.mockStatic(APIMonitor.class);
            mockedApiMonitor.when(() -> APIMonitor.setJobStatus(any(JobStatus.class))).thenCallRealMethod();
            mockedApiMonitor.when(() -> APIMonitor.setDoMonitor(anyBoolean())).thenCallRealMethod();

            mockedApiMonitor.when(() -> APIMonitor.setInstanceStatus(anyString(), any(CloudVmStatus.class)))
                    .thenAnswer(invocation -> {
                        this.setInstanceStatusIntercepted = true;
                        this.interceptedInstanceId = invocation.getArgument(0);
                        this.interceptedStatus = invocation.getArgument(1);
                        return null;
                    });

            when(mockStaticStatus.getInstanceId()).thenReturn("i-531543124");
            when(mockStaticStatus.getJobStatus()).thenReturn(JobStatus.Running);
            when(mockStaticStatus.getJobId()).thenReturn("5644");
            when(mockStaticStatus.getSecurityGroup()).thenReturn("sg-321421");
            when(mockStaticStatus.getRole()).thenReturn(VMImageType.AGENT);
            when(mockStaticStatus.getVmRegion()).thenReturn(VMRegion.US_EAST_2);
            when(mockStaticStatus.getVmStatus()).thenReturn(VMStatus.running);
            when(mockStaticStatus.getStartTime()).thenReturn(new Date());
            setStaticStatus(mockStaticStatus);
        }

        @AfterEach
        void tearDownUpdateStatusTests() {
            if (mockedApiMonitor != null) mockedApiMonitor.close();
        }

        @Test
        @DisplayName("isLocal=true, no TPS info")
        void updateInstanceStatus_Local_NoTps() throws Exception {
            apiMonitorInstance = new APIMonitor(true, mockInitialVmStatus);
            when(mockTpsMonitor.getTPSInfo()).thenReturn(null);

            invokeUpdateInstanceStatus(apiMonitorInstance);

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockApiTestHarnessInstance).getUserTracker();
            verify(mockUserTracker).getSnapshot();
            verify(mockApiTestHarnessInstance).getTPSMonitor();
            verify(mockTpsMonitor).getTPSInfo();
            verify(mockApiTestHarnessInstance).checkAgentThreads();
            verify(mockResultsReporter, never()).sendTpsResults(anyString(), anyString(), any(), anyBoolean());

            assertFalse(setInstanceStatusIntercepted, "setInstanceStatus should not have been called");
        }

        @Test
        @DisplayName("isLocal=true, with TPS info")
        void updateInstanceStatus_Local_WithTps() throws Exception {
            apiMonitorInstance = new APIMonitor(true, mockInitialVmStatus);
            when(mockTpsMonitor.getTPSInfo()).thenReturn(mockTpsInfoContainer);
            when(mockTpsInfoContainer.getTotalTps()).thenReturn(123);

            when(mockAgentRunData.getJobId()).thenReturn("4241");
            when(mockAgentRunData.getInstanceId()).thenReturn("i-421521421");

            invokeUpdateInstanceStatus(apiMonitorInstance);

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockApiTestHarnessInstance).getUserTracker();
            verify(mockUserTracker).getSnapshot();
            verify(mockApiTestHarnessInstance).getTPSMonitor();
            verify(mockTpsMonitor).getTPSInfo();
            verify(mockTpsInfoContainer).getTotalTps();
            verify(mockApiTestHarnessInstance).checkAgentThreads();
            verify(mockResultsReporter, times(1)).sendTpsResults(
                    eq("4241"), eq("i-421521421"), eq(mockTpsInfoContainer), eq(true)
            );

            assertFalse(setInstanceStatusIntercepted, "setInstanceStatus should not have been called");
        }

        @Test
        @DisplayName("isLocal=false, no TPS info")
        void updateInstanceStatus_Remote_NoTps() throws Exception {
            String expectedInstanceId = "i-531543124";
            when(mockTpsMonitor.getTPSInfo()).thenReturn(null);

            invokeUpdateInstanceStatus(apiMonitorInstance);

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockApiTestHarnessInstance).getUserTracker();
            verify(mockUserTracker).getSnapshot();
            verify(mockApiTestHarnessInstance).getTPSMonitor();
            verify(mockTpsMonitor).getTPSInfo();
            verify(mockApiTestHarnessInstance).checkAgentThreads();
            verify(mockResultsReporter, never()).sendTpsResults(anyString(), anyString(), any(), anyBoolean());

            assertTrue(setInstanceStatusIntercepted, "setInstanceStatus should have been called");
            assertEquals(expectedInstanceId, interceptedInstanceId);
            assertNotNull(interceptedStatus);
            assertEquals(mockUserSnapshot, interceptedStatus.getUserDetails());
        }

        @Test
        @DisplayName("isLocal=false, with TPS info")
        void updateInstanceStatus_Remote_WithTps() throws Exception {
            String expectedInstanceId = "i-531543124";
            int expectedTps = 45;
            when(mockTpsMonitor.getTPSInfo()).thenReturn(mockTpsInfoContainer);
            when(mockTpsInfoContainer.getTotalTps()).thenReturn(expectedTps);
            when(mockAgentRunData.getInstanceId()).thenReturn(expectedInstanceId);
            when(mockAgentRunData.getJobId()).thenReturn("job-update");

            invokeUpdateInstanceStatus(apiMonitorInstance);

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockApiTestHarnessInstance).getUserTracker();
            verify(mockUserTracker).getSnapshot();
            verify(mockApiTestHarnessInstance).getTPSMonitor();
            verify(mockTpsMonitor).getTPSInfo();
            verify(mockTpsInfoContainer).getTotalTps();
            verify(mockApiTestHarnessInstance).checkAgentThreads();
            verify(mockResultsReporter, times(1)).sendTpsResults(
                    eq("job-update"), eq(expectedInstanceId), eq(mockTpsInfoContainer), eq(true)
            );

            assertTrue(setInstanceStatusIntercepted, "setInstanceStatus should have been called");
            assertEquals(expectedInstanceId, interceptedInstanceId);
            assertNotNull(interceptedStatus);
            assertEquals(mockUserSnapshot, interceptedStatus.getUserDetails());
            assertEquals(expectedTps, interceptedStatus.getTotalTps());
        }

        @Test
        @DisplayName("Should handle exception during processing gracefully")
        void updateInstanceStatus_HandlesInternalException() throws Exception {
            RuntimeException internalError = new RuntimeException("Failed to get user snapshot");
            when(mockApiTestHarnessInstance.getUserTracker()).thenThrow(internalError);

            assertDoesNotThrow(
                    () -> invokeUpdateInstanceStatus(apiMonitorInstance),
                    "updateInstanceStatus should catch internal exceptions"
            );

            verify(mockApiTestHarnessInstance).getStatus();
            verify(mockApiTestHarnessInstance).getUserTracker();
            verify(mockTpsMonitor, never()).getTPSInfo();
            verify(mockApiTestHarnessInstance, never()).checkAgentThreads();

            assertFalse(setInstanceStatusIntercepted, "setInstanceStatus should not have been called");
        }
    }
}