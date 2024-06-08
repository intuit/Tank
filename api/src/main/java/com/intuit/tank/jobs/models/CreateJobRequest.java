/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.jobs.models;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.projects.models.Namespace;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
public class CreateJobRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="projectName")
    private String projectName;

    @XmlElement(name="projectId")
    private Integer projectId;

    @XmlElement(name="jobInstanceName")
    private String jobInstanceName;
    
    @XmlElement(name="rampTime")
    private String rampTime;
    
    @XmlElement(name="simulationTime")
    private String simulationTime;
    
    @XmlElement(name="userIntervalIncrement")
    private int userIntervalIncrement;

    @XmlElement(name="targetRampRate")
    private double targetRampRate;

    @XmlElement(name="stopBehavior")
    private String stopBehavior = StopBehavior.END_OF_TEST.name();

    @XmlElement(name="vmInstance")
    private String vmInstance;

    @XmlElement(name="numUsersPerAgent")
    private int numUsersPerAgent;

    @XmlElement(name="targetRatePerAgent")
    private double targetRatePerAgent;
    
    @XmlElement(name="jobRegions")
    private Set<CreateJobRegion> jobRegions = new HashSet<CreateJobRegion>();

    @XmlElement(name = "workloadType")
    private IncrementStrategy workloadType;

    public CreateJobRequest(String projectName) {
    	this.projectName = projectName;
    }

    public CreateJobRequest() {}

    /**
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return the project id
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @return job Name
     */
    public String getJobInstanceName() {
        return jobInstanceName;
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
     * @return the targetRampRate
     */
    public double getTargetRampRate() {
        return targetRampRate;
    }

    /**
     * @return the jobRegions
     */
    public Set<CreateJobRegion> getJobRegions() {
        return jobRegions;
    }

    /**
     * @return the workloadType (linear / nonlinear)
     */
    public IncrementStrategy getWorkloadType() {
        return workloadType;
    }

    /**
     * @return the stopBehavior
     */
    public String getStopBehavior() {
        return stopBehavior;
    }

    /**
     * @return the vmInstance
     */
    public String getVmInstance() {
        return vmInstance;
    }

    /**
     * @return the numUsersPerAgent
     */
    public int getNumUsersPerAgent() {
        return numUsersPerAgent;
    }

    /**
     * @return the targetRatePerAgent
     */
    public double getTargetRatePerAgent() { return targetRatePerAgent; }

    /**
     * @param jobRegions
     *            the jobRegions to set
     */
    private void setJobRegions(Set<CreateJobRegion> jobRegions) {
        this.jobRegions = jobRegions;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
