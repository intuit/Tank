/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * BrokerStatus
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "cloudVm", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloudVm", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "instanceId",
        "jobId",
        "securityGroup",
        "jobStatus",
        "role",
        "vmRegion",
        "vmStatus",
        "validationFailures",
        "totalUsers",
        "currentUsers",
        "startTime",
        "endTime",
        "reportTime",
        "totalTps",
        "userDetails"
})
public class CloudVmStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "instanceId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceId;

    @XmlElement(name = "jobId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String jobId;

    @XmlElement(name = "securityGroup", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String securityGroup;

    @XmlElement(name = "jobStatus", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private JobStatus jobStatus = JobStatus.Unknown;

    @XmlElement(name = "role", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private VMImageType role = VMImageType.AGENT;

    @XmlElement(name = "vmRegion", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private VMRegion vmRegion;

    @XmlElement(name = "vmStatus", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private VMStatus vmStatus = VMStatus.unknown;

    @XmlElement(name = "validationFailures", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private ValidationStatus validationFailures;

    @XmlElement(name = "totalUsers", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int totalUsers;

    @XmlElement(name = "currentUsers", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int currentUsers;

    @XmlElement(name = "startTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date startTime;

    @XmlElement(name = "endTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date endTime;

    @XmlElement(name = "reportTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date reportTime;

    @XmlElement(name = "totalTps", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private int totalTps;

    @XmlElementWrapper(name = "userDetails", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "userDetail", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private List<UserDetail> userDetails = new ArrayList<UserDetail>();

    protected CloudVmStatus() {
    }

    /**
     * @param instanceId
     * @param jobId
     * @param securityGroup
     * @param jobStatus
     * @param vmStatus
     * @param totalUsers
     * @param currentUsers
     */
    public CloudVmStatus(String instanceId, String jobId, String securityGroup, JobStatus jobStatus, VMImageType role,
            VMRegion vmRegion,
            VMStatus vmStatus, ValidationStatus validationFailures,
            int totalUsers, int currentUsers, Date startTime, Date endTime) {
        super();
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.securityGroup = securityGroup;
        this.vmRegion = vmRegion;
        this.role = role;
        this.jobStatus = jobStatus;
        this.vmStatus = vmStatus;
        this.validationFailures = validationFailures;
        this.totalUsers = totalUsers;
        this.currentUsers = currentUsers;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 
     * @param copy
     */
    public CloudVmStatus(CloudVmStatus copy) {
        this.instanceId = copy.instanceId;
        this.jobId = copy.jobId;
        this.securityGroup = copy.securityGroup;
        this.vmRegion = copy.vmRegion;
        this.role = copy.role;
        this.jobStatus = copy.jobStatus;
        this.vmStatus = copy.vmStatus;
        this.validationFailures = copy.validationFailures;
        this.totalUsers = copy.totalUsers;
        this.currentUsers = copy.currentUsers;
        this.startTime = copy.startTime;
        this.endTime = copy.endTime;
        this.totalTps = copy.totalTps;
    }

    /**
     * @return the tpsInfo
     */
    public int getTotalTps() {
        return totalTps;
    }

    /**
     * @param tpsInfo
     *            the tpsInfo to set
     */
    public void setTotalTps(int totalTps) {
        this.totalTps = totalTps;
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

    /**
     * @return the validationFailures
     */
    public ValidationStatus getValidationFailures() {
        return validationFailures;
    }

    /**
     * @return the instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @return the vmRegion
     */
    public VMRegion getVmRegion() {
        return vmRegion;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return the securityGroup
     */
    public String getSecurityGroup() {
        return securityGroup;
    }

    /**
     * @return the jobStatus
     */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * @return the vmStatus
     */
    public VMStatus getVmStatus() {
        return vmStatus;
    }

    /**
     * @return the totalUsers
     */
    public int getTotalUsers() {
        return totalUsers;
    }

    /**
     * @return the currentUsers
     */
    public int getCurrentUsers() {
        return currentUsers;
    }

    /**
     * @return the role
     */
    public VMImageType getRole() {
        return role;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @return the reportTime
     */
    public Date getReportTime() {
        return reportTime;
    }

    /**
     * @param reportTime
     *            the reportTime to set
     */
    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    /**
     * @param jobStatus
     *            the jobStatus to set
     */
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * @param vmStatus
     *            the vmStatus to set
     */
    public void setVmStatus(VMStatus vmStatus) {
        this.vmStatus = vmStatus;
    }

    /**
     * @param currentUsers
     *            the currentUsers to set
     */
    public void setCurrentUsers(int currentUsers) {
        this.currentUsers = currentUsers;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
        if (!(obj instanceof CloudVmStatus)) {
            return false;
        }
        CloudVmStatus o = (CloudVmStatus) obj;
        return new EqualsBuilder().append(o.getInstanceId(), getInstanceId()).append(o.getJobId(), getJobId())
                .isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getInstanceId()).append(getJobId()).toHashCode();
    }

}
