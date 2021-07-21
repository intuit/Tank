package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.script.RequestDataPhase;

//@Entity
//@Audited
//@Table(name = "step_data")
public class RequestData implements Serializable {// extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String key;
    private String value;
    private String type;
    private RequestDataPhase phase = RequestDataPhase.POST_REQUEST;

    public RequestData() {

    }

    /**
     * @param key
     * @param value
     * @param type
     */
    public RequestData(String key, String value, String type) {
        super();
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * @return the phase
     */
    public RequestDataPhase getPhase() {
        return phase != null ? phase : RequestDataPhase.POST_REQUEST;
    }

    /**
     * @param phase
     *            the phase to set
     */
    public void setPhase(RequestDataPhase phase) {
        this.phase = phase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("type", type).append("name", key)
                .append("value", value).append("phase", getPhase())
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RequestData)) {
            return false;
        }
        RequestData o = (RequestData) obj;
        return new EqualsBuilder().append(o.getKey(), getKey()).append(o.getType(), getType())
                .append(o.getValue(), getValue()).append(o.getPhase(), getPhase()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getKey()).append(getValue()).append(getType()).append(getPhase())
                .toHashCode();
    }
}
