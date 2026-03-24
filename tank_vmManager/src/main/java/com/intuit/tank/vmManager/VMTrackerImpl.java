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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

import com.amazonaws.xray.AWSXRay;
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
    // Tracks when we last received ANY report from an instance (set before executor enqueue).
    // Used for staleness detection — immune to DiscardOldestPolicy dropping the task.
    private Map<String, Long> lastSeenMap = new ConcurrentHashMap<>();

    private static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(50),
                    threadFactoryRunnable -> {
                        Thread t = Executors.defaultThreadFactory().newThread(threadFactoryRunnable);
                        t.setDaemon(true);
                        return t;
                    },
                    new ThreadPoolExecutor.DiscardOldestPolicy());

    // Dedicated executor for terminal status updates — small queue + CallerRunsPolicy
    // guarantees delivery without silently dropping, and avoids blocking the REST thread
    // for extended periods when killInstances loops over many instances.
    private static final ThreadPoolExecutor TERMINAL_EXECUTOR =
            new ThreadPoolExecutor(2, 10, 60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(20),
                    threadFactoryRunnable -> {
                        Thread t = Executors.defaultThreadFactory().newThread(threadFactoryRunnable);
                        t.setDaemon(true);
                        t.setName("terminal-status-" + t.getId());
                        return t;
                    },
                    (r, executor) -> {
                        // CallerRunsPolicy with logging — runs on caller thread when queue is full
                        LOG.warn("Terminal executor queue full (size={}), running on caller thread: {}",
                            executor.getQueue().size(), Thread.currentThread().getName());
                        if (!executor.isShutdown()) {
                            r.run();
                        }
                    });

    private ScheduledExecutorService stalenessSweeper;

    /**
     * 
     */
    @PostConstruct
    public void init() {
        devMode = new TankConfig().getStandalone();
        stalenessSweeper = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "staleness-sweeper");
            t.setDaemon(true);
            return t;
        });
        stalenessSweeper.scheduleAtFixedRate(this::sweepStaleJobs, 30, 30, TimeUnit.SECONDS);
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
        // Record ingest time BEFORE enqueue — immune to DiscardOldestPolicy drops.
        // Used by staleness detection instead of reportTime (which is set inside the executor).
        lastSeenMap.put(status.getInstanceId(), System.currentTimeMillis());

        boolean isTerminal = status.getJobStatus() == JobStatus.Completed
            || status.getJobStatus() == JobStatus.Stopped
            || status.getVmStatus() == VMStatus.terminated;
        if (isTerminal) {
            // Terminal updates use a dedicated executor with CallerRunsPolicy —
            // guarantees delivery (never drops), falls back to caller thread if queue is full
            TERMINAL_EXECUTOR.execute(() -> setStatusThread(status));
        } else {
            EXECUTOR.execute(() -> setStatusThread(status));
        }
    }

    private void setStatusThread(@Nonnull final CloudVmStatus status) {
        AWSXRay.getGlobalRecorder().beginNoOpSegment();
        try {
            synchronized (getCacheSyncObject(status.getJobId())) {
                status.setReportTime(new Date());
                CloudVmStatus currentStatus = getStatus(status.getInstanceId());

                if (!shouldUpdateStatus(currentStatus, status)) {
                    LOG.debug(new ObjectMessage(Map.of("Message",
                        "Skipping full status update for instance " + status.getInstanceId() +
                        " - current status is terminal: " + (currentStatus != null ? currentStatus.getVmStatus() : "null"))));
                    return;
                }

                statusMap.put(status.getInstanceId(), status);
                if (status.getVmStatus() == VMStatus.running
                        && (status.getJobStatus() == JobStatus.Completed)
                        && !isDevMode()) {
                    AmazonInstance amzInstance = new AmazonInstance(status.getVmRegion());
                    amzInstance.killInstances(Collections.singletonList(status.getInstanceId()));
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
        } finally {
            AWSXRay.endSegment();
        }
    }

    private String getProjectForJobId(String jobId) {
        String ret = jobToProjectIdMap.get(jobId);
        if (ret == null) {
            try {
                JobInstance jobInstance = jobInstanceDao.get().findById(Integer.valueOf(jobId));
                Workload wkld = workloadDaoInstance.get().findById(jobInstance.getWorkloadId());
                ret = Integer.toString(wkld.getProject().getId());
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

    /**
     * Determines whether an incoming status update should be applied.
     * Rejects updates when the current status is terminal, UNLESS the incoming
     * status is a valid forward transition (e.g., stopping → terminated from killInstances).
     */
    private boolean shouldUpdateStatus(CloudVmStatus currentStatus, CloudVmStatus incomingStatus) {
        if (currentStatus == null) {
            return true;
        }
        VMStatus currentVm = currentStatus.getVmStatus();
        // Already fully terminated — no further updates
        if (currentVm == VMStatus.terminated) {
            return false;
        }
        // For stopping/stopped/shutting_down, only allow forward transition to terminated/Completed
        if (currentVm == VMStatus.shutting_down || currentVm == VMStatus.stopped || currentVm == VMStatus.stopping) {
            return incomingStatus.getVmStatus() == VMStatus.terminated
                || incomingStatus.getJobStatus() == JobStatus.Completed;
        }
        return true;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void removeStatusForInstance(String instanceId) {
        statusMap.remove(instanceId);
        lastSeenMap.remove(instanceId);
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
        // Normalize completed/terminated agents to zero users — agent may report stale currentUsers
        // if threads haven't fully exited before the final status report
        if (status.getJobStatus() == JobStatus.Completed || status.getVmStatus() == VMStatus.terminated) {
            status.setCurrentUsers(0);
            status.setUserDetails(Collections.emptyList());
        }
        cloudVmStatusContainer.getStatuses().remove(status);
        cloudVmStatusContainer.getStatuses().add(status);
        cloudVmStatusContainer.calculateUserDetails();
        boolean isFinished = true;
        boolean paused = true;
        boolean rampPaused = true;
        boolean stopped = true;
        boolean running = true;

        // look up the job
        JobInstance job = jobInstanceDao.get().findById(Integer.parseInt(status.getJobId()));
        
        // Take a snapshot to avoid ConcurrentModificationException if another thread modifies
        // the set while we iterate (defense in depth, even though we're synchronized)
        Set<CloudVmStatus> statusesSnapshot = new HashSet<>(cloudVmStatusContainer.getStatuses());
        
        int activeInstanceCount = 0;
        for (CloudVmStatus s : statusesSnapshot) {
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
        
        // Staleness detection: if all non-replaced agents are either Completed or stale
        // (no report in 3× the configured report interval), treat the job as finished.
        // This catches the case where an agent's final Completed report was lost.
        if (!isFinished && activeInstanceCount > 0) {
            long reportIntervalMs = Math.max(new TankConfig().getAgentConfig().getStatusReportIntervalMilis(15_000), 15_000);
            long staleThresholdMs = reportIntervalMs * 3;
            Date now = new Date();
            long nowMs = now.getTime();
            boolean allCompletedOrStale = true;
            for (CloudVmStatus s : statusesSnapshot) {
                if (s.getVmStatus() == VMStatus.replaced) continue;
                boolean isCompleted = s.getJobStatus() == JobStatus.Completed;
                // Use lastSeenMap (set before executor enqueue) for staleness — immune to
                // DiscardOldestPolicy dropping updates, which would leave reportTime stale
                Long lastSeen = lastSeenMap.get(s.getInstanceId());
                boolean isStale = lastSeen != null && (nowMs - lastSeen) > staleThresholdMs;
                if (!isCompleted && !isStale) {
                    allCompletedOrStale = false;
                    break;
                }
            }
            if (allCompletedOrStale) {
                LOG.warn("Job {} — all agents are either Completed or stale (no report in {}ms). Treating as finished.",
                    status.getJobId(), staleThresholdMs);
                isFinished = true;
                // Force stale agents to Completed in the container
                for (CloudVmStatus s : statusesSnapshot) {
                    if (s.getVmStatus() == VMStatus.replaced) continue;
                    if (s.getJobStatus() != JobStatus.Completed) {
                        s.setJobStatus(JobStatus.Completed);
                        s.setCurrentUsers(0);
                        s.setUserDetails(Collections.emptyList());
                        s.setEndTime(now);
                    }
                }
                // Recalculate aggregated user details after force-completing stale agents
                cloudVmStatusContainer.calculateUserDetails();
            }
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
            job.setEndTime(cloudVmStatusContainer.getEndTime());
            JobQueueStatus oldStatus = job.getStatus();
            JobQueueStatus newStatus = job.getStatus();
            if (isFinished) {
                newStatus = JobQueueStatus.Completed;
                stopJob(Integer.toString(job.getId()));
            } else if (paused) {
                newStatus = JobQueueStatus.Paused;
            } else if (rampPaused) {
                newStatus = JobQueueStatus.RampPaused;
            } else if (stopped) {
                newStatus = JobQueueStatus.Stopped;
            } else if (running) {
                newStatus = JobQueueStatus.Running;
                if (job.getStartTime() == null && status.getJobStatus() == JobStatus.Running) {
                    job.setStartTime(status.getStartTime());
                    jobEventProducer.fire(new JobEvent(Integer.toString(job.getId()), "", JobLifecycleEvent.LOAD_STARTED));
                }
            }

            if (oldStatus != newStatus) {
                LOG.info(new ObjectMessage(Map.of("Message",
                    "Job " + status.getJobId() + " status transition: " + oldStatus + " -> " + newStatus +
                    " (isFinished=" + isFinished + ", paused=" + paused + ", rampPaused=" + rampPaused +
                    ", stopped=" + stopped + ", running=" + running + ")")));
            }
            LOG.trace("Setting Container for job=" + status.getJobId() + " newStatus to " + newStatus);
            job.setStatus(newStatus);
            jobInstanceDao.get().saveOrUpdate(job);

            // Job StartTime is source of truth for cloudVMStatusContainer StartTime
            if (job.getStartTime() != null && cloudVmStatusContainer.getStartTime() != null) {
                if (job.getStartTime().before(cloudVmStatusContainer.getStartTime())) {
                    cloudVmStatusContainer.setStartTime(job.getStartTime());
                }
            }
        }
    }

    /**
     * Periodic sweep over all tracked jobs to detect stale agents when no inbound
     * reports arrive (e.g., all agents died simultaneously). For each unfinished job
     * (endTime == null), checks if all agents are Completed or stale and forces completion.
     */
    private void sweepStaleJobs() {
        long reportIntervalMs;
        try {
            reportIntervalMs = Math.max(new TankConfig().getAgentConfig().getStatusReportIntervalMilis(15_000), 15_000);
        } catch (Exception e) {
            LOG.error("Error reading config in staleness sweep, using default 15s: " + e.getMessage(), e);
            reportIntervalMs = 15_000;
        }
        long staleThresholdMs = reportIntervalMs * 3;
        Date now = new Date();
        long nowMs = now.getTime();

        for (Map.Entry<String, CloudVmStatusContainer> entry : jobMap.entrySet()) {
            String jobId = entry.getKey();
            try {
                CloudVmStatusContainer container = entry.getValue();
                if (container.getEndTime() != null) {
                    continue; // already finished
                }
                synchronized (getCacheSyncObject(jobId)) {
                    // Re-check after acquiring lock
                    if (container.getEndTime() != null) {
                        continue;
                    }
                    Set<CloudVmStatus> statuses = container.getStatuses();
                    if (statuses.isEmpty()) {
                        continue;
                    }
                    boolean allCompletedOrStale = true;
                    CloudVmStatus latestStatus = null;
                    for (CloudVmStatus s : statuses) {
                        if (s.getVmStatus() == VMStatus.replaced) continue;
                        if (latestStatus == null) latestStatus = s;
                        boolean isCompleted = s.getJobStatus() == JobStatus.Completed;
                        Long lastSeen = lastSeenMap.get(s.getInstanceId());
                        boolean isStale = lastSeen != null && (nowMs - lastSeen) > staleThresholdMs;
                        if (!isCompleted && !isStale) {
                            allCompletedOrStale = false;
                            break;
                        }
                    }
                    if (allCompletedOrStale && latestStatus != null) {
                        LOG.warn("Staleness sweep: job {} — all agents Completed or stale. Triggering finish via addStatusToJobContainer.",
                            jobId);
                        for (CloudVmStatus s : statuses) {
                            if (s.getVmStatus() == VMStatus.replaced) continue;
                            if (s.getJobStatus() != JobStatus.Completed) {
                                s.setJobStatus(JobStatus.Completed);
                                s.setCurrentUsers(0);
                                s.setUserDetails(Collections.emptyList());
                                s.setEndTime(now);
                            }
                        }
                        addStatusToJobContainer(latestStatus, container);
                    }
                }
            } catch (Exception e) {
                LOG.error("Error in staleness sweep for job " + jobId + ": " + e.getMessage(), e);
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
        TERMINAL_EXECUTOR.shutdown();
        if (stalenessSweeper != null) {
            stalenessSweeper.shutdown();
        }
    }
}
