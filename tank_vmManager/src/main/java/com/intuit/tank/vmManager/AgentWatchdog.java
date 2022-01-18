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
import java.util.stream.Collectors;

import com.amazonaws.xray.AWSXRay;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final VmManagerConfig vmManagerConfig = new TankConfig().getVmManagerConfig();

    private long sleepTime;
    private long maxWaitForStart;
    private long maxWaitForResponse;
    private int maxRestarts;
    private VMTracker vmTracker;
    private VMInstanceRequest instanceRequest;
    private List<VMInformation> vmInfo;
    private ArrayList<VMInformation> startedInstances;
    private ArrayList<VMInformation> runningInstances;
    private ArrayList<VMInformation> reportedInstances;
    private boolean stopped;
    private long startTime;
    private int restartCount;
    private int rebootCount;
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
        this.maxWaitForResponse = maxWaitForResponse;
        this.maxWaitForStart = 1000 * 60 * 3; //vmManagerConfig.getMaxAgentStartMills(1000 * 60 * 3);     // 3 minutes
        this.maxRestarts = 20;
        this.sleepTime = sleepTime;
        this.expectedInstanceCount = vmInfo.size();

        LOG.info("AgentWatchdog settings: "
                + "\nmaxWaitForResponse: " + maxWaitForResponse
                + "\nmaxWaitForStart: " + maxWaitForStart
                + "\nmaxRestarts: " + maxRestarts
                + "\nsleepTime: " + sleepTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sleepTime", sleepTime)
                .append("maxWaitForStart", maxWaitForStart)
                .append("maxWaitForResponse", maxWaitForResponse)
                .append("maxRestarts", maxRestarts).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOG.info("Starting WatchDog: " + this.toString());
        AWSXRay.getGlobalRecorder().beginNoOpSegment(); //jdbcInterceptor will throw SegmentNotFoundException,RuntimeException without this
        try {
            startedInstances = new ArrayList<VMInformation>(vmInfo);
            runningInstances = new ArrayList<VMInformation>();
            reportedInstances = new ArrayList<VMInformation>();
            while (restartCount <= maxRestarts && !stopped) {
//                if (!vmTracker.isRunning(instanceRequest.getJobId())) {
//                    LOG.info("vmTracker is running.  Breaking from loop.");
//                    break;
//                }
                // First check for running instances
                checkForRunningInstances();
                LOG.info(runningInstances.size() + " instances running. " + reportedInstances.size() + " instances reported. Waiting for remaining " + startedInstances.size() + " agents to start running.");
                // When started instances is empty all instances have started.
                // If not, check if we should restart.
                if (!startedInstances.isEmpty()) {
                    if (shouldRelaunchInstances()) {
                        relaunch(startedInstances);
                        startTime = System.currentTimeMillis();
                    }
                    LOG.info("Waiting for " + startedInstances.size() + " agents to start: "
                            + getInstanceIdList(startedInstances));
                    Thread.sleep(sleepTime);
                    continue;
                } else {
                    LOG.info("All Agents Running.");
                    vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), "All Agents Started.",
                            JobLifecycleEvent.AGENT_STARTED));
                }
                // Now check for reporting instances
                checkForReportingInstances();
                LOG.info(runningInstances.size() + " instances running. " + reportedInstances.size() + " instances reported. Waiting for remaining " + runningInstances.size() + " agents to report back...");
                // When runningInstances is empty all instances have reported back.
                // If not, check if its time for a restart.
                LOG.info( reportedInstances.size() + "   " + expectedInstanceCount); // TODO delete
                if (!runningInstances.isEmpty() && reportedInstances.size() < expectedInstanceCount) {
                    if (shouldRebootInstances()) {
                        relaunch(runningInstances);
                        startTime = System.currentTimeMillis();
                    }
                    LOG.info("Waiting for " + runningInstances.size() + " agents to report: "
                            + getInstanceIdList(runningInstances));
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
            // TODO Terminate all instances
        } finally {
            LOG.info("Exiting Watchdog " + this.toString());
            AWSXRay.endSegment();
        }
    }

    private void checkForRunningInstances() {
        CloudVmStatusContainer vmStatusForJob = vmTracker.getVmStatusForJob(instanceRequest.getJobId());
        // First check if the job is stopped.
        if (vmStatusForJob == null || vmStatusForJob.getEndTime() != null) {
            stopped = true;
            throw new RuntimeException("Job appears to have been stopped. Exiting...");
        }
        // Then collect the started instances and check for those in "running" status.
        // Remove them from startedInstances and add to runningInstances.
        List<VMInformation> foundInstances = amazonInstance.describeInstances(startedInstances.stream().map(VMInformation::getInstanceId).toArray(String[]::new));
        for (VMInformation info : foundInstances) {
            LOG.info("checking info.getState: " + info.getState());
            // TODO Does this: Checks the state of the machine.
            if ("running".equalsIgnoreCase(info.getState())) {
                removeInstance(startedInstances, info.getInstanceId());
                addInstance(runningInstances, info);
            }
        }
    }

    private void checkForReportingInstances() {
        String jobId = instanceRequest.getJobId();
        CloudVmStatusContainer vmStatusForJob = vmTracker.getVmStatusForJob(jobId);
        if (vmStatusForJob == null || vmStatusForJob.getEndTime() != null) {
            stopped = true;
            throw new RuntimeException("Job appears to have been stopped. Exiting...");
        }
        for (CloudVmStatus status : vmStatusForJob.getStatuses()) {
            LOG.info("checking vmstatus: " + status.getVmStatus());
            // TODO Does this: Checks the state of Tank job.
            if (status.getVmStatus() == VMStatus.running) {
//                    || (status.getJobStatus() != JobStatus.Unknown && status.getJobStatus() != JobStatus.Starting)) {
                VMInformation removedInstance = removeInstance(runningInstances, status.getInstanceId());
                addInstance(reportedInstances, removedInstance);
            }
        }
    }

//    private void reboot(List<VMInformation> instances) {
//        rebootCount++;
//        if (rebootCount <= maxRestarts) {
//
//            String msg = "Have " + instances.size()
//                    + " agents that started but failed to report status correctly. Rebooting. "
//                    + getInstanceIdList(instances);
//            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.AGENT_RESTARTED));
//            LOG.info(msg);
//            startTime = System.currentTimeMillis();
//            amazonInstance.reboot(instances);
//            checkForStart = true;
//        } else {
//            stopped = true;
//            String msg = "Have "
//                    + instances.size()
//                    + " agents that failed to report correctly and have exceeded the maximum number of restarts. Killing job.";
//            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.JOB_KILLED));
//            LOG.info(msg);
//            killJob();
//        }
//    }

    private String getInstanceIdList(List<VMInformation> instances) {
        return StringUtils.join(instances, ", ");
    }

    private void relaunch(ArrayList<VMInformation> instances) {
        restartCount++;
        if (restartCount > maxRestarts) {
            stopped = true;
            String msg = "Have "
                    + this.startedInstances.size()
                    + " agents that failed to start correctly and have exceeded the maximum number of restarts. Killing job.";
            vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.JOB_ABORTED));
            LOG.info(msg);
            // TODO Do we have to kill jobs here?
            throw new RuntimeException("Killing jobs and exiting");
        }
        String msg = "Have " + instances.size() + " agents that failed to start or report correctly. Relaunching. "
                + getInstanceIdList(instances);
        vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.AGENT_REBOOTED));
        LOG.info(msg);
        // Relaunch instances and remove old ones from vmTracker
        // Kill instances first
        List<String> instanceIds = instances.stream()
                .map(VMInformation::getInstanceId).collect(Collectors.toCollection(() -> new ArrayList<>(instances.size())));
        amazonInstance.killInstances(instanceIds);
        // Set terminated status on the DAO
        VMImageDao dao = new VMImageDao();
        for (VMInformation info : instances) {
            vmInfo.remove(info); // TODO Remove if line 264 is removed
            vmTracker.setStatus(createTerminatedVmStatus(info));
            VMInstance image = dao.getImageByInstanceId(info.getInstanceId());
            if (image != null) {
                image.setStatus(VMStatus.terminated.name());
                dao.saveOrUpdate(image);
            }
        }
        LOG.info("Setting number of instances to relaunch to: " + instances.size());
        // Set instance request to have same number of instances as those not started/running
        instanceRequest.setNumberOfInstances(instances.size());
        // Delete all the instances present that have been terminated
        instances.clear();
        // Create and send instance start request
        List<VMInformation> newVms = amazonInstance.create(instanceRequest);
        // Add new instances
        for (VMInformation newInfo : newVms) {
            vmInfo.add(newInfo); // TODO Does this update the gui or is it safe to delete?
            // Add directly to started instances since these are restarted from scratch
            startedInstances.add(newInfo);
            vmTracker.setStatus(createCloudStatus(instanceRequest, newInfo));
            LOG.info("Added image (" + newInfo.getInstanceId() + ") to VMImage table");
            try {
                dao.addImageFromInfo(instanceRequest.getJobId(), newInfo,
                        instanceRequest.getRegion());
            } catch (Exception e) {
                LOG.warn("Error persisting VM Image: " + e);
            }
        }
        LOG.info("At end of relaunch"
                + "\nstartedInstances: " + startedInstances.size()
                + "\nrunningInstances" + runningInstances.size()
                + "\nreportedInstances: " + reportedInstances.size());
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

    /**
     * @param info
     * @return
     */
    private CloudVmStatus createTerminatedVmStatus(VMInformation info) {
//        LOG.info(info);
//        LOG.info(instanceRequest);
        return new CloudVmStatus(info.getInstanceId(), instanceRequest.getJobId(), "unknown",
                JobStatus.Stopped, VMImageType.AGENT, instanceRequest.getRegion(),
                VMStatus.terminated, new ValidationStatus(), 0, 0, null, null);
    }

    private boolean shouldRelaunchInstances() {
        return startTime + maxWaitForStart < System.currentTimeMillis();
    }

    private boolean shouldRebootInstances() {
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
