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

import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_EVENT_TIME_KEY;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.tracing.Tracing;
import io.opentelemetry.context.Context;
import com.intuit.tank.logging.ControllerLoggingConfig;
import com.intuit.tank.vm.vmManager.VMTracker;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.ProjectStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobInstanceDao.JobState;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.apache.logging.log4j.message.ObjectMessage;

/**
 * VMStatusCache
 * 
 * @author dangleton
 * 
 */
@Named
@ApplicationScoped
public class VMTrackerImpl implements VMTracker {
    private static final Logger LOG = LogManager.getLogger(VMTrackerImpl.class);

    private ConcurrentMap<String, String> locks = new ConcurrentHashMap<String, String>();
    private boolean devMode = false;

    @Inject
    private Event<JobEvent> jobEventProducer;

    @Inject
    private Instance<JobInstanceDao> jobInstanceDao;

    @Inject
    private Instance<WorkloadDao> workloadDaoInstance;

    private Map<String, String> jobToProjectIdMap = new ConcurrentHashMap<String, String>();
    private Map<String, ProjectStatusContainer> projectContainerMap = new ConcurrentHashMap<String, ProjectStatusContainer>();
    private Map<String, CloudVmStatus> statusMap = new ConcurrentHashMap<String, CloudVmStatus>();
    private Map<String, CloudVmStatusContainer> jobMap = new ConcurrentHashMap<String, CloudVmStatusContainer>();
    private Set<String> stoppedJobs = new HashSet<String>();

    private static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(50),
                    threadFactoryRunnable -> {
                        Thread t = Executors.defaultThreadFactory().newThread(threadFactoryRunnable);
                        t.setDaemon(true);
                        return t;
                    },
                    VMTrackerImpl::discardOldest);

    /**
     * When the queue is full, drops the oldest queued job container update to make room, and says so.
     * Instance statuses don't depend on it: {@link #setStatus} records them before queueing.
     */
    private static void discardOldest(Runnable task, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        executor.getQueue().poll();
        LOG.warn(new ObjectMessage(Map.of("Message",
                "Status update queue is full; dropped the oldest job container update")));
        executor.execute(task);
    }

    /**
     * 
     */
    @PostConstruct
    public void init() {
        devMode = new TankConfig().getStandalone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloudVmStatus getStatus(@Nonnull String instanceId) {
        return statusMap.get(instanceId);
    }

    public ProjectStatusContainer getProjectStatusContainer(String projectId) {
        return projectContainerMap.get(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishEvent(JobEvent event) {
        try {
            jobEventProducer.fire(event);
        } catch (Exception e) {
            LOG.error("Error firing Event: " + e, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(@Nonnull final CloudVmStatus status) {
        // Recorded now, so getStatus sees it at once: an agent can register seconds after launch, and the
        // queue below can lag, or drop updates, when many agents report at once
        boolean recorded = recordStatus(status);
        // The reporting request has already returned 204; trace the update as part of that request's trace
        Runnable task = Context.current().wrap(() -> Tracing.inSpan("VMTracker.SetStatus",
                () -> updateJobContainers(status, recorded)));
        EXECUTOR.execute(task);
    }

    /** Both steps of {@link #setStatus} on the calling thread */
    private void setStatusThread(@Nonnull final CloudVmStatus status) {
        updateJobContainers(status, recordStatus(status));
    }

    /**
     * Records an instance's status, unless the one it has should stand: an instance that is shutting
     * down or gone keeps that status, and a launch status ({@link VMStatus#starting}) never replaces
     * one the agent has moved past, as when it registers before the launch status is recorded.
     *
     * @return whether the status was recorded
     */
    private boolean recordStatus(@Nonnull final CloudVmStatus status) {
        status.setReportTime(new Date());
        boolean[] recorded = { false };
        statusMap.compute(status.getInstanceId(), (id, currentStatus) -> {
            // agents that don't report their zone keep the one recorded at launch
            if (status.getZone() == null && currentStatus != null) {
                status.setZone(currentStatus.getZone());
            }
            if (!shouldUpdateStatus(currentStatus, status)) {
                LOG.debug(new ObjectMessage(Map.of("Message",
                    "Skipping status update for instance " + id + " - current status is "
                    + (currentStatus != null ? currentStatus.getVmStatus() : "null"))));
                return currentStatus;
            }
            recorded[0] = true;
            return status;
        });
        return recorded[0];
    }

    private void updateJobContainers(@Nonnull final CloudVmStatus status, boolean recorded) {
        synchronized (getCacheSyncObject(status.getJobId())) {
            if (recorded) {
                if (status.getVmStatus() == VMStatus.running
                		&& (status.getJobStatus() == JobStatus.Completed)
                		&& !isDevMode()) {
	                        AmazonInstance amzInstance = new AmazonInstance(status.getVmRegion());
	                        amzInstance.killInstances(Collections.singletonList(status.getInstanceId()));
                }
            }
            String jobId = status.getJobId();
            CloudVmStatusContainer cloudVmStatusContainer = jobMap.get(jobId);
            if (cloudVmStatusContainer == null) {
                cloudVmStatusContainer = new CloudVmStatusContainer();
                cloudVmStatusContainer.setJobId(jobId);

                jobMap.put(jobId, cloudVmStatusContainer);
                JobInstance job = jobInstanceDao.get().findById(Integer.parseInt(jobId));
                if (job != null) {
                    JobQueueStatus newStatus = getQueueStatus(job.getStatus(), status.getJobStatus());
                    cloudVmStatusContainer.setStatus(newStatus);
                    if (newStatus != job.getStatus()) {
                        job.setStatus(newStatus);
                        new JobInstanceDao().saveOrUpdate(job);
                    }
                } else {
                    JobQueueStatus newStatus = getQueueStatus(cloudVmStatusContainer.getStatus(), status.getJobStatus());
                    cloudVmStatusContainer.setStatus(newStatus);
                }
            }
            cloudVmStatusContainer.setReportTime(status.getReportTime());
            addStatusToJobContainer(status, cloudVmStatusContainer);
            String projectId = getProjectForJobId(jobId);
            if (projectId != null) {
                ProjectStatusContainer projectStatusContainer = getProjectStatusContainer(projectId);
                if (projectStatusContainer == null) {
                    projectStatusContainer = new ProjectStatusContainer();
                    projectContainerMap.put(projectId, projectStatusContainer);
                }
                projectStatusContainer.addStatusContainer(cloudVmStatusContainer);
            }
        }
    }

    private String getProjectForJobId(String jobId) {
        String ret = jobToProjectIdMap.get(jobId);
        if (ret == null) {
            try {
                JobInstance jobInstance = jobInstanceDao.get().findById(Integer.valueOf(jobId));
                Workload wkld = jobInstance != null ? workloadDaoInstance.get().findById(jobInstance.getWorkloadId()) : null;
                if (wkld != null) {
                    ret = Integer.toString(wkld.getProject().getId());
                } else {
                    LOG.warn("cannot get projectId for jobId " + jobId + ": no such job or workload");
                    ret = "";
                }
            } catch (Exception e) {
                LOG.error("cannot get projectId for jobId " + jobId + ": " + e.toString(), e);
                ret = "";
            }
            jobToProjectIdMap.put(jobId, ret);
        }
        return StringUtils.isNotBlank(ret) ? ret : null;
    }

    /**
     * @param oldStatus
     * @param jobStatus
     * @return
     */
    private JobQueueStatus getQueueStatus(JobQueueStatus oldStatus, JobStatus jobStatus) {
        try {
            return JobQueueStatus.valueOf(jobStatus.name());
        } catch (Exception e) {
            LOG.error("Error converting status from " + jobStatus);
        }
        return oldStatus;
    }

    private boolean shouldUpdateStatus(CloudVmStatus currentStatus, CloudVmStatus newStatus) {
        if (currentStatus != null && currentStatus != newStatus) {
            VMStatus status = currentStatus.getVmStatus();
            if (status == VMStatus.shutting_down
                    || status == VMStatus.stopped
                    || status == VMStatus.stopping
                    || status == VMStatus.terminated) {
                return false;
            }
            return !(newStatus.getVmStatus() == VMStatus.starting && REGISTERED.contains(status));
        }
        return true;
    }

    /** Whether two job states match, times to the second: the database keeps no finer, and returns Timestamps */
    private static boolean sameState(JobState a, JobState b) {
        return a.status() == b.status() && sameSecond(a.startTime(), b.startTime()) && sameSecond(a.endTime(), b.endTime());
    }

    private static boolean sameSecond(Date a, Date b) {
        return a == null ? b == null : b != null && a.getTime() / 1000 == b.getTime() / 1000;
    }

    /** Statuses an agent reaches only after registering, which a launch status must not undo */
    private static final Set<VMStatus> REGISTERED = EnumSet.of(VMStatus.pending, VMStatus.ready, VMStatus.running);

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void removeStatusForInstance(String instanceId) {
        statusMap.remove(instanceId);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void removeStatusForJob(String jobId) {
        CloudVmStatusContainer cloudVmStatusContainer = jobMap.get(jobId);
        if (cloudVmStatusContainer != null) {
            for (CloudVmStatus s : cloudVmStatusContainer.getStatuses()) {
                removeStatusForInstance(s.getInstanceId());
            }
            jobMap.remove(jobId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloudVmStatusContainer getVmStatusForJob(String jobId) {
        return jobMap.get(jobId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDevMode() {
        return devMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CloudVmStatusContainer> getAllJobs() {
        return new HashSet<CloudVmStatusContainer>(jobMap.values());
    }

    @Override
    public boolean isRunning(String id) {
        return !stoppedJobs.contains(id);
    }

    @Override
    public void stopJob(String id) {
        stoppedJobs.add(id);
    }

    /**
     * @param status
     * @param cloudVmStatusContainer
     **/
    private void addStatusToJobContainer(CloudVmStatus status, CloudVmStatusContainer cloudVmStatusContainer) {
        ControllerLoggingConfig.setupThreadContext();
        cloudVmStatusContainer.getStatuses().remove(status);
        cloudVmStatusContainer.getStatuses().add(status);
        cloudVmStatusContainer.calculateUserDetails();
        boolean isFinished = true;
        boolean paused = true;
        boolean rampPaused = true;
        boolean stopped = true;
        boolean running = true;

        // look up the job's status and times only: every agent reports every few seconds, and loading the
        // job loads all its VM instances
        int jobId = Integer.parseInt(status.getJobId());
        JobState job = jobInstanceDao.get().findState(jobId);
        
        // Take a snapshot to avoid ConcurrentModificationException if another thread modifies
        // the set while we iterate (defense in depth, even though we're synchronized)
        Set<CloudVmStatus> statusesSnapshot = new HashSet<>(cloudVmStatusContainer.getStatuses());
        
        int activeInstanceCount = 0;
        for (CloudVmStatus containerStatus : statusesSnapshot) {
            // each instance's latest status: the container's copy can be older, if its update was dropped
            CloudVmStatus s = statusMap.getOrDefault(containerStatus.getInstanceId(), containerStatus);
            VMStatus vmStatus = s.getVmStatus();
            // skip replaced instances - these were replaced by AgentWatchdog due to failure
            // but do NOT skip terminated/stopping/stopped/shutting_down - these are active agents in transition
            if (vmStatus == VMStatus.replaced) {
                continue;
            }
            activeInstanceCount++;
            
            JobStatus jobStatus = s.getJobStatus();
            if (jobStatus != JobStatus.Completed) {  // If no VMs are Completed
                isFinished = false;
            }
            if (jobStatus != JobStatus.Paused) {  // If no VMs are Paused
                paused = false;
            }
            if (jobStatus != JobStatus.RampPaused) {  // If no VMs are RampPaused
                rampPaused = false;
            }
            if (jobStatus != JobStatus.Stopped) {  // If no VMs are Stopped
                stopped = false;
            }
            if (jobStatus != JobStatus.Running) {  // If no VMs are Running
                running = false;
            }
        }
        
        // if all instances are replaced and replacements haven't reported yet,
        // don't change job status - wait for active agents to report
        if (activeInstanceCount == 0) {
            LOG.info(new ObjectMessage(Map.of("Message",
                "No active instances for job " + status.getJobId() + 
                " (all replaced or empty) - skipping status calculation until replacements report")));
            return;
        }
        
        LOG.debug(new ObjectMessage(Map.of("Message",
            "Status calc complete for job " + status.getJobId() + 
            " - isFinished=" + isFinished + ", paused=" + paused + 
            ", rampPaused=" + rampPaused + ", stopped=" + stopped + ", running=" + running)));
        
        if (isFinished) {
            LOG.info(new ObjectMessage(Map.of("Message","Setting end time on container " + cloudVmStatusContainer.getJobId())));
            if (cloudVmStatusContainer.getEndTime() == null) {
                jobEventProducer.fire(new JobEvent(status.getJobId(), "", JobLifecycleEvent.JOB_FINISHED)
                        .addContextEntry(NOTIFICATIONS_EVENT_EVENT_TIME_KEY,
                                new SimpleDateFormat(TankConstants.DATE_FORMAT_WITH_TIMEZONE).format(new Date())));
            }
            cloudVmStatusContainer.setEndTime(new Date());
        }
        if (job != null) {
            Date startTime = job.startTime();
            Date endTime = cloudVmStatusContainer.getEndTime();
            JobQueueStatus oldStatus = job.status();
            JobQueueStatus newStatus = job.status();
            if (isFinished) {
                newStatus = JobQueueStatus.Completed;
                stopJob(status.getJobId());
            } else if (paused) {
                newStatus = JobQueueStatus.Paused;
            } else if (rampPaused) {
                newStatus = JobQueueStatus.RampPaused;
            } else if (stopped) {
                newStatus = JobQueueStatus.Stopped;
            } else if (running) {
                newStatus = JobQueueStatus.Running;
                if (startTime == null && status.getJobStatus() == JobStatus.Running) {
                    startTime = status.getStartTime();
                    jobEventProducer.fire(new JobEvent(status.getJobId(), "", JobLifecycleEvent.LOAD_STARTED));
                }
            }

            if (oldStatus != newStatus) {
                LOG.info(new ObjectMessage(Map.of("Message",
                    "Job " + status.getJobId() + " status transition: " + oldStatus + " -> " + newStatus +
                    " (isFinished=" + isFinished + ", paused=" + paused + ", rampPaused=" + rampPaused +
                    ", stopped=" + stopped + ", running=" + running + ")")));
            }
            LOG.trace("Setting Container for job=" + status.getJobId() + " newStatus to " + newStatus);
            JobState newState = new JobState(newStatus, startTime, endTime);
            if (!sameState(job, newState)) {
                jobInstanceDao.get().updateState(jobId, newState);
            }

            // Job StartTime is source of truth for cloudVMStatusContainer StartTime
            if (startTime != null && cloudVmStatusContainer.getStartTime() != null) {
                if (startTime.before(cloudVmStatusContainer.getStartTime())) {
                    cloudVmStatusContainer.setStartTime(startTime);
                }
            }
        }
    }

    private Object getCacheSyncObject(final String id) {
        locks.putIfAbsent(id, id);
        return locks.get(id);
    }

    @PreDestroy
    private void destroy() {
        EXECUTOR.shutdown();
    }
}
