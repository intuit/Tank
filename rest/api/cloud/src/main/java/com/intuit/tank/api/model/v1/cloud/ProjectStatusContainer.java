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

import com.intuit.tank.vm.settings.TimeUtil;

/**
 * BrokerStatus
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "projectStatusContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectStatusContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "reportTime",
        "jobId",
        "userDetails"
})
public class ProjectStatusContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "reportTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date reportTime = new Date();

    @XmlElement(name = "jobId", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String jobId;

    @XmlElementWrapper(name = "userDetails", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "userDetail", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private List<UserDetail> userDetails = null;

    @XmlTransient
    private Map<Date, List<UserDetail>> detailMap = new HashMap<Date, List<UserDetail>>();

    @XmlTransient
    private Map<String, CloudVmStatusContainer> containers = new HashMap<String, CloudVmStatusContainer>();

    /**
     * @FrameworkUseOnly
     */
    public ProjectStatusContainer() {
    }

    public void addStatusContainer(CloudVmStatusContainer container) {
        containers.put(container.getJobId(), container);
        calculateUserDetails();
    }

    public void calculateUserDetails() {
        List<UserDetail> details;
        Map<String, Integer> map = new HashMap<String, Integer>();
        long oldTime = System.currentTimeMillis() - 120000;// 2 minutes
        Set<String> toRemove = new HashSet<String>();
        for (CloudVmStatusContainer container : containers.values()) {
            if (container.getReportTime().getTime() < oldTime) {
                toRemove.add(container.getJobId());
            } else {
                for (UserDetail detail : container.getUserDetails()) {
                    Integer val = map.get(detail.getScript());
                    if (val == null) {
                        val = 0;
                    }
                    map.put(detail.getScript(), val + detail.getUsers());
                }
            }
        }
        details = map.entrySet().stream().map(entry -> new UserDetail(entry.getKey(), entry.getValue())).sorted().collect(Collectors.toList());
        userDetails = details;
        detailMap.put(TimeUtil.normalizeToPeriod(15, new Date()), details);
        for (String s : toRemove) {
            containers.remove(s);
        }
    }

    /**
     * @return the detailMap
     */
    public Map<Date, List<UserDetail>> getDetailMap() {
        return detailMap;
    }

    /**
     * @return the userDetails
     */
    public List<UserDetail> getUserDetails() {
        return userDetails;
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

}
