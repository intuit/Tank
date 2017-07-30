/*
 * #%L
 * Automation Rest Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package com.intuit.tank.api.model.v1.automation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.harness.StopBehavior;

/**
 * 
 * @author Kevin McGoldrick
 * 
 */
@XmlRootElement
public class CreateJobRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="name")
    private String name;
    
    @XmlElement(name="rampTime")
    private String rampTime;
    
    @XmlElement(name="simulationTime")
    private String simulationTime;
    
    @XmlElement(name="userIntervalIncrement")
    private int userIntervalIncrement;
    
    @XmlElement(name="stopBehavior")
    private String stopBehavior = StopBehavior.END_OF_TEST.getDisplay();
    
    @XmlElement(name="jobRegions")
    private Set<CreateJobRegion> jobRegions = new HashSet<CreateJobRegion>();

//    public CreateJobRequest(String name) {
//    	this.name = name;
//    }

    public CreateJobRequest() {}

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rampTime
     */
    public String getRampTime() {
        return rampTime;
    }

    /**
     * @return the simulationTime
     */
    public String getSimulationTime() {
        return simulationTime;
    }

    /**
     * @return the userIntervalIncrement
     */
    public int getUserIntervalIncrement() {
        return userIntervalIncrement;
    }

    /**
     * @return the jobRegions
     */
    public Set<CreateJobRegion> getJobRegions() {
        return jobRegions;
    }
    
    
    /**
     * @return the stopBehavior
     */
    public String getStopBehavior() {
        return stopBehavior;
    }
    
    /**
     * @param jobRegions
     *            the jobRegions to set
     */
    private void setJobRegions(Set<CreateJobRegion> jobRegions) {
        this.jobRegions = jobRegions;
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
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}