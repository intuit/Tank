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
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.ProjectStatusContainer;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

/**
 * VMStatusCache
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
public class VMTrackerImpl implements VMTracker {
    private static final Logger LOG = Logger.getLogger(VMTrackerImpl.class);

    private ConcurrentMap<String, String> locks = new ConcurrentHashMap<String, String>();
    private boolean devMode = false;

    @Inject
    private Event<JobEvent> jobEventProducer;

    @Inject
    private Instance<JobInstanceDao> jobDaoInstance;
    @Inject
    private Instance<WorkloadDao> workloadDaoInstance;

    private Map<String, String> jobToProjectIdMap = new ConcurrentHashMap<String, String>();
    private Map<String, ProjectStatusContainer> projectContainerMap = new ConcurrentHashMap<String, ProjectStatusContainer>();
    private Map<String, CloudVmStatus> statusMap = new ConcurrentHashMap<String, CloudVmStatus>();
    private Map<String, CloudVmStatusContainer> jobMap = new ConcurrentHashMap<String, CloudVmStatusContainer>();
    private Set<String> stoppedJobs = new HashSet<String>();

    /**
     * 
     */
    public VMTrackerImpl() {
        devMode = new TankConfig().getStandalone();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public CloudVmStatus getStatus(@Nonnull String instanceId) {
        CloudVmStatus status = statusMap.get(instanceId);
        return status;
    }

    public ProjectStatusContainer getProjectStatusContainer(String projectId) {
        return projectContainerMap.get(projectId);
    }

    /**
     * @{inheritDoc
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
     * @{inheritDoc
     */
    @Override
    public void setStatus(@Nonnull CloudVmStatus status) {
        LOG.info("setting status: " + status);
        synchronized (getCacheSyncObject(status.getJobId())) {
            status.setReportTime(new Date());
            CloudVmStatus curentStatus = getStatus(status.getInstanceId());
            if (shouldUpdateStatus(curentStatus)) {
                if (curentStatus != null) {
                    statusMap.remove(curentStatus);
                }
                statusMap.put(status.getInstanceId(), status);
                if (status.getVmStatus() == VMStatus.running
                        && (status.getJobStatus() == JobStatus.Completed)) {
                    if (!isDevMode()) {
                        AmazonInstance amzInstance = new AmazonInstance(null, status.getVmRegion());
                        amzInstance.kill(Arrays.asList(new String[] { status.getInstanceId() }));
                    }
                }
            }
            String jobId = status.getJobId();
            CloudVmStatusContainer cloudVmStatusContainer = jobMap.get(jobId);
            if (cloudVmStatusContainer == null) {
                cloudVmStatusContainer = new CloudVmStatusContainer();
                cloudVmStatusContainer.setJobId(jobId);

                jobMap.put(jobId, cloudVmStatusContainer);
                JobInstance job = getJob(jobId);
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
                JobInstance jobInstance = jobDaoInstance.get().findById(Integer.valueOf(jobId));
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
     * @param status
     * @return
     */
    private JobQueueStatus getQueueStatus(JobQueueStatus oldStatus, JobStatus jobSatatus) {
        try {
            return JobQueueStatus.valueOf(jobSatatus.name());
        } catch (Exception e) {
            LOG.error("Error converting status from " + jobSatatus);
        }
        return oldStatus;
    }

    /**
     * @param curentStatus
     * @return
     */
    private boolean shouldUpdateStatus(CloudVmStatus curentStatus) {
        boolean ret = true;
        if (curentStatus != null) {
            VMStatus status = curentStatus.getVmStatus();
            if (status == VMStatus.shutting_down || status == VMStatus.stopped || status == VMStatus.stopping
                    || status == VMStatus.terminated) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * 
     * @{inheritDoc
     */
    @Override
    public void removeStatusForInstance(String instanceId) {
        statusMap.remove(instanceId);
    }

    /**
     * 
     * @{inheritDoc
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
     * @{inheritDoc
     */
    @Override
    public CloudVmStatusContainer getVmStatusForJob(String jobId) {
        return jobMap.get(jobId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isDevMode() {
        return devMode;
    }

    /**
     * @{inheritDoc
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
     */
    private void addStatusToJobContainer(CloudVmStatus status,
            CloudVmStatusContainer cloudVmStatusContainer) {
        LOG.info("Adding Status to container");
        cloudVmStatusContainer.getStatuses().remove(status);
        cloudVmStatusContainer.getStatuses().add(status);
        cloudVmStatusContainer.calculateUserDetails();
        boolean isFinished = true;
        boolean paused = true;
        boolean rampPaused = true;
        boolean stopped = true;

        // look up the job
        JobInstance job = getJob(status.getJobId());
        for (CloudVmStatus s : cloudVmStatusContainer.getStatuses()) {
            JobStatus jobStatus = s.getJobStatus();
            if (jobStatus != JobStatus.Completed) {
                isFinished = false;
            }
            if (jobStatus != JobStatus.Paused) {
                paused = false;
            }
            if (jobStatus != JobStatus.RampPaused) {
                rampPaused = false;
            }
            if (jobStatus != JobStatus.Stopped) {
                stopped = false;
            }
        }
        if (isFinished) {
            LOG.info("Setting end time on container " + cloudVmStatusContainer.getJobId());
            if (cloudVmStatusContainer.getEndTime() == null) {
                String jobId = status.getJobId();
                jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.JOB_FINISHED)
                        .addContextEntry(NOTIFICATIONS_EVENT_EVENT_TIME_KEY,
                                new SimpleDateFormat(TankConstants.DATE_FORMAT_WITH_TIMEZONE).format(new Date())));
            }
            cloudVmStatusContainer.setEndTime(new Date());
        }
        if (job != null) {
            job.setEndTime(cloudVmStatusContainer.getEndTime());
            JobQueueStatus newStatus = job.getStatus();
            boolean needsUpdate = false;
            boolean loadStarted = false;
            if (isFinished) {
                newStatus = JobQueueStatus.Completed;
                needsUpdate = true;
                stopJob(Integer.toString(job.getId()));
            } else if (paused) {
                needsUpdate = true;
                newStatus = JobQueueStatus.Paused;
            } else if (rampPaused) {
                needsUpdate = true;
                newStatus = JobQueueStatus.RampPaused;
            } else if (stopped) {
                newStatus = JobQueueStatus.Stopped;
                needsUpdate = true;
            } else {
                newStatus = JobQueueStatus.Running;
                if (job.getStartTime() == null && status.getJobStatus() == JobStatus.Running) {
                    job.setStartTime(status.getStartTime());
                    needsUpdate = true;
                    loadStarted = true;

                }
            }
            if (job.getStartTime() != null && cloudVmStatusContainer.getStartTime() != null) {
                if (job.getStartTime().before(cloudVmStatusContainer.getStartTime())) {
                    cloudVmStatusContainer.setStartTime(job.getStartTime());
                }
            }
            if (needsUpdate || newStatus != job.getStatus()) {
                LOG.info("Setting newStatus to " + newStatus);
                job.setStatus(newStatus);
                job = new JobInstanceDao().saveOrUpdate(job);
            }
            if (loadStarted) {
                jobEventProducer.fire(new JobEvent(Integer.toString(job.getId()), "", JobLifecycleEvent.LOAD_STARTED));
            }
        }
    }

    /**
     * @param jobId
     * @return
     */
    private JobInstance getJob(String jobId) {
        JobInstanceDao dao = new JobInstanceDao();
        JobInstance job = null;
        try {
            int id = Integer.parseInt(jobId);
            job = dao.findById(id);
        } catch (NumberFormatException e) {
            // dev mode. using synthetic jobID
            LOG.warn("Using Local mode ignoring job status.");
        }
        return job;
    }

    private Object getCacheSyncObject(final String id) {
        locks.putIfAbsent(id, id);
        return locks.get(id);
    }
}
