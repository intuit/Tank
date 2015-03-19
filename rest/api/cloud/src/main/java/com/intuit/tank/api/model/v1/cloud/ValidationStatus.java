/**
 * Copyright 2013 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.cloud;

/*
 * #%L
 * Cloud Rest API
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
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * ValidationStatus
 * 
 * @author psadikov
 * 
 */
@XmlRootElement(name = "validationCloudVm", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validationCloudVm", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "kills",
        "aborts",
        "gotos",
        "skips",
        "skipGroups",
        "restarts"
})
public class ValidationStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "kills", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int kills;

    @XmlElement(name = "aborts", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int aborts;

    @XmlElement(name = "gotos", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int gotos;

    @XmlElement(name = "skips", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int skips;

    @XmlElement(name = "skipGroups", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int skipGroups;

    @XmlElement(name = "restarts", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int restarts;

    public ValidationStatus() {
    }

    /**
     * 
     */
    public ValidationStatus(int kills, int aborts, int gotos, int skips, int skipGroups, int restarts) {
        super();
        this.kills = kills;
        this.aborts = aborts;
        this.gotos = gotos;
        this.skips = skips;
        this.skipGroups = skipGroups;
        this.restarts = restarts;
    }

    /**
     * @return total number of failures
     */
    public int getTotal() {
        return kills + aborts + gotos + skips + skipGroups + restarts;
    }

    /**
     * @return the validationKills
     */
    public int getValidationKills() {
        return kills;
    }

    /**
     * @return the validationAborts
     */
    public int getValidationAborts() {
        return aborts;
    }

    /**
     * @return the validationGotos
     */
    public int getValidationGotos() {
        return gotos;
    }

    /**
     * @return the validationSkips
     */
    public int getValidationSkips() {
        return skips;
    }

    /**
     * @return the validationSkipGroups
     */
    public int getValidationSkipGroups() {
        return skipGroups;
    }

    /**
     * @return the validationRestarts
     */
    public int getValidationRestarts() {
        return restarts;
    }

    /**
     * Add fail counts from another ValidationStatus
     */
    public void addFailures(ValidationStatus other) {
        this.kills += other.kills;
        this.aborts += other.aborts;
        this.gotos += other.gotos;
        this.skips += other.skips;
        this.skipGroups += other.skipGroups;
        this.restarts += other.restarts;
    }

    /**
     * Update kill count
     */
    public void addKill() {
        kills++;
    }

    /**
     * Update abort count
     */
    public void addAbort() {
        aborts++;
    }

    /**
     * Update goto count
     */
    public void addGoto() {
        gotos++;
    }

    /**
     * Update skip count
     */
    public void addSkip() {
        skips++;
    }

    /**
     * Update skip group count
     */
    public void addSkipGroup() {
        skipGroups++;
    }

    /**
     * Update restart count
     */
    public void addRestart() {
        restarts++;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValidationStatus)) {
            return false;
        }
        ValidationStatus o = (ValidationStatus) obj;
        return new EqualsBuilder()
                .append(o.getValidationKills(), getValidationKills())
                .append(o.getValidationAborts(), getValidationAborts())
                .append(o.getValidationGotos(), getValidationGotos())
                .append(o.getValidationSkips(), getValidationSkips())
                .append(o.getValidationSkipGroups(), getValidationSkipGroups())
                .append(o.getValidationRestarts(), getValidationRestarts())
                .isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 37)
                .append(getValidationKills())
                .append(getValidationAborts())
                .append(getValidationGotos())
                .append(getValidationSkips())
                .append(getValidationSkipGroups())
                .append(getValidationRestarts())
                .toHashCode();
    }

}
