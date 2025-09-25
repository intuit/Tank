package com.intuit.tank.http;

/*
 * #%L
 * Intuit Tank Agent Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

/**
 * WebSocket response wrapper for debugger display
 */
public class WebSocketResponse extends BaseResponse {
    
    private static final String NEWLINE = "\n";
    
    private String action;
    private String connectionId;
    private String url;
    private String payload;
    private String receivedMessage;
    private String expectedContent;
    private String savedVariable;
    private long connectionTime;
    private boolean success;
    private String errorMessage;
    
    /**
     * Constructor
     */
    public WebSocketResponse() {
        super();
    }
    
    /**
     * Set WebSocket response details for successful operations
     */
    public void setWebSocketDetails(String action, String connectionId,
                                   String receivedMessage, String expectedContent,
                                   String savedVariable, long responseTimeMs,
                                   boolean success) {
        this.action = action;
        this.connectionId = connectionId;
        this.receivedMessage = receivedMessage;
        this.expectedContent = expectedContent;
        this.savedVariable = savedVariable;
        this.responseTime = responseTimeMs;
        this.success = success;
        this.httpCode = success ? 200 : 500; // Use HTTP codes for compatibility
        
        buildLogMessage();
    }
    
    /**
     * Set WebSocket details including request information for consolidated logging
     */
    public void setWebSocketDetails(String action, String connectionId,
                                   String url, String payload, String receivedMessage, 
                                   String expectedContent, String savedVariable, 
                                   long responseTimeMs, boolean success) {
        this.action = action;
        this.connectionId = connectionId;
        this.receivedMessage = receivedMessage;
        this.expectedContent = expectedContent;
        this.savedVariable = savedVariable;
        this.responseTime = responseTimeMs;
        this.success = success;
        this.httpCode = success ? 200 : 500;
        
        // Store request details for logging
        this.url = url;
        this.payload = payload;
        
        buildLogMessage();
    }
    
    /**
     * Set error details
     */
    public void setError(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.httpCode = 500;
        buildLogMessage();
    }
    
    /**
     * Build formatted log message for debugger display
     */
    private void buildLogMessage() {
        StringBuilder sb = new StringBuilder();
        
        // Format similar to HTTP responses but consolidated for WebSocket operations
        sb.append("WEBSOCKET OPERATION: ").append(action != null ? action.toUpperCase() : "UNKNOWN").append(NEWLINE);
        sb.append("WEBSOCKET STATUS: ").append(success ? "Success" : (errorMessage != null ? errorMessage : "Failed")).append(NEWLINE);
        sb.append("WEBSOCKET TIME: ").append(responseTime).append("ms").append(NEWLINE);
        
        // Connection details
        sb.append("WEBSOCKET HEADER: connection-id = ").append(connectionId != null ? connectionId : "N/A").append(NEWLINE);
        sb.append("WEBSOCKET HEADER: ws-status = ").append(success ? "connected" : "error").append(NEWLINE);
        
        // Request details (if available)
        if (url != null && !url.isEmpty()) {
            sb.append("WEBSOCKET HEADER: url = ").append(url).append(NEWLINE);
        }
        if (payload != null && !payload.isEmpty()) {
            sb.append("WEBSOCKET HEADER: payload = ").append(payload).append(NEWLINE);
        }
        
        if (expectedContent != null && !expectedContent.isEmpty()) {
            sb.append("WEBSOCKET HEADER: validation = ").append(
                receivedMessage != null && receivedMessage.contains(expectedContent) 
                    ? "PASSED" : "FAILED"
            ).append(NEWLINE);
        }
        
        if (savedVariable != null && !savedVariable.isEmpty()) {
            sb.append("WEBSOCKET HEADER: saved-to = ").append(savedVariable).append(NEWLINE);
        }
        
        // Calculate message size
        int messageSize = 0;
        if (receivedMessage != null && !receivedMessage.isEmpty()) {
            messageSize = receivedMessage.getBytes().length;
        }
        sb.append("WEBSOCKET SIZE: ").append(messageSize).append(" bytes").append(NEWLINE);
        
        // Message content (received or sent)
        if (receivedMessage != null && !receivedMessage.isEmpty()) {
            sb.append("WEBSOCKET MESSAGE: ").append(receivedMessage).append(NEWLINE);
        } else if (action != null && action.equalsIgnoreCase("connect")) {
            sb.append("WEBSOCKET MESSAGE: Connection established").append(NEWLINE);
        } else if (action != null && action.equalsIgnoreCase("disconnect")) {
            sb.append("WEBSOCKET MESSAGE: Connection closed").append(NEWLINE);
        } else if (action != null && action.equalsIgnoreCase("send")) {
            sb.append("WEBSOCKET MESSAGE: Message sent successfully").append(NEWLINE);
        }
        
        this.responseLogMsg = sb.toString();
    }
    
    @Override
    public String getLogMsg() {
        if (responseLogMsg == null) {
            buildLogMessage();
        }
        return responseLogMsg;
    }
    
    @Override
    public String getValue(String key) {
        // For WebSocket responses, we could return parts of the received message
        // For now, return the full message if key is "body" or "message"
        if ("body".equals(key) || "message".equals(key)) {
            return receivedMessage;
        }
        return null;
    }
}
