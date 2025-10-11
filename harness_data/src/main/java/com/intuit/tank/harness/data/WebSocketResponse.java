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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * WebSocketResponse represents the response/receive portion of WebSocket operations.
 */
@XmlType(name = "webSocketResponse", propOrder = {
    "timeoutMs",
    "optional",
    "expectedContent",
    "saveVariable"
}, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class WebSocketResponse {

    @XmlElement(name = "timeoutMs")
    private Integer timeoutMs;
    @XmlAttribute(name = "optional")
    private Boolean optional = false;
    @XmlElement(name = "expectedContent")
    private String expectedContent;
    @XmlElement(name = "saveVariable")
    private String saveVariable;

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

    /**
     * @return the optional
     */
    public Boolean getOptional() {
        return optional != null ? optional : false;
    }

    /**
     * @param optional the optional to set
     */
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return Boolean.TRUE.equals(optional);
    }

    /**
     * @return the expectedContent
     */
    public String getExpectedContent() {
        return expectedContent;
    }

    /**
     * @param expectedContent the expectedContent to set
     */
    public void setExpectedContent(String expectedContent) {
        this.expectedContent = expectedContent;
    }

    /**
     * @return the saveVariable
     */
    public String getSaveVariable() {
        return saveVariable;
    }

    /**
     * @param saveVariable the saveVariable to set
     */
    public void setSaveVariable(String saveVariable) {
        this.saveVariable = saveVariable;
    }
    public void validate(WebSocketAction action) {
        // WebSocketResponse is not used by any of the core actions (CONNECT, SEND, DISCONNECT)
        // This class is retained for future extensibility
        throw new IllegalArgumentException(action + " action should not have WebSocketResponse");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" : ");
        if (expectedContent != null) {
            sb.append("expect='").append(expectedContent).append("'");
        }
        if (saveVariable != null) {
            sb.append(", save=#{").append(saveVariable).append("}");
        }
        if (timeoutMs != null) {
            sb.append(", timeout=").append(timeoutMs).append("ms");
        }
        if (isOptional()) {
            sb.append(", optional");
        }
        return sb.toString();
    }
}
