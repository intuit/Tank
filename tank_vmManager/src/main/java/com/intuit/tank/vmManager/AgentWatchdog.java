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
import java.util.Date;
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
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.VMInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
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
        
        // Container might not exist yet if setStatus() async tasks haven't completed
        // OR it could be null because the job was killed/removed externally
        if (vmStatusForJob == null) {
            // Check if job still exists and is active - if not, it was killed externally
            JobInstanceDao dao = new JobInstanceDao();
            JobInstance job = dao.findById(Integer.parseInt(jobId));
            if (job == null || job.getStatus() == JobQueueStatus.Completed
                    || job.getStatus() == JobQueueStatus.Aborted) {
                stopped = true;
                throw new RuntimeException("Job " + jobId + " was stopped or does not exist. Exiting watchdog.");
            }
            // Job exists and is active - container just not yet created (async race)
            LOG.debug(new ObjectMessage(Map.of("Message", 
                "Job container not yet created for job " + jobId + " - waiting for async status updates")));
            return;  // Return and check again on next iteration
        }
        
        // Only treat as stopped if container exists AND has an end time (user/system stopped the job)
        if (vmStatusForJob.getEndTime() != null) {
            stopped = true;
            throw new RuntimeException("Job appears to have been stopped. Exiting...");
        }
        for (CloudVmStatus status : vmStatusForJob.getStatuses()) {
            // Checks the state of Tank job.
            if (status.getVmStatus().equals(VMStatus.pending)) { // agent reported back ready, only relaunch "starting" agents
                VMInformation removedInstance = removeInstance(startedInstances, status.getInstanceId());
                if (removedInstance != null) {
                    addInstance(reportedInstances, removedInstance);
                    long startupTimeMs = System.currentTimeMillis() - startTime;
                    LOG.info(new ObjectMessage(Map.of(
                        "Message", "Agent reported ready",
                        "instanceId", status.getInstanceId(),
                        "jobId", jobId,
                        "startupTimeMs", startupTimeMs,
                        "startupTimeSec", startupTimeMs / 1000.0,
                        "remainingToReport", startedInstances.size())));
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
            LOG.info(new ObjectMessage(Map.of("Message", msg)));
            
            // kill the job directly
            killJobDirectly(jobId);
            
            // fire event for logging/notification only (observers would need to be changed to RequestScoped)
            try {
                vmTracker.publishEvent(new JobEvent(instanceRequest.getJobId(), msg, JobLifecycleEvent.JOB_ABORTED));
            } catch (Exception e) {
                LOG.warn("Failed to publish JOB_ABORTED event (non-critical): " + e.getMessage());
            }
            
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
        // Create defensive copy - instances IS startedInstances (same reference),
        // and we modify startedInstances inside the loop via removeInstance()
        List<VMInformation> instancesToProcess = new ArrayList<>(instances);
        for (VMInformation info : instancesToProcess) {
            vmInfo.remove(info);
            
            // Remove from all tracking lists FIRST - this prevents stale heartbeats from being processed
            // for this instance after we mark it as replaced (no need to modify shouldUpdateStatus)
            removeInstance(startedInstances, info.getInstanceId());
            removeInstance(reportedInstances, info.getInstanceId());

            // NOW mark as replaced - safe because instance is removed from tracking
            CloudVmStatus replacedStatus = vmTracker.getStatus(info.getInstanceId());
            if (replacedStatus != null) {
                replacedStatus.setVmStatus(VMStatus.replaced);
                vmTracker.setStatus(replacedStatus);
            } else {
                // Create new replaced status if none exists (race condition protection)
                CloudVmStatus newReplacedStatus = new CloudVmStatus(
                    info.getInstanceId(), instanceRequest.getJobId(), "unknown",
                    JobStatus.Stopped, VMImageType.AGENT, instanceRequest.getRegion(),
                    VMStatus.replaced, new ValidationStatus(), 0, 0, null, null);
                vmTracker.setStatus(newReplacedStatus);
                LOG.warn(new ObjectMessage(Map.of("Message",
                    "Created new 'replaced' status for instance " + info.getInstanceId() +
                    " - original status was missing (possible race condition)")));
            }
            
            // Also update in the database for persistence
            VMInstance image = dao.getImageByInstanceId(info.getInstanceId());
            if (image != null) {
                image.setStatus(VMStatus.replaced.name());  // mark as replaced (not terminated) for audit trail
                dao.saveOrUpdate(image);
            }

            LOG.info(new ObjectMessage(Map.of("Message",
                "Marked instance " + info.getInstanceId() +
                " as REPLACED (visible in UI but filtered from job status) for job " + jobId)));
        }
        LOG.info(new ObjectMessage(Map.of("Message","Setting number of instances to relaunch to: " + instances.size() + " for job " + jobId)));
        instanceRequest.setNumberOfInstances(instances.size());
        instances.clear();
        // Create and send instance start request
        List<VMInformation> newVms = amazonInstance.create(instanceRequest);
        // Add new instances - set to 'starting' status so watchdog waits for actual /v2/agent/ready call
        for (VMInformation newInfo : newVms) {
            vmInfo.add(newInfo);
            // Add to startedInstances - watchdog will wait for this agent to actually report
            startedInstances.add(newInfo);
            CloudVmStatus newStatus = createCloudStatus(instanceRequest, newInfo);
            vmTracker.setStatus(newStatus);
            LOG.info(new ObjectMessage(Map.of(
                "Message", "Created replacement agent with status " + newStatus.getVmStatus() + 
                    " - watchdog will wait for /v2/agent/ready call",
                "instanceId", newInfo.getInstanceId(),
                "jobId", jobId,
                "publicIp", newInfo.getPublicIp() != null ? newInfo.getPublicIp() : "N/A",
                "privateIp", newInfo.getPrivateIp() != null ? newInfo.getPrivateIp() : "N/A")));
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
     * Creates initial cloud status for a newly launched replacement agent.
     * CRITICAL: Must use VMStatus.starting (not pending) so watchdog waits for actual agent registration.
     * 
     * Status flow: starting → (agent calls /v2/agent/ready) → pending → (receives START) → ready → running
     * 
     * @param req the instance request
     * @param info the VM information for the new instance
     * @return CloudVmStatus with starting state
     */
    private CloudVmStatus createCloudStatus(VMInstanceRequest req, VMInformation info) {
        return new CloudVmStatus(info.getInstanceId(), req.getJobId(),
                req.getInstanceDescription() != null ? req.getInstanceDescription().getSecurityGroup() : "unknown",
                JobStatus.Starting,
                VMImageType.AGENT, req.getRegion(), VMStatus.starting, new ValidationStatus(), 0, 0, null, null);
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

    // TODO: This method duplicates logic from JobEventSender.killJob(). Consider extracting to a shared service
    private void killJobDirectly(String jobId) {
        LOG.info(new ObjectMessage(Map.of("Message", "Killing job " + jobId + " directly from watchdog")));

        vmTracker.stopJob(jobId);

        List<String> allInstanceIds = vmInfo.stream()
                .map(VMInformation::getInstanceId)
                .collect(Collectors.toList());
        if (!allInstanceIds.isEmpty()) {
            LOG.info(new ObjectMessage(Map.of("Message", "Killing " + allInstanceIds.size() + " instances for job " + jobId)));
            amazonInstance.killInstances(allInstanceIds);
        }        

        for (VMInformation info : vmInfo) {
            CloudVmStatus status = vmTracker.getStatus(info.getInstanceId());
            if (status != null) {
                status.setVmStatus(VMStatus.terminated);
                status.setJobStatus(JobStatus.Completed);
                status.setEndTime(new Date());
                vmTracker.setStatus(status);
            }
        }        

        try {
            JobInstanceDao dao = new JobInstanceDao();
            JobInstance job = dao.findById(Integer.parseInt(jobId));
            if (job != null) {
                job.setStatus(JobQueueStatus.Completed);
                job.setEndTime(new Date());
                dao.saveOrUpdate(job);
                LOG.info(new ObjectMessage(Map.of("Message", "Updated job " + jobId + " status to Completed")));
            }
        } catch (Exception e) {
            LOG.error("Error updating job status in database: " + e.getMessage(), e);
        }
    }
}
