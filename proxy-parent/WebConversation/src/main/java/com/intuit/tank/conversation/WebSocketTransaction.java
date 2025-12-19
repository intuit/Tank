package com.intuit.tank.conversation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a complete WebSocket connection lifecycle:
 * - Initial HTTP upgrade handshake
 * - All messages exchanged (client→server and server→client)
 * - Connection metadata
 * 
 * This is converted to a series of WebSocketStep elements in the Tank script.
 */
@XmlRootElement(name = "webSocketTransaction", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "webSocketTransaction", namespace = Namespace.NAMESPACE_V1)
public class WebSocketTransaction {

    @XmlAttribute(name = "url")
    private String url;

    @XmlAttribute(name = "protocol")
    private Protocol protocol;

    @XmlElement(name = "handshakeHeader", namespace = Namespace.NAMESPACE_V1)
    @XmlElementWrapper(name = "handshakeHeaders", namespace = Namespace.NAMESPACE_V1)
    private Map<String, String> handshakeHeaders;

    @XmlElement(name = "message", namespace = Namespace.NAMESPACE_V1)
    @XmlElementWrapper(name = "messages", namespace = Namespace.NAMESPACE_V1)
    private List<WebSocketMessage> messages;

    private transient String connectionId; // Generated on demand, not serialized

    /**
     * Default constructor for JAXB
     */
    public WebSocketTransaction() {
        this.messages = new ArrayList<>();
        this.handshakeHeaders = new HashMap<>();
    }

    /**
     * Create a WebSocket transaction
     * 
     * @param url WebSocket URL (ws:// or wss://)
     */
    public WebSocketTransaction(String url) {
        this();
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (!url.startsWith("ws://") && !url.startsWith("wss://")) {
            throw new IllegalArgumentException("URL must start with ws:// or wss://, got: " + url);
        }
        
        this.url = url;
        this.protocol = url.startsWith("wss://") ? Protocol.wss : Protocol.ws;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getHandshakeHeaders() {
        return handshakeHeaders;
    }

    public void setHandshakeHeaders(Map<String, String> handshakeHeaders) {
        this.handshakeHeaders = handshakeHeaders;
    }

    public void addHandshakeHeader(String key, String value) {
        this.handshakeHeaders.put(key, value);
    }

    public List<WebSocketMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<WebSocketMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(WebSocketMessage message) {
        this.messages.add(message);
    }

    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Get only client messages (client→server)
     */
    public List<WebSocketMessage> getClientMessages() {
        return messages.stream()
            .filter(WebSocketMessage::isFromClient)
            .collect(Collectors.toList());
    }

    /**
     * Get only server messages (server→client)
     */
    public List<WebSocketMessage> getServerMessages() {
        return messages.stream()
            .filter(WebSocketMessage::isFromServer)
            .collect(Collectors.toList());
    }

    /**
     * Generate a stable connection ID based on the URL.
     * Format: ws_<hash>
     * 
     * This matches the agent's connection ID strategy and ensures
     * the same URL always generates the same ID.
     */
    public String generateConnectionId() {
        if (connectionId == null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest(url.getBytes(StandardCharsets.UTF_8));
                
                // Convert first 4 bytes to hex
                StringBuilder sb = new StringBuilder("ws_");
                for (int i = 0; i < 4; i++) {
                    sb.append(String.format("%02x", hash[i] & 0xff));
                }
                connectionId = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                // Fallback to simple hash if MD5 unavailable
                connectionId = "ws_" + Integer.toHexString(url.hashCode());
            }
        }
        return connectionId;
    }

    @Override
    public String toString() {
        return String.format("WebSocketTransaction[url=%s, protocol=%s, messages=%d, clientMsgs=%d, serverMsgs=%d]",
                           url, protocol, messages.size(), getClientMessages().size(), getServerMessages().size());
    }
}



