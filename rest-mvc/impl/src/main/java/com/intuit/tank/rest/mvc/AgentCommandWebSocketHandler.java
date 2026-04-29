package com.intuit.tank.rest.mvc;

import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.services.agent.AgentServiceV2;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.Type;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AgentCommandWebSocketHandler extends TextWebSocketHandler implements AgentWsCommandSender {

    private static final Logger LOG = LogManager.getLogger(AgentCommandWebSocketHandler.class);

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

    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "AgentWsHeartbeat");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService transferExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "AgentWsTransfer");
        t.setDaemon(true);
        return t;
    });

    private final AgentWsFileTransferService fileTransferService = new AgentWsFileTransferService();

    @Autowired
    private ServletContext servletContext;

    public AgentCommandWebSocketHandler() {
        heartbeatScheduler.scheduleAtFixedRate(this::heartbeatTick, 30L, 30L, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOG.info(new ObjectMessage(Map.of("Message", "WS connection established: " + session.getId())));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        AgentWsEnvelope envelope;
        try {
            envelope = AgentWsEnvelope.fromJson(message.getPayload());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Invalid WS frame, closing session: " + e.getMessage())));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (envelope.getType() == null) {
            LOG.warn(new ObjectMessage(Map.of("Message", "WS frame missing type, closing session")));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // Require hello before any other frame type
        if (envelope.getType() != Type.hello && !sessionIdentity.containsKey(session.getId())) {
            LOG.warn(new ObjectMessage(Map.of("Message", "WS frame received before hello, closing session")));
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        switch (envelope.getType()) {
            case hello -> handleHello(session, envelope);
            case ack -> handleAck(session, envelope);
            case file_ack -> handleFileAck(session, envelope);
            case pong -> handlePong(session, envelope);
            case close -> handleClose(session, envelope);
            default -> {
                LOG.warn(new ObjectMessage(Map.of("Message", "Unexpected WS frame type from agent: " + envelope.getType())));
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
        LOG.info(new ObjectMessage(Map.of("Message", "Agent " + instanceId + " registered via WS for job " + envelope.getJobId()
                + " (agentSession=" + envelope.getAgentSessionId() + ")")));

        sendAck(session, instanceId, "hello", envelope.getAgentSessionId(), AckStatus.ok);

        if (isWsFileTransferEnabled()) {
            fileTransferReady.put(instanceId, false);
            String authorization = session.getHandshakeHeaders() != null
                    ? session.getHandshakeHeaders().getFirst("Authorization")
                    : null;
            transferExecutor.submit(() -> registerAndPushFiles(session, envelope, authorization));
        } else {
            fileTransferReady.put(instanceId, true);
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
            LOG.info(new ObjectMessage(Map.of("Message", "Agent " + boundId + " completed WS file transfer")));
        } else if (envelope.getStatus() == AckStatus.failed) {
            fileTransferReady.put(boundId, false);
            LOG.warn(new ObjectMessage(Map.of("Message", "Agent " + boundId + " reported WS transfer failure: " + envelope.getError())));
        }
    }

    private void handleClose(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            LOG.info(new ObjectMessage(Map.of("Message", "Agent " + boundId + " sent close: " + envelope.getReason())));
            agentSessions.remove(boundId, session);
            agentLastSeen.remove(boundId);
            fileTransferReady.remove(boundId);
            pendingPings.remove(boundId);
            missedPongs.remove(boundId);
        }
        session.close(CloseStatus.NORMAL);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            agentSessions.remove(boundId, session);
            agentLastSeen.remove(boundId);
            fileTransferReady.remove(boundId);
            pendingPings.remove(boundId);
            missedPongs.remove(boundId);
        }
        LOG.info(new ObjectMessage(Map.of("Message", "WS session closed: " + session.getId() + " status=" + status)));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        LOG.error(new ObjectMessage(Map.of("Message", "WS transport error for session " + session.getId() + ": " + exception.getMessage())), exception);
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            agentSessions.remove(boundId, session);
            agentLastSeen.remove(boundId);
            fileTransferReady.remove(boundId);
            pendingPings.remove(boundId);
            missedPongs.remove(boundId);
        }
    }

    @Override
    public boolean hasSession(String instanceId) {
        WebSocketSession session = agentSessions.get(instanceId);
        return session != null && session.isOpen();
    }

    @Override
    public boolean sendCommand(String instanceId, String jobId, String command, long ackTimeoutMillis) {
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
            LOG.info(new ObjectMessage(Map.of("Message", "Sent WS command " + command + " (id=" + commandId + ") to agent " + instanceId)));

            AgentWsEnvelope ack = ackFuture.get(ackTimeoutMillis, TimeUnit.MILLISECONDS);
            return ack != null && ack.getStatus() == AckStatus.ok;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "WS command " + command + " to agent " + instanceId + " failed: " + e.getMessage())));
            pendingAcks.remove(commandId);
            return false;
        }
    }

    @Override
    public boolean isFileTransferReady(String instanceId) {
        if (!isWsFileTransferEnabled()) {
            return true;
        }
        return Boolean.TRUE.equals(fileTransferReady.get(instanceId));
    }

    private void registerAndPushFiles(WebSocketSession session, AgentWsEnvelope hello, String authorizationHeader) {
        try {
            JobManager jobManager = resolveJobManager();
            if (jobManager == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "Unable to resolve JobManager for WS transfer")));
                return;
            }

            AgentData agentData = jobManager.buildAgentDataForWsHello(
                    hello.getJobId(),
                    hello.getInstanceId(),
                    resolveInstanceUrl(session),
                    hello.getCapacity());

            AgentTestStartData startData = jobManager.registerAgentForJob(agentData);
            if (startData == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "No job start data available for WS transfer: job=" + hello.getJobId())));
                return;
            }

            File supportFilesArchive = null;
            AgentServiceV2 agentService = resolveAgentService();
            if (agentService != null) {
                supportFilesArchive = agentService.getSupportFiles();
            }

            List<AgentWsFileTransferService.TransferFile> files =
                    fileTransferService.buildTransferFiles(startData, authorizationHeader, supportFilesArchive);
            AgentWsEnvelope jobConfig = AgentWsEnvelope.jobConfig(
                    hello.getInstanceId(),
                    hello.getJobId(),
                    startData,
                    files.size());
            sendEnvelope(session, jobConfig);

            AgentConfig config = resolveAgentConfig();
            int chunkBytes = config != null ? config.getCommandWsFileTransferChunkBytes() : 49152;
            fileTransferService.sendFiles(session, hello.getInstanceId(), hello.getJobId(), files, chunkBytes);
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "WS file transfer failed for " + hello.getInstanceId() + ": " + e.getMessage())), e);
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
                try {
                    sendEnvelope(session, ping);
                    pendingPings.put(instanceId, new PendingPing(pingId, now));
                } catch (Exception e) {
                    LOG.warn(new ObjectMessage(Map.of("Message", "Failed heartbeat ping for " + instanceId + ": " + e.getMessage())));
                }
            }
        }
    }

    private void sendEnvelope(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(envelope.toJson()));
        }
    }

    private AgentConfig resolveAgentConfig() {
        try {
            return new TankConfig().getAgentConfig();
        } catch (Exception e) {
            return null;
        }
    }

    private AgentServiceV2 resolveAgentService() {
        if (servletContext == null) {
            return null;
        }
        try {
            return new ServletInjector<AgentServiceV2>().getManagedBean(servletContext, AgentServiceV2.class);
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "Error resolving AgentServiceV2: " + e.getMessage())), e);
            return null;
        }
    }

    private boolean isWsFileTransferEnabled() {
        AgentConfig config = resolveAgentConfig();
        return config != null && config.isCommandWsFileTransferEnabled();
    }

    private JobManager resolveJobManager() {
        if (servletContext == null) {
            return null;
        }
        try {
            return new ServletInjector<JobManager>().getManagedBean(servletContext, JobManager.class);
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "Error resolving JobManager: " + e.getMessage())), e);
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
}
