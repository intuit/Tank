package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AgentReadoptionTest {

    private static final long NOW = 1_800_000_000_000L;
    private static final long TWENTY_MINUTES = 20 * 60_000L;
    private static final long MIN_AGE = 5 * 60_000L;
    private static final String TOKEN = "agent-token";
    private static final int PORT = 8090;

    private ControllerInitiatedAgentWsClient wsClient;
    private JobManager jobManager;
    private AgentReadoption readoption;

    @BeforeEach
    void setUp() {
        wsClient = mock(ControllerInitiatedAgentWsClient.class);
        jobManager = mock(JobManager.class);
        readoption = new AgentReadoption(jobManager, null);
    }

    @Test
    void adoptsRunningAgentWhenHelloMatchesItsJobTag() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.connect(eq("i-east"), eq("ws://10.0.0.5:8090/ws/control"), eq(TOKEN), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello("i-east", "16156", "session-1", "cmd-9")));

        int adopted = readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE);

        assertEquals(1, adopted);
        verify(jobManager).adoptAgent("i-east", "16156");
    }

    @Test
    void connectsToWestAgentsOnTheirPublicIp() {
        VMInformation agent = agent("i-west", "16156", VMRegion.US_WEST_2, NOW - TWENTY_MINUTES);
        when(wsClient.connect(eq("i-west"), eq("ws://54.0.0.5:8090/ws/control"), eq(TOKEN), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello("i-west", "16156", "session-1", null)));

        assertEquals(1, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(jobManager).adoptAgent("i-west", "16156");
    }

    @Test
    void skipsAgentThatAlreadyHasAnOpenSession() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.hasSession("i-east")).thenReturn(true);

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient, never()).connect(anyString(), anyString(), anyString(), anyLong());
        verify(jobManager, never()).adoptAgent(anyString(), anyString());
    }

    @Test
    void skipsAgentStillInsideTheLaunchBootstrapWindow() {
        VMInformation agent = agent("i-east", "16180", VMRegion.US_EAST, NOW - 60_000L);

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient, never()).connect(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void skipsAgentWithoutLaunchTime() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        agent.setLaunchTime(null);

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient, never()).connect(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void rejectsAndDisconnectsAgentReportingADifferentJob() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.connect(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello("i-east", "99999", "session-1", null)));

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient).disconnect("i-east");
        verify(jobManager, never()).adoptAgent(anyString(), anyString());
    }

    @Test
    void rejectsAndDisconnectsAgentReportingADifferentInstanceId() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.connect(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello("i-other", "16156", "session-1", null)));

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient).disconnect("i-east");
        verify(jobManager, never()).adoptAgent(anyString(), anyString());
    }

    @Test
    void rejectsAndDisconnectsAgentThatIsStillBootstrapping() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.connect(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello("i-east", "16156", "session-1", null, null, true)));

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient).disconnect("i-east");
        verify(jobManager, never()).adoptAgent(anyString(), anyString());
    }

    @Test
    void unreachableAgentIsSkippedAndOthersAreStillAdopted() {
        VMInformation unreachable = agent("i-down", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        unreachable.setPrivateIp("10.0.0.9");
        VMInformation reachable = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.connect(eq("i-down"), anyString(), anyString(), anyLong())).thenReturn(Optional.empty());
        when(wsClient.connect(eq("i-east"), anyString(), anyString(), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello("i-east", "16156", "session-1", null)));

        assertEquals(1, readoption.adopt(List.of(unreachable, reachable), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(jobManager).adoptAgent("i-east", "16156");
        verify(jobManager, never()).adoptAgent(eq("i-down"), anyString());
    }

    @Test
    void connectsOnDedicatedThreadsInsteadOfTheSharedForkJoinPool() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        List<String> connectThreads = new CopyOnWriteArrayList<>();
        when(wsClient.connect(anyString(), anyString(), anyString(), anyLong())).thenAnswer(invocation -> {
            connectThreads.add(Thread.currentThread().getName());
            return Optional.of(AgentWsEnvelope.hello("i-east", "16156", "session-1", null));
        });

        readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE);

        assertEquals(List.of("ws-agent-readoption-connect"), connectThreads);
    }

    @Test
    void startupDoesNothingWhenReadoptionIntervalIsZero() {
        AgentReadoption disabled = new AgentReadoption(jobManager, tankConfig(true, 0));

        disabled.onStartup(null);

        verify(jobManager, never()).getControllerInitiatedAgentWsClient();
    }

    @Test
    void startupDoesNothingWhenReadoptionIntervalIsNegative() {
        AgentReadoption disabled = new AgentReadoption(jobManager, tankConfig(true, -1));

        disabled.onStartup(null);

        verify(jobManager, never()).getControllerInitiatedAgentWsClient();
    }

    @Test
    void startupDoesNothingWhenWsCommandModeIsOff() {
        AgentReadoption disabled = new AgentReadoption(jobManager, tankConfig(false, 300));

        disabled.onStartup(null);

        verify(jobManager, never()).getControllerInitiatedAgentWsClient();
    }

    @Test
    void startupTurnsOnStaleSessionClosingWhenReadoptionIntervalIsSet() {
        when(jobManager.getControllerInitiatedAgentWsClient()).thenReturn(wsClient);
        AgentReadoption enabled = new AgentReadoption(jobManager, tankConfig(true, 300));

        try {
            enabled.onStartup(null);
            verify(wsClient).setCloseStaleSessions(true);
        } finally {
            enabled.stop();
        }
    }

    @Test
    void startupFailureDoesNotEscapeToTheContainer() {
        TankConfig tankConfig = mock(TankConfig.class);
        when(tankConfig.getAgentConfig()).thenThrow(new IllegalStateException("settings unavailable"));

        assertDoesNotThrow(() -> new AgentReadoption(jobManager, tankConfig).onStartup(null));
    }

    @Test
    void startupDoesNothingOnAStandaloneController() {
        TankConfig tankConfig = tankConfig(true, 300);
        when(tankConfig.getStandalone()).thenReturn(true);

        new AgentReadoption(jobManager, tankConfig).onStartup(null);

        verify(jobManager, never()).getControllerInitiatedAgentWsClient();
    }

    @Test
    void stopBeforeStartupIsANoOp() {
        assertDoesNotThrow(() -> new AgentReadoption().stop());
    }

    @Test
    void skipsAgentWithNoReachableAddress() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        agent.setPrivateIp(null);
        agent.setPublicIp(null);

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient, never()).connect(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void rejectsAgentWithoutAJobTag() {
        VMInformation agent = agent("i-east", null, VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.connect(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello("i-east", "16156", "session-1", null)));

        assertEquals(0, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(wsClient).disconnect("i-east");
    }

    @Test
    void adoptsAgentWhoseHelloOmitsTheInstanceId() {
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, NOW - TWENTY_MINUTES);
        when(wsClient.connect(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Optional.of(AgentWsEnvelope.hello(null, "16156", "session-1", null)));

        assertEquals(1, readoption.adopt(List.of(agent), wsClient, TOKEN, PORT, NOW, MIN_AGE));
        verify(jobManager).adoptAgent("i-east", "16156");
    }

    @Test
    void reconcileLooksUpRunningAgentsInEveryRegion() throws Exception {
        TankConfig tankConfig = tankConfig(true, 300);
        when(tankConfig.getInstanceName()).thenReturn("controller-1");
        when(tankConfig.getVmManagerConfig().getRegions()).thenReturn(Set.of(VMRegion.US_EAST));
        when(tankConfig.getAgentConfig().getAgentToken()).thenReturn(TOKEN);
        when(tankConfig.getAgentConfig().getAgentPort()).thenReturn(PORT);
        when(jobManager.getControllerInitiatedAgentWsClient()).thenReturn(wsClient);
        // launched just now, so it is still inside the bootstrap window and is not connected to
        VMInformation agent = agent("i-east", "16156", VMRegion.US_EAST, System.currentTimeMillis());

        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class,
                (mock, context) -> when(mock.findRunningAgents("controller-1")).thenReturn(List.of(agent)))) {
            invokeReconcile(new AgentReadoption(jobManager, tankConfig));

            assertEquals(1, amazon.constructed().size());
            verify(amazon.constructed().get(0)).findRunningAgents("controller-1");
        }
        verify(wsClient).hasSession("i-east");
        verify(wsClient, never()).connect(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void reconcileFailureIsLoggedAndNotRethrown() {
        TankConfig tankConfig = mock(TankConfig.class);
        when(tankConfig.getVmManagerConfig()).thenThrow(new IllegalStateException("settings unavailable"));

        assertDoesNotThrow(() -> invokeReconcile(new AgentReadoption(jobManager, tankConfig)));
        verify(jobManager, never()).getControllerInitiatedAgentWsClient();
    }

    private static void invokeReconcile(AgentReadoption target) throws Exception {
        Method reconcile = AgentReadoption.class.getDeclaredMethod("reconcile");
        reconcile.setAccessible(true);
        reconcile.invoke(target);
    }

    private TankConfig tankConfig(boolean wsEnabled, int readoptionIntervalSeconds) {
        AgentConfig agentConfig = mock(AgentConfig.class);
        when(agentConfig.isCommandWsEnabled()).thenReturn(wsEnabled);
        when(agentConfig.getAgentReadoptionIntervalSeconds()).thenReturn(readoptionIntervalSeconds);
        VmManagerConfig vmManagerConfig = mock(VmManagerConfig.class);
        when(vmManagerConfig.getRegions()).thenReturn(Set.of());
        TankConfig tankConfig = mock(TankConfig.class);
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        when(tankConfig.getVmManagerConfig()).thenReturn(vmManagerConfig);
        return tankConfig;
    }

    private VMInformation agent(String instanceId, String jobId, VMRegion region, long launchTimeMs) {
        VMInformation info = new VMInformation();
        info.setInstanceId(instanceId);
        info.setJobId(jobId);
        info.setRegion(region);
        info.setPrivateIp("10.0.0.5");
        info.setPublicIp("54.0.0.5");
        Calendar launchTime = Calendar.getInstance();
        launchTime.setTimeInMillis(launchTimeMs);
        info.setLaunchTime(launchTime);
        return info;
    }
}
