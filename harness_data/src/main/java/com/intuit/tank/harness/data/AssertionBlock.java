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

import java.util.ArrayList;
import java.util.List;

/**
 * Container for WebSocket assertions used in websocket-close and websocket-assert steps.
 *
 * Usage in XML:
 * <assertions>
 *   <expect pattern='"subscribed"' />
 *   <expect pattern='"data"' minCount="10" />
 *   <save pattern='"price":(\d+)' variable="lastPrice" occurrence="last" />
 * </assertions>
 *
 * Note: For "must NOT see" patterns, use fail-on at connect level for immediate failure.
 */
@XmlType(name = "assertionBlock", propOrder = {
    "expects", "saves"
}, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class AssertionBlock {

    @XmlElement(name = "expect")
    private List<WebSocketAssertion> expects;

    @XmlElement(name = "save")
    private List<WebSocketAssertion> saves;

    public AssertionBlock() {
    }

    public List<WebSocketAssertion> getExpects() {
        if (expects == null) {
            expects = new ArrayList<>();
        }
        return expects;
    }

    public void setExpects(List<WebSocketAssertion> expects) {
        this.expects = expects;
    }

    public List<WebSocketAssertion> getSaves() {
        if (saves == null) {
            saves = new ArrayList<>();
        }
        return saves;
    }

    public void setSaves(List<WebSocketAssertion> saves) {
        this.saves = saves;
    }

    public boolean isEmpty() {
        return (expects == null || expects.isEmpty())
            && (saves == null || saves.isEmpty());
    }

    @Override
    public String toString() {
        return "AssertionBlock[expects=" + (expects != null ? expects.size() : 0) +
               ", saves=" + (saves != null ? saves.size() : 0) + "]";
    }
}
