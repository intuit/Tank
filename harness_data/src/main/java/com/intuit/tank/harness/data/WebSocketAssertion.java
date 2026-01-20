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
import jakarta.xml.bind.annotation.XmlType;

/**
 * Represents an assertion for WebSocket message validation.
 * Used within AssertionBlock for expect and save operations.
 *
 * Usage in XML:
 * <expect pattern='"subscribed"' minCount="1" />
 * <save pattern='"price":(\d+)' variable="lastPrice" occurrence="last" />
 *
 * Note: For "must NOT see" patterns, use fail-on at the WebSocket step level.
 */
@XmlType(name = "webSocketAssertion", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class WebSocketAssertion {

    @XmlAttribute(required = true)
    private String pattern;

    @XmlAttribute
    private boolean regex = false;

    @XmlAttribute
    private Integer minCount;  // null means at least 1 for expect

    @XmlAttribute
    private Integer maxCount;

    @XmlAttribute
    private String variable;

    @XmlAttribute
    private SaveOccurrence occurrence;

    public WebSocketAssertion() {
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

    public Integer getMinCount() {
        return minCount;
    }

    public void setMinCount(Integer minCount) {
        this.minCount = minCount;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public SaveOccurrence getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(SaveOccurrence occurrence) {
        this.occurrence = occurrence;
    }

    /**
     * Get effective minCount (defaults to 1 for expect assertions)
     */
    public int getEffectiveMinCount() {
        return minCount != null ? minCount : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent Builder for WebSocketAssertion
     */
    public static class Builder {
        private final WebSocketAssertion instance = new WebSocketAssertion();

        public Builder pattern(String value) {
            instance.setPattern(value);
            return this;
        }

        public Builder regex(boolean value) {
            instance.setRegex(value);
            return this;
        }

        public Builder minCount(int value) {
            instance.setMinCount(value);
            return this;
        }

        public Builder maxCount(int value) {
            instance.setMaxCount(value);
            return this;
        }

        public Builder variable(String value) {
            instance.setVariable(value);
            return this;
        }

        public Builder occurrence(SaveOccurrence value) {
            instance.setOccurrence(value);
            return this;
        }

        public WebSocketAssertion build() {
            return instance;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WebSocketAssertion[");
        sb.append("pattern='").append(pattern).append("'");
        if (regex) sb.append(", regex");
        if (minCount != null) sb.append(", min=").append(minCount);
        if (maxCount != null) sb.append(", max=").append(maxCount);
        if (variable != null) sb.append(", save=").append(variable);
        if (occurrence != null) sb.append(", occurrence=").append(occurrence);
        sb.append("]");
        return sb.toString();
    }
}
