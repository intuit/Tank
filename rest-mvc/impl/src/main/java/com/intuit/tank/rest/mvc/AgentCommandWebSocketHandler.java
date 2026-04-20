package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
        }
    }

    private void handleClose(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            LOG.info(new ObjectMessage(Map.of("Message", "Agent " + boundId + " sent close: " + envelope.getReason())));
            agentSessions.remove(boundId, session);
            agentLastSeen.remove(boundId);
        }
        session.close(CloseStatus.NORMAL);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String boundId = sessionIdentity.remove(session.getId());
        if (boundId != null) {
            agentSessions.remove(boundId, session);
            agentLastSeen.remove(boundId);
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

    private void sendAck(WebSocketSession session, String instanceId, String ackForType, String ackForId, AckStatus status) throws IOException {
        AgentWsEnvelope ack = AgentWsEnvelope.ack(instanceId, ackForType, ackForId, status);
        synchronized (session) {
            session.sendMessage(new TextMessage(ack.toJson()));
        }
    }
}
