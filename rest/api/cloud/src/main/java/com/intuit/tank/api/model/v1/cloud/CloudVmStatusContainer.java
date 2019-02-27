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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.settings.TimeUtil;

/**
 * BrokerStatus
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "cloudVmContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloudVmStatusContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "statuses",
        "status",
        "startTime",
        "endTime",
        "reportTime",
        "jobId",
        "userDetails"
})
public class CloudVmStatusContainer implements Serializable, Comparable<CloudVmStatusContainer> {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "statuses", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "status", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Set<CloudVmStatus> statuses = new HashSet<CloudVmStatus>();

    @XmlElement(name = "status", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private JobQueueStatus status = JobQueueStatus.Created;

    @XmlElement(name = "startTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date startTime = new Date();

    @XmlElement(name = "endTime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date endTime;

    @XmlElement(name = "reportTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date reportTime = new Date();

    @XmlElement(name = "jobId", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String jobId;

    @XmlElementWrapper(name = "userDetails", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "userDetail", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private List<UserDetail> userDetails = new ArrayList<UserDetail>();

    @XmlTransient
    private Map<Date, List<UserDetail>> detailMap = new HashMap<Date, List<UserDetail>>();

    /**
     * @FrameworkUseOnly
     */
    public CloudVmStatusContainer() {
    }

    /**
     * @param statuses
     */
    public CloudVmStatusContainer(Set<CloudVmStatus> statuses) {
        this.statuses = statuses;
    }

    public JobQueueStatus getStatus() {
        return status;
    }

    public void setStatus(JobQueueStatus status) {
        this.status = status;
    }

    public void calculateUserDetails() {
        List<UserDetail> details;
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (CloudVmStatus status : statuses) {
            for (UserDetail detail : status.getUserDetails()) {
                Integer val = map.get(detail.getScript());
                if (val == null) {
                    val = 0;
                }
                map.put(detail.getScript(), val + detail.getUsers());
            }
        }
        details = map.entrySet().stream().map(entry -> new UserDetail(entry.getKey(), entry.getValue())).sorted().collect(Collectors.toList());
        userDetails = details;
        detailMap.put(TimeUtil.normalizeToPeriod(15, new Date()), details);
    }

    /**
     * @return the detailMap
     */
    public Map<Date, List<UserDetail>> getDetailMap() {
        return detailMap;
    }

    /**
     * @return the statuses
     */
    public Set<CloudVmStatus> getStatuses() {
        return statuses;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
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
     * @return the userDetails
     */
    public List<UserDetail> getUserDetails() {
        return userDetails;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
     * @inheritDoc
     */
    @Override
    public int compareTo(CloudVmStatusContainer o) {
        if (this.startTime != null) {
            return this.startTime.compareTo(o.startTime);
        } else if (o.startTime != null) {
            return -1;
        }
        // both null
        return 0;
    }

}
