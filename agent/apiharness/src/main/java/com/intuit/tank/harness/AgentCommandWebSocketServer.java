package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AgentCommandWebSocketServer extends WebSocketServer {

    private static final Logger LOG = LogManager.getLogger(AgentCommandWebSocketServer.class);
    private static final int MAX_APPLIED_COMMAND_IDS = 10_000;

    private final String instanceId;
    private final String jobId;
    private final int capacity;
    private final String agentSessionId;

    private final Set<String> appliedCommandIds = ConcurrentHashMap.newKeySet();
    private volatile String lastAppliedCommandId;

    public AgentCommandWebSocketServer(int port, String instanceId, String jobId, int capacity) {
        super(new InetSocketAddress(port));
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.capacity = capacity;
        this.agentSessionId = UUID.randomUUID().toString();
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS connected to agent " + instanceId + " from " + connection.getRemoteSocketAddress())));
        try {
            AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, lastAppliedCommandId, capacity);
            connection.send(hello.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send WS hello: " + e.getMessage())));
            connection.close();
        }
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(message);
            if (envelope.getType() == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "WS frame missing type")));
                return;
            }

            switch (envelope.getType()) {
                case command -> handleCommand(connection, envelope);
                case ping -> handlePing(connection, envelope);
                case close -> {
                    LOG.info(new ObjectMessage(Map.of("Message", "Controller closed WS: " + envelope.getReason())));
                    connection.close();
                }
                default -> LOG.warn(new ObjectMessage(Map.of("Message", "Unexpected WS frame type: " + envelope.getType())));
            }
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed parsing WS message: " + e.getMessage())));
        }
    }

    private void handleCommand(WebSocket connection, AgentWsEnvelope envelope) {
        String commandId = envelope.getCommandId();
        String command = envelope.getCommand();

        if (appliedCommandIds.size() > MAX_APPLIED_COMMAND_IDS) {
            appliedCommandIds.clear();
        }

        if (commandId != null && !appliedCommandIds.add(commandId)) {
            sendAck(connection, commandId, AckStatus.duplicate, null);
            return;
        }

        try {
            CommandListener.applyCommand(command);
            lastAppliedCommandId = commandId;
            sendAck(connection, commandId, AckStatus.ok, null);
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "Failed to apply command " + command + " for agent " + instanceId + ": " + e.getMessage())));
            sendAck(connection, commandId, AckStatus.failed, e.getMessage());
        }
    }

    private void handlePing(WebSocket connection, AgentWsEnvelope envelope) {
        try {
            AgentWsEnvelope pong = AgentWsEnvelope.pong(instanceId, agentSessionId, envelope.getPingId(), lastAppliedCommandId);
            connection.send(pong.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send pong: " + e.getMessage())));
        }
    }

    private void sendAck(WebSocket connection, String commandId, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.ack(instanceId, "command", commandId, status);
            ack.setError(error);
            connection.send(ack.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send command ack: " + e.getMessage())));
        }
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS disconnected from agent " + instanceId + " code=" + code + " reason=" + reason)));
    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        LOG.error(new ObjectMessage(Map.of("Message", "Agent WS server transport error: " + ex.getMessage())), ex);
    }

    @Override
    public void onStart() {
        LOG.info(new ObjectMessage(Map.of("Message", "Agent WS control server started for " + instanceId)));
    }
}
