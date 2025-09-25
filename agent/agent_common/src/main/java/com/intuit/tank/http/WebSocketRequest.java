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
 * WebSocket request wrapper for debugger display
 */
public class WebSocketRequest extends BaseRequest {
    
    private static final String NEWLINE = "\n";
    
    private String action;
    private String connectionId;
    private String url;
    private String payload;
    private Integer timeoutMs;
    
    /**
     * Constructor
     */
    public WebSocketRequest() {
        super(null, null); // No HTTP client needed for WebSocket
    }
    
    /**
     * Set WebSocket request details
     */
    public void setWebSocketDetails(String action, String connectionId, 
                                   String url, String payload, Integer timeoutMs) {
        this.action = action;
        this.connectionId = connectionId;
        this.url = url;
        this.payload = payload;
        this.timeoutMs = timeoutMs;
        
        // Build log message
        buildLogMessage();
    }
    
    /**
     * Build formatted log message for debugger display
     */
    private void buildLogMessage() {
        StringBuilder sb = new StringBuilder();
        
        // Format similar to HTTP requests
        if (url != null) {
            sb.append("REQUEST URL: ").append(action != null ? action.toUpperCase() : "WEBSOCKET");
            sb.append(" ").append(url).append(NEWLINE);
        } else {
            sb.append("REQUEST URL: WEBSOCKET ").append(action != null ? action.toUpperCase() : "UNKNOWN").append(NEWLINE);
        }
        
        sb.append("CONTENT TYPE: text/plain").append(NEWLINE);
        sb.append("REQUEST HEADER: connection-id = ").append(connectionId != null ? connectionId : "N/A").append(NEWLINE);
        sb.append("REQUEST HEADER: ws-action = ").append(action != null ? action.toLowerCase() : "unknown").append(NEWLINE);
        
        if (timeoutMs != null) {
            sb.append("REQUEST HEADER: timeout-ms = ").append(timeoutMs).append(NEWLINE);
        }
        
        // Calculate size if payload exists
        int requestSize = 0;
        if (payload != null && !payload.isEmpty()) {
            requestSize = payload.getBytes().length;
        }
        sb.append("REQUEST SIZE: ").append(requestSize).append(NEWLINE);
        
        if (payload != null && !payload.isEmpty()) {
            sb.append("REQUEST BODY: ").append(payload).append(NEWLINE);
        }
        
        this.logMsg = sb.toString();
    }
    
    @Override
    public String getLogMsg() {
        return logMsg;
    }
    
    @Override
    public String getKey(String key) {
        // Not applicable for WebSocket requests
        return null;
    }
    
    @Override
    public void setKey(String key, String value) {
        // Not applicable for WebSocket requests
    }
    
    @Override
    public void setNamespace(String name, String value) {
        // Not applicable for WebSocket requests
    }
}
