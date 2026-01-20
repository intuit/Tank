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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Represents a fail-on pattern for WebSocket sessions.
 * When an incoming message matches this pattern, the test fails immediately.
 *
 * Usage in XML:
 * <fail-on pattern='"type":"error"' />
 * <fail-on pattern='"status":5\d\d' regex="true" />
 */
@XmlType(name = "failOnPattern", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class FailOnPattern {

    @XmlAttribute(required = true)
    private String pattern;

    @XmlAttribute
    private boolean regex = false;

    public FailOnPattern() {
    }

    public FailOnPattern(String pattern, boolean regex) {
        this.pattern = pattern;
        this.regex = regex;
    }

    public FailOnPattern(String pattern) {
        this(pattern, false);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        this.pattern = pattern;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "FailOnPattern[" + pattern + (regex ? " (regex)" : "") + "]";
    }
}
