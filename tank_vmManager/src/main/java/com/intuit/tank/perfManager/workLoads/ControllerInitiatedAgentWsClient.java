package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class ControllerInitiatedAgentWsClient {

    private static final Logger LOG = LogManager.getLogger(ControllerInitiatedAgentWsClient.class);

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ConcurrentHashMap<String, SessionContext> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<AgentWsEnvelope>> pendingAcks = new ConcurrentHashMap<>();

    public Optional<AgentWsEnvelope> connect(String instanceId, String wsUrl, String token, long helloTimeoutMillis) {
        try {
            SessionContext existing = sessions.get(instanceId);
            if (existing != null && existing.isOpen()) {
                return Optional.empty();
            }

            CompletableFuture<AgentWsEnvelope> helloFuture = new CompletableFuture<>();
            Listener listener = new Listener(instanceId, helloFuture);

            WebSocket webSocket = httpClient.newWebSocketBuilder()
                    .header("Authorization", "bearer " + token)
                    .buildAsync(URI.create(wsUrl), listener)
                    .join();

            SessionContext context = new SessionContext(webSocket, helloFuture);
            SessionContext previous = sessions.put(instanceId, context);
            if (previous != null) {
                previous.close();
            }

            AgentWsEnvelope hello = helloFuture.get(helloTimeoutMillis, TimeUnit.MILLISECONDS);
            if (hello != null) {
                LOG.info(new ObjectMessage(Map.of("Message",
                        "Controller initiated WS connected to agent " + instanceId + " via " + wsUrl)));
                return Optional.of(hello);
            }
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "Failed to connect WS to agent " + instanceId + ": " + e.getMessage())));
        }

        SessionContext failed = sessions.remove(instanceId);
        if (failed != null) {
            failed.close();
        }
        return Optional.empty();
    }

    public boolean hasSession(String instanceId) {
        SessionContext context = sessions.get(instanceId);
        return context != null && context.isOpen();
    }

    public boolean sendCommand(String instanceId, String jobId, String command, long ackTimeoutMillis) {
        SessionContext context = sessions.get(instanceId);
        if (context == null || !context.isOpen()) {
            return false;
        }

        String commandId = UUID.randomUUID().toString();
        CompletableFuture<AgentWsEnvelope> ackFuture = new CompletableFuture<>();
        pendingAcks.put(commandId, ackFuture);

        try {
            AgentWsEnvelope cmd = AgentWsEnvelope.command(commandId, instanceId, jobId, command);
            context.send(cmd.toJson());

            AgentWsEnvelope ack = ackFuture.get(ackTimeoutMillis, TimeUnit.MILLISECONDS);
            return ack != null && ack.getStatus() == AckStatus.ok;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "WS command " + command + " to " + instanceId + " failed: " + e.getMessage())));
            return false;
        } finally {
            pendingAcks.remove(commandId);
        }
    }

    private void handleText(String instanceId, String text, CompletableFuture<AgentWsEnvelope> helloFuture) {
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(text);
            if (envelope.getType() == null) {
                return;
            }
            switch (envelope.getType()) {
                case hello -> helloFuture.complete(envelope);
                case ack -> {
                    String ackForId = envelope.getAckForId();
                    if (ackForId != null) {
                        CompletableFuture<AgentWsEnvelope> future = pendingAcks.remove(ackForId);
                        if (future != null) {
                            future.complete(envelope);
                        }
                    }
                }
                default -> {
                    // no-op for pong/close/ping in PoC client path
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void onClosed(String instanceId) {
        SessionContext context = sessions.remove(instanceId);
        if (context != null) {
            context.markClosed();
        }
    }

    private class Listener implements WebSocket.Listener {
        private final String instanceId;
        private final CompletableFuture<AgentWsEnvelope> helloFuture;
        private final StringBuilder messageBuffer = new StringBuilder();

        private Listener(String instanceId, CompletableFuture<AgentWsEnvelope> helloFuture) {
            this.instanceId = instanceId;
            this.helloFuture = helloFuture;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            messageBuffer.append(data);
            if (last) {
                String fullMessage = messageBuffer.toString();
                messageBuffer.setLength(0);
                handleText(instanceId, fullMessage, helloFuture);
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
            onClosed(instanceId);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "Controller initiated WS listener error for " + instanceId + ": " + error.getMessage())));
            onClosed(instanceId);
        }
    }

    private static class SessionContext {
        private final WebSocket webSocket;
        private final CompletableFuture<AgentWsEnvelope> helloFuture;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        private SessionContext(WebSocket webSocket, CompletableFuture<AgentWsEnvelope> helloFuture) {
            this.webSocket = webSocket;
            this.helloFuture = helloFuture;
        }

        private boolean isOpen() {
            return !closed.get() && !webSocket.isInputClosed() && !webSocket.isOutputClosed();
        }

        private void send(String text) {
            webSocket.sendText(text, true).join();
        }

        private void close() {
            if (closed.compareAndSet(false, true)) {
                try {
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing previous session");
                } catch (Exception ignored) {
                }
                helloFuture.completeExceptionally(new IllegalStateException("Closed"));
            }
        }

        private void markClosed() {
            closed.set(true);
            helloFuture.completeExceptionally(new IllegalStateException("Closed"));
        }
    }
}
