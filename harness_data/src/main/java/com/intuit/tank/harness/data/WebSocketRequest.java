package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * WebSocketRequest represents the request portion of a WebSocket operation.
 */
@XmlType(name = "webSocketRequest", propOrder = {
    "url",
    "payload",
    "timeoutMs"
}, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class WebSocketRequest {

    @XmlElement(name = "url")
    private String url;
    @XmlElement(name = "payload")
    private String payload;
    @XmlElement(name = "timeoutMs")
    private Integer timeoutMs;

    /**
     * @return the url
     */
    public String getUrl() { 
        return url; 
    }
    
    /**
     * @param url the url to set
     */
    public void setUrl(String url) { 
        this.url = url; 
    }

    /**
     * @return the payload
     */
    public String getPayload() { 
        return payload; 
    }
    
    /**
     * @param payload the payload to set
     */
    public void setPayload(String payload) { 
        this.payload = payload; 
    }

    /**
     * @return the timeoutMs
     */
    public Integer getTimeoutMs() { 
        return timeoutMs; 
    }
    
    /**
     * @param timeoutMs the timeoutMs to set
     */
    public void setTimeoutMs(Integer timeoutMs) { 
        this.timeoutMs = timeoutMs; 
    }
    public void validate(WebSocketAction action) {
        switch (action) {
            case CONNECT:
                if (url == null || url.trim().isEmpty()) {
                    throw new IllegalArgumentException("CONNECT action requires <url>");
                }
                break;
            case SEND:
                if (payload == null || payload.trim().isEmpty()) {
                    throw new IllegalArgumentException("SEND action requires <payload>");
                }
                break;
            case EXPECT:
                // EXPECT uses WebSocketResponse, not WebSocketRequest
                throw new IllegalArgumentException("EXPECT action should use WebSocketResponse, not WebSocketRequest");
            case DISCONNECT:
                // No required fields
                break;
            default:
                throw new IllegalArgumentException("Unknown WebSocket action: " + action);
        }
    }

    @Override
    public String toString() {
        String u = (url != null) ? url : "";
        String p = "";
        if (payload != null && !payload.isEmpty()) {
            p = payload.length() > 50 ? payload.substring(0, 50) + "..." : payload;
        }
        return getClass().getSimpleName() + " : " + u + " payload=" + p;
    }
}
