/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.projects;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.project.TestPlan;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

@XmlRootElement(name = "project", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectTO", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "id",
        "created",
        "modified",
        "creator",
        "name",
        "productName",
        "rampTime",
        "location",
        "stopBehavior",
        "simulationTime",
        "userIntervalIncrement",
        "jobRegions",
        "testPlans",
        "workloadType",
        "terminationPolicy",
        "comments",
        "variables",
        "dataFileIds",
        "useEips"
})
public class ProjectTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Integer id;

    @XmlElement(name = "created", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date created;

    @XmlElement(name = "modified", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date modified;

    @XmlElement(name = "creator", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String creator;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String name;

    @XmlElement(name = "productName", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String productName;

    @XmlElement(name = "rampTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String rampTime;

    @XmlElement(name = "location", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String location;

    @XmlElement(name = "stopBehavior", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String stopBehavior;

    @XmlElement(name = "simulationTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Long simulationTime;

    @XmlElement(name = "userIntervalIncrement", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private int userIntervalIncrement;

    @XmlElement(name = "jobRegions", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<AutomationJobRegion> jobRegions = new HashSet<>();

    @XmlElement(name = "testPlans", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<AutomationTestPlan> testPlans = new ArrayList<>();

    @XmlElement(name = "workloadType", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private IncrementStrategy workloadType;

    @XmlElement(name = "terminationPolicy", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private TerminationPolicy terminationPolicy;

    @XmlElement(name = "comments", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String comments;

    @XmlElement(name = "useEips", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private boolean useEips;

    @XmlElementWrapper(name = "variables", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "variable", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<KeyPair> variables = new ArrayList<KeyPair>();

    @XmlElementWrapper(name = "data-file-ids", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "data-file-id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<Integer> dataFileIds = new ArrayList<Integer>();

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
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator
     *            the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @return the rampTime
     */
    public String getRampTime() {
        return rampTime;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the stopBehavior
     */
    public String getStopBehavior() {
        return stopBehavior;
    }

    /**
     * @return the simulationTime
     */
    public Long getSimulationTime() {
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
    public Set<AutomationJobRegion> getJobRegions() {
        return jobRegions;
    }

    /**
     * @return the testPlans
     */
    public List<AutomationTestPlan> getTestPlans() { return testPlans; }

    /**
     * @return the workloadType
     */
    public IncrementStrategy getWorkloadType() {
        return workloadType;
    }

    /**
     * @return the terminationPolicy
     */
    public TerminationPolicy getTerminationPolicy() {
        return terminationPolicy;
    }

    /**
     * @param rampTime
     *            the rampTime to set
     */
    public void setRampTime(String rampTime) {
        this.rampTime = rampTime;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @param stopBehavior
     *            the stopBehavior to set
     */
    public void setStopBehavior(String stopBehavior) {
        this.stopBehavior = stopBehavior;
    }

    /**
     * @param simulationTime
     *            the simulationTime to set
     */
    public void setSimulationTime(Long simulationTime) {
        this.simulationTime = simulationTime;
    }

    /**
     * @param userIntervalIncrement
     *            the userIntervalIncrement to set
     */
    public void setUserIntervalIncrement(int userIntervalIncrement) {
        this.userIntervalIncrement = userIntervalIncrement;
    }

    /**
     * @param jobRegions
     *            the jobRegions to set
     */
    public void setJobRegions(Set<AutomationJobRegion> jobRegions) {
        this.jobRegions = jobRegions;
    }

    /**
     * @param testPlans
     *            the testPlans to set
     */
    private void setTestPlans(List<AutomationTestPlan> testPlans) {
        this.testPlans = testPlans;
    }

    /**
     * @param workloadType
     *            the workloadType to set
     */
    public void setWorkloadType(IncrementStrategy workloadType) {
        this.workloadType = workloadType;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @param terminationPolicy
     *            the terminationPolicy to set
     */
    public void setTerminationPolicy(TerminationPolicy terminationPolicy) {
        this.terminationPolicy = terminationPolicy;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @return the variables
     */
    public List<KeyPair> getVariables() {
        return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(List<KeyPair> variables) {
        this.variables = variables;
    }

    /**
     * @return the dataFiles
     */
    public List<Integer> getDataFileIds() {
        return dataFileIds;
    }

    /**
     * @param dataFileIds
     *            the dataFiles to set
     */
    public void setDataFiles(List<Integer> dataFileIds) {
        this.dataFileIds = dataFileIds;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return name;
    }

}
