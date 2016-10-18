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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.vm.common.TankConstants;

public class VMNodeBean extends JobNodeBean {

    private static final long serialVersionUID = 1L;

    public VMNodeBean(CloudVmStatus vmStatus, boolean hasRights, FastDateFormat fmt) {
        super();
        this.setHasRights(hasRights);
        this.setName("Agent");
        this.setJobId(vmStatus.getJobId());
        this.setId(vmStatus.getInstanceId());
        this.setReportMode("");
        this.setStatus(vmStatus.getJobStatus().toString());
        this.setRegion(vmStatus.getVmRegion().toString());
        this.setActiveUsers(String.valueOf(vmStatus.getCurrentUsers()));
        this.setNumFailures(vmStatus.getValidationFailures());
        this.setUserDetails(vmStatus.getUserDetails());
        this.setTotalUsers(String.valueOf(vmStatus.getTotalUsers()));
        setTps(vmStatus.getTotalTps());

        if (vmStatus.getStartTime() != null) {
            this.setStartTime(fmt.format(vmStatus.getStartTime()));
        } else {
            this.setStartTime("");
        }

        if (vmStatus.getEndTime() != null) {
            this.setEndTime(fmt.format(vmStatus.getEndTime()));
        } else {
            this.setEndTime("");
        }

    }

    @Override
    public void reCalculate() {

    }

    // private int calculateTPS(CloudVmStatus vmStatus) {
    // int ret = 0;
    // if (vmStatus != null && vmStatus.getTpsInfo() != null) {
    // Map<Date, Map<String, TPSInfo>> tpsInfoMap = new HashMap<Date, Map<String,TPSInfo>>();
    // Date maxTime = vmStatus.getTpsInfo().getMaxTime();
    // // don't accept any data from older than 60 minutes
    // int transactionCount = 0;
    // for (TPSInfo info : vmStatus.getTpsInfo().getTpsInfos()) {
    // Map<String, TPSInfo> map = tpsInfoMap.get(info.getTimestamp());
    // if (map == null) {
    // map = new HashMap<String, TPSInfo>();
    // tpsInfoMap.put(info.getTimestamp(), map);
    // }
    // map.put(info.getKey(), info);
    // if (info.getTimestamp().equals(maxTime)) {
    // transactionCount += info.getTransactions();
    // }
    // }
    // ret = Math.round(transactionCount / vmStatus.getTpsInfo().getPeriod());
    // setTpsDetailMap(tpsInfoMap);
    // }
    // return ret;
    // }

    @Override
    public List<JobNodeBean> getSubNodes() {
        return new ArrayList<JobNodeBean>();
    }

    @Override
    public boolean hasSubNodes() {
        return false;
    }

    @Override
    public boolean isKillable() {
        return JobStatusHelper.canBeKilled(getStatus());
    }

    @Override
    public boolean isStopable() {
        return JobStatusHelper.canBeStopped(getStatus());
    }

    @Override
    public boolean isRunnable() {
        return JobStatusHelper.canBeRun(getStatus());
    }

    @Override
    public boolean isPausable() {
        return JobStatusHelper.canBePaused(getStatus());
    }

    @Override
    public boolean isRampPausable() {
        return JobStatusHelper.canRampBePaused(getStatus());
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getType() {
        return "vm";
    }

}
