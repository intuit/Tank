/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vmManager;

/*
 * #%L
 * VmManager
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.project.VMInstance;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

/**
 * AgentWatchdog
 * 
 * @author dangleton
 * 
 */
public class AgentWatchdog implements Runnable {

    private static final Logger LOG = LogManager.getLogger(AgentWatchdog.class);

    private long sleepTime;
    private long maxWaitForStart;
    private long maxWaitForResponse;
    private int maxRestarts;
    private VMTracker vmTracker;
    private VMInstanceRequest instanceRequest;
    private List<VMInformation> vmInfo;
    private boolean stopped;
    private boolean checkForStart = true;
    private long startTime;
    private int restartCount;
    private int rebootCount;
    private AmazonInstance amazonInstance;

    /**
     * @param requestForAgents
     * @param vmTrackerImpl
     */
    public AgentWatchdog(VMInstanceRequest instanceRequest, List<VMInformation> vmInfo, VMTracker vmTracker) {
        this.instanceRequest = instanceRequest;
        this.vmInfo = vmInfo;
        this.vmTracker = vmTracker;
        this.startTime = System.currentTimeMillis();
        this.amazonInstance = new AmazonInstance(null, instanceRequest.getRegion());

        VmManagerConfig vmManagerConfig = new TankConfig().getVmManagerConfig();
        this.maxWaitForResponse = vmManagerConfig.getMaxAgentReportMills(1000 * 60 * 5);
        this.maxWaitForStart = vmManagerConfig.getMaxAgentStartMills(1000 * 60 * 3);
        this.maxRestarts = vmManagerConfig.getMaxRestarts(2);
        this.sleepTime = vmManagerConfig.getWatchdogSleepTime(30 * 1000);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sleepTime", sleepTime).append("maxWaitForStart", maxWaitForStart)
                .append("maxWaitForResponse", maxWaitForResponse)
                .append("maxRestarts", maxRestarts).toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void run() {
        LOG.info("Starting WatchDog: " + this.toString());
        try {
            List<VMInformation> instances = new ArrayList<VMInformation>(vmInfo);
            while (rebootCount <= maxRestarts && restartCount <= maxRestarts && !stopped) {
                if (!vmTracker.isRunning(instanceRequest.getJobId())) {
                    break;
                }
                if (checkForStart) {
                    LOG.info("Checking for " + instances.size() + " running agents...");
                    removeRunningInstances(instances);
                    if (!instances.isEmpty()) {
                        if (shouldRelaunchInstances()) {
                            relaunch(instances);
                        } else {
                            LOG.info("Waiting for " + instances.size() + " agents to start: "
                                    + getInstanceIdList(instances));
                        }
                        Thread.sleep(sleepTime);
                        continue;
                    } else {
                        LOG.info("All Agents Started.");
                        vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), "All Agents Started.",
                                JobLifecycleEvent.AGENT_STARTED));
                        checkForStart = false;
                        startTime = System.currentTimeMillis();
                    }
                }
                // all instances are now started
                instances = new ArrayList<VMInformation>(vmInfo);
                String jobId = instanceRequest.getJobId();
                // check to see if all agents have reported back
                LOG.info("Checking for " + instances.size() + " reporting agents...");
                removeReportingInstances(jobId, instances);
                if (!instances.isEmpty()) {
                    if (shouldRebootInstances()) {
                        reboot(instances);
                    } else {
                        LOG.info("Waiting for " + instances.size() + " agents to report: "
                                + getInstanceIdList(instances));
                    }
                    Thread.sleep(sleepTime);
                    continue;
                } else {
                    LOG.info("All Agents Reported back.");
                    vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(),
                            "All Agents Reported Back and are ready to start load.", JobLifecycleEvent.AGENT_REPORTED));
                    stopped = true;
                }

            }
        } catch (Exception e) {
            LOG.error("Error in Watchdog: " + e.toString(), e);
        }
        LOG.info("Exiting Watchdog " + this.toString());

    }

    /**
     * @param instances
     * 
     */
    private void reboot(List<VMInformation> instances) {
        rebootCount++;
        if (rebootCount <= maxRestarts) {

            String msg = "Have " + instances.size()
                    + " agents that started but failed to report status correctly. rebooting "
                    + getInstanceIdList(instances);
            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.AGENT_RESTARTED));
            LOG.info(msg);
            startTime = System.currentTimeMillis();
            amazonInstance.reboot(instances);
            checkForStart = true;
        } else {
            stopped = true;
            String msg = "Have "
                    + instances.size()
                    + " agents that failed to report correctly and have exceeded the maximum number of restarts. Killing job.";
            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.JOB_KILLED));
            LOG.info(msg);
            killJob();
        }
    }

    /**
     * @param instances
     * @return
     */
    private String getInstanceIdList(List<VMInformation> instances) {
        return StringUtils.join(instances, ", ");
    }

    /**
     * @param instances
     */
    private void removeReportingInstances(String jobId, List<VMInformation> instances) {
        CloudVmStatusContainer vmStatusForJob = vmTracker.getVmStatusForJob(jobId);
        if (vmStatusForJob != null && vmStatusForJob.getEndTime() == null) {
            for (CloudVmStatus status : vmStatusForJob.getStatuses()) {
                if (status.getVmStatus() == VMStatus.running
                        || (status.getJobStatus() != JobStatus.Unknown && status.getJobStatus() != JobStatus.Starting)) {
                    removeInstance(status.getInstanceId(), instances);
                }
            }
        } else {
            stopped = true;
            throw new RuntimeException("Job appears to have been stopped. Exiting...");
        }
    }

    /**
     * @param instances
     * 
     */
    private void relaunch(List<VMInformation> instances) {
        restartCount++;
        if (restartCount <= maxRestarts) {
            startTime = System.currentTimeMillis();
            String msg = "Have " + instances.size() + " agents that failed to start correctly. Restarting "
                    + getInstanceIdList(instances);
            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.AGENT_REBOOTED));
            LOG.info(msg);
            // relaunch instances and remove old onesn from vmTracker
            // kill them first just to be sure
            amazonInstance.killInstances(instances);
            VMImageDao dao = new VMImageDao();
            for (VMInformation info : instances) {
                vmInfo.remove(info);
                vmTracker.setStatus(createTerminatedVmStatus(info));
                VMInstance image = dao.getImageByInstanceId(info.getInstanceId());
                if (image != null) {
                    image.setStatus(VMStatus.terminated.name());
                    dao.saveOrUpdate(image);
                }
            }
            instanceRequest.setNumberOfInstances(instances.size());
            List<VMInformation> newVms = new AmazonInstance(instanceRequest, instanceRequest.getRegion()).create();
            instances.clear();
            for (VMInformation newInfo : newVms) {
                vmInfo.add(newInfo);
                instances.add(newInfo);
                vmTracker.setStatus(createCloudStatus(instanceRequest, newInfo));
                LOG.info("Added image (" + newInfo.getInstanceId() + ") to VMImage table");
                try {
                    new VMImageDao().addImageFromInfo(instanceRequest.getJobId(), newInfo,
                            instanceRequest.getRegion());
                } catch (Exception e) {
                    LOG.warn("Error persisting VM Image: " + e);
                }
            }
        } else {
            stopped = true;
            String msg = "Have "
                    + instances.size()
                    + " agents that failed to start correctly and have exceeded the maximum number of restarts. Killing job.";
            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.JOB_ABORTED));
            LOG.info(msg);
            killJob();
        }
    }

    /**
     * 
     */
    private void killJob() {
        throw new RuntimeException("Killing jobs and exiting");
    }

    /**
     * @param request2
     * @param info
     * @return
     */
    private CloudVmStatus createCloudStatus(VMInstanceRequest req, VMInformation info) {
        CloudVmStatus ret = new CloudVmStatus(info.getInstanceId(), req.getJobId(),
                req.getInstanceDescription() != null ? req.getInstanceDescription().getSecurityGroup() : "unknown",
                JobStatus.Starting,
                VMImageType.AGENT, req.getRegion(), VMStatus.pending, new ValidationStatus(), 0, 0, null, null);
        return ret;
    }

    /**
     * @param request2
     * @param info
     * @return
     */
    private CloudVmStatus createTerminatedVmStatus(VMInformation info) {
        LOG.info(info);
        LOG.info(instanceRequest);
        CloudVmStatus ret = new CloudVmStatus(info.getInstanceId(), instanceRequest.getJobId(), "unknown",
                JobStatus.Stopped, VMImageType.AGENT, instanceRequest.getRegion(),
                VMStatus.terminated, new ValidationStatus(), 0, 0, null, null);
        return ret;
    }

    /**
     * @return
     */
    private boolean shouldRelaunchInstances() {
        return startTime + maxWaitForStart < System.currentTimeMillis();
    }

    /**
     * @return
     */
    private boolean shouldRebootInstances() {
        return startTime + maxWaitForResponse < System.currentTimeMillis();
    }

    /**
     * 
     */
    private void removeRunningInstances(List<VMInformation> instances) {
        List<String> instanceIds = new ArrayList<String>();
        CloudVmStatusContainer vmStatusForJob = vmTracker.getVmStatusForJob(instanceRequest.getJobId());
        if (shouldRelaunchInstances() && (vmStatusForJob == null || vmStatusForJob.getEndTime() != null)) {
            stopped = true;
            throw new RuntimeException("Job appears to have been stopped. Exiting...");
        }
        for (VMInformation info : instances) {
            instanceIds.add(info.getInstanceId());
        }
        List<VMInformation> foundInstances = amazonInstance.describeInstances(instanceIds
                .toArray(new String[instanceIds.size()]));
        for (VMInformation info : foundInstances) {
            if ("running".equalsIgnoreCase(info.getState())) {
                removeInstance(info.getInstanceId(), instances);
            }
        }

    }

    /**
     * @param info
     * @param instances
     */
    private void removeInstance(String foundInstanceId, List<VMInformation> instances) {
        for (int i = instances.size(); --i >= 0;) {// count down loop so no concurrent modification
            if (foundInstanceId.equals(instances.get(i).getInstanceId())) {
                instances.remove(i);
            }
        }

    }
}
