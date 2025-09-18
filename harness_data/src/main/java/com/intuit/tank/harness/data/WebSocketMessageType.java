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
 * WebSocket message types for Tank scripts.
 */
@XmlType(name = "webSocketMessageType", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlEnum(String.class)
public enum WebSocketMessageType {

    @XmlEnumValue("text")
    TEXT("text");

    private final String value;

    WebSocketMessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static WebSocketMessageType fromValue(String value) {
        if (value == null) {
            return TEXT;
        }
        
        String normalizedValue = value.toLowerCase().trim();
        for (WebSocketMessageType type : WebSocketMessageType.values()) {
            if (type.value.equals(normalizedValue)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unknown WebSocket message type: " + value + 
            ". Valid types are: text");
    }

    public boolean isDataFrame() {
        return this == TEXT;
    }

    public boolean isControlFrame() {
        return false;
    }

    public static WebSocketMessageType getDefault() {
        return TEXT;
    }

    @Override
    public String toString() {
        return value;
    }
}
