/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.projects;

import com.intuit.tank.rest.mvc.rest.models.projects.adapter.XmlGenericMapAdapter;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.Location;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.*;

@XmlRootElement(name = "automationRequest", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationRequest", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "name",
        "scriptName",
        "productName",
        "comments",
        "rampTime",
        "location",
        "stopBehavior",
        "simulationTime",
        "userIntervalIncrement",
        "filterIds",
        "filterGroupIds",
        "externalScriptIds",
        "dataFileIds",
        "jobRegions",
        "testPlans",
        "workloadType",
        "terminationPolicy",
        "variables"
})
public class AutomationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String name;

    @XmlElement(name = "scriptName", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String scriptName;

    @XmlElement(name = "productName", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String productName;

    @XmlElement(name = "comments", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String comments;

    @XmlElement(name = "rampTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String rampTime;

    @XmlElement(name = "simulationTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String simulationTime;

    @XmlElement(name = "userIntervalIncrement", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private int userIntervalIncrement;

    @XmlElement(name = "location", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Location location;

    @XmlElement(name = "stopBehavior", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private StopBehavior stopBehavior = StopBehavior.END_OF_SCRIPT_GROUP;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @XmlElementWrapper(name = "filterIds", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<Integer> filterIds = new ArrayList<Integer>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @XmlElementWrapper(name = "filterGroupIds", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<Integer> filterGroupIds = new ArrayList<Integer>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @XmlElementWrapper(name = "externalScripts", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<Integer> externalScriptIds = new ArrayList<Integer>();

    @XmlElementWrapper(name = "dataFileIds", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<Integer> dataFileIds = new ArrayList<Integer>();

    @XmlElementWrapper(name = "jobRegions", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "region", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<AutomationJobRegion> jobRegions = new HashSet<AutomationJobRegion>();

    @XmlElementWrapper(name = "testPlans", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "testPlans", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<AutomationTestPlan> testPlans = new ArrayList<>();

    @XmlElement(name = "workloadType", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private IncrementStrategy workloadType;

    @XmlElement(name = "terminationPolicy", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private TerminationPolicy terminationPolicy;

    @XmlElement(name = "variables", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    @XmlJavaTypeAdapter(XmlGenericMapAdapter.class)
    private Map<String, String> variables = new HashMap<String, String>();

    /**
     * Fluent interface for Building automation requests.
     * 
     * @return the builder
     */
    public static AutomationRequestBuilder builder() {
        return new AutomationRequestBuilder();
    }

    /**
     * @return the scriptName
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * @param scriptName
     *            the scriptName to set
     */
    private void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    /**
     * @return the stopBehavior
     */
    public StopBehavior getStopBehavior() {
        return stopBehavior;
    }

    /**
     * @param stopBehavior
     *            the stopBehavior to set
     */
    public void setStopBehavior(StopBehavior stopBehavior) {
        this.stopBehavior = stopBehavior;
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
    private void setName(String name) {
        this.name = name;
    }

    /**
     * 
     */
    private AutomationRequest() {

    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @return the project comments
     */
    public String getComments() {
        return comments;
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
     * @return the filterIds
     */
    public List<Integer> getFilterIds() {
        return filterIds;
    }

    /**
     * @return the extrnalScriptIds
     */
    public List<Integer> getExternalScriptIds() {
        return externalScriptIds;
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
     * @return the project workloadType
     */
    public IncrementStrategy getWorkloadType() {
        return workloadType;
    }

    /**
     * @return the project termination policy
     */
    public TerminationPolicy getTerminationPolicy() {
        return terminationPolicy;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location != null ? location : Location.unspecified;
    }

    /**
     * @return the filterGroupIds
     */
    public List<Integer> getFilterGroupIds() {
        return filterGroupIds;
    }

    /**
     * @param filterGroupIds
     *            the filterGroupIds to set
     */
    private void setFilterGroupIds(List<Integer> filterGroupIds) {
        this.filterGroupIds = filterGroupIds;
    }

    /**
     * @return the dataFileIds
     */
    public List<Integer> getDataFileIds() {
        return dataFileIds;
    }

    /**
     * @param location
     *            the location to set
     */
    private void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @param productName
     *            the productName to set
     */
    private void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @param comments
     *            the comments to set
     */
    private void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @param rampTime
     *            the rampTime to set
     */
    private void setRampTime(String rampTime) {
        this.rampTime = rampTime;
    }

    /**
     * @param simulationTime
     *            the simulationTime to set
     */
    private void setSimulationTime(String simulationTime) {
        this.simulationTime = simulationTime;
    }

    /**
     * @param userIntervalIncrement
     *            the userIntervalIncrement to set
     */
    private void setUserIntervalIncrement(int userIntervalIncrement) {
        this.userIntervalIncrement = userIntervalIncrement;
    }

    /**
     * @param filterIds
     *            the filterIds to set
     */
    private void setFilterIds(List<Integer> filterIds) {
        this.filterIds = filterIds;
    }

    /**
     * @param filterIds
     *            the filterIds to set
     */
    private void setExternalScriptIds(List<Integer> filterIds) {
        this.externalScriptIds = filterIds;
    }

    /**
     * @param filterIds
     *            the filterIds to set
     */
    private void setDataFileIds(List<Integer> filterIds) {
        this.dataFileIds = filterIds;
    }

    /**
     * @param jobRegions
     *            the jobRegions to set
     */
    private void setJobRegions(Set<AutomationJobRegion> jobRegions) {
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
    private void setWorkloadType(IncrementStrategy workloadType) {
        this.workloadType = workloadType;
    }

    /**
     * @param terminationPolicy
     *            the termination policy to set
     */
    private void setTerminationPolicy(TerminationPolicy terminationPolicy) {
        this.terminationPolicy = terminationPolicy;
    }

    /**
     * @return the variables
     */
    public Map<String, String> getVariables() {
        return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */
    private void setVariables(Map<String, String> variables) {
        this.variables = variables;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(59, 25).append(productName).append(rampTime).append(simulationTime)
                .append(userIntervalIncrement).append(jobRegions).append(variables).toHashCode();
    }

    public static class AutomationRequestBuilder extends AutomationRequestBuilderBase<AutomationRequestBuilder> {

        public AutomationRequestBuilder() {
            super(new AutomationRequest());
        }

        public AutomationRequest build() {
            return getInstance();
        }
    }

    static class AutomationRequestBuilderBase<GeneratorT extends AutomationRequestBuilderBase<GeneratorT>> {
        private AutomationRequest instance;

        /**
         * 
         */
        public AutomationRequestBuilderBase() {
            this(new AutomationRequest());
        }

        protected AutomationRequestBuilderBase(AutomationRequest aInstance) {
            instance = aInstance;
        }

        protected AutomationRequest getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withName(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withScriptName(String aValue) {
            instance.setScriptName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withProductName(String aValue) {
            instance.setProductName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withLocation(Location aValue) {
            instance.setLocation(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withStopBehavior(StopBehavior aValue) {
            instance.setStopBehavior(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withComments(String aValue) {
            instance.setComments(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withRampTime(String aValue) {
            instance.setRampTime(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withSimulationTime(String aValue) {
            instance.setSimulationTime(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withUserIntervalIncrement(int aValue) {
            instance.setUserIntervalIncrement(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withFilterIds(List<Integer> aValue) {
            instance.setFilterIds(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedFilterId(Integer aValue) {
            if (instance.getFilterIds() == null) {
                instance.setFilterIds(new ArrayList<Integer>());
            }

            ((ArrayList<Integer>) instance.getFilterIds()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withDataFileIds(List<Integer> aValue) {
            instance.setDataFileIds(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedDataFileId(Integer aValue) {
            if (instance.getDataFileIds() == null) {
                instance.setDataFileIds(new ArrayList<Integer>());
            }

            ((ArrayList<Integer>) instance.getDataFileIds()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withExternalScriptIds(List<Integer> aValue) {
            instance.setExternalScriptIds(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedExternalScriptId(Integer aValue) {
            if (instance.getExternalScriptIds() == null) {
                instance.setExternalScriptIds(new ArrayList<Integer>());
            }

            ((ArrayList<Integer>) instance.getExternalScriptIds()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withFilterGroupIds(List<Integer> aValue) {
            instance.setFilterGroupIds(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedFilterGroupId(Integer aValue) {
            if (instance.getFilterGroupIds() == null) {
                instance.setFilterGroupIds(new ArrayList<Integer>());
            }

            ((ArrayList<Integer>) instance.getFilterGroupIds()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withJobRegions(Set<AutomationJobRegion> aValue) {
            instance.setJobRegions(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedJobRegion(AutomationJobRegion aValue) {
            if (instance.getJobRegions() == null) {
                instance.setJobRegions(new HashSet<AutomationJobRegion>());
            }

            ((Set<AutomationJobRegion>) instance.getJobRegions()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withTestPlans(List<AutomationTestPlan> aValue) {
            instance.setTestPlans(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedTestPlan(AutomationTestPlan aValue) {
            if (instance.getTestPlans() == null) {
                instance.setTestPlans(new ArrayList<>());
            }

            ((List<AutomationTestPlan>) instance.getTestPlans()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withWorkloadType(IncrementStrategy aValue) {
            instance.setWorkloadType(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withTerminationPolicy(TerminationPolicy aValue) {
            instance.setTerminationPolicy(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withVariables(Map<String, String> aValue) {
            instance.setVariables(aValue);

            return (GeneratorT) this;
        }
    }

}
