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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * WebSocket actions for Tank scripts.
 */
@XmlType(name = "webSocketAction", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlEnum(String.class)
public enum WebSocketAction {

    @XmlEnumValue("connect")
    CONNECT("connect"),
    @XmlEnumValue("send")
    SEND("send"),
    @XmlEnumValue("disconnect")
    DISCONNECT("disconnect");

    private final String value;

    WebSocketAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static WebSocketAction fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        String normalizedValue = value.toLowerCase().trim();
        for (WebSocketAction action : WebSocketAction.values()) {
            if (action.value.equals(normalizedValue)) {
                return action;
            }
        }
        
        throw new IllegalArgumentException("Unknown WebSocket action: " + value + 
            ". Valid actions are: connect, send, disconnect");
    }

    public boolean requiresExistingConnection() {
        return this != CONNECT;
    }

    public boolean isBlocking() {
        return this == CONNECT || this == DISCONNECT;
    }

    public boolean sendsData() {
        return this == SEND;
    }

    @Override
    public String toString() {
        return value;
    }
}
