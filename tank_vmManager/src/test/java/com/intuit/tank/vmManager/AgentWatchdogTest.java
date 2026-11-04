package com.intuit.tank.vmManager;

import com.intuit.tank.dao.VMImageDao;
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
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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
        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTracker, null);

        String result = agentWatchdog.toString();

        assertNotNull(result);
        assertTrue(result.endsWith("[sleepTime=30000,maxWaitForResponse=600000,maxRestarts=2]"));
    }

    @Test
    public void alreadyPendingRunTest(@Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer cloudVmStatusContainerMock) {
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        CloudVmStatus vmstatus = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Running, VMImageType.AGENT, VMRegion.STANDALONE, null, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        Set<CloudVmStatus> set = Stream.of(vmstatus).collect(Collectors.toCollection(HashSet::new));
        when(cloudVmStatusContainerMock.getStatuses()).thenReturn(set);
        when(vmTrackerMock.getVmStatusForJob(null)).thenReturn(cloudVmStatusContainerMock);

        VMInformation vmInformation = new VMInformation();
        vmInformation.setState("pending");
        vmInformation.setInstanceId("i-123456789");
        List<VMInformation> vmInfo = Collections.singletonList( vmInformation );
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 1000, null);
        agentWatchdog.run();

        verify(amazonInstanceMock, never()).killInstances(Mockito.anyList());
        verify(amazonInstanceMock, never()).reboot(Mockito.anyList());
        verify(cloudVmStatusContainerMock, times(1)).getEndTime();
        verify(cloudVmStatusContainerMock, times(1)).getStatuses();
    }

    @Test
    public void progressToPendingRunTest(@Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer cloudVmStatusContainerMock) {
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        CloudVmStatus vmstatusStarting = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, null, VMStatus.starting, new ValidationStatus(), 1, 1, new Date(), new Date());
        CloudVmStatus vmstatusPending = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, null, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
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

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 1000, null);
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
        
        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTracker, null);
        
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
    
    @Test
    public void checkForReportingInstances_movesPendingReadyAndRunningAgents(
            @Mock VMTracker vmTrackerMock, 
            @Mock CloudVmStatusContainer cloudVmStatusContainerMock) throws Exception {
        
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        
        CloudVmStatus vmstatusStarting = new CloudVmStatus(
            "i-still-starting", "123", "sg-123456", 
            JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, null, 
            VMStatus.starting, new ValidationStatus(), 1, 1, new Date(), new Date());
        
        CloudVmStatus vmstatusPending = new CloudVmStatus(
            "i-reported", "123", "sg-123456", 
            JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, null, 
            VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());

        CloudVmStatus vmstatusReady = new CloudVmStatus(
            "i-ready", "123", "sg-123456",
            JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, null,
            VMStatus.ready, new ValidationStatus(), 1, 1, new Date(), new Date());

        CloudVmStatus vmstatusRunning = new CloudVmStatus(
            "i-running", "123", "sg-123456",
            JobStatus.Running, VMImageType.AGENT, VMRegion.STANDALONE, null,
            VMStatus.running, new ValidationStatus(), 1, 1, new Date(), new Date());
        
        Set<CloudVmStatus> statuses = new HashSet<>();
        statuses.add(vmstatusStarting);
        statuses.add(vmstatusPending);
        statuses.add(vmstatusReady);
        statuses.add(vmstatusRunning);
        when(cloudVmStatusContainerMock.getStatuses()).thenReturn(statuses);
        when(vmTrackerMock.getVmStatusForJob("123")).thenReturn(cloudVmStatusContainerMock);

        VMInformation vmInfoStarting = new VMInformation();
        vmInfoStarting.setState("pending");
        vmInfoStarting.setInstanceId("i-still-starting");
        
        VMInformation vmInfoPending = new VMInformation();
        vmInfoPending.setState("pending");
        vmInfoPending.setInstanceId("i-reported");

        VMInformation vmInfoReady = new VMInformation();
        vmInfoReady.setState("pending");
        vmInfoReady.setInstanceId("i-ready");

        VMInformation vmInfoRunning = new VMInformation();
        vmInfoRunning.setState("pending");
        vmInfoRunning.setInstanceId("i-running");
        
        List<VMInformation> vmInfo = new ArrayList<>();
        vmInfo.add(vmInfoStarting);
        vmInfo.add(vmInfoPending);
        vmInfo.add(vmInfoReady);
        vmInfo.add(vmInfoRunning);
        
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setJobId("123");
        instanceRequest.setRegion(VMRegion.STANDALONE);

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 50, null);

        setField(agentWatchdog, "startedInstances", new ArrayList<>(vmInfo));
        setField(agentWatchdog, "reportedInstances", new ArrayList<VMInformation>());
        setField(agentWatchdog, "startTime", System.currentTimeMillis());

        Method checkForReportingInstances = AgentWatchdog.class.getDeclaredMethod("checkForReportingInstances");
        checkForReportingInstances.setAccessible(true);
        checkForReportingInstances.invoke(agentWatchdog);

        @SuppressWarnings("unchecked")
        ArrayList<VMInformation> startedInstances = (ArrayList<VMInformation>) getField(agentWatchdog, "startedInstances");
        @SuppressWarnings("unchecked")
        ArrayList<VMInformation> reportedInstances = (ArrayList<VMInformation>) getField(agentWatchdog, "reportedInstances");

        assertEquals(Set.of("i-still-starting"), instanceIds(startedInstances));
        assertEquals(Set.of("i-reported", "i-ready", "i-running"), instanceIds(reportedInstances));
        verify(cloudVmStatusContainerMock, times(1)).getStatuses();
    }

    @Test
    public void relaunch_invokesWsBootstrapCallbackWithNewVms(@Mock VMTracker vmTrackerMock) throws Exception {
        VMInformation oldVm = new VMInformation();
        oldVm.setInstanceId("i-old");

        VMInformation newVm = new VMInformation();
        newVm.setInstanceId("i-new");

        ArrayList<VMInformation> vmInfo = new ArrayList<>();
        vmInfo.add(oldVm);
        ArrayList<VMInformation> startedInstances = new ArrayList<>();
        startedInstances.add(oldVm);
        ArrayList<VMInformation> reportedInstances = new ArrayList<>();
        List<VMInformation> newVms = Collections.singletonList(newVm);

        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setJobId("123");
        instanceRequest.setRegion(VMRegion.STANDALONE);
        when(amazonInstanceMock.create(instanceRequest)).thenReturn(newVms);

        AtomicReference<VMInstanceRequest> callbackRequest = new AtomicReference<>();
        AtomicReference<List<VMInformation>> callbackVms = new AtomicReference<>();
        AgentWatchdog.WsBootstrapCallback callback = (req, vms) -> {
            callbackRequest.set(req);
            callbackVms.set(vms);
        };

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 50, callback);

        setField(agentWatchdog, "startedInstances", startedInstances);
        setField(agentWatchdog, "reportedInstances", reportedInstances);

        try (MockedConstruction<VMImageDao> daoMock = Mockito.mockConstruction(VMImageDao.class)) {
            Method relaunch = AgentWatchdog.class.getDeclaredMethod("relaunch", ArrayList.class);
            relaunch.setAccessible(true);
            relaunch.invoke(agentWatchdog, startedInstances);

            assertEquals(1, daoMock.constructed().size());
        }

        assertSame(instanceRequest, callbackRequest.get());
        assertSame(newVms, callbackVms.get());
        assertEquals(Set.of("i-new"), instanceIds(vmInfo));
        assertEquals(Set.of("i-new"), instanceIds(startedInstances));
        verify(amazonInstanceMock).killInstances(Collections.singletonList("i-old"));
        verify(amazonInstanceMock).create(instanceRequest);
    }

    @Test
    public void convenienceConstructors_useDefaults(@Mock VMTracker vmTrackerMock) {
        AgentWatchdog defaults = new AgentWatchdog(jobRequest(), new ArrayList<>(), vmTrackerMock);
        assertTrue(defaults.toString().endsWith("[sleepTime=30000,maxWaitForResponse=600000,maxRestarts=2]"));

        AgentWatchdog withAmazon = new AgentWatchdog(
            jobRequest(), new ArrayList<>(), vmTrackerMock, amazonInstanceMock, 10, 50);
        assertTrue(withAmazon.toString().contains("sleepTime=10"));
    }

    @Test
    public void run_exitsWhenJobHasNoStatusContainer(@Mock VMTracker vmTrackerMock) {
        when(vmTrackerMock.getVmStatusForJob("123")).thenReturn(null);

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            jobRequest(), new ArrayList<>(List.of(vm("i-1"))), vmTrackerMock, amazonInstanceMock, 10, 50, null);
        agentWatchdog.run();

        verify(vmTrackerMock, never()).publishEvent(any());
        verifyNoInteractions(amazonInstanceMock);
    }

    @Test
    public void run_exitsWhenJobAlreadyEnded(@Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer containerMock) {
        when(containerMock.getEndTime()).thenReturn(new Date());
        when(vmTrackerMock.getVmStatusForJob("123")).thenReturn(containerMock);

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            jobRequest(), new ArrayList<>(List.of(vm("i-1"))), vmTrackerMock, amazonInstanceMock, 10, 50, null);
        agentWatchdog.run();

        verify(containerMock, never()).getStatuses();
        verifyNoInteractions(amazonInstanceMock);
    }

    @Test
    public void run_relaunchesSlowAgentsThenFinishesWhenReplacementsReport(
            @Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer containerMock) throws Exception {
        VMInstanceRequest instanceRequest = jobRequest();
        VMInformation oldVm = vm("i-old");
        VMInformation newVm = vm("i-new");
        when(amazonInstanceMock.create(instanceRequest)).thenReturn(List.of(newVm));

        when(containerMock.getEndTime()).thenReturn(null);
        when(containerMock.getStatuses())
            .thenReturn(Set.of(status("i-old", VMStatus.starting)))
            .thenReturn(Set.of(status("i-new", VMStatus.pending)));
        when(vmTrackerMock.getVmStatusForJob("123")).thenReturn(containerMock);

        // a failing callback must not stop the watchdog
        AgentWatchdog.WsBootstrapCallback callback = (req, vms) -> { throw new IllegalStateException("ws down"); };
        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, new ArrayList<>(List.of(oldVm)), vmTrackerMock, amazonInstanceMock, 10, 50, callback);
        // pretend the agents were launched long ago so the first pass relaunches them
        setField(agentWatchdog, "startTime", 0L);

        com.intuit.tank.project.VMInstance image = mock(com.intuit.tank.project.VMInstance.class);
        try (MockedConstruction<VMImageDao> daoMock = Mockito.mockConstruction(VMImageDao.class, (dao, ctx) -> {
            when(dao.getImageByInstanceId("i-old")).thenReturn(image);
            when(dao.addImageFromInfo(any(), any(), any())).thenThrow(new RuntimeException("db down"));
        })) {
            agentWatchdog.run();

            verify(daoMock.constructed().get(0)).saveOrUpdate(image);
        }

        verify(image).setStatus(VMStatus.replaced.name());
        verify(amazonInstanceMock).killInstances(List.of("i-old"));
        assertEquals(1, instanceRequest.getNumberOfInstances());

        // old instance is reported as replaced, the new one as starting
        org.mockito.ArgumentCaptor<CloudVmStatus> statusCaptor = org.mockito.ArgumentCaptor.forClass(CloudVmStatus.class);
        verify(vmTrackerMock, times(2)).setStatus(statusCaptor.capture());
        assertEquals(VMStatus.replaced, statusCaptor.getAllValues().get(0).getVmStatus());
        assertEquals(VMStatus.starting, statusCaptor.getAllValues().get(1).getVmStatus());

        org.mockito.ArgumentCaptor<com.intuit.tank.vm.event.JobEvent> eventCaptor =
            org.mockito.ArgumentCaptor.forClass(com.intuit.tank.vm.event.JobEvent.class);
        verify(vmTrackerMock, times(2)).publishEvent(eventCaptor.capture());
        assertEquals(com.intuit.tank.vm.api.enumerated.JobLifecycleEvent.AGENT_REBOOTED,
            eventCaptor.getAllValues().get(0).getEvent());
        assertEquals(com.intuit.tank.vm.api.enumerated.JobLifecycleEvent.AGENT_REPORTED,
            eventCaptor.getAllValues().get(1).getEvent());
    }

    @Test
    public void relaunch_pastMaxRestarts_killsJobDirectly(@Mock VMTracker vmTrackerMock) throws Exception {
        VMInstanceRequest instanceRequest = jobRequest();
        ArrayList<VMInformation> vmInfo = new ArrayList<>(List.of(vm("i-1"), vm("i-2")));
        ArrayList<VMInformation> startedInstances = new ArrayList<>(vmInfo);

        CloudVmStatus trackedStatus = status("i-1", VMStatus.running);
        when(vmTrackerMock.getStatus("i-1")).thenReturn(trackedStatus);
        when(vmTrackerMock.getStatus("i-2")).thenReturn(null);
        doThrow(new IllegalStateException("no observers")).when(vmTrackerMock).publishEvent(any());

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 50, null);
        setField(agentWatchdog, "startedInstances", startedInstances);
        setField(agentWatchdog, "reportedInstances", new ArrayList<VMInformation>());
        setField(agentWatchdog, "restartCount", getField(agentWatchdog, "maxRestarts"));

        com.intuit.tank.project.JobInstance job = mock(com.intuit.tank.project.JobInstance.class);
        try (MockedConstruction<com.intuit.tank.dao.JobInstanceDao> daoMock = Mockito.mockConstruction(
                com.intuit.tank.dao.JobInstanceDao.class,
                (dao, ctx) -> when(dao.findById(123)).thenReturn(job))) {
            Exception thrown = assertThrows(Exception.class, () -> invokeRelaunch(agentWatchdog, startedInstances));
            assertEquals("Killing jobs and exiting", thrown.getMessage());

            verify(daoMock.constructed().get(0)).saveOrUpdate(job);
        }

        verify(vmTrackerMock).stopJob("123");
        verify(amazonInstanceMock).killInstances(List.of("i-1", "i-2"));
        verify(amazonInstanceMock, never()).create(any());
        assertEquals(VMStatus.terminated, trackedStatus.getVmStatus());
        assertEquals(JobStatus.Completed, trackedStatus.getJobStatus());
        assertNotNull(trackedStatus.getEndTime());
        verify(vmTrackerMock).setStatus(trackedStatus);
        verify(job).setStatus(com.intuit.tank.vm.api.enumerated.JobQueueStatus.Completed);
        assertTrue((Boolean) getField(agentWatchdog, "stopped"));
    }

    @Test
    public void relaunch_pastMaxRestarts_noInstancesAndDbFailure_stillAborts(@Mock VMTracker vmTrackerMock) throws Exception {
        VMInstanceRequest instanceRequest = jobRequest();
        ArrayList<VMInformation> startedInstances = new ArrayList<>();

        AgentWatchdog agentWatchdog = new AgentWatchdog(
            instanceRequest, new ArrayList<>(), vmTrackerMock, amazonInstanceMock, 10, 50, null);
        setField(agentWatchdog, "startedInstances", startedInstances);
        setField(agentWatchdog, "restartCount", getField(agentWatchdog, "maxRestarts"));

        try (MockedConstruction<com.intuit.tank.dao.JobInstanceDao> ignored = Mockito.mockConstruction(
                com.intuit.tank.dao.JobInstanceDao.class,
                (dao, ctx) -> when(dao.findById(123)).thenThrow(new RuntimeException("db down")))) {
            assertThrows(Exception.class, () -> invokeRelaunch(agentWatchdog, startedInstances));
        }

        verify(vmTrackerMock).stopJob("123");
        verify(vmTrackerMock).publishEvent(any());
        verifyNoInteractions(amazonInstanceMock);
    }

    private static void invokeRelaunch(AgentWatchdog agentWatchdog, ArrayList<VMInformation> instances) throws Exception {
        Method relaunch = AgentWatchdog.class.getDeclaredMethod("relaunch", ArrayList.class);
        relaunch.setAccessible(true);
        try {
            relaunch.invoke(agentWatchdog, instances);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private static VMInstanceRequest jobRequest() {
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setJobId("123");
        instanceRequest.setRegion(VMRegion.STANDALONE);
        return instanceRequest;
    }

    private static VMInformation vm(String instanceId) {
        VMInformation info = new VMInformation();
        info.setInstanceId(instanceId);
        info.setState("pending");
        return info;
    }

    private static CloudVmStatus status(String instanceId, VMStatus vmStatus) {
        return new CloudVmStatus(instanceId, "123", "sg-123456", JobStatus.Starting, VMImageType.AGENT,
            VMRegion.STANDALONE, null, vmStatus, new ValidationStatus(), 1, 1, new Date(), null);
    }

    private static Set<String> instanceIds(List<VMInformation> instances) {
        return instances.stream().map(VMInformation::getInstanceId).collect(Collectors.toSet());
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = AgentWatchdog.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = AgentWatchdog.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
