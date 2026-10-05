package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vmManager.environment.JobRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Reconnects the controller to running agents it no longer has a WS session for, such as after a
 * controller restart or a dropped connection. Agents are found by the Controller and JobId EC2 tags,
 * and adopted only when their WS hello confirms the same instance and job.
 */
@ApplicationScoped
public class AgentReadoption {

    private static final Logger LOG = LogManager.getLogger(AgentReadoption.class);

    // JobRequest.connectAndBootstrapAgent owns an agent's WS session for max(10 min, max-agent-wait-time)
    // after launch; connecting before then would replace its session mid-bootstrap.
    private static final long MIN_AGENT_AGE_MS = Long.getLong("tank.ws.readopt.minAgentAgeMs", 15 * 60_000L);
    private static final long BOOTSTRAP_MARGIN_MS = 5 * 60_000L;
    private static final long INTERVAL_MS = Math.max(5_000L, Long.getLong("tank.ws.readopt.intervalMs", 30_000L));
    private static final long HELLO_TIMEOUT_MS = 10_000L;
    // Each connect can block for its connect and hello timeouts; keep that off the common ForkJoinPool,
    // which JobManager.sendCommand uses to deliver pause and kill.
    private static final int CONNECT_THREADS = 8;

    @Inject
    private JobManager jobManager;

    @Inject
    private TankConfig tankConfig;

    private volatile ScheduledExecutorService executor;

    public AgentReadoption() {
    }

    AgentReadoption(JobManager jobManager, TankConfig tankConfig) {
        this.jobManager = jobManager;
        this.tankConfig = tankConfig;
    }

    void onStartup(@Observes @Initialized(ApplicationScoped.class) Object ignored) {
        try {
            AgentConfig agentConfig = tankConfig.getAgentConfig();
            if (!agentConfig.isCommandWsEnabled() || !agentConfig.isAgentReadoptionEnabled() || tankConfig.getStandalone()) {
                LOG.info(new ObjectMessage(Map.of("Message", "[WS] Agent re-adoption disabled")));
                return;
            }
            jobManager.getControllerInitiatedAgentWsClient().setCloseStaleSessions(true);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(daemonThread("ws-agent-readoption"));
            scheduler.scheduleWithFixedDelay(this::reconcile, 0, INTERVAL_MS, TimeUnit.MILLISECONDS);
            executor = scheduler;
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] Agent re-adoption enabled for controller "
                    + tankConfig.getInstanceName() + " every " + INTERVAL_MS + "ms")));
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] Agent re-adoption failed to start: " + e.getMessage())), e);
        }
    }

    @PreDestroy
    void stop() {
        ScheduledExecutorService scheduler = executor;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void reconcile() {
        try {
            List<VMInformation> runningAgents = new ArrayList<>();
            for (VMRegion region : tankConfig.getVmManagerConfig().getRegions()) {
                runningAgents.addAll(new AmazonInstance(region).findRunningAgents(tankConfig.getInstanceName()));
            }
            AgentConfig agentConfig = tankConfig.getAgentConfig();
            long minAgentAgeMs = Math.max(MIN_AGENT_AGE_MS, agentConfig.getMaxAgentWaitTime() + BOOTSTRAP_MARGIN_MS);
            adopt(runningAgents, jobManager.getControllerInitiatedAgentWsClient(),
                    agentConfig.getAgentToken(), agentConfig.getAgentPort(), System.currentTimeMillis(), minAgentAgeMs);
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] Agent re-adoption pass failed: " + e.getMessage())), e);
        }
    }

    int adopt(List<VMInformation> runningAgents, ControllerInitiatedAgentWsClient wsClient,
              String token, int port, long nowMs, long minAgentAgeMs) {
        List<VMInformation> candidates = runningAgents.stream()
                .filter(agent -> !wsClient.hasSession(agent.getInstanceId()))
                .filter(agent -> agent.getLaunchTime() != null
                        && nowMs - agent.getLaunchTime().getTimeInMillis() >= minAgentAgeMs)
                .toList();
        if (candidates.isEmpty()) {
            return 0;
        }
        ExecutorService connectPool = Executors.newFixedThreadPool(
                Math.min(CONNECT_THREADS, candidates.size()), daemonThread("ws-agent-readoption-connect"));
        try {
            List<CompletableFuture<Boolean>> attempts = candidates.stream()
                    .map(agent -> CompletableFuture.supplyAsync(() -> adoptAgent(agent, wsClient, token, port), connectPool))
                    .toList();
            return (int) attempts.stream().filter(CompletableFuture::join).count();
        } finally {
            connectPool.shutdown();
        }
    }

    private boolean adoptAgent(VMInformation agent, ControllerInitiatedAgentWsClient wsClient, String token, int port) {
        String instanceId = agent.getInstanceId();
        String jobId = agent.getJobId();
        String host = JobRequest.resolveWsHost(agent.getRegion(), agent);
        if (host == null) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] No reachable host to re-adopt agent " + instanceId)));
            return false;
        }

        Optional<AgentWsEnvelope> hello = wsClient.connect(
                instanceId, "ws://" + host + ":" + port + "/ws/control", token, HELLO_TIMEOUT_MS);
        if (hello.isEmpty()) {
            return false;
        }
        if (!isRunningAgentForJob(hello.get(), instanceId, jobId)) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] Not re-adopting " + instanceId + " for job " + jobId
                    + ": hello reported instance " + hello.get().getInstanceId() + " job " + hello.get().getJobId()
                    + " needsBootstrap " + hello.get().getNeedsBootstrap())));
            wsClient.disconnect(instanceId);
            return false;
        }

        jobManager.adoptAgent(instanceId, jobId);
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] Re-adopted agent " + instanceId + " for job " + jobId
                + " lastAppliedCommandId " + hello.get().getLastAppliedCommandId())));
        return true;
    }

    private static boolean isRunningAgentForJob(AgentWsEnvelope hello, String instanceId, String jobId) {
        return jobId != null
                && jobId.equals(hello.getJobId())
                && (hello.getInstanceId() == null || instanceId.equals(hello.getInstanceId()))
                && !Boolean.TRUE.equals(hello.getNeedsBootstrap());
    }

    private static ThreadFactory daemonThread(String name) {
        return runnable -> {
            Thread thread = new Thread(runnable, name);
            thread.setDaemon(true);
            return thread;
        };
    }
}
