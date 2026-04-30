package com.intuit.tank.rest.mvc;

import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.Type;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AgentCommandWebSocketHandler extends TextWebSocketHandler implements AgentWsCommandSender {

    private static final Logger LOG = LogManager.getLogger(AgentCommandWebSocketHandler.class);

    public enum AgentWsState {
        LAUNCHED,
        CONNECTED,
        REGISTERED,
        TRANSFERRING,
        READY,
        RUNNING,
        DISCONNECTED
    }

    // instanceId -> active WS session
    private final ConcurrentHashMap<String, WebSocketSession> agentSessions = new ConcurrentHashMap<>();

    // sessionId -> bound instanceId (one identity per session, immutable after hello)
    private final ConcurrentHashMap<String, String> sessionIdentity = new ConcurrentHashMap<>();

    // instanceId -> last seen timestamp
    private final ConcurrentHashMap<String, Long> agentLastSeen = new ConcurrentHashMap<>();

    // commandId -> ack future (for pending ack tracking)
    private final ConcurrentHashMap<String, CompletableFuture<AgentWsEnvelope>> pendingAcks = new ConcurrentHashMap<>();

    // instanceId -> whether initial ws file transfer completed
    private final ConcurrentHashMap<String, Boolean> fileTransferReady = new ConcurrentHashMap<>();

    // heartbeat state
    private final ConcurrentHashMap<String, PendingPing> pendingPings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> missedPongs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PendingChunkAck> pendingChunkAcks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> agentJobs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> jobExpectedAgents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> jobTransferCompleteAgents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AgentWsState> agentWsState = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> agentTransferProgress = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> agentExpectedFiles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> agentCompletedFiles = new ConcurrentHashMap<>();

    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "AgentWsHeartbeat");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService transferExecutor = new ThreadPoolExecutor(
            8, 16, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(500),
            r -> { Thread t = new Thread(r, "AgentWsTransfer"); t.setDaemon(true); return t; },
            new ThreadPoolExecutor.CallerRunsPolicy());

    private final ExecutorService heartbeatSendExecutor = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "AgentWsHeartbeatSend");
        t.setDaemon(true);
        return t;
    });

    private final AgentWsFileTransferService fileTransferService = new AgentWsFileTransferService();

    @Autowired
    private ServletContext servletContext;

    private volatile JobManager cachedJobManager;
    private volatile VMTracker cachedVMTracker;
    private volatile AgentConfig cachedAgentConfig;

    public AgentCommandWebSocketHandler() {
        heartbeatScheduler.scheduleAtFixedRate(this::heartbeatTick, 30L, 30L, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String remote = session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown";
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] === CONNECTION OPENED === session=" + session.getId() + " remote=" + remote)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        AgentWsEnvelope envelope;
        try {
            envelope = AgentWsEnvelope.fromJson(message.getPayload());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ✗ Invalid frame, closing session: " + e.getMessage())));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (envelope.getType() == null) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ✗ Frame missing type, closing session")));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        String boundId = sessionIdentity.getOrDefault(session.getId(), "?");
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] ◄── RECV " + envelope.getType() + " from " + boundId
                + (envelope.getInstanceId() != null ? " (instanceId=" + envelope.getInstanceId() + ")" : ""))));

        // Require hello before any other frame type
        if (envelope.getType() != Type.hello && !sessionIdentity.containsKey(session.getId())) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ✗ Frame before hello, closing session")));
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        switch (envelope.getType()) {
            case hello -> handleHello(session, envelope);
            case ack -> handleAck(session, envelope);
            case file_ack -> handleFileAck(session, envelope);
            case status_update -> handleStatusUpdate(session, envelope);
            case pong -> handlePong(session, envelope);
            case close -> handleClose(session, envelope);
            default -> {
                LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ✗ Unexpected frame type: " + envelope.getType())));
                sendAck(session, envelope.getInstanceId(), "hello", session.getId(), AckStatus.unsupported);
            }
        }
    }

    private void handleHello(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        String instanceId = envelope.getInstanceId();
        if (instanceId == null || envelope.getJobId() == null || envelope.getAgentSessionId() == null) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Hello frame missing required fields, closing")));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // Check if this session already has a bound identity — reject rebind
        String existingIdentity = sessionIdentity.get(session.getId());
        if (existingIdentity != null && !existingIdentity.equals(instanceId)) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Session " + session.getId() + " already bound to " + existingIdentity
                    + ", rejecting rebind to " + instanceId)));
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // Bind identity to session
        sessionIdentity.put(session.getId(), instanceId);

        // Replace old session if agent reconnects from a different connection
        WebSocketSession oldSession = agentSessions.put(instanceId, session);
        if (oldSession != null && oldSession.isOpen() && !oldSession.getId().equals(session.getId())) {
            LOG.info(new ObjectMessage(Map.of("Message", "Replacing old WS session for agent " + instanceId)));
            sessionIdentity.remove(oldSession.getId());
            try { oldSession.close(CloseStatus.GOING_AWAY); } catch (IOException ignored) {}
        }

        agentLastSeen.put(instanceId, System.currentTimeMillis());
        agentWsState.put(instanceId, AgentWsState.REGISTERED);
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] ◄── HELLO from " + instanceId + " job=" + envelope.getJobId()
                + " agentSession=" + envelope.getAgentSessionId() + " capacity=" + envelope.getCapacity())));

        sendAck(session, instanceId, "hello", envelope.getAgentSessionId(), AckStatus.ok);
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] ──► ACK(hello) to " + instanceId)));

        if (isWsEnabled()) {
            fileTransferReady.put(instanceId, false);
            String authorization = session.getHandshakeHeaders() != null
                    ? session.getHandshakeHeaders().getFirst("Authorization")
                    : null;
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ⚙ Starting file transfer pipeline for " + instanceId)));
            transferExecutor.submit(() -> registerAndPushFiles(session, envelope, authorization));
        } else {
            fileTransferReady.put(instanceId, true);
            agentWsState.put(instanceId, AgentWsState.READY);
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ⚙ WS disabled — skipping file transfer for " + instanceId)));
        }
    }

    private void handleAck(WebSocketSession session, AgentWsEnvelope envelope) {
        String ackForId = envelope.getAckForId();
        if (ackForId != null) {
            CompletableFuture<AgentWsEnvelope> future = pendingAcks.remove(ackForId);
            if (future != null) {
                future.complete(envelope);
            }
        }
        String boundId = sessionIdentity.get(session.getId());
        if (boundId != null) {
            agentLastSeen.put(boundId, System.currentTimeMillis());
        }
    }

    private void handlePong(WebSocketSession session, AgentWsEnvelope envelope) {
        String boundId = sessionIdentity.get(session.getId());
        if (boundId != null) {
            agentLastSeen.put(boundId, System.currentTimeMillis());
            PendingPing pending = pendingPings.get(boundId);
            if (pending != null && Objects.equals(pending.pingId, envelope.getPingId())) {
                pendingPings.remove(boundId);
                missedPongs.remove(boundId);
            }
        }
    }

    private void handleFileAck(WebSocketSession session, AgentWsEnvelope envelope) {
        String boundId = sessionIdentity.get(session.getId());
        if (boundId == null) {
            return;
        }
        agentLastSeen.put(boundId, System.currentTimeMillis());
        if (envelope.getStatus() == AckStatus.all_files_complete) {
            fileTransferReady.put(boundId, true);
            agentWsState.put(boundId, AgentWsState.READY);
            Integer expected = agentExpectedFiles.get(boundId);
            if (expected != null) {
                agentCompletedFiles.put(boundId, expected);
                agentTransferProgress.put(boundId, expected + "/" + expected + " files");
            }
            PendingChunkAck pending = pendingChunkAcks.get(boundId);
            if (pending != null && pending.matches(envelope.getFileId(), envelope.getChunkIndex())) {
                pendingChunkAcks.remove(boundId, pending);
                pending.future.complete(null);
            }
            handleJobTransferComplete(boundId);
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ◄── FILE_ACK from " + boundId + " | ✓ ALL FILES COMPLETE — agent ready for start")));
        } else if (envelope.getStatus() == AckStatus.complete) {
            PendingChunkAck pending = pendingChunkAcks.get(boundId);
            if (pending != null && pending.matches(envelope.getFileId(), envelope.getChunkIndex())) {
                pendingChunkAcks.remove(boundId, pending);
                pending.future.complete(null);
            }
            updateTransferProgress(boundId);
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ◄── FILE_ACK from " + boundId + " | file complete (fileId=" + envelope.getFileId() + ")")));
        } else if (envelope.getStatus() == AckStatus.chunk_received) {
            PendingChunkAck pending = pendingChunkAcks.get(boundId);
            if (pending != null && pending.matches(envelope.getFileId(), envelope.getChunkIndex())) {
                pendingChunkAcks.remove(boundId, pending);
                pending.future.complete(null);
            }
            // don't log every chunk ack — too noisy
        } else if (envelope.getStatus() == AckStatus.failed) {
            fileTransferReady.put(boundId, false);
            agentTransferProgress.put(boundId, "failed");
            PendingChunkAck pending = pendingChunkAcks.get(boundId);
            if (pending != null && pending.matches(envelope.getFileId(), envelope.getChunkIndex())) {
                pendingChunkAcks.remove(boundId, pending);
                pending.future.completeExceptionally(new IOException(envelope.getError()));
            }
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ◄── FILE_ACK from " + boundId + " | ✗ FAILED: " + envelope.getError())));
        }
    }

    private void handleStatusUpdate(WebSocketSession session, AgentWsEnvelope envelope) {
        String boundId = sessionIdentity.get(session.getId());
        if (boundId == null) {
            return;
        }
        agentLastSeen.put(boundId, System.currentTimeMillis());
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] ◄── STATUS from " + boundId
                + " | vm=" + (envelope.getInstanceStatus() != null ? envelope.getInstanceStatus().getVmStatus() : "null")
                + " job=" + (envelope.getInstanceStatus() != null ? envelope.getInstanceStatus().getJobStatus() : "null"))));

        CloudVmStatus status = envelope.getInstanceStatus();
        if (status == null) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Missing status payload in status_update frame from " + boundId)));
            return;
        }

        status.setInstanceId(boundId);

        VMTracker tracker = resolveVMTracker();
        if (tracker == null) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Unable to resolve VMTracker for WS status update from " + boundId)));
            return;
        }

        try {
            tracker.setStatus(status);
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed WS status update from " + boundId + ": " + e.getMessage())));
        }
    }

    private void handleClose(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            LOG.info(new ObjectMessage(Map.of("Message", "Agent " + boundId + " sent close: " + envelope.getReason())));
            agentSessions.remove(boundId, session);
            fileTransferReady.remove(boundId);
            pendingPings.remove(boundId);
            missedPongs.remove(boundId);
            pendingChunkAcks.remove(boundId);
            agentJobs.remove(boundId);
            agentWsState.put(boundId, AgentWsState.DISCONNECTED);
        }
        session.close(CloseStatus.NORMAL);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            agentSessions.remove(boundId, session);
            fileTransferReady.remove(boundId);
            pendingPings.remove(boundId);
            missedPongs.remove(boundId);
            pendingChunkAcks.remove(boundId);
            agentJobs.remove(boundId);
            agentWsState.put(boundId, AgentWsState.DISCONNECTED);
            markDisconnectedIfActive(boundId);
        }
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] === CONNECTION CLOSED === agent=" + (boundId != null ? boundId : "unbound") + " session=" + session.getId() + " status=" + status)));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        LOG.error(new ObjectMessage(Map.of("Message", "WS transport error for session " + session.getId() + ": " + exception.getMessage())), exception);
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            agentSessions.remove(boundId, session);
            fileTransferReady.remove(boundId);
            pendingPings.remove(boundId);
            missedPongs.remove(boundId);
            pendingChunkAcks.remove(boundId);
            agentJobs.remove(boundId);
            agentWsState.put(boundId, AgentWsState.DISCONNECTED);
            markDisconnectedIfActive(boundId);
        }
    }

    @Override
    public boolean hasSession(String instanceId) {
        WebSocketSession session = agentSessions.get(instanceId);
        return session != null && session.isOpen();
    }

    @Override
    public boolean sendCommand(String instanceId, String jobId, String command, long ackTimeoutMillis) {
        ackTimeoutMillis = 3000L;
        WebSocketSession session = agentSessions.get(instanceId);
        if (session == null || !session.isOpen()) {
            return false;
        }

        String commandId = UUID.randomUUID().toString();
        CompletableFuture<AgentWsEnvelope> ackFuture = new CompletableFuture<>();
        pendingAcks.put(commandId, ackFuture);

        try {
            AgentWsEnvelope cmdEnvelope = AgentWsEnvelope.command(commandId, instanceId, jobId, command);
            synchronized (session) {
                session.sendMessage(new TextMessage(cmdEnvelope.toJson()));
            }
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ──► COMMAND to " + instanceId + " | " + command + " (id=" + commandId + ")")));

            AgentWsEnvelope ack = ackFuture.get(ackTimeoutMillis, TimeUnit.MILLISECONDS);
            boolean success = ack != null && ack.getStatus() == AckStatus.ok;
            if (success && "start".equalsIgnoreCase(command)) {
                agentWsState.put(instanceId, AgentWsState.RUNNING);
            }
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ◄── ACK(command) from " + instanceId + " | " + command + " = " + (success ? "✓ OK" : "✗ FAILED"))));
            return success;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ✗ COMMAND " + command + " to " + instanceId + " FAILED: " + e.getMessage())));
            pendingAcks.remove(commandId);
            return false;
        }
    }

    @Override
    public boolean isFileTransferReady(String instanceId) {
        if (!isWsEnabled()) {
            return true;
        }
        return Boolean.TRUE.equals(fileTransferReady.get(instanceId));
    }

    @Override
    public String getWsState(String instanceId) {
        AgentWsState state = agentWsState.get(instanceId);
        return state != null ? state.name().toLowerCase() : null;
    }

    @Override
    public String getTransferProgress(String instanceId) {
        return agentTransferProgress.get(instanceId);
    }

    @Override
    public Long getLastSeen(String instanceId) {
        return agentLastSeen.get(instanceId);
    }

    private void registerAndPushFiles(WebSocketSession session, AgentWsEnvelope hello, String authorizationHeader) {
        String agentId = hello.getInstanceId();
        String jobId = hello.getJobId();
        try {
            agentWsState.put(agentId, AgentWsState.TRANSFERRING);
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ⚙ " + agentId + " | Resolving JobManager...")));
            JobManager jobManager = resolveJobManager();
            if (jobManager == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ✗ " + agentId + " | Failed to resolve JobManager — cannot transfer files")));
                try {
                    sendEnvelope(session, AgentWsEnvelope.close(agentId, "bootstrap_failed", "JobManager unavailable"));
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException ignored) {}
                return;
            }

            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ⚙ " + agentId + " | Registering agent for job " + jobId)));
            AgentData agentData = jobManager.buildAgentDataForWsHello(
                    jobId, agentId, resolveInstanceUrl(session), hello.getCapacity());

            AgentTestStartData startData = jobManager.registerAgentForJob(agentData);
            if (startData == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "[WS] ✗ " + agentId + " | registerAgentForJob returned null — job " + jobId + " not in cache?")));
                try {
                    sendEnvelope(session, AgentWsEnvelope.close(agentId, "bootstrap_failed", "Job " + jobId + " not found in cache"));
                    session.close(CloseStatus.SERVER_ERROR);
                } catch (IOException ignored) {}
                return;
            }
            agentJobs.put(agentId, jobId);
            if (startData.getTotalAgents() > 0) {
                jobExpectedAgents.put(jobId, startData.getTotalAgents());
            }
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ✓ " + agentId + " | Registered for job " + jobId
                    + " users=" + startData.getConcurrentUsers() + " ramp=" + startData.getRampTime()
                    + " scriptUrl=" + startData.getScriptUrl())));

            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ⚙ " + agentId + " | Building transfer files...")));
            List<AgentWsFileTransferService.TransferFile> files =
                    fileTransferService.buildTransferFiles(startData, authorizationHeader);
            agentExpectedFiles.put(agentId, files.size());
            agentCompletedFiles.put(agentId, 0);
            agentTransferProgress.put(agentId, "0/" + files.size() + " files");
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ⚙ " + agentId + " | Built " + files.size() + " files to transfer")));

            for (AgentWsFileTransferService.TransferFile f : files) {
                LOG.info(new ObjectMessage(Map.of("Message", "[WS]   • " + f.getFileType() + " | " + f.getFileName() + " | " + (f.getContent() != null ? f.getContent().length : 0) + " bytes")));
            }

            AgentWsEnvelope jobConfig = AgentWsEnvelope.jobConfig(agentId, jobId, startData, files.size());
            sendEnvelope(session, jobConfig);
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ──► JOB_CONFIG to " + agentId + " | expectedFiles=" + files.size())));

            int chunkBytes = 49152;
            fileTransferService.sendFiles(session, agentId, jobId, files, chunkBytes, this::prepareChunkAckGate);
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] ✓ " + agentId + " | All files sent — waiting for agent ack")));
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] ✗ " + agentId + " | File transfer FAILED: " + e.getMessage())), e);
            try {
                sendEnvelope(session, AgentWsEnvelope.close(agentId, "bootstrap_failed", "Transfer error: " + e.getMessage()));
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ignored) {}
        }
    }

    private void heartbeatTick() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, WebSocketSession> entry : agentSessions.entrySet()) {
            String instanceId = entry.getKey();
            WebSocketSession session = entry.getValue();

            if (session == null || !session.isOpen()) {
                pendingPings.remove(instanceId);
                missedPongs.remove(instanceId);
                continue;
            }

            PendingPing pendingPing = pendingPings.get(instanceId);
            if (pendingPing != null && (now - pendingPing.sentAtMs) > 5000L) {
                int missed = missedPongs.merge(instanceId, 1, Integer::sum);
                pendingPings.remove(instanceId);
                if (missed >= 2) {
                    LOG.warn(new ObjectMessage(Map.of("Message", "Closing stale WS session for " + instanceId + " after missed pong threshold")));
                    try {
                        session.close(CloseStatus.SESSION_NOT_RELIABLE);
                    } catch (IOException ignored) {
                    }
                    continue;
                }
            }

            if (!pendingPings.containsKey(instanceId)) {
                String pingId = UUID.randomUUID().toString();
                AgentWsEnvelope ping = AgentWsEnvelope.ping(pingId);
                heartbeatSendExecutor.submit(() -> {
                    try {
                        sendEnvelope(session, ping);
                        pendingPings.put(instanceId, new PendingPing(pingId, now));
                    } catch (Exception e) {
                        LOG.warn(new ObjectMessage(Map.of("Message", "[WS] Failed heartbeat ping for " + instanceId + ": " + e.getMessage())));
                    }
                });
            }
        }
    }

    private void sendEnvelope(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(envelope.toJson()));
        }
    }

    private AgentConfig resolveAgentConfig() {
        AgentConfig config = cachedAgentConfig;
        if (config != null) {
            return config;
        }
        try {
            config = new TankConfig().getAgentConfig();
            cachedAgentConfig = config;
            return config;
        } catch (Exception e) {
            return null;
        }
    }



    private boolean isWsEnabled() {
        AgentConfig config = resolveAgentConfig();
        return config != null && config.isCommandWsEnabled();
    }

    private JobManager resolveJobManager() {
        JobManager jobManager = cachedJobManager;
        if (jobManager != null) {
            return jobManager;
        }
        if (servletContext == null) {
            return null;
        }
        try {
            jobManager = new ServletInjector<JobManager>().getManagedBean(servletContext, JobManager.class);
            cachedJobManager = jobManager;
            return jobManager;
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "Error resolving JobManager: " + e.getMessage())), e);
            return null;
        }
    }

    private VMTracker resolveVMTracker() {
        VMTracker tracker = cachedVMTracker;
        if (tracker != null) {
            return tracker;
        }
        if (servletContext == null) {
            return null;
        }
        try {
            tracker = new ServletInjector<VMTracker>().getManagedBean(servletContext, VMTracker.class);
            cachedVMTracker = tracker;
            return tracker;
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "Error resolving VMTracker: " + e.getMessage())), e);
            return null;
        }
    }

    private String resolveInstanceUrl(WebSocketSession session) {
        int agentPort = 8090;
        AgentConfig config = resolveAgentConfig();
        if (config != null) {
            agentPort = config.getAgentPort();
        }

        InetSocketAddress remote = session.getRemoteAddress();
        String host = "localhost";
        if (remote != null) {
            if (remote.getAddress() != null && remote.getAddress().getHostAddress() != null) {
                host = remote.getAddress().getHostAddress();
            } else if (remote.getHostString() != null) {
                host = remote.getHostString();
            }
        }
        return "http://" + host + ":" + agentPort;
    }

    private CompletableFuture<Void> prepareChunkAckGate(String instanceId, String fileId, int chunkIndex) {
        CompletableFuture<Void> gate = new CompletableFuture<>();
        pendingChunkAcks.put(instanceId, new PendingChunkAck(fileId, chunkIndex, gate));
        return gate;
    }

    private void updateTransferProgress(String instanceId) {
        Integer expected = agentExpectedFiles.get(instanceId);
        int completed = agentCompletedFiles.merge(instanceId, 1, Integer::sum);
        if (expected != null) {
            int visibleCompleted = Math.min(completed, expected);
            agentTransferProgress.put(instanceId, visibleCompleted + "/" + expected + " files");
        }
    }

    private void markDisconnectedIfActive(String instanceId) {
        VMTracker tracker = resolveVMTracker();
        if (tracker == null) {
            return;
        }
        CloudVmStatus current = tracker.getStatus(instanceId);
        if (current == null || current.getVmStatus() == null || isTerminalVmStatus(current.getVmStatus())) {
            return;
        }
        CloudVmStatus disconnected = new CloudVmStatus(current);
        disconnected.setUserDetails(current.getUserDetails());
        disconnected.setVmStatus(VMStatus.disconnected);
        tracker.setStatus(disconnected);
    }

    private boolean isTerminalVmStatus(VMStatus status) {
        return status == VMStatus.shutting_down
                || status == VMStatus.stopped
                || status == VMStatus.stopping
                || status == VMStatus.terminated
                || status == VMStatus.replaced
                || status == VMStatus.disconnected;
    }

    private void handleJobTransferComplete(String instanceId) {
        String jobId = agentJobs.get(instanceId);
        if (jobId == null) {
            return;
        }
        Set<String> completedAgents = jobTransferCompleteAgents.computeIfAbsent(jobId, key -> ConcurrentHashMap.newKeySet());
        completedAgents.add(instanceId);
        Integer expectedAgents = jobExpectedAgents.get(jobId);
        if (expectedAgents != null && expectedAgents > 0 && completedAgents.size() >= expectedAgents) {
            fileTransferService.clearJobCache(jobId);
            jobExpectedAgents.remove(jobId);
            jobTransferCompleteAgents.remove(jobId);
        }
    }

    private void sendAck(WebSocketSession session, String instanceId, String ackForType, String ackForId, AckStatus status) throws IOException {
        AgentWsEnvelope ack = AgentWsEnvelope.ack(instanceId, ackForType, ackForId, status);
        synchronized (session) {
            session.sendMessage(new TextMessage(ack.toJson()));
        }
    }

    private static class PendingPing {
        private final String pingId;
        private final long sentAtMs;

        private PendingPing(String pingId, long sentAtMs) {
            this.pingId = pingId;
            this.sentAtMs = sentAtMs;
        }
    }

    private static class PendingChunkAck {
        private final String fileId;
        private final int chunkIndex;
        private final CompletableFuture<Void> future;

        private PendingChunkAck(String fileId, int chunkIndex, CompletableFuture<Void> future) {
            this.fileId = fileId;
            this.chunkIndex = chunkIndex;
            this.future = future;
        }

        private boolean matches(String fileId, Integer chunkIndex) {
            return Objects.equals(this.fileId, fileId) && chunkIndex != null && this.chunkIndex == chunkIndex;
        }
    }
}
