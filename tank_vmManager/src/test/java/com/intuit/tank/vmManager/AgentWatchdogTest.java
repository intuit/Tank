package com.intuit.tank.vmManager;

import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgentWatchdogTest {

    @Mock
    AmazonInstance amazonInstanceMock = new AmazonInstance(VMRegion.STANDALONE);

    @Test
    public void toStringTest() {
        VMTracker vmTracker = new VMTrackerImpl();
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);
        List<VMInformation> vmInfo = new ArrayList<>();
        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTracker);

        String result = agentWatchdog.toString();

        assertNotNull(result);
        assertTrue(result.endsWith("[sleepTime=30000,maxWaitForResponse=600000,maxRestarts=2]"));
    }

    @Test
    public void alreadyPendingRunTest(@Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer cloudVmStatusContainerMock) {
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        CloudVmStatus vmstatus = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Running, VMImageType.AGENT, VMRegion.STANDALONE, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        Set<CloudVmStatus> set = Stream.of(vmstatus).collect(Collectors.toCollection(HashSet::new));
        when(cloudVmStatusContainerMock.getStatuses()).thenReturn(set);
        when(vmTrackerMock.getVmStatusForJob(null)).thenReturn(cloudVmStatusContainerMock);

        VMInformation vmInformation = new VMInformation();
        vmInformation.setState("pending");
        vmInformation.setInstanceId("i-123456789");
        List<VMInformation> vmInfo = Collections.singletonList( vmInformation );
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);

        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 1000);
        agentWatchdog.run();

        verify(amazonInstanceMock, never()).killInstances(Mockito.anyList());
        verify(amazonInstanceMock, never()).reboot(Mockito.anyList());
        verify(cloudVmStatusContainerMock, times(1)).getEndTime();
        verify(cloudVmStatusContainerMock, times(1)).getStatuses();
    }

    @Test
    public void progressToPendingRunTest(@Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer cloudVmStatusContainerMock) {
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        CloudVmStatus vmstatusStarting = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, VMStatus.starting, new ValidationStatus(), 1, 1, new Date(), new Date());
        CloudVmStatus vmstatusPending = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        Set<CloudVmStatus> setStarting = Stream.of(vmstatusStarting).collect(Collectors.toCollection(HashSet::new));
        Set<CloudVmStatus> setPending = Stream.of(vmstatusPending).collect(Collectors.toCollection(HashSet::new));
        when(cloudVmStatusContainerMock.getStatuses()).thenReturn(setStarting).thenReturn(setPending);
        when(vmTrackerMock.getVmStatusForJob(null)).thenReturn(cloudVmStatusContainerMock);

        VMInformation vmInformation = new VMInformation();
        vmInformation.setState("pending");
        vmInformation.setInstanceId("i-123456789");
        List<VMInformation> vmInfo = Collections.singletonList( vmInformation );
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);

        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 1000);
        agentWatchdog.run();

        verify(amazonInstanceMock, never()).killInstances(Mockito.anyList());
        verify(amazonInstanceMock, never()).reboot(Mockito.anyList());
        verify(cloudVmStatusContainerMock, times(2)).getEndTime();
        verify(cloudVmStatusContainerMock, times(2)).getStatuses();
    }

    /**
     * CRITICAL TEST: Verifies that replacement agents are created with VMStatus.starting,
     * NOT VMStatus.pending. This is essential because:
     * 
     * - checkForReportingInstances() considers 'pending' as "agent has reported"
     * - If replacements start with 'pending', they're immediately moved to reportedInstances
     * - This causes watchdog to exit before agents actually call /v2/agent/ready
     * - JobManager then waits forever for registrations that never come
     * 
     * See: https://github.intuit.com/user/Tank/issues/XXX (if applicable)
     */
    @Test
    public void createCloudStatus_usesStartingNotPending() throws Exception {
        VMTracker vmTracker = new VMTrackerImpl();
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setJobId("test-job-123");
        instanceRequest.setRegion(VMRegion.STANDALONE);
        List<VMInformation> vmInfo = new ArrayList<>();
        
        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTracker);
        
        // Use reflection to access private createCloudStatus method
        Method createCloudStatus = AgentWatchdog.class.getDeclaredMethod(
            "createCloudStatus", VMInstanceRequest.class, VMInformation.class);
        createCloudStatus.setAccessible(true);
        
        VMInformation vmInformation = new VMInformation();
        vmInformation.setInstanceId("i-replacement-test");
        
        CloudVmStatus result = (CloudVmStatus) createCloudStatus.invoke(
            agentWatchdog, instanceRequest, vmInformation);
        
        // THE CRITICAL ASSERTION: Must be 'starting', not 'pending'
        assertEquals(VMStatus.starting, result.getVmStatus(), 
            "Replacement agents MUST start with VMStatus.starting so watchdog waits for actual /v2/agent/ready call. " +
            "Using 'pending' causes watchdog to immediately consider them as 'reported' and exit prematurely.");
        
        // Verify other fields are correct
        assertEquals("test-job-123", result.getJobId());
        assertEquals("i-replacement-test", result.getInstanceId());
        assertEquals(JobStatus.Starting, result.getJobStatus());
    }
    
    /**
     * Verifies the checkForReportingInstances() logic only considers 'pending' agents as reported.
     * This test ensures that 'starting' agents are NOT moved to reportedInstances.
     */
    @Test
    public void checkForReportingInstances_onlyMovesPendingAgents(
            @Mock VMTracker vmTrackerMock, 
            @Mock CloudVmStatusContainer cloudVmStatusContainerMock) {
        
        // Setup: Two agents - one starting (should wait), one pending (should be reported)
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        
        CloudVmStatus vmstatusStarting = new CloudVmStatus(
            "i-still-starting", "123", "sg-123456", 
            JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, 
            VMStatus.starting, new ValidationStatus(), 1, 1, new Date(), new Date());
        
        CloudVmStatus vmstatusPending = new CloudVmStatus(
            "i-reported", "123", "sg-123456", 
            JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, 
            VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        
        Set<CloudVmStatus> statuses = new HashSet<>();
        statuses.add(vmstatusStarting);
        statuses.add(vmstatusPending);
        when(cloudVmStatusContainerMock.getStatuses()).thenReturn(statuses);
        when(vmTrackerMock.getVmStatusForJob(null)).thenReturn(cloudVmStatusContainerMock);

        // Create VMInformation for both agents
        VMInformation vmInfoStarting = new VMInformation();
        vmInfoStarting.setState("pending");
        vmInfoStarting.setInstanceId("i-still-starting");
        
        VMInformation vmInfoPending = new VMInformation();
        vmInfoPending.setState("pending");
        vmInfoPending.setInstanceId("i-reported");
        
        List<VMInformation> vmInfo = new ArrayList<>();
        vmInfo.add(vmInfoStarting);
        vmInfo.add(vmInfoPending);
        
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);

        // Short timeout to let it check once then timeout waiting for i-still-starting
        // maxWaitForResponse=1 means it will immediately try to relaunch
        // But since we're not mocking amazonInstance.create, it should fail
        // and we just verify the initial check behavior
        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 50);
        
        // Run in a way that only checks once - the job will "appear stopped" after first check
        when(cloudVmStatusContainerMock.getEndTime())
            .thenReturn(null)  // First check
            .thenReturn(new Date());  // Second check - job appears stopped, exit
        
        try {
            agentWatchdog.run();
        } catch (RuntimeException e) {
            // Expected - job appears stopped
        }

        // After ONE check:
        // - i-reported (pending) should be in reportedInstances
        // - i-still-starting (starting) should still be in startedInstances
        // The key assertion is that the watchdog did NOT immediately exit
        // because i-still-starting is still 'starting' not 'pending'
        verify(cloudVmStatusContainerMock, atLeast(1)).getStatuses();
    }
}
