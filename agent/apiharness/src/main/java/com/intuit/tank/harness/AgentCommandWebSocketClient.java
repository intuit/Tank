package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AgentCommandWebSocketClient implements WebSocket.Listener {

    private static final Logger LOG = LogManager.getLogger(AgentCommandWebSocketClient.class);

    private static final int[] BACKOFF_SECONDS = {1, 2, 5, 10, 20, 30};

    private final String controllerBaseUrl;
    private final String wsPath;
    private final String token;
    private final String instanceId;
    private final String jobId;
    private final String agentSessionId;
    private final HttpClient httpClient;

    private static final int MAX_APPLIED_COMMAND_IDS = 10_000;

    private final AtomicReference<WebSocket> webSocketRef = new AtomicReference<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private final Set<String> appliedCommandIds = ConcurrentHashMap.newKeySet();
    private volatile String lastAppliedCommandId = null;

    // Buffer for accumulating partial text frames
    private final StringBuilder messageBuffer = new StringBuilder();

    public AgentCommandWebSocketClient(String controllerBaseUrl, String wsPath, String token,
                                       String instanceId, String jobId) {
        this.controllerBaseUrl = controllerBaseUrl;
        this.wsPath = wsPath;
        this.token = token;
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.agentSessionId = UUID.randomUUID().toString();
        this.httpClient = HttpClient.newHttpClient();
    }

    public void connect() {
        Thread connectThread = new Thread(this::connectWithRetry, "WS-Connect-" + instanceId);
        connectThread.setDaemon(true);
        connectThread.start();
    }

    private void connectWithRetry() {
        int attempt = 0;
        while (running.get()) {
            try {
                String wsUrl = buildWsUrl();
                LOG.info(new ObjectMessage(Map.of("Message", "Connecting WS to " + wsUrl)));

                WebSocket ws = httpClient.newWebSocketBuilder()
                        .header("Authorization", "bearer " + token)
                        .buildAsync(URI.create(wsUrl), this)
                        .join();

                webSocketRef.set(ws);
                attempt = 0; // reset on successful connect

                // Send hello
                AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, lastAppliedCommandId);
                ws.sendText(hello.toJson(), true);
                LOG.info(new ObjectMessage(Map.of("Message", "WS hello sent for agent " + instanceId + " job " + jobId)));

                // Block until connection closes (handled by listener callbacks)
                // The listener methods will handle incoming frames
                return;

            } catch (Exception e) {
                int backoff = BACKOFF_SECONDS[Math.min(attempt, BACKOFF_SECONDS.length - 1)];
                // Add jitter: 0-500ms
                int jitter = (int) (Math.random() * 500);
                LOG.warn(new ObjectMessage(Map.of("Message", "WS connect failed (attempt " + attempt + "): " + e.getMessage()
                        + ", retrying in " + backoff + "s")));
                try {
                    Thread.sleep(backoff * 1000L + jitter);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
                attempt++;
            }
        }
    }

    private String buildWsUrl() {
        String base = controllerBaseUrl;
        // Convert http(s) to ws(s)
        if (base.startsWith("https://")) {
            base = "wss://" + base.substring("https://".length());
        } else if (base.startsWith("http://")) {
            base = "ws://" + base.substring("http://".length());
        }
        // Remove trailing slash
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + wsPath;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        LOG.info(new ObjectMessage(Map.of("Message", "WS connection opened for agent " + instanceId)));
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        messageBuffer.append(data);
        if (last) {
            String fullMessage = messageBuffer.toString();
            messageBuffer.setLength(0);
            handleMessage(webSocket, fullMessage);
        }
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        LOG.info(new ObjectMessage(Map.of("Message", "WS closed: code=" + statusCode + " reason=" + reason)));
        webSocketRef.set(null);
        scheduleReconnect();
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        LOG.error(new ObjectMessage(Map.of("Message", "WS error for agent " + instanceId + ": " + error.getMessage())), error);
        webSocketRef.set(null);
        scheduleReconnect();
    }

    private void scheduleReconnect() {
        if (running.get() && reconnecting.compareAndSet(false, true)) {
            Thread reconnect = new Thread(() -> {
                try {
                    connectWithRetry();
                } finally {
                    reconnecting.set(false);
                }
            }, "WS-Reconnect-" + instanceId);
            reconnect.setDaemon(true);
            reconnect.start();
        }
    }

    private void handleMessage(WebSocket webSocket, String text) {
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(text);
            if (envelope.getType() == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "Received WS frame with no type")));
                return;
            }

            switch (envelope.getType()) {
                case command -> handleCommand(webSocket, envelope);
                case ping -> handlePing(webSocket, envelope);
                case ack -> LOG.info(new ObjectMessage(Map.of("Message", "Received ack: " + envelope.getAckForType())));
                default -> LOG.warn(new ObjectMessage(Map.of("Message", "Unexpected WS frame type: " + envelope.getType())));
            }
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to parse WS frame: " + e.getMessage())));
        }
    }

    private void handleCommand(WebSocket webSocket, AgentWsEnvelope envelope) {
        String commandId = envelope.getCommandId();
        String command = envelope.getCommand();

        // Bound the dedup set to prevent unbounded growth
        if (appliedCommandIds.size() > MAX_APPLIED_COMMAND_IDS) {
            appliedCommandIds.clear();
            LOG.info(new ObjectMessage(Map.of("Message", "Cleared appliedCommandIds set (exceeded " + MAX_APPLIED_COMMAND_IDS + ")")));
        }

        // Deduplicate
        if (commandId != null && !appliedCommandIds.add(commandId)) {
            LOG.info(new ObjectMessage(Map.of("Message", "Duplicate WS command " + commandId + " ignored")));
            sendAck(webSocket, commandId, AckStatus.duplicate);
            return;
        }

        LOG.info(new ObjectMessage(Map.of("Message", "Received WS command: " + command + " (id=" + commandId + ")")));

        try {
            applyCommand(command);
            lastAppliedCommandId = commandId;
            sendAck(webSocket, commandId, AckStatus.ok);
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "Error applying WS command " + command + ": " + e.getMessage())), e);
            sendAck(webSocket, commandId, AckStatus.failed);
        }
    }

    private void applyCommand(String command) {
        CommandListener.applyCommand(command);
    }

    private void handlePing(WebSocket webSocket, AgentWsEnvelope envelope) {
        try {
            AgentWsEnvelope pong = AgentWsEnvelope.pong(instanceId, agentSessionId, envelope.getPingId(), lastAppliedCommandId);
            webSocket.sendText(pong.toJson(), true);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send pong: " + e.getMessage())));
        }
    }

    private void sendAck(WebSocket webSocket, String commandId, AckStatus status) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.ack(instanceId, "command", commandId, status);
            webSocket.sendText(ack.toJson(), true);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send ack for " + commandId + ": " + e.getMessage())));
        }
    }

    public void close() {
        running.set(false);
        WebSocket ws = webSocketRef.get();
        if (ws != null) {
            try {
                AgentWsEnvelope closeEnvelope = AgentWsEnvelope.close(instanceId, "agent_shutdown", "Agent shutting down");
                ws.sendText(closeEnvelope.toJson(), true);
            } catch (IOException ignored) {}
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "Agent shutting down");
        }
    }
}
