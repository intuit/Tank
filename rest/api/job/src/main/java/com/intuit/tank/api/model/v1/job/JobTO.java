/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.job;

/*
 * #%L
 * Job Rest Api
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

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * ProjectAggregate
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "job", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Job", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "id",
        "name",
        "status",
        "numUsers",
        "location",
        "startTime",
        "endTime",
        "rampTimeMilis",
        "simulationTimeMilis"
})
public class JobTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Integer id;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String name;

    @XmlElement(name = "status", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String status;

    @XmlElement(name = "numUsers", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int numUsers;

    @XmlElement(name = "location", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String location;

    @XmlElement(name = "startTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date startTime;

    @XmlElement(name = "endTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date endTime;

    @XmlElement(name = "rampTimeMilis", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private long rampTimeMilis;

    @XmlElement(name = "simulationTimeMilis", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private long simulationTimeMilis;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the numUsers
     */
    public int getNumUsers() {
        return numUsers;
    }

    /**
     * @param numUsers
     *            the numUsers to set
     */
    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the startTime
     */
    public Date getSteadyStateStartTime() {
        if (startTime != null) {
            return new Date(startTime.getTime() + rampTimeMilis);
        }
        return null;
    }

    /**
     * @return the startTime
     */
    public Date getSteadyStateEndTime() {
        Date ret = null;
        if (startTime != null) {
            if (simulationTimeMilis > 0) {
                ret = new Date(startTime.getTime() + simulationTimeMilis);
            } else if (endTime != null) {
                ret = new Date(endTime.getTime() - rampTimeMilis);
            }
        }
        return ret;
    }

    /**
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the rampTimeMilis
     */
    public long getRampTimeMilis() {
        return rampTimeMilis;
    }

    /**
     * @param rampTimeMilis
     *            the rampTimeMilis to set
     */
    public void setRampTimeMilis(long rampTimeMilis) {
        this.rampTimeMilis = rampTimeMilis;
    }

    /**
     * @return the simulationTimeMilis
     */
    public long getSimulationTimeMilis() {
        return simulationTimeMilis;
    }

    /**
     * @param simulationTimeMilis
     *            the simulationTimeMilis to set
     */
    public void setSimulationTimeMilis(long simulationTimeMilis) {
        this.simulationTimeMilis = simulationTimeMilis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
