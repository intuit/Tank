package com.intuit.tank.vmManager.environment;

import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vmManager.AgentWatchdog;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class JobRequestTest {

    private final IEnvironmentInstance environment = mock(IEnvironmentInstance.class);

    @Test
    public void refreshPublicAddress_picksUpPublicIpForRemoteRegion() {
        VMInformation launched = vm("pending", null);
        VMInformation described = vm("running", "54.0.0.1");
        when(environment.describeInstances(List.of("i-1"))).thenReturn(List.of(described));

        VMInformation refreshed = JobRequest.refreshPublicAddress(launched, environment);

        assertSame(described, refreshed);
        assertEquals("54.0.0.1", JobRequest.resolveWsHost(VMRegion.US_WEST_2, refreshed));
    }

    @Test
    public void refreshPublicAddress_picksUpPublicIpInControllerRegion() {
        VMInformation launched = vm("pending", null);
        VMInformation described = vm("running", "3.0.0.1");
        when(environment.describeInstances(List.of("i-1"))).thenReturn(List.of(described));

        VMInformation refreshed = JobRequest.refreshPublicAddress(launched, environment);

        assertEquals("3.0.0.1", refreshed.getPublicIp());
        // The controller still reaches same-region agents on their private IP
        assertEquals("10.0.0.1", JobRequest.resolveWsHost(VMRegion.US_EAST_2, refreshed));
    }

    @Test
    public void refreshPublicAddress_skipsWhenPublicIpKnown() {
        VMInformation launched = vm("pending", "54.0.0.1");

        assertSame(launched, JobRequest.refreshPublicAddress(launched, environment));
        verify(environment, never()).describeInstances(any());
    }

    @Test
    public void refreshPublicAddress_stopsOnceRunningWithoutPublicIp() {
        VMInformation privateOnly = vm("running", null);

        assertSame(privateOnly, JobRequest.refreshPublicAddress(privateOnly, environment));
        verify(environment, never()).describeInstances(any());
        assertEquals("10.0.0.1", JobRequest.resolveWsHost(VMRegion.US_WEST_2, privateOnly));
    }

    @Test
    public void refreshPublicAddress_keepsLaunchInfoWhenDescribeFindsNothing() {
        VMInformation launched = vm("pending", null);
        when(environment.describeInstances(List.of("i-1"))).thenReturn(List.of());

        assertSame(launched, JobRequest.refreshPublicAddress(launched, environment));
    }

    @Test
    public void refreshPublicAddress_skipsWithoutEnvironmentOrWhenPublicDnsKnown() {
        VMInformation launched = vm("pending", null);
        assertSame(launched, JobRequest.refreshPublicAddress(launched, null));

        launched.setPublicDNS("ec2-54-0-0-1.compute.amazonaws.com");
        assertSame(launched, JobRequest.refreshPublicAddress(launched, environment));
        verify(environment, never()).describeInstances(any());
    }

    @Test
    public void resolveWsHost_fallsBackThroughAvailableAddresses() {
        VMInformation info = new VMInformation();
        assertNull(JobRequest.resolveWsHost(VMRegion.US_WEST_2, info));

        info.setPrivateDNS("ip-10-0-0-1.internal");
        assertEquals("ip-10-0-0-1.internal", JobRequest.resolveWsHost(VMRegion.US_WEST_2, info));

        // a blank private IP is ignored, even in a controller region
        info.setPrivateIp(" ");
        assertEquals("ip-10-0-0-1.internal", JobRequest.resolveWsHost(VMRegion.US_EAST, info));

        info.setPrivateIp("10.0.0.1");
        assertEquals("10.0.0.1", JobRequest.resolveWsHost(VMRegion.US_WEST_2, info));

        info.setPublicDNS("ec2-54-0-0-1.compute.amazonaws.com");
        assertEquals("ec2-54-0-0-1.compute.amazonaws.com", JobRequest.resolveWsHost(VMRegion.US_WEST_2, info));
        assertEquals("10.0.0.1", JobRequest.resolveWsHost(VMRegion.US_EAST, info));
    }

    // ============ run ============

    @Test
    public void run_increasingStrategy_launchesAgentsPersistsImagesAndStartsWatchdog() {
        VMTracker tracker = mock(VMTracker.class);
        VMJobRequest request = jobRequest(IncrementStrategy.increasing, 100, 30);
        request.setUserEips(true);
        List<VMInformation> launched = List.of(vm("pending", "54.0.0.1", "i-1"), vm("pending", "54.0.0.2", "i-2"));
        // persisting the first image fails; the second must still be persisted
        doThrow(new IllegalStateException("tracker down")).doNothing().when(tracker).setStatus(any());

        List<VMInstanceRequest> created = new ArrayList<>();
        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class,
                     (mock, ctx) -> when(mock.create(any())).thenAnswer(inv -> {
                         created.add(inv.getArgument(0));
                         return launched;
                     }));
             MockedConstruction<VMImageDao> dao = mockConstruction(VMImageDao.class);
             MockedConstruction<AgentWatchdog> watchdog = mockConstruction(AgentWatchdog.class, (mock, ctx) ->
                 // the replacement-agent callback has no job manager here, so it returns without connecting
                 ((AgentWatchdog.WsBootstrapCallback) ctx.arguments().get(3)).bootstrap(
                         (VMInstanceRequest) ctx.arguments().get(0), launched))) {
            new JobRequest(request, tracker).run();

            assertEquals(1, amazon.constructed().size());
            assertEquals(1, watchdog.constructed().size());
            verify(dao.constructed().get(0), times(1)).addImageFromInfo("7", launched.get(1), VMRegion.US_WEST_2);
            verify(watchdog.constructed().get(0), timeout(2_000)).run();
        }

        VMInstanceRequest instanceRequest = created.get(0);
        assertEquals("7", instanceRequest.getJobId());
        assertEquals(VMProvider.Amazon, instanceRequest.getProvider());
        assertEquals(VMRegion.US_WEST_2, instanceRequest.getRegion());
        assertEquals(4, instanceRequest.getNumberOfInstances());
        assertEquals(25, instanceRequest.getNumUsersPerAgent());
        assertEquals("c5.large", instanceRequest.getSize());
        assertTrue(instanceRequest.isUseEips());

        ArgumentCaptor<CloudVmStatus> statusCaptor = ArgumentCaptor.forClass(CloudVmStatus.class);
        verify(tracker, times(2)).setStatus(statusCaptor.capture());
        assertEquals("i-1", statusCaptor.getAllValues().get(0).getInstanceId());
        assertEquals("7", statusCaptor.getAllValues().get(1).getJobId());
    }

    @Test
    public void run_standardStrategy_usesUserCountAsMachineCount() {
        VMJobRequest request = jobRequest(IncrementStrategy.standard, 3, 0);
        request.setProvider(VMProvider.Amazon);
        List<VMInstanceRequest> created = new ArrayList<>();
        try (MockedConstruction<AmazonInstance> ignoredAmazon = mockConstruction(AmazonInstance.class,
                     (mock, ctx) -> when(mock.create(any())).thenAnswer(inv -> {
                         created.add(inv.getArgument(0));
                         return List.of();
                     }));
             MockedConstruction<VMImageDao> ignoredDao = mockConstruction(VMImageDao.class);
             MockedConstruction<AgentWatchdog> ignoredWatchdog = mockConstruction(AgentWatchdog.class)) {
            new JobRequest(request, mock(VMTracker.class), mock(JobManager.class)).run();
        }
        assertEquals(3, created.get(0).getNumberOfInstances());
    }

    @Test
    public void run_pharosProvider_hasNoEnvironmentAndLogsError() {
        VMJobRequest request = jobRequest(IncrementStrategy.standard, 1, 0);
        request.setProvider(VMProvider.Pharos);
        VMTracker tracker = mock(VMTracker.class);
        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class)) {
            new JobRequest(request, tracker).run();
            assertTrue(amazon.constructed().isEmpty());
        }
        verifyNoInteractions(tracker);
    }

    @Test
    public void run_environmentConstructionFails_logsError() {
        VMJobRequest request = jobRequest(IncrementStrategy.standard, 1, 0);
        VMTracker tracker = mock(VMTracker.class);
        try (MockedConstruction<AmazonInstance> ignored = mockConstruction(AmazonInstance.class,
                (mock, ctx) -> { throw new IllegalStateException("no credentials"); })) {
            new JobRequest(request, tracker).run();
        }
        verifyNoInteractions(tracker);
    }

    @Test
    public void run_requestCannotBeRead_logsError() {
        VMJobRequest request = mock(VMJobRequest.class);
        when(request.getJobId()).thenThrow(new IllegalStateException("corrupt request"));
        VMTracker tracker = mock(VMTracker.class);

        new JobRequest(request, tracker).run();

        verifyNoInteractions(tracker);
    }

    // ============ controller-initiated WS bootstrap ============

    @Test
    public void startControllerInitiatedWs_connectsEachAgentOnItsOwnThread() throws Exception {
        JobManager jobManager = mock(JobManager.class);
        ControllerInitiatedAgentWsClient wsClient = mock(ControllerInitiatedAgentWsClient.class);
        when(jobManager.getControllerInitiatedAgentWsClient()).thenReturn(wsClient);
        when(wsClient.connectAndBootstrap(any(), anyString(), anyString(), anyString(), any(), anyLong(), anyLong()))
                .thenReturn(true);
        VMJobRequest request = jobRequest(IncrementStrategy.standard, 1, 0);
        // no environment, so the bootstrap threads never describe instances
        request.setProvider(VMProvider.Pharos);
        JobRequest jobRequest = new JobRequest(request, mock(VMTracker.class), jobManager);

        try (MockedConstruction<TankConfig> ignored = mockTankConfig(true)) {
            invokeStartControllerInitiatedWs(jobRequest, instanceRequest(),
                    List.of(vm("running", "54.0.0.1", "i-1"), vm("running", "54.0.0.2", "i-2")));
        }

        // hello timeout is at least 30s, transfer timeout at least 120s
        verify(wsClient, timeout(2_000)).connectAndBootstrap(jobManager, "i-1", "http://54.0.0.1:8090",
                "ws://54.0.0.1:8090/ws/control", "token", 45_000L, 300_000L);
        verify(wsClient, timeout(2_000)).connectAndBootstrap(eq(jobManager), eq("i-2"), eq("http://54.0.0.2:8090"),
                eq("ws://54.0.0.2:8090/ws/control"), eq("token"), anyLong(), anyLong());
    }

    @Test
    public void startControllerInitiatedWs_skipsWhenDisabledOrNothingToConnect() throws Exception {
        JobManager jobManager = mock(JobManager.class);
        JobRequest jobRequest = new JobRequest(jobRequest(IncrementStrategy.standard, 1, 0), mock(VMTracker.class), jobManager);

        try (MockedConstruction<TankConfig> ignored = mockTankConfig(false)) {
            invokeStartControllerInitiatedWs(jobRequest, instanceRequest(), List.of(vm("running", "54.0.0.1")));
        }
        try (MockedConstruction<TankConfig> ignored = mockTankConfig(true)) {
            invokeStartControllerInitiatedWs(jobRequest, instanceRequest(), null);
            invokeStartControllerInitiatedWs(jobRequest, instanceRequest(), List.of());
        }
        verifyNoInteractions(jobManager);
    }

    @Test
    public void connectAndBootstrapAgent_incompleteAttemptStopsWhenInterrupted() throws Exception {
        ControllerInitiatedAgentWsClient wsClient = mock(ControllerInitiatedAgentWsClient.class);
        when(wsClient.connectAndBootstrap(any(), anyString(), anyString(), anyString(), any(), anyLong(), anyLong()))
                .thenReturn(false);
        JobRequest jobRequest = pharosJobRequest(VMRegion.US_EAST_2);

        // with the interrupt flag set the retry sleep returns immediately and the loop exits
        Thread.currentThread().interrupt();
        try {
            invokeConnectAndBootstrapAgent(jobRequest, wsClient, vm("running", "54.0.0.1"), 60_000L);
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
        // same-region agents are reached on the private IP
        verify(wsClient).connectAndBootstrap(any(), eq("i-1"), eq("http://10.0.0.1:8090"),
                eq("ws://10.0.0.1:8090/ws/control"), eq("token"), eq(1L), eq(2L));
    }

    @Test
    public void connectAndBootstrapAgent_noReachableHostCountsAsFailedAttempt() throws Exception {
        ControllerInitiatedAgentWsClient wsClient = mock(ControllerInitiatedAgentWsClient.class);
        VMInformation noAddress = new VMInformation();
        noAddress.setInstanceId("i-1");
        noAddress.setState("running");

        Thread.currentThread().interrupt();
        try {
            invokeConnectAndBootstrapAgent(pharosJobRequest(VMRegion.US_WEST_2), wsClient, noAddress, 60_000L);
        } finally {
            Thread.interrupted();
        }
        verifyNoInteractions(wsClient);
    }

    @Test
    public void connectAndBootstrapAgent_givesUpAfterMaxWait() throws Exception {
        ControllerInitiatedAgentWsClient wsClient = mock(ControllerInitiatedAgentWsClient.class);

        invokeConnectAndBootstrapAgent(pharosJobRequest(VMRegion.US_WEST_2), wsClient, vm("running", "54.0.0.1"), 0L);

        verifyNoInteractions(wsClient);
        assertFalse(Thread.currentThread().isInterrupted());
    }

    private static JobRequest pharosJobRequest(VMRegion region) {
        VMJobRequest request = new VMJobRequest("7", "reporting", "STANDARD", 1, region,
                IncrementStrategy.standard, "END_OF_SCRIPT_GROUP", "c5.large", 0);
        request.setProvider(VMProvider.Pharos);
        return new JobRequest(request, mock(VMTracker.class), mock(JobManager.class));
    }

    private static VMJobRequest jobRequest(IncrementStrategy strategy, int users, int usersPerAgent) {
        return new VMJobRequest("7", "reporting", "STANDARD", users, VMRegion.US_WEST_2,
                strategy, "END_OF_SCRIPT_GROUP", "c5.large", usersPerAgent);
    }

    private static VMInstanceRequest instanceRequest() {
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setJobId("7");
        instanceRequest.setRegion(VMRegion.US_WEST_2);
        return instanceRequest;
    }

    private static MockedConstruction<TankConfig> mockTankConfig(boolean wsEnabled) {
        AgentConfig agentConfig = mock(AgentConfig.class);
        when(agentConfig.isCommandWsEnabled()).thenReturn(wsEnabled);
        when(agentConfig.getAgentToken()).thenReturn("token");
        when(agentConfig.getAgentPort()).thenReturn(8090);
        when(agentConfig.getMaxAgentResponseTime()).thenReturn(45_000L);
        when(agentConfig.getMaxAgentWaitTime()).thenReturn(300_000L);
        return mockConstruction(TankConfig.class, (mock, ctx) -> when(mock.getAgentConfig()).thenReturn(agentConfig));
    }

    private static void invokeStartControllerInitiatedWs(JobRequest jobRequest, VMInstanceRequest instanceRequest,
                                                         List<VMInformation> vmInfo) throws Exception {
        Method method = JobRequest.class.getDeclaredMethod("startControllerInitiatedWs", VMInstanceRequest.class, List.class);
        method.setAccessible(true);
        invoke(method, jobRequest, instanceRequest, vmInfo);
    }

    private static void invokeConnectAndBootstrapAgent(JobRequest jobRequest, ControllerInitiatedAgentWsClient wsClient,
                                                       VMInformation info, long maxConnectWaitMillis) throws Exception {
        Method method = JobRequest.class.getDeclaredMethod("connectAndBootstrapAgent", ControllerInitiatedAgentWsClient.class,
                VMInstanceRequest.class, VMInformation.class, String.class, int.class, long.class, long.class, long.class);
        method.setAccessible(true);
        invoke(method, jobRequest, wsClient, instanceRequest(), info, "token", 8090, 1L, 2L, maxConnectWaitMillis);
    }

    private static void invoke(Method method, Object target, Object... args) throws Exception {
        try {
            method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private static VMInformation vm(String state, String publicIp, String instanceId) {
        VMInformation info = vm(state, publicIp);
        info.setInstanceId(instanceId);
        return info;
    }

    private static VMInformation vm(String state, String publicIp) {
        VMInformation info = new VMInformation();
        info.setInstanceId("i-1");
        info.setState(state);
        info.setPrivateIp("10.0.0.1");
        info.setPublicIp(publicIp);
        return info;
    }
}
