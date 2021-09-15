/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.config;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * ConfigInclusionExclusionRule
 * 
 * @author dangleton
 * 
 */
public class ConfigInclusionExclusionRule {

    private TransactionPart transactionPart;
    private String header;
    private MatchType match;
    private String value;

    /**
     * @param transactionPart
     * @param header
     * @param match
     */
    public ConfigInclusionExclusionRule(TransactionPart transactionPart, String header, MatchType match, String value) {
        super();
        this.transactionPart = transactionPart;
        this.header = header;
        this.match = match;
        this.value = value;
    }

    /**
     * @return the transactionPart
     */
    public TransactionPart getTransactionPart() {
        return transactionPart;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @return the match
     */
    public MatchType getMatch() {
        return match;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 73).append(value).append(match).append(header).append(transactionPart)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ConfigInclusionExclusionRule)) {
            return false;
        }
        ConfigInclusionExclusionRule rhs = (ConfigInclusionExclusionRule) o;
        return new EqualsBuilder().append(value, rhs.value).append(transactionPart, rhs.transactionPart)
                .append(match, rhs.match).append(header, rhs.header).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @param value2
     * @return
     */
    public boolean matches(String headerValue) {
        boolean ret = false;
        if (MatchType.contains == match) {
            ret = headerValue.toLowerCase().contains(value.toLowerCase());
        } else if (MatchType.equals == match) {
            ret = headerValue.equalsIgnoreCase(value);
        } else if (MatchType.startsWith == match) {
            ret = headerValue.toLowerCase().startsWith(value.toLowerCase());
        } else if (MatchType.endsWith == match) {
            ret = headerValue.toLowerCase().endsWith(value.toLowerCase());
        } else if (MatchType.notContains == match) {
            ret = !headerValue.toLowerCase().contains(value.toLowerCase());
        } else if (MatchType.notEquals == match) {
            ret = !headerValue.equalsIgnoreCase(value);
        } else if (MatchType.notStartsWith == match) {
            ret = !headerValue.toLowerCase().startsWith(value.toLowerCase());
        } else if (MatchType.notEndsWith == match) {
            ret = !headerValue.toLowerCase().endsWith(value.toLowerCase());
        }
        return ret;
    }
}
