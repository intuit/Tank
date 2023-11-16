package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.google.common.collect.ImmutableMap;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.logging.log4j.message.ObjectMessage;

public class AgentRunData {
    private static final Logger LOG = LogManager.getLogger(AgentRunData.class);

    private int numUsers = 1;
    private long rampTime = 0;
    private IncrementStrategy incrementStrategy = IncrementStrategy.increasing;
    private int numStartUsers = 1;
    private int userInterval = 1;
    private double baseDelay = 0;
    private double initialDelay = 0;
    private double rampRateDelay = 0;
    private double targetRampRate = 1.0;
    private String testPlans = "";
    private String instanceId;
    private String jobId = "0";
    private String machineName = "invalid";
    private String reportingMode = TankConstants.RESULTS_NONE;
    private long simulationTime = 0;
    private int agentInstanceNum;
    private StopBehavior stopBehavior = StopBehavior.END_OF_SCRIPT_GROUP;
    private int totalAgents = 1;
    private String projectName;
    private String tankhttpClientClass;
    private LoggingProfile activeProfile = LoggingProfile.STANDARD;

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
     * @return the rampTime in milliseconds
     */
    public long getRampTimeMillis() {
        return rampTime;
    }

    /**
     * @param rampTime
     *            the rampTime to set in milliseconds
     */
    public void setRampTimeMillis(long rampTime) {
        this.rampTime = rampTime;
    }

    /**
     * @return the incrementStrategy
     */
    public IncrementStrategy getIncrementStrategy() { return incrementStrategy; }

    /**
     * @param incrementStrategy
     *            the incrementStrategy to set
     */
    public void setIncrementStrategy(IncrementStrategy incrementStrategy) {
        this.incrementStrategy = incrementStrategy;
    }

    /**
     * @return the numStartUsers
     */
    public int getNumStartUsers() {
        return numStartUsers;
    }

    /**
     * @param numStartUsers
     *            the numStartThreads to set
     */
    public void setNumStartUsers(int numStartUsers) {
        this.numStartUsers = numStartUsers;
    }

    /**
     * @return the tankhttpClientClass
     */
    public String getTankhttpClientClass() {
        return tankhttpClientClass;
    }

    /**
     * @param tankhttpClientClass
     *            the tankhttpClientClass to set
     */
    public void setTankhttpClientClass(String tankhttpClientClass) {
        this.tankhttpClientClass = tankhttpClientClass;
    }

    /**
     * @return the userInterval
     */
    public int getUserInterval() {
        return userInterval;
    }

    /**
     * @param userInterval
     *            the userInterval to set
     */
    public void setUserInterval(int userInterval) {
        if (userInterval > 0) {
            this.userInterval = userInterval;
        }
    }
    /**
     * @return the baseDelay
     */
    public double getBaseDelay() {
        return baseDelay;
    }

    /**
     * @param baseDelay
     *            the baseDelay to set
     */
    public void setBaseDelay(double baseDelay) {
        this.baseDelay = baseDelay;
    }


    /**
     * @return the initialDelay
     */
    public double getInitialDelay() {
        return initialDelay;
    }

    /**
     * @param initialDelay
     *            the initialDelay to set
     */
    public void setInitialDelay(double initialDelay) {
        this.initialDelay = initialDelay;
    }

    /**
     * @return the rampRateDelay
     */
    public double getRampRateDelay() {
        return rampRateDelay;
    }

    /**
     * @param rampRateDelay
     *            the rampRateDelay to set
     */
    public void setRampRateDelay(double rampRateDelay) {
        this.rampRateDelay = rampRateDelay;
    }

    /**
     * @return the targetRampRate
     */
    public double getTargetRampRate() {
        return targetRampRate;
    }

    /**
     * @param targetRampRate
     *            the targetRampRate to set
     */
    public void setTargetRampRate(double targetRampRate) {
        this.targetRampRate = targetRampRate;
    }

    /**
     * @return the testPlans
     */
    public String getTestPlans() {
        return testPlans;
    }

    /**
     * @param testPlans
     *            the testPlans to set
     */
    public void setTestPlans(String testPlans) {
        this.testPlans = testPlans;
    }

    /**
     * @return the instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @param instanceId
     *            the instanceId to set
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the machineName
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * @param machineName
     *            the machineName to set
     */
    public void setMachineName(String machineName) {
        this.machineName = machineName;
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
        this.reportingMode = reportingMode;
    }

    /**
     * @return the simulationTime in milliseconds
     */
    public long getSimulationTimeMillis() {
        return simulationTime;
    }

    /**
     * @param simulationTime
     *            the simulationTime to set in milliseconds
     */
    public void setSimulationTimeMillis(long simulationTime) {
        this.simulationTime = simulationTime;
    }

    /**
     * @return the agentInstanceNum
     */
    public int getAgentInstanceNum() {
        return agentInstanceNum;
    }

    /**
     * @param agentInstanceNum
     *            the agentInstanceNum to set
     */
    public void setAgentInstanceNum(int agentInstanceNum) {
        this.agentInstanceNum = agentInstanceNum;
    }

    /**
     * @return the totalAgents
     */
    public int getTotalAgents() {
        return totalAgents;
    }

    /**
     * @param totalAgents
     *            the totalAgents to set
     */
    public void setTotalAgents(int totalAgents) {
        this.totalAgents = totalAgents;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName
     *            the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the activeProfile
     */
    public LoggingProfile getActiveProfile() {
        return activeProfile;
    }

    /**
     * @param activeProfile
     *            the activeProfile to set
     */
    public void setActiveProfile(LoggingProfile activeProfile) {
        this.activeProfile = activeProfile;
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
        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Setting stopBehavior to " + stopBehavior)));
        this.stopBehavior = stopBehavior;
    }

}
