package com.intuit.tank.agent;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

public class AgentStatusReporter {

    private String agentStatus;
    private String jobStatus;
    private String role;
    private String region;
    private String activeUsers;
    private String totalUsers;
    private String usersChange;
    private String startTime;
    private String endTime;
    private String totalTime;
    private String jobId;
    private String instanceId;

    /**
     * 
     */
    public AgentStatusReporter() {
    }

    /**
     * @param agentStatus
     * @param jobStatus
     * @param role
     * @param region
     * @param activeUsers
     * @param totalUsers
     * @param usersChange
     * @param startTime
     * @param endTime
     * @param totalTime
     * @param jobId
     * @param instanceId
     */
    public AgentStatusReporter(String agentStatus, String jobStatus, String role, String region, String activeUsers,
            String totalUsers, String usersChange, String startTime, String endTime, String totalTime, String jobId,
            String instanceId) {
        this.agentStatus = agentStatus;
        this.jobStatus = jobStatus;
        this.role = role;
        this.region = region;
        this.activeUsers = activeUsers;
        this.totalUsers = totalUsers;
        this.usersChange = usersChange;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalTime = totalTime;
        this.jobId = jobId;
        this.instanceId = instanceId;
    }

    /**
     * @return the agentStatus
     */
    public String getAgentStatus() {
        return agentStatus;
    }

    /**
     * @param agentStatus
     *            the agentStatus to set
     */
    public void setAgentStatus(String agentStatus) {
        this.agentStatus = agentStatus;
    }

    /**
     * @return the jobStatus
     */
    public String getJobStatus() {
        return jobStatus;
    }

    /**
     * @param jobStatus
     *            the jobStatus to set
     */
    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region
     *            the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the activeUsers
     */
    public String getActiveUsers() {
        return activeUsers;
    }

    /**
     * @param activeUsers
     *            the activeUsers to set
     */
    public void setActiveUsers(String activeUsers) {
        this.activeUsers = activeUsers;
    }

    /**
     * @return the totalUsers
     */
    public String getTotalUsers() {
        return totalUsers;
    }

    /**
     * @param totalUsers
     *            the totalUsers to set
     */
    public void setTotalUsers(String totalUsers) {
        this.totalUsers = totalUsers;
    }

    /**
     * @return the usersChange
     */
    public String getUsersChange() {
        return usersChange;
    }

    /**
     * @param usersChange
     *            the usersChange to set
     */
    public void setUsersChange(String usersChange) {
        this.usersChange = usersChange;
    }

    /**
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the totalTime
     */
    public String getTotalTime() {
        return totalTime;
    }

    /**
     * @param totalTime
     *            the totalTime to set
     */
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
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

}
