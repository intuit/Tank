/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.cloud;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.google.common.collect.ImmutableMap;
import com.intuit.tank.logging.ControllerLoggingConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.ThreadContext;

@Named
@ApplicationScoped
public class JobEventListener implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(JobEventListener.class);

    @Inject
    private Instance<JobEventSender> controllerSource;

    @Inject
    private VMTracker vmTracker;

    @Inject
    private JobInstanceDao jobInstanceDao;

    public void observerJobKillRequest(@Observes JobEvent request) {
        ControllerLoggingConfig.setupThreadContext();
        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Got Job Event: " + request)));

        String jobId = request.getJobId();
        JobLifecycleEvent eventType = request.getEvent();

        if (request.getEvent() == JobLifecycleEvent.JOB_ABORTED) {
            controllerSource.get().killJob(request.getJobId(), false);
        } else if (eventType == JobLifecycleEvent.AGENT_REPORTED) { // Update Job Status on Agent Relaunch (AgentWatchDog - All Agents Reporting Back)
            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "AGENT_REPORTED event received for job: " + jobId + ". Checking overall job readiness.")));
            try {
                synchronized (jobId.intern()) {
                    JobInstance job = jobInstanceDao.findById(Integer.valueOf(jobId));
                    if (job != null && job.getStatus() == JobQueueStatus.Starting) {
                        CloudVmStatusContainer vmStatusContainer = vmTracker.getVmStatusForJob(jobId);

                        int expectedTotalAgentCount = 0;
                        int readyAgentCount = 0;

                        if (vmStatusContainer != null) {
                            Set<CloudVmStatus> currentStatuses = vmStatusContainer.getStatuses();

                            // Expected Count = all non-terminated VMs for this job
                            List<CloudVmStatus> nonTerminatedStatuses = currentStatuses.stream()
                                    .filter(status -> status.getEndTime() == null)
                                    .toList();
                            expectedTotalAgentCount = nonTerminatedStatuses.size();
                            List<String> nonTerminatedIds = nonTerminatedStatuses.stream().map(CloudVmStatus::getInstanceId).collect(Collectors.toList());
                            LOG.debug(new ObjectMessage(ImmutableMap.of("jobId", jobId, "message", "DEBUG: Non-terminated instances counted for expectedTotalAgentCount", "count", expectedTotalAgentCount, "ids", nonTerminatedIds)));

                            // Ready Count = subset of expected that are also VMStatus.pending
                            List<CloudVmStatus> readyStatuses = nonTerminatedStatuses.stream()
                                    .filter(status -> status.getVmStatus() == VMStatus.pending)
                                    .toList();
                            readyAgentCount = readyStatuses.size();
                            List<String> readyIds = readyStatuses.stream().map(CloudVmStatus::getInstanceId).collect(Collectors.toList());
                            LOG.debug(new ObjectMessage(ImmutableMap.of("jobId", jobId, "message", "DEBUG: Ready (non-terminated and pending) instances counted for readyAgentCount", "count", readyAgentCount, "ids", readyIds)));
                        } else {
                            LOG.debug(new ObjectMessage(ImmutableMap.of("jobId", jobId, "message", "DEBUG: VMStatusContainer was null, counts remain 0")));
                        }

                        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Job " + jobId + ": Total expected (non-terminated) agents from VMTracker: " + expectedTotalAgentCount + ", Currently ready agents: " + readyAgentCount)));

                        boolean allReady = (expectedTotalAgentCount > 0 && readyAgentCount >= expectedTotalAgentCount);
                        LOG.debug(new ObjectMessage(ImmutableMap.of("jobId", jobId, "message", "DEBUG: Readiness check result", "expected", expectedTotalAgentCount, "ready", readyAgentCount, "allReady", allReady)));

                        if (allReady) {
                            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "All " + readyAgentCount + " expected agents for job " + jobId + " are ready. Updating job status to Running.")));

                            job.setStatus(JobQueueStatus.Running);

                            if (job.getStartTime() == null) {
                                LOG.debug(new ObjectMessage(ImmutableMap.of("jobId", jobId, "message", "DEBUG: Setting job StartTime as it was null")));
                                job.setStartTime(new Date());
                            }

                            jobInstanceDao.saveOrUpdate(job);
                            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Job " + jobId + " status successfully updated to Running.")));

                        } else {
                            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Job " + jobId + ": Not all agents ready yet (" + readyAgentCount + "/" + expectedTotalAgentCount + "). Waiting for overall job readiness across all regions...")));
                        }

                    } else {
                        LOG.warn(new ObjectMessage(ImmutableMap.of("Message", "Job " + jobId + ": Received AGENT_REPORTED, but job is null or not in Starting state (current state: " + (job != null ? job.getStatus() : "null") + "). Ignoring event for state transition.")));
                    }
                }
            } catch (Exception e) {
                LOG.error(new ObjectMessage(ImmutableMap.of("Message", "Error processing AGENT_REPORTED event for job " + jobId)), e);
            }
        } else if (request.getEvent() == JobLifecycleEvent.JOB_FINISHED ||
                request.getEvent() == JobLifecycleEvent.JOB_KILLED) {
            ThreadContext.clearAll();
        } else {
            LOG.debug(new ObjectMessage(ImmutableMap.of("Message", "Ignoring Job Event: " + request)));
        }
    }
}
