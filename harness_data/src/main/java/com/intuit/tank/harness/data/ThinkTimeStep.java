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

@XmlType(name = "thinkTime", propOrder = { "minTime", "maxTime" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ThinkTimeStep extends TestStep {

    @XmlAttribute
    private String minTime = "0";
    @XmlAttribute
    private String maxTime = "0";

    /**
     * @return the minTime
     */
    public String getMinTime() {
        return minTime;
    }

    /**
     * @param minTime
     *            the minTime to set
     */
    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    /**
     * @return the maxTime
     */
    public String getMaxTime() {
        return maxTime;
    }

    /**
     * @param maxTime
     *            the maxTime to set
     */
    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public String getInfo() {
        return "ThinkTime(" + minTime + ", " +
                maxTime + ')';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + minTime + ", " + maxTime;
    }

}
