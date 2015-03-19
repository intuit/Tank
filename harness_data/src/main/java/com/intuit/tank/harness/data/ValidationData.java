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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.intuit.tank.script.RequestDataPhase;

@XmlType(name = "validation", propOrder = { "condition", "phase" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationData extends ResponseData {
    @XmlAttribute
    private String condition;

    @XmlAttribute
    private RequestDataPhase phase;

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition
     *            the condition to set
     */
    public void setCondition(String condition) {
        this.condition = condition;
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

    public ValidationData copy() {
        ValidationData ret = new ValidationData();
        ret.setCondition(condition);
        ret.setKey(getKey());
        ret.setPhase(phase);
        ret.setValue(getValue());
        return ret;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return getKey() + " " + condition + " " + getValue();
    }

}
