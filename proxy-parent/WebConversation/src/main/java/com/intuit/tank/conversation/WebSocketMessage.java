package com.intuit.tank.conversation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * WebSocketMessage represents a single WebSocket message (text or binary) sent between client and server
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "webSocketMessage", namespace = Namespace.NAMESPACE_V1)
public class WebSocketMessage {

    @XmlEnum(String.class)
    public enum Type {
        TEXT,
        BINARY
    }

    @XmlAttribute(name = "fromClient")
    private boolean fromClient;

    @XmlAttribute(name = "type")
    private Type type;

    @XmlElement(name = "content", namespace = Namespace.NAMESPACE_V1)
    private byte[] content;

    @XmlAttribute(name = "timestamp")
    private long timestamp;

    /**
     * default constructor for JAXB
     */
    public WebSocketMessage() {
    }

    /**
     * create a WebSocket message
     * 
     * @param fromClient true if sent by client, false if sent by server
     * @param type text or binary
     * @param content message payload
     * @param timestamp when message was received/sent
     */
    public WebSocketMessage(boolean fromClient, Type type, byte[] content, long timestamp) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.fromClient = fromClient;
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
    }

    public boolean isFromClient() {
        return fromClient;
    }

    public boolean isFromServer() {
        return !fromClient;
    }

    public void setFromClient(boolean fromClient) {
        this.fromClient = fromClient;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isText() {
        return type == Type.TEXT;
    }

    public boolean isBinary() {
        return type == Type.BINARY;
    }

    /**
     * get content as string (for text messages)
     * 
     * @return content decoded as utf-8
     */
    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketMessage that = (WebSocketMessage) o;
        return fromClient == that.fromClient &&
               timestamp == that.timestamp &&
               type == that.type &&
               java.util.Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fromClient, type, timestamp);
        result = 31 * result + java.util.Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        String direction = fromClient ? "CLIENT→SERVER" : "SERVER→CLIENT";
        String contentPreview = content.length > 50 
            ? new String(content, 0, 50, StandardCharsets.UTF_8) + "..." 
            : new String(content, StandardCharsets.UTF_8);
        return String.format("WebSocketMessage[%s, %s, %d bytes, timestamp=%d]",
                           direction, type, content.length, timestamp);
    }
}

