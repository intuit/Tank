package com.intuit.tank.perfManager.workLoads;

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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Workload;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.service.v1.project.ProjectServiceUrlBuilder;
import com.intuit.tank.vm.scheduleManager.AgentDispatcher;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.JobRequestImpl;
import com.intuit.tank.vm.vmManager.Notification;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vm.vmManager.VMChannel;
import com.intuit.tank.vm.vmManager.JobRequestImpl.Builder;

public class WorkLoadFactory {
    private static final Logger LOG = LogManager.getLogger(WorkLoadFactory.class);

    @Inject
    private JobInstanceDao jobInstanceDao;

    @Inject
    private AgentDispatcher agentDispatcher;

    @Inject
    private VMChannel channel;

    /**
     * 
     * @param jobInstanceId
     * @return
     */
    public IncreasingWorkLoad getModelRunner(int jobInstanceId) {
        JobInstance job = jobInstanceDao.findById(jobInstanceId);
        JobRequest request = jobToJobRequest(job);
        return new IncreasingWorkLoad(channel, agentDispatcher, request);
    }

    private Set<? extends RegionRequest> getRegions(JobInstance job) {
        Set<JobRegion> ret = new HashSet<JobRegion>();
        JobRegionDao dao = new JobRegionDao();
        HashSet<VMRegion> regionSet = new HashSet<VMRegion>();
        for (EntityVersion version : job.getJobRegionVersions()) {
            JobRegion jobRegion = null;
            if (version.getObjectId() > 0 && version.getVersionId() > 0) {
                try {
                    jobRegion = dao.findRevision(version.getObjectId(), version.getVersionId());
                } catch (Exception e) {
                    LOG.error("Error getting region revision: " + e.toString(), e);
                }
            }
            if (jobRegion != null) {
                ret.add(fixJobRegion(jobRegion, job));
                if (regionSet.contains(jobRegion.getRegion())) {
                    LOG.warn("attempt to add multiple regions to job");
                } else {
                    regionSet.add(jobRegion.getRegion());
                }
            } else {
                LOG.warn("Attempt to add jobRegion version that does not exist. id = " + version.getObjectId()
                        + " : version = " + version.getVersionId());
                jobRegion = dao.findById(version.getObjectId());
                if (jobRegion != null) {
                    ret.add(fixJobRegion(jobRegion, job));
                    if (regionSet.contains(jobRegion.getRegion())) {
                        LOG.warn("attempt to add multiple regions to job");
                    } else {
                        regionSet.add(jobRegion.getRegion());
                    }
                } else {
                    LOG.warn("Cannot find job region with id " + version.getObjectId()
                            + ". Returning current job Regions.");
                    Workload workload = new WorkloadDao().findById(job.getWorkloadId());
                    ret = workload.getJobConfiguration().getJobRegions();
                    for (JobRegion region : ret) {
                        fixJobRegion(region, job);
                    }
                    break;
                    // throw new RuntimeException("Cannot find job region with id " + version.getObjectId());
                }
            }
        }
        ret = JobRegionDao.cleanRegions(ret);
        return ret;
    }

    private JobRegion fixJobRegion(JobRegion jobRegion, JobInstance job) {
        if (!NumberUtils.isDigits(jobRegion.getUsers())) {
            long users = TestParamUtil.evaluateExpression(jobRegion.getUsers(), job.getExecutionTime(),
                    job.getSimulationTime(), job.getRampTime());
            jobRegion.setUsers(Long.toString(users));
        }
        return jobRegion;
    }

    private JobRequest jobToJobRequest(JobInstance job) {
        Builder builder = JobRequestImpl.builder();
        builder.withBaselineVirtualUsers(job.getBaselineVirtualUsers()).withId(Integer.toString(job.getId()))
                .withIncrementStrategy(job.getIncrementStrategy())
                .withLocation(job.getLocation()).withRampTime(job.getRampTime())
                .withLoggingProfile(job.getLoggingProfile())
                .withStopBehavior(job.getStopBehavior())
                .withReportingMode(job.getReportingMode())
                .withUseEips(job.isUseEips())
                .withVmInstanceType(job.getVmInstanceType())
                .withnumUsersPerAgent(job.getNumUsersPerAgent())
                .withSimulationTime(job.getSimulationTime()).withStatus(job.getStatus())
                .withTerminationPolicy(job.getTerminationPolicy())
                .withUserIntervalIncrement(job.getUserIntervalIncrement());
        builder.withRegions(getRegions(job));
        builder.withNofitications(getNotifications(job));
        builder.withDataFileIds(getDataFileIds(job));
        if (job.getTerminationPolicy() == TerminationPolicy.script) {
            builder.withSimulationTime(0);
        }
        Workload workload = new WorkloadDao().findById(job.getWorkloadId());
        builder.withScriptXmlUrl(buildScriptXml(Integer.toString(job.getId()), workload));
        return builder.build();
    }

    /**
     * @param job
     * @return
     */
    private Set<Integer> getDataFileIds(JobInstance job) {
        DataFileDao dataFileDao = new DataFileDao();
        Set<Integer> ret = new HashSet<Integer>();
        for (EntityVersion version : job.getDataFileVersions()) {
            DataFile dataFile = dataFileDao.findRevision(version.getObjectId(), version.getVersionId());
            if (dataFile != null) {
                ret.add(dataFile.getId());
            } else {
                LOG.warn("Attempt to add dataFile that does not exist.");
            }
        }
        return ret;
    }

    /**
     * @param job
     * @return
     */
    private Set<? extends Notification> getNotifications(JobInstance job) {
        HashSet<JobNotification> ret = new HashSet<JobNotification>();
        JobNotificationDao dao = new JobNotificationDao();
        for (EntityVersion version : job.getNotificationVersions()) {
            JobNotification notification = dao.findRevision(version.getObjectId(), version.getVersionId());
            if (notification != null) {
                ret.add(notification);
            } else {
                LOG.warn("Attempt to add Notification that does not exist.");
            }
        }
        return ret;
    }

    public String buildScriptXml(String jobId, Workload workload) {
        // String scriptString = WorkloadScriptUtil.getScriptForWorkload(workload);
        // ProjectDaoUtil.storeScriptFile(jobId, scriptString);
        return ProjectServiceUrlBuilder.getScriptXmlUrl(jobId);
    }

}
