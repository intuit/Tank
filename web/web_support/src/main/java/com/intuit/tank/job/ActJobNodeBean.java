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

import org.apache.commons.lang.time.DateFormatUtils;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.persistence.databases.DataBaseFactory;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;

public class ActJobNodeBean extends JobNodeBean {

    private static final long serialVersionUID = 1L;
    private List<VMNodeBean> vmBeans = new ArrayList<VMNodeBean>();
    private String jobDetails;

    public ActJobNodeBean(JobInstance job, boolean hasRights) {
        super();
        this.setHasRights(hasRights);
        this.setName(job.getName());
        this.setJobId(String.valueOf(job.getId()));
        this.setId(String.valueOf(job.getId()));
        this.setReportMode(job.getReportingMode().toString());
        this.setStatus(job.getStatus().toString());
        this.setRegion("");
        this.setActiveUsers(String.valueOf(job.getBaselineVirtualUsers()));
        this.setTotalUsers(String.valueOf(job.getTotalVirtualUsers()));
        this.jobDetails = job.getJobDetails();

        if (job.getStartTime() != null) {
            this.setStartTime(DateFormatUtils.format(job.getStartTime(), TankConstants.DATE_FORMAT));
        } else {
            this.setStartTime("");
        }

        if (job.getEndTime() != null) {
            this.setEndTime(DateFormatUtils.format(job.getEndTime(), TankConstants.DATE_FORMAT));
        } else {
            this.setEndTime("");
        }

    }

    public ActJobNodeBean(String jobId, CloudVmStatusContainer container) {
        super();
        this.setName(jobId);
        this.setJobId(jobId);
        this.setId(jobId);
        this.setReportMode("");
        this.setStatus(container.getStatus().name());
        this.setRegion("");
        this.setActiveUsers("");
        this.setTotalUsers("");

        if (container.getStartTime() != null) {
            this.setStartTime(DateFormatUtils.format(container.getStartTime(), TankConstants.DATE_FORMAT));
        } else {
            this.setStartTime("");
        }

        if (container.getEndTime() != null) {
            this.setEndTime(DateFormatUtils.format(container.getEndTime(), TankConstants.DATE_FORMAT));
        } else {
            this.setEndTime("");
        }
    }

    @Override
    public void reCalculate() {
        this.setTps(calculateTPS());
    }

    private int calculateTPS() {
        int ret = 0;
        for (VMNodeBean bean : vmBeans) {
            ret += bean.getTps();
        }
        return ret;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public boolean isJobNode() {
        return true;
    }

    /**
     * @return the jobBeans
     */
    public List<VMNodeBean> getVmBeans() {
        return vmBeans;
    }

    /**
     * @param jobBeans
     *            the jobBeans to set
     */
    public void setVmBeans(List<VMNodeBean> vmBeans) {
        this.vmBeans = vmBeans;
    }

    /**
     * Adds a vmBean to the jobNode
     * 
     * @param vmNode
     *            Node to be added.
     */
    public void addVMBean(VMNodeBean vmNode) {
        getVmBeans().add(vmNode);
    }

    @Override
    public List<VMNodeBean> getSubNodes() {
        return vmBeans;
    }

    @Override
    public boolean hasSubNodes() {
        if (vmBeans.size() > 0) {
            return true;
        }
        else {
            return false;
        }
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
        return "job";
    }

}
