package com.intuit.tank.harness;

import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.*;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.harness.AgentRunData;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APIMonitorTest {

    @Mock private APITestHarness mockApiTestHarnessInstance;
    @Mock private TankConfig mockTankConfig;
    @Mock private AgentConfig mockAgentConfig;
    @Mock private WatsAgentStatusResponse mockAgentStatusResponse;
    @Mock private CloudVmStatus mockInitialVmStatus; // For constructor
    @Mock private AgentRunData mockAgentRunData;
    @Mock private LoggingProfile mockLoggingProfile;
    @Mock private ResultsReporter mockResultsReporter;
    @Mock private TPSInfoContainer mockTpsInfoContainer;
    @Mock private CloudVmStatus mockStaticStatus;


    private MockedStatic<APITestHarness> mockedApiHarness;

    private APIMonitor apiMonitorInstance;

    private static final long DEFAULT_REPORT_INTERVAL = 15000L;

    private static Field statusField;
    private static Object originalStaticStatusValue;



    @BeforeEach
    void setUp() throws Exception {
        resetStaticStatus();

        mockedApiHarness = Mockito.mockStatic(APITestHarness.class);
        mockedApiHarness.when(APITestHarness::getInstance).thenReturn(mockApiTestHarnessInstance);

        // lenient() needed only for logging side effects during setup
        lenient().when(mockApiTestHarnessInstance.getAgentRunData()).thenReturn(mockAgentRunData);
        lenient().when(mockAgentRunData.getActiveProfile()).thenReturn(mockLoggingProfile);

        when(mockApiTestHarnessInstance.getTankConfig()).thenReturn(mockTankConfig);
        when(mockTankConfig.getAgentConfig()).thenReturn(mockAgentConfig);
        when(mockAgentConfig.getStatusReportIntervalMilis(anyLong()))
                .thenReturn(DEFAULT_REPORT_INTERVAL);

        lenient().when(mockApiTestHarnessInstance.getResultsReporter()).thenReturn(mockResultsReporter);


        apiMonitorInstance = new APIMonitor(false, mockInitialVmStatus);
        APIMonitor.setDoMonitor(true);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockedApiHarness.close();
        restoreStaticStatus();
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


    @Nested
    @DisplayName("calculateJobStatus Tests")
    class CalculateJobStatusTests {

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
            // Arrange
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
        when(mockStaticStatus.getJobStatus()).thenReturn(initialJobStatus); //
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
}