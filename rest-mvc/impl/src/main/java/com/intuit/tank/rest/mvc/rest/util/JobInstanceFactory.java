/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.BaseDao;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Workload;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.util.TestParameterContainer;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.common.util.ReportUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds and queues jobs from a saved project. Shared by the web UI's job dialog and the REST API, so a
 * job snapshots the same settings, regions, data files and notifications however it is queued.
 */
public final class JobInstanceFactory {

    private static final Logger LOG = LogManager.getLogger(JobInstanceFactory.class);

    private JobInstanceFactory() {
    }

    /**
     * An unsaved job, with the validator that measured it (reused for the job details).
     */
    public record Proposal(JobInstance job, JobValidator validator) {
    }

    /**
     * Builds an unsaved job from the project's saved configuration.
     *
     * <p>Regions, data files and notifications are recorded by their current audit revision, so later
     * edits to the project do not change a queued job. Times and total users are evaluated from the
     * project's expressions.</p>
     *
     * @param workload a saved workload with its job configuration and test plans loaded
     * @param name     the job name, or null for {@link #defaultName}
     * @param creator  the user queueing the job
     */
    public static Proposal propose(Workload workload, String projectName, String name, String creator) {
        JobConfiguration config = workload.getJobConfiguration();
        JobValidator validator = new JobValidator(workload.getTestPlans(), config.getVariables(), false);
        long maxDuration = workload.getTestPlans().stream()
                .mapToLong(plan -> validator.getDurationMs(plan.getName()))
                .max().orElse(0);
        TestParameterContainer times = TestParamUtil.evaluateTestTimes(maxDuration,
                config.getRampTimeExpression() != null ? config.getRampTimeExpression() : "",
                config.getSimulationTimeExpression() != null ? config.getSimulationTimeExpression() : "");
        Set<JobRegion> regions = JobRegionDao.cleanRegions(config.getJobRegions());
        int totalUsers = regions.stream()
                .mapToInt(region -> (int) TestParamUtil.evaluateExpression(region.getUsers(), maxDuration,
                        times.getSimulationTime(), times.getRampTime()))
                .sum();

        JobInstance job = new JobInstance(workload, null);
        job.setName(name != null ? name : defaultName(projectName, config.getIncrementStrategy(), totalUsers, new Date()));
        job.setCreator(creator);
        job.setScheduledTime(new Date());
        job.setVariables(new HashMap<>(config.getVariables()));
        job.setAllowOverride(config.isAllowOverride());
        job.getDataFileVersions().addAll(versions(new DataFileDao(), config.getDataFileIds(), DataFile.class));
        job.getNotificationVersions().addAll(versions(new JobNotificationDao(), ids(config.getNotifications()),
                JobNotification.class));
        job.getJobRegionVersions().addAll(versions(new JobRegionDao(), ids(regions), JobRegion.class));
        job.setExecutionTime(maxDuration);
        job.setRampTime(times.getRampTime());
        job.setSimulationTime(times.getSimulationTime());
        job.setTotalVirtualUsers(totalUsers);
        return new Proposal(job, validator);
    }

    /**
     * The web UI's default job name: the project name, the total users (or "nonlinear"), and the time.
     */
    public static String defaultName(String projectName, IncrementStrategy strategy, int totalUsers, Date when) {
        String kind = strategy == IncrementStrategy.standard ? "_nonlinear_" : "_" + totalUsers + "_users_";
        return projectName + kind + ReportUtil.getTimestamp(when);
    }

    /**
     * A queued job and the queue it was added to.
     */
    public record Queued(JobInstance job, JobQueue queue) {
    }

    /**
     * Saves a proposed job, adds it to the project's queue and stores its harness script.
     *
     * @param workload the workload the job was proposed from
     */
    public static Queued queue(int projectId, Workload workload, JobInstance job) {
        // save the job first: its hash code is its id, and it goes into the queue's hash set
        JobInstance saved = new JobInstanceDao().saveOrUpdate(job);
        JobQueueDao queueDao = new JobQueueDao();
        JobQueue queue = queueDao.findOrCreateForProjectId(projectId);
        queue.addJob(saved);
        queue = queueDao.saveOrUpdate(queue);
        storeScript(workload, saved);
        return new Queued(saved, queue);
    }

    /**
     * Stores the harness script now, so later edits to the scripts do not change the job. The job is already
     * saved, so a failure is logged rather than thrown: starting the job generates a missing script.
     */
    private static void storeScript(Workload workload, JobInstance job) {
        try {
            new WorkloadDao().loadScriptsForWorkload(workload);
            HDWorkload hdWorkload = ConverterUtil.convertWorkload(workload, job);
            ProjectDaoUtil.storeScriptFile(Integer.toString(job.getId()), ConverterUtil.getWorkloadXML(hdWorkload));
        } catch (RuntimeException e) {
            LOG.warn("Job {} was queued but its script could not be stored yet; it will be generated when the job "
                    + "starts: {}", job.getId(), e.toString(), e);
        }
    }

    private static Set<Integer> ids(Collection<? extends BaseEntity> entities) {
        return entities.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    @SuppressWarnings("rawtypes")
    private static Set<EntityVersion> versions(BaseDao dao, Collection<Integer> ids, Class<? extends BaseEntity> type) {
        Set<EntityVersion> result = new HashSet<>();
        for (Integer id : ids) {
            result.add(new EntityVersion(id, dao.getHeadRevisionNumber(id), type));
        }
        return result;
    }
}
