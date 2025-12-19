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

    private static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(50),
                    threadFactoryRunnable -> {
                        Thread t = Executors.defaultThreadFactory().newThread(threadFactoryRunnable);
                        t.setDaemon(true);
                        return t;
                    },
                    new ThreadPoolExecutor.DiscardOldestPolicy());

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
        Runnable task = () -> setStatusThread(status);
        EXECUTOR.execute(task);
    }

    private void setStatusThread(@Nonnull final CloudVmStatus status) {
        AWSXRay.getGlobalRecorder().beginNoOpSegment();  //initiation call has already returned 204
        synchronized (getCacheSyncObject(status.getJobId())) {
            status.setReportTime(new Date());
            CloudVmStatus currentStatus = getStatus(status.getInstanceId());
            
            LOG.debug(new ObjectMessage(Map.of("Message",
                "Status update for instance " + status.getInstanceId() + 
                " - VMStatus: " + status.getVmStatus() + ", JobStatus: " + status.getJobStatus() +
                ", Job: " + status.getJobId())));
            
            if (shouldUpdateStatus(currentStatus)) {
                statusMap.put(status.getInstanceId(), status);
                if (status.getVmStatus() == VMStatus.running
                		&& (status.getJobStatus() == JobStatus.Completed)
                		&& !isDevMode()) {
	                        AmazonInstance amzInstance = new AmazonInstance(status.getVmRegion());
	                        amzInstance.killInstances(Collections.singletonList(status.getInstanceId()));
                }
            } else {
                LOG.debug(new ObjectMessage(Map.of("Message",
                    "Skipping status update for instance " + status.getInstanceId() +
                    " - current status is " + (currentStatus != null ? currentStatus.getVmStatus() : "null"))));
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
        AWSXRay.endSegment();
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
     * If the vm is shutting down, terminated, or replaced, don't update the status to something else.
     * @param currentStatus
     * @return
     */
    private boolean shouldUpdateStatus(CloudVmStatus currentStatus) {
        if (currentStatus != null) {
            VMStatus status = currentStatus.getVmStatus();
            return (status != VMStatus.shutting_down
            		&& status != VMStatus.stopped
            		&& status != VMStatus.stopping
                    && status != VMStatus.terminated
                    && status != VMStatus.replaced);
        }
        return true;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void removeStatusForInstance(String instanceId) {
        CloudVmStatus status = statusMap.remove(instanceId);

        // also remove from the job's container to keep counts accurate
        if (status != null) {
            String jobId = status.getJobId();
            // Synchronize on the same lock used by setStatusThread to prevent
            // ConcurrentModificationException when iterating over statuses
            synchronized (getCacheSyncObject(jobId)) {
                CloudVmStatusContainer container = jobMap.get(jobId);
                if (container != null) {
                    boolean removed = container.getStatuses().remove(status);
                    if (removed) {
                        LOG.info(new ObjectMessage(Map.of("Message",
                            "Removed instance " + instanceId + " from container for job " + jobId)));
                    }
                }
            }
        }
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

        // look up the job
        JobInstance job = jobInstanceDao.get().findById(Integer.parseInt(status.getJobId()));
        
        // Log all statuses in container for debugging
        LOG.debug(new ObjectMessage(Map.of("Message",
            "Status calculation for job " + status.getJobId() + " - Container has " + 
            cloudVmStatusContainer.getStatuses().size() + " statuses")));
        
        for (CloudVmStatus s : cloudVmStatusContainer.getStatuses()) {
            VMStatus vmStatus = s.getVmStatus();
            // skip replaced instances - these were replaced by AgentWatchdog due to failure
            // but do NOT skip terminated/stopping/stopped/shutting_down - these are active agents in transition
            if (vmStatus == VMStatus.replaced) {
                LOG.info(new ObjectMessage(Map.of("Message",
                    "Skipping replaced instance " + s.getInstanceId() +
                    " in job status calculation for job " + status.getJobId())));
                continue;
            }
            
            LOG.trace(new ObjectMessage(Map.of("Message",
                "Checking instance " + s.getInstanceId() + 
                " - VMStatus: " + vmStatus + ", JobStatus: " + s.getJobStatus())));
            
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

    private Object getCacheSyncObject(final String id) {
        locks.putIfAbsent(id, id);
        return locks.get(id);
    }

    @PreDestroy
    private void destroy() {
        EXECUTOR.shutdown();
    }
}
