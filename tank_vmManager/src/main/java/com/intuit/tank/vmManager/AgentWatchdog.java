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
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.logging.ControllerLoggingConfig;
import com.intuit.tank.vm.vmManager.VMTracker;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
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
import org.apache.logging.log4j.message.ObjectMessage;

/**
 * AgentWatchdog
 * 
 * @author dangleton
 * 
 */
public class AgentWatchdog implements Runnable {

    private static final Logger LOG = LogManager.getLogger(AgentWatchdog.class);
    private static final VmManagerConfig vmManagerConfig = new TankConfig().getVmManagerConfig();

    private long sleepTime;
    private long maxWaitForResponse;
    private int maxRestarts;
    private VMTracker vmTracker;
    private VMInstanceRequest instanceRequest;
    private List<VMInformation> vmInfo;
    private ArrayList<VMInformation> startedInstances;
    private ArrayList<VMInformation> reportedInstances;
    private boolean stopped;
    private long startTime;
    private int restartCount;
    private AmazonInstance amazonInstance;
    private int expectedInstanceCount;

    /**
     * Constructor
     *
     * @param instanceRequest
     * @param vmInfo
     * @param vmTracker
     */
    public AgentWatchdog(VMInstanceRequest instanceRequest, List<VMInformation> vmInfo, VMTracker vmTracker) {
        this(instanceRequest, vmInfo, vmTracker,
                new AmazonInstance(instanceRequest.getRegion()),
                vmManagerConfig.getWatchdogSleepTime(30 * 1000),  // 30 seconds
                vmManagerConfig.getMaxAgentReportMills(1000 * 60 * 3) // 3 minutes
        );
    }

    /**
     * Constructor
     *
     * @param instanceRequest
     * @param vmInfo
     * @param vmTracker
     * @param amazonInstance
     * @param maxWaitForResponse
     */
    public AgentWatchdog(VMInstanceRequest instanceRequest, List<VMInformation> vmInfo, VMTracker vmTracker, AmazonInstance amazonInstance, long sleepTime, long maxWaitForResponse) {
        this.instanceRequest = instanceRequest;
        this.vmInfo = vmInfo;
        this.vmTracker = vmTracker;
        this.startTime = System.currentTimeMillis();
        this.amazonInstance = amazonInstance;

        VmManagerConfig vmManagerConfig = new TankConfig().getVmManagerConfig();
        this.maxWaitForResponse = vmManagerConfig.getMaxAgentReportMills(1000 * 60 * 3); // 3 minutes
        this.maxRestarts = vmManagerConfig.getMaxRestarts(3);
        this.sleepTime = sleepTime;
        this.expectedInstanceCount = vmInfo.size();

        LOG.info(new ObjectMessage(Map.of("Message","AgentWatchdog settings: { "
                + "maxWaitForResponse: " + maxWaitForResponse
                + ", maxRestarts: " + maxRestarts
                + ", sleepTime: " + sleepTime + " }")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sleepTime", sleepTime)
                .append("maxWaitForResponse", maxWaitForResponse)
                .append("maxRestarts", maxRestarts).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        ControllerLoggingConfig.setupThreadContext();
        String jobId = instanceRequest.getJobId();
        LOG.info(new ObjectMessage(Map.of("Message","Starting WatchDog: " + this.toString() + " for job " + jobId)));
        AWSXRay.getGlobalRecorder().beginNoOpSegment(); //jdbcInterceptor will throw SegmentNotFoundException,RuntimeException without this
        try {
            startedInstances = new ArrayList<VMInformation>(vmInfo);
            reportedInstances = new ArrayList<VMInformation>();
            while (restartCount <= maxRestarts && !stopped) {
                checkForReportingInstances();
                LOG.info(new ObjectMessage(Map.of("Message","Job " + jobId + ": " + startedInstances.size() + " instances started. " + reportedInstances.size() + " instances reported. Waiting for remaining " + startedInstances.size() + " agents to report back...")));
                // When runningInstances is empty all instances have reported back.
                // If not, check if its time for a restart.
                if (!startedInstances.isEmpty()) {
                    if (shouldRelaunchInstances()) {
                        relaunch(startedInstances);
                        startTime = System.currentTimeMillis();
                    }
                    LOG.info(new ObjectMessage(Map.of("Message","Job " + jobId + ": " + "Waiting for " + startedInstances.size() + " agents to report: "
                            + getInstanceIdList(startedInstances))));
                    Thread.sleep(sleepTime);
                } else {
                    LOG.info(new ObjectMessage(Map.of("Message","All Agents Reported back for job " + jobId + ".")));
                    vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(),
                            "All Agents Reported Back and are ready to start load.", JobLifecycleEvent.AGENT_REPORTED));
                    stopped = true;
                }
            }
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message","Error in Watchdog: " + e.toString())), e);
            // TODO Terminate all instances
        } finally {
            LOG.info(new ObjectMessage(Map.of("Message","Exiting Watchdog " + this.toString())));
            AWSXRay.endSegment();
        }
    }

    /**
     * Find instances that have reported and remove from started instances and add to reported instances.
     */
    private void checkForReportingInstances() {
        String jobId = instanceRequest.getJobId();
        CloudVmStatusContainer vmStatusForJob = vmTracker.getVmStatusForJob(jobId);
        if (vmStatusForJob == null || vmStatusForJob.getEndTime() != null) {
            stopped = true;
            throw new RuntimeException("Job appears to have been stopped. Exiting...");
        }
        for (CloudVmStatus status : vmStatusForJob.getStatuses()) {
            // Checks the state of Tank job.
            if (status.getVmStatus().equals(VMStatus.pending)) { // agent reported back ready, only relaunch "starting" agents
                VMInformation removedInstance = removeInstance(startedInstances, status.getInstanceId());
                if (removedInstance != null) {
                    addInstance(reportedInstances, removedInstance);
                }
            }
        }
    }

    private String getInstanceIdList(List<VMInformation> instances) {
        return StringUtils.join(instances, ", ");
    }

    /**
     * Relaunch all passed instances.
     * Kill instances first then create a new request to start.  Instances are added to started instances list.
     * @param instances
     */
    private void relaunch(ArrayList<VMInformation> instances) {
        restartCount++;
        String jobId = instanceRequest.getJobId();
        LOG.info(new ObjectMessage(Map.of("Message","Restart Count for job " + jobId + ": " + restartCount)));
        if (restartCount > maxRestarts) {
            stopped = true;
            String msg = "Have "
                    + this.startedInstances.size()
                    + " agents that failed to start correctly and have exceeded the maximum number of restarts. Killing job.";
            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.JOB_ABORTED));
            LOG.info(new ObjectMessage(Map.of("Message", msg)));
            // TODO Do we have to kill jobs here?
            throw new RuntimeException("Killing jobs and exiting");
        }
        String msg = "Have " + instances.size() + " agents that failed to start or report correctly for job " + jobId + ". Relaunching. "
                + getInstanceIdList(instances);
        vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.AGENT_REBOOTED));
        LOG.info(new ObjectMessage(Map.of("Message", msg)));
        // Kill instances first
        List<String> instanceIds = instances.stream()
                .map(VMInformation::getInstanceId).collect(Collectors.toCollection(() -> new ArrayList<>(instances.size())));
        amazonInstance.killInstances(instanceIds);
        // Set terminated status on the DAO
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
        LOG.info(new ObjectMessage(Map.of("Message","Setting number of instances to relaunch to: " + instances.size() + " for job " + jobId)));
        instanceRequest.setNumberOfInstances(instances.size());
        instances.clear();
        // Create and send instance start request
        List<VMInformation> newVms = amazonInstance.create(instanceRequest);
        // Add new instances
        for (VMInformation newInfo : newVms) {
            vmInfo.add(newInfo);
            // Add directly to started instances since these are restarted from scratch
            startedInstances.add(newInfo);
            vmTracker.setStatus(createCloudStatus(instanceRequest, newInfo));
            LOG.info(new ObjectMessage(Map.of(
                "Message", "Added relaunched image to VMImage table for job " + jobId,
                "instanceId", newInfo.getInstanceId(),
                "publicIp", newInfo.getPublicIp() != null ? newInfo.getPublicIp() : "N/A",
                "privateIp", newInfo.getPrivateIp() != null ? newInfo.getPrivateIp() : "N/A"
            )));
            try {
                dao.addImageFromInfo(instanceRequest.getJobId(), newInfo,
                        instanceRequest.getRegion());
            } catch (Exception e) {
                LOG.warn("Error persisting VM Image: " + e);
            }
        }
        LOG.info(new ObjectMessage(Map.of("Message","At end of relaunch for job " + jobId
                + " startedInstances: " + startedInstances.size()
                + " reportedInstances: " + reportedInstances.size())));
    }

    /**
     * @param req
     * @param info
     * @return
     */
    private CloudVmStatus createCloudStatus(VMInstanceRequest req, VMInformation info) {
        return new CloudVmStatus(info.getInstanceId(), req.getJobId(),
                req.getInstanceDescription() != null ? req.getInstanceDescription().getSecurityGroup() : "unknown",
                JobStatus.Starting,
                VMImageType.AGENT, req.getRegion(), VMStatus.pending, new ValidationStatus(), 0, 0, null, null);
    }

    private CloudVmStatus createTerminatedVmStatus(VMInformation info) {
        return new CloudVmStatus(info.getInstanceId(), instanceRequest.getJobId(), "unknown",
                JobStatus.Stopped, VMImageType.AGENT, instanceRequest.getRegion(),
                VMStatus.terminated, new ValidationStatus(), 0, 0, null, null);
    }

    private boolean shouldRelaunchInstances() {
        return startTime + maxWaitForResponse < System.currentTimeMillis();
    }

    private static void addInstance(List<VMInformation> instances, VMInformation info) {
        instances.add(info);
    }

    private static VMInformation removeInstance(List<VMInformation> instances, String instanceIdToDelete) {
        VMInformation instanceRemoved = null;
        for (int i = instances.size(); --i >= 0;) {// count down loop so no concurrent modification
            if (instanceIdToDelete.equals(instances.get(i).getInstanceId())) {
                instanceRemoved = instances.get(i);
                instances.remove(i);
            }
        }
        return instanceRemoved;
    }
}
