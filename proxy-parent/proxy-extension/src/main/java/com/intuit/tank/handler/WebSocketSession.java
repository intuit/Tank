package com.intuit.tank.handler;

import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Tracks the state of a single WebSocket connection during proxy recording.
 * 
 * Lifecycle:
 * 1. Created when HTTP 101 upgrade response is detected
 * 2. addMessage() called for each frame relayed (client→server or server→client)
 * 3. close() called when connection ends
 * 4. toTransaction() converts to WebSocketTransaction for XML serialization
 * 
 * Thread-safe: multiple threads may call addMessage() concurrently
 * (client→server and server→client relay threads).
 */
public class WebSocketSession {

    private final String url;
    private final String connectionId;
    private final Map<String, String> handshakeHeaders;
    private final long startTime;
    private volatile long endTime;
    private volatile boolean closed;

    // Thread-safe list for concurrent access from relay threads
    private final List<CapturedMessage> messages = new CopyOnWriteArrayList<>();

    // For handling fragmented messages
    private final StringBuilder clientFragmentBuffer = new StringBuilder();
    private final StringBuilder serverFragmentBuffer = new StringBuilder();
    private WebSocketFrame.Opcode clientFragmentOpcode = null;
    private WebSocketFrame.Opcode serverFragmentOpcode = null;

    // Callback for UI updates when messages are added
    private Runnable messageCallback;

    /**
     * Represents a captured WebSocket message with metadata
     */
    public static class CapturedMessage {
        public final boolean fromClient;
        public final WebSocketFrame.Opcode type;
        public final byte[] payload;
        public final long timestamp;

        public CapturedMessage(boolean fromClient, WebSocketFrame.Opcode type, byte[] payload, long timestamp) {
            this.fromClient = fromClient;
            this.type = type;
            this.payload = payload;
            this.timestamp = timestamp;
        }

        public String getPayloadAsText() {
            return new String(payload, StandardCharsets.UTF_8);
        }

        public String getPayloadAsBase64() {
            return Base64.getEncoder().encodeToString(payload);
        }
    }

    /**
     * Create a new WebSocket session
     * 
     * @param url WebSocket URL (ws:// or wss://)
     * @param handshakeHeaders Headers from the upgrade request
     */
    public WebSocketSession(String url, Map<String, String> handshakeHeaders) {
        this.url = url;
        this.connectionId = generateConnectionId(url);
        this.handshakeHeaders = handshakeHeaders != null ? handshakeHeaders : Collections.emptyMap();
        this.startTime = System.currentTimeMillis();
        this.closed = false;
    }

    /**
     * Record a WebSocket frame.
     * Handles fragmentation: buffers fragments until FIN=1, then emits complete message.
     * 
     * @param frame The WebSocket frame
     * @param fromClient True if client→server, false if server→client
     */
    public void addFrame(WebSocketFrame frame, boolean fromClient) {
        if (closed) {
            return;
        }

        // Skip control frames (PING, PONG, CLOSE) - we only record data messages
        if (frame.isControl()) {
            return;
        }

        // Handle fragmentation
        StringBuilder fragmentBuffer = fromClient ? clientFragmentBuffer : serverFragmentBuffer;

        if (frame.getOpcode() == WebSocketFrame.Opcode.CONTINUATION) {
            // Continuation of a fragmented message
            fragmentBuffer.append(frame.getPayloadAsText());
        } else {
            // Start of a new message (possibly fragmented)
            fragmentBuffer.setLength(0);
            fragmentBuffer.append(frame.getPayloadAsText());
            if (fromClient) {
                clientFragmentOpcode = frame.getOpcode();
            } else {
                serverFragmentOpcode = frame.getOpcode();
            }
        }

        // If FIN=1, message is complete - record it
        if (frame.isFin()) {
            WebSocketFrame.Opcode opcode = fromClient ? clientFragmentOpcode : serverFragmentOpcode;
            if (opcode == null) {
                opcode = frame.getOpcode();
            }

            byte[] payload = fragmentBuffer.toString().getBytes(StandardCharsets.UTF_8);
            messages.add(new CapturedMessage(fromClient, opcode, payload, System.currentTimeMillis()));

            // Notify callback
            if (messageCallback != null) {
                messageCallback.run();
            }

            // Reset fragment state
            fragmentBuffer.setLength(0);
            if (fromClient) {
                clientFragmentOpcode = null;
            } else {
                serverFragmentOpcode = null;
            }
        }
    }

    /**
     * Set callback to be notified when messages are added.
     */
    public void setMessageCallback(Runnable callback) {
        this.messageCallback = callback;
    }

    /**
     * Mark the session as closed
     */
    public void close() {
        if (!closed) {
            closed = true;
            endTime = System.currentTimeMillis();
        }
    }

    /**
     * Convert to WebSocketTransaction for XML serialization.
     * Uses existing model from WebConversation module.
     */
    public WebSocketTransaction toTransaction() {
        WebSocketTransaction tx = new WebSocketTransaction(url);
        tx.setHandshakeHeaders(new java.util.HashMap<>(handshakeHeaders));

        for (CapturedMessage msg : messages) {
            WebSocketMessage.Type type = (msg.type == WebSocketFrame.Opcode.TEXT)
                    ? WebSocketMessage.Type.TEXT
                    : WebSocketMessage.Type.BINARY;

            WebSocketMessage wsMsg = new WebSocketMessage(
                    msg.fromClient,
                    type,
                    msg.payload,
                    msg.timestamp
            );

            tx.addMessage(wsMsg);
        }

        return tx;
    }

    /**
     * Generate a stable connection ID based on URL.
     * Uses MD5 hash to ensure same URL always produces same ID.
     */
    private static String generateConnectionId(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(url.getBytes(StandardCharsets.UTF_8));

            // Convert first 4 bytes to hex
            StringBuilder sb = new StringBuilder("ws_");
            for (int i = 0; i < 4; i++) {
                sb.append(String.format("%02x", hash[i] & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash if MD5 unavailable
            return "ws_" + Integer.toHexString(url.hashCode());
        }
    }

    // Getters

    public String getUrl() {
        return url;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public List<CapturedMessage> getMessages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public int getMessageCount() {
        return messages.size();
    }

    @Override
    public String toString() {
        return String.format("WebSocketSession[url=%s, connId=%s, messages=%d, closed=%s]",
                url, connectionId, messages.size(), closed);
    }
}
