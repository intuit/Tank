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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.Location;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;

@MappedSuperclass
public abstract class BaseJob extends BaseEntity {

    public static final String PROPERTY_WORKLOAD_ID = "workloadId";
    public static final String PROPERTY_PROJECT_ID = "projectId";
    private static final long serialVersionUID = 1L;

    @Column(name = "workload_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private IncrementStrategy incrementStrategy = IncrementStrategy.increasing;

    @Column(name = "location")
    private String location = Location.unspecified.name();

    @Column(name = "termination_policy", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private TerminationPolicy terminationPolicy = TerminationPolicy.time;

    @Column(name = "ramp_time_seconds")
    private int rampTimeSeconds;

    @Column(name = "ramp_time_ms")
    private Long rampTime;

    @Column(name = "ramp_time_exp")
    private String rampTimeExpression;

    @Column(name = "baseline_virtual_users")
    private int baselineVirtualUsers;

    @Column(name = "vm_instance_type", columnDefinition = "varchar(255) NULL DEFAULT 'c3.2xlarge'")
    private String vmInstanceType = "c3.2xlarge";

    @Column(name = "num_users_per_agent", columnDefinition = "INT(11) NOT NULL DEFAULT '4000'")
    private int numUsersPerAgent = 4000;

    @Column(name = "logging_profile")
    private String loggingProfile = LoggingProfile.STANDARD.name();

    @Column(name = "stop_behavior")
    private String stopBehavior = StopBehavior.END_OF_SCRIPT_GROUP.name();

    @Column(name = "tank_client_class")
    private String tankClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";

    @Column(name = "simulation_time")
    private int simulationTimeSeconds;

    @Column(name = "simulation_time_ms")
    private Long simulationTime;

    @Column(name = "execution_time_ms")
    private Long executionTime;

    @Column(name = "simulation_time_exp")
    private String simulationTimeExpression;

    @Column(name = "user_interval_increment_seconds")
    private int userIntervalIncrement;

    @Column(name = "reporting_mode", nullable = false)
    private String reportingMode = TankConstants.RESULTS_NONE;

    @Column(name = "allow_variable_override")
    private Boolean allowOverride;

    @Column(name = "use_eips")
    private Boolean useEips;

    /**
     * 
     */
    public BaseJob() {
        super();
        List<VmInstanceType> types = new TankConfig().getVmManagerConfig().getInstanceTypes();
        for (VmInstanceType type : types) {
            if (type.isDefault()) {
                this.vmInstanceType = type.getName();
                this.numUsersPerAgent = type.getUsers();
            }
        }
        tankClientClass = new TankConfig().getAgentConfig().getTankClientClassDefault();
    }

    /**
     * 
     */
    public BaseJob(BaseJob copy) {
        this.baselineVirtualUsers = copy.baselineVirtualUsers;
        this.incrementStrategy = copy.incrementStrategy;
        this.rampTime = copy.rampTime;
        this.simulationTime = copy.simulationTime;
        this.terminationPolicy = copy.terminationPolicy;
        this.userIntervalIncrement = copy.userIntervalIncrement;
        this.location = copy.location;
        this.reportingMode = copy.reportingMode;
        this.allowOverride = copy.allowOverride;
        this.loggingProfile = copy.loggingProfile;
        this.rampTimeExpression = copy.rampTimeExpression;
        this.simulationTimeExpression = copy.simulationTimeExpression;
        this.stopBehavior = copy.stopBehavior;
        this.executionTime = copy.executionTime;
        this.numUsersPerAgent = copy.numUsersPerAgent;
        this.vmInstanceType = copy.vmInstanceType;
        this.useEips = copy.useEips;
        this.tankClientClass = copy.getTankClientClass();
    }

    public abstract Map<String, String> getVariables();
    
    public abstract Set<Integer> getDataFileIds();

    /**
     * @return the tankClientClass
     */
    public String getTankClientClass() {
        return tankClientClass;
    }

    /**
     * @param tankClientClass
     *            the tankClientClass to set
     */
    public void setTankClientClass(String tankClientClass) {
        this.tankClientClass = tankClientClass;
    }

    /**
     * @return the allowOverride
     */
    public boolean isAllowOverride() {
        return allowOverride != null ? allowOverride : false;
    }

    /**
     * @param allowOverride
     *            the allowOverride to set
     */
    public void setAllowOverride(boolean allowOverride) {
        this.allowOverride = allowOverride;
    }

    /**
     * @return the allowOverride
     */
    public boolean isUseEips() {
        return useEips != null ? useEips : new TankConfig().getVmManagerConfig().isUseElasticIps();
    }

    /**
     * @param allowOverride
     *            the allowOverride to set
     */
    public void setUseEips(boolean useEips) {
        this.useEips = useEips;
    }

    /**
     * 
     * @return
     */
    public String getLoggingProfile() {
        return loggingProfile;
    }

    /**
     * 
     * @param loggingProfile
     */
    public void setLoggingProfile(String loggingProfile) {
        this.loggingProfile = loggingProfile;
    }

    /**
     * @return the stopBehavior
     */
    public String getStopBehavior() {
        return stopBehavior != null ? stopBehavior : StopBehavior.END_OF_SCRIPT_GROUP.name();
    }

    /**
     * @param stopBehavior
     *            the stopBehavior to set
     */
    public void setStopBehavior(String stopBehavior) {
        this.stopBehavior = stopBehavior;
    }

    /**
     * @return the incrementStrategy
     */
    public IncrementStrategy getIncrementStrategy() {
        return incrementStrategy;
    }

    /**
     * @return the reportingMode
     */
    public String getReportingMode() {
        return reportingMode;
    }

    /**
     * @param reportingMode
     *            the reportingMode to set
     */
    public void setReportingMode(String reportingMode) {
        if (reportingMode != null) {
            this.reportingMode = reportingMode;
        }
    }

    /**
     * @param incrementStrategy
     *            the incrementStrategy to set
     */
    public void setIncrementStrategy(IncrementStrategy incrementStrategy) {
        if (incrementStrategy != null) {
            this.incrementStrategy = incrementStrategy;
        }
    }

    /**
     * @return the terminationPolicy
     */
    public TerminationPolicy getTerminationPolicy() {
        return terminationPolicy;
    }

    /**
     * @param terminationPolicy
     *            the terminationPolicy to set
     */
    public void setTerminationPolicy(TerminationPolicy terminationPolicy) {
        if (terminationPolicy != null) {
            this.terminationPolicy = terminationPolicy;
        }
    }

    /**
     * @return the rampTime
     */
    public long getRampTime() {
        return rampTime != null ? rampTime : rampTimeSeconds * 1000;
    }

    /**
     * @return the rampTimeExpression
     */
    public String getRampTimeExpression() {
        return rampTimeExpression;
    }

    /**
     * @param rampTimeExpression
     *            the rampTimeExpression to set
     */
    public void setRampTimeExpression(String rampTimeExpression) {
        this.rampTimeExpression = rampTimeExpression;
    }

    /**
     * @return the simulationTimeExpression
     */
    public String getSimulationTimeExpression() {
        return simulationTimeExpression;
    }

    /**
     * @param simulationTimeExpression
     *            the simulationTimeExpression to set
     */
    public void setSimulationTimeExpression(String simulationTimeExpression) {
        this.simulationTimeExpression = simulationTimeExpression;
    }

    /**
     * @return the vmInstanceType
     */
    public String getVmInstanceType() {
        return vmInstanceType;
    }

    /**
     * @param vmInstanceType
     *            the vmInstanceType to set
     */
    public void setVmInstanceType(String vmInstanceType) {
        this.vmInstanceType = vmInstanceType;
    }

    /**
     * @return the numUsersPerAgent
     */
    public int getNumUsersPerAgent() {
        return numUsersPerAgent;
    }

    /**
     * @param numUsersPerAgent
     *            the numUsersPerAgent to set
     */
    public void setNumUsersPerAgent(int numUsersPerAgent) {
        this.numUsersPerAgent = numUsersPerAgent;
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
        if (location != null) {
            this.location = location;
        }
    }

    /**
     * @param rampTime
     *            the rampTime to set
     */
    public void setRampTime(long rampTime) {
        this.rampTime = rampTime;
    }

    /**
     * @return the baselineVirtualUsers
     */
    public int getBaselineVirtualUsers() {
        return baselineVirtualUsers;
    }

    /**
     * @param baselineVirtualUsers
     *            the baselineVirtualUsers to set
     */
    public void setBaselineVirtualUsers(int baselineVirtualUsers) {
        this.baselineVirtualUsers = baselineVirtualUsers;
    }

    /**
     * @return the simulationTime
     */
    public long getSimulationTime() {
        return simulationTime != null ? simulationTime : simulationTimeSeconds * 1000L;
    }

    /**
     * @param simulationTime
     *            the simulationTime to set
     */
    public void setSimulationTime(long simulationTime) {
        this.simulationTime = simulationTime;
    }

    /**
     * @return the executionTime
     */
    public Long getExecutionTime() {
        return executionTime;
    }

    /**
     * @param executionTime
     *            the executionTime to set
     */
    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * @return the userIntervalIncrement
     */
    public int getUserIntervalIncrement() {
        return userIntervalIncrement;
    }

    /**
     * @param userIntervalIncrement
     *            the userIntervalIncrement to set
     */
    public void setUserIntervalIncrement(int userIntervalIncrement) {
        this.userIntervalIncrement = userIntervalIncrement;
    }

}