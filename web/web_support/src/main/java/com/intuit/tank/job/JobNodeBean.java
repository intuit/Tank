package com.intuit.tank.job;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.intuit.tank.api.model.v1.cloud.UserDetail;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.auth.Security;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.settings.AccessRight;

public abstract class JobNodeBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private String jobId;
    private String id;
    private String reportMode;
    private String status;
    private String region;
    private String activeUsers;
    private ValidationStatus numFailures;
    private List<UserDetail> userDetails = new ArrayList<UserDetail>();
    private String totalFails;
    private String totalUsers;
    private String startTime;
    private String endTime;
    private boolean hasRights = false;
    private Map<Date, List<UserDetail>> statusDetailMap;
    // private Map<Date, Map<String, TPSInfo>> tpsInfoMap;
    private int tps;

    public abstract String getType();

    public abstract void reCalculate();

    /**
     * @return the hasRights
     */
    public boolean isHasRights() {
        return hasRights;
    }

    public boolean isJobNode() {
        return false;
    }

    /**
     * @return the tps
     */
    public int getTps() {
        if (JobStatus.Running.name().equalsIgnoreCase(status) || "project".equalsIgnoreCase(getType())) {
            return tps;
        }
        return 0;
    }

    /**
     * @param tps
     *            the tps to set
     */
    public void setTps(int tps) {
        this.tps = tps;
    }

    /**
     * @return the statusDetailMap
     */
    public Map<Date, List<UserDetail>> getStatusDetailMap() {
        return statusDetailMap;
    }

    // /**
    // * @return the statusDetailMap
    // */
    // public Map<Date, Map<String, TPSInfo>> getTpsDetailMap() {
    // return tpsInfoMap;
    // }
    //
    // /**
    // * @param statusDetailMap
    // * the statusDetailMap to set
    // */
    // public void setTpsDetailMap(Map<Date, Map<String, TPSInfo>> tpsInfoMap) {
    // this.tpsInfoMap = tpsInfoMap;
    // }
    /**
     * @param statusDetailMap
     *            the statusDetailMap to set
     */
    public void setStatusDetailMap(Map<Date, List<UserDetail>> statusDetailMap) {
        this.statusDetailMap = statusDetailMap;
    }

    /**
     * @param hasRights
     *            the hasRights to set
     */
    public void setHasRights(boolean hasRights) {
        this.hasRights = hasRights;
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
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the reportMode
     */
    public String getReportMode() {
        return reportMode;
    }

    /**
     * @param reportMode
     *            the reportMode to set
     */
    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
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
     * @return the validation errors
     */
    public ValidationStatus getNumFailures() {
        return numFailures;
    }

    /**
     * @param validationStatus
     * 
     */
    public void setNumFailures(ValidationStatus fails) {
        this.numFailures = fails;
        this.totalFails = String.valueOf(numFailures.getTotal());
    }

    /**
     * @return the userDetails
     */
    public List<UserDetail> getUserDetails() {
        return userDetails;
    }

    /**
     * @param userDetails
     *            the userDetails to set
     */
    public void setUserDetails(List<UserDetail> userDetails) {
        this.userDetails = userDetails;
    }

    public String getTotalFails() {
        return totalFails;
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
     * if the node is deleteable
     * 
     * @return
     */
    public boolean isDeleteable() {
        return false;
    }

    public abstract List<? extends JobNodeBean> getSubNodes();

    public abstract boolean hasSubNodes();

    public abstract boolean isKillable();

    public abstract boolean isStopable();

    public abstract boolean isRunnable();

    public abstract boolean isPausable();

    public abstract boolean isRampPausable();

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 41).append(id).toHashCode();
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JobNodeBean)) {
            return false;
        }
        try {
            JobNodeBean o = (JobNodeBean) obj;
            return new EqualsBuilder().append(this.id, o.id).isEquals();
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean canControlJob(Security security) {
        return this.hasRights || security.hasRight(AccessRight.CONTROL_JOB);
    }

}
