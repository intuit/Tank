package com.intuit.tank.runner.method;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.WebSocketAction;
import com.intuit.tank.harness.data.WebSocketMessageType;
import com.intuit.tank.harness.data.WebSocketRequest;
import com.intuit.tank.harness.data.WebSocketStep;

/**
 * Test enum validation and type safety for WebSocket classes
 */
public class WebSocketEnumValidationTest {

    @Test
    public void testWebSocketActionValidValues() {
        // Test all valid actions (Core 3 actions)
        assertEquals(WebSocketAction.CONNECT, WebSocketAction.fromValue("connect"));
        assertEquals(WebSocketAction.SEND, WebSocketAction.fromValue("send"));
        assertEquals(WebSocketAction.DISCONNECT, WebSocketAction.fromValue("disconnect"));
    }

    @Test
    public void testWebSocketActionInvalidValue() {
        // Test invalid action throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            WebSocketAction.fromValue("invalid_action");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            WebSocketAction.fromValue("conenct"); // Typo
        });
        
        // Test case insensitive (should work, not throw)
        assertEquals(WebSocketAction.SEND, WebSocketAction.fromValue("Send"));
        assertEquals(WebSocketAction.CONNECT, WebSocketAction.fromValue("CONNECT"));
    }

    @Test
    public void testWebSocketActionNullValue() {
        // Test null returns null
        assertNull(WebSocketAction.fromValue(null));
    }

    @Test
    public void testWebSocketMessageTypeValidValues() {
        // Test all valid message types (MVP: TEXT only)
        assertEquals(WebSocketMessageType.TEXT, WebSocketMessageType.fromValue("text"));
    }

    @Test
    public void testWebSocketMessageTypeInvalidValue() {
        // Test invalid message type throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            WebSocketMessageType.fromValue("binary"); // Not supported in MVP
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            WebSocketMessageType.fromValue("ping"); // PING is an action, not message type
        });
        
        // Test case insensitive (should work, not throw)
        assertEquals(WebSocketMessageType.TEXT, WebSocketMessageType.fromValue("Text"));
        assertEquals(WebSocketMessageType.TEXT, WebSocketMessageType.fromValue("TEXT"));
    }

    @Test
    public void testWebSocketMessageTypeNullValue() {
        // Test null returns default (TEXT)
        assertEquals(WebSocketMessageType.TEXT, WebSocketMessageType.fromValue(null));
    }

    @Test
    public void testWebSocketActionProperties() {
        // Test action properties
        assertTrue(WebSocketAction.CONNECT.isBlocking());
        assertFalse(WebSocketAction.CONNECT.requiresExistingConnection());
        
        assertTrue(WebSocketAction.SEND.requiresExistingConnection());
        assertTrue(WebSocketAction.SEND.sendsData());
        assertFalse(WebSocketAction.SEND.isBlocking());
        
        assertTrue(WebSocketAction.DISCONNECT.isBlocking());
        assertTrue(WebSocketAction.DISCONNECT.requiresExistingConnection());
        assertFalse(WebSocketAction.DISCONNECT.sendsData());
    }

    @Test
    public void testWebSocketMessageTypeProperties() {
        // Test message type properties (MVP: TEXT only)
        assertTrue(WebSocketMessageType.TEXT.isDataFrame());
        assertFalse(WebSocketMessageType.TEXT.isControlFrame());
        
        // Control frames (PING/PONG) are actions, not message types in MVP
    }

    @Test
    public void testWebSocketStepWithStringSetters() {
        // Test that string setters still work for backward compatibility
        WebSocketStep step = new WebSocketStep();
        
        step.setAction("connect");
        assertEquals(WebSocketAction.CONNECT, step.getAction());
        
        step.setAction("send");
        assertEquals(WebSocketAction.SEND, step.getAction());
        
        // Test invalid string throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            step.setAction("invalid");
        });
    }

    @Test
    public void testWebSocketRequestBasicFields() {
        // Test basic WebSocketRequest fields (MVP)
        WebSocketRequest request = new WebSocketRequest();
        
        request.setUrl("ws://example.com");
        assertEquals("ws://example.com", request.getUrl());
        
        request.setPayload("test message");
        assertEquals("test message", request.getPayload());
        
        request.setTimeoutMs(5000);
        assertEquals(Integer.valueOf(5000), request.getTimeoutMs());
    }

    @Test
    public void testEnumToStringMethods() {
        // Test toString methods
        assertEquals("connect", WebSocketAction.CONNECT.toString());
        assertEquals("send", WebSocketAction.SEND.toString());
        assertEquals("text", WebSocketMessageType.TEXT.toString());
        
        // Test getValue methods
        assertEquals("connect", WebSocketAction.CONNECT.getValue());
        assertEquals("text", WebSocketMessageType.TEXT.getValue());
    }

    @Test
    public void testWebSocketRequestValidation() {
        // Test CONNECT validation - requires URL
        WebSocketRequest connectRequest = new WebSocketRequest();
        assertThrows(IllegalArgumentException.class, () -> {
            connectRequest.validate(WebSocketAction.CONNECT);
        });
        
        connectRequest.setUrl("ws://example.com");
        connectRequest.validate(WebSocketAction.CONNECT); // Should pass
        
        // Test SEND validation - requires payload
        WebSocketRequest sendRequest = new WebSocketRequest();
        assertThrows(IllegalArgumentException.class, () -> {
            sendRequest.validate(WebSocketAction.SEND);
        });
        
        sendRequest.setPayload("test message");
        sendRequest.validate(WebSocketAction.SEND); // Should pass
        
        // Test DISCONNECT validation - no requirements
        WebSocketRequest disconnectRequest = new WebSocketRequest();
        disconnectRequest.validate(WebSocketAction.DISCONNECT); // Should pass
    }
}
