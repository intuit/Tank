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
import java.util.stream.Collectors;

import com.intuit.tank.vm.vmManager.models.VMStatus;
import org.apache.commons.lang3.time.FastDateFormat;

import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.project.JobInstance;

public class ActJobNodeBean extends JobNodeBean {

    private static final long serialVersionUID = 1L;
    private List<VMNodeBean> vmBeans = new ArrayList<VMNodeBean>();
    private String jobDetails;
    private String estimatedNonlinearSteadyStateUsers;

    public ActJobNodeBean(JobInstance job, boolean hasRights, FastDateFormat fmt) {
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
        this.setTargetRampRate(String.valueOf(job.getTargetRampRate() * job.getNumAgents()));
        this.setUseTwoStep(job.isUseTwoStep());
        this.jobDetails = job.getJobDetails();
        this.estimatedNonlinearSteadyStateUsers = estimateNonlinearSteadyStateUsers(job.getTargetRampRate(), job.getRampTime(), job.getNumAgents());
        this.setStartTime(job.getStartTime());
        this.setEndTime(job.getEndTime());
    }

    public ActJobNodeBean(String jobId, CloudVmStatusContainer container, FastDateFormat fmt) {
        super();
        this.setName(jobId);
        this.setJobId(jobId);
        this.setId(jobId);
        this.setReportMode("");
        this.setStatus(container.getStatus().name());
        this.setRegion("");
        this.setActiveUsers("");
        this.setTotalUsers("");
        this.setStartTime(container.getStartTime());
        this.setEndTime(container.getEndTime());
    }

    @Override
    public void reCalculate() {
        this.setTps(calculateTPS());
    }

    private int calculateTPS() {
        return vmBeans.stream().mapToInt(JobNodeBean::getTps).sum();
    }

    public String getJobDetails() {
        return jobDetails;
    }

    private String estimateNonlinearSteadyStateUsers(double targetRampRate, long rampTime, int numAgents) {
        return String.format("%.2f", targetRampRate * ((double) rampTime / 1000) * numAgents);
    }

    public String getNonlinearSteadyStateUsers() {
        return estimatedNonlinearSteadyStateUsers;
    }

    /**
     * {@inheritDoc}
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
     * @param vmBeans
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
    public List<VMNodeBean> getCurrentSubNodes() {
        // Filter out both terminated and replaced agents from the count
        // terminated = normal shutdown, replaced = watchdog replaced a failed agent
        return vmBeans.stream()
                .filter(vm -> !vm.getStatus().equals(VMStatus.terminated.toString())
                           && !vm.getStatus().equals(VMStatus.replaced.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getTotalSubNodesRunning() {
        return Long.toString(vmBeans.stream().filter(vm -> vm.getStatus().equals(VMStatus.running.toString())).count());
    }

    @Override
    public String getTotalSubNodesReady() {
        return Long.toString(vmBeans.stream().filter(vm -> vm.getStatus().equals(VMStatus.ready.toString())).count());
    }

    @Override
    public boolean allSubNodesCompleted(){
        return true; // terminated instances no longer sub nodes
    }

    @Override
    public boolean hasSubNodes() {
        return vmBeans.size() > 0;
    }

    @Override
    public boolean isKillable() {
        return JobStatusHelper.canBeKilled(getStatus());
    }

    @Override
    public boolean isStoppable() {
        return JobStatusHelper.canBeStopped(getStatus());
    }

    @Override
    public boolean isRunnable() {
        return JobStatusHelper.canBeRun(getStatus());
    }

    @Override
    public boolean isStartable() {
        return JobStatusHelper.canStartLoad(getStatus());
    }

    @Override
    public boolean isPauseable() {
        return JobStatusHelper.canBePaused(getStatus());
    }

    @Override
    public boolean isRampPauseable() {
        return JobStatusHelper.canRampBePaused(getStatus());
    }

    @Override
    public boolean isDeletable() { return JobStatusHelper.canBeDeleted(getStatus()); }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "job";
    }

}
