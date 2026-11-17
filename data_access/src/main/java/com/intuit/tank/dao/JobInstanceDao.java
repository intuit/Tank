/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

/**
 * JobInstance
 * 
 * @author dangleton
 * 
 */
public class JobInstanceDao extends BaseDao<JobInstance> {

    /**
     * @param entityClass
     */
    public JobInstanceDao() {
        super();
        setReloadEntities(true);
    }

    public List<JobInstance> getForStatus(List<JobQueueStatus> statuses) {
        if (statuses.isEmpty()) {
            return List.of();
        }
        return list(cb -> {
            CriteriaQuery<JobInstance> query = cb.createQuery(JobInstance.class);
            Root<JobInstance> job = query.from(JobInstance.class);
            return query.select(job).where(job.get(JobInstance.PROPERTY_STATUS).in(statuses));
        });
    }

    public List<JobInstance> findCompleted() {
        return list(cb -> {
            CriteriaQuery<JobInstance> query = cb.createQuery(JobInstance.class);
            Root<JobInstance> job = query.from(JobInstance.class);
            return query.select(job).where(
                    cb.isNotNull(job.get(JobInstance.PROPERTY_START_TIME)),
                    cb.isNotNull(job.get(JobInstance.PROPERTY_END_TIME)));
        });
    }

    public List<JobInstance> findNotCompleted() {
        return list(cb -> {
            CriteriaQuery<JobInstance> query = cb.createQuery(JobInstance.class);
            Root<JobInstance> job = query.from(JobInstance.class);
            return query.select(job).where(cb.not(job.get(JobInstance.PROPERTY_STATUS)
                    .in(JobQueueStatus.Completed, JobQueueStatus.Aborted)));
        });
    }

    /**
     * Reads one job's details text without loading the job and its eager collections.
     *
     * @param jobId
     * @return the job's details, or null when there is no such job
     */
    public String findJobDetails(int jobId) {
        List<String> results = list(cb -> {
            CriteriaQuery<String> query = cb.createQuery(String.class);
            Root<JobInstance> job = query.from(JobInstance.class);
            return query.select(job.get(JobInstance.PROPERTY_JOB_DETAILS))
                    .where(cb.equal(job.get(JobInstance.PROPERTY_ID), jobId));
        });
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Reads every job's status without loading the jobs and their eager collections.
     *
     * @return each job's status by job id, in id order
     */
    public Map<Integer, JobQueueStatus> findStatuses() {
        List<Tuple> rows = list(cb -> {
            CriteriaQuery<Tuple> query = cb.createTupleQuery();
            Root<JobInstance> job = query.from(JobInstance.class);
            return query.multiselect(job.get(JobInstance.PROPERTY_ID), job.get(JobInstance.PROPERTY_STATUS))
                    .orderBy(cb.asc(job.get(JobInstance.PROPERTY_ID)));
        });
        Map<Integer, JobQueueStatus> statuses = new LinkedHashMap<>();
        rows.forEach(row -> statuses.put(row.get(0, Integer.class), row.get(1, JobQueueStatus.class)));
        return statuses;
    }

    /**
     * What a job list shows about each job.
     */
    public record JobSummary(int id, String name, Date startTime, Date endTime, String location,
            JobQueueStatus status, int totalVirtualUsers, long rampTime, long simulationTime) {
    }

    /**
     * Reads the summary of every job that is not deleted without loading the jobs and their eager collections.
     *
     * @return the jobs' summaries, newest first
     */
    public List<JobSummary> findSummaries() {
        List<Tuple> rows = list(cb -> {
            CriteriaQuery<Tuple> query = cb.createTupleQuery();
            Root<JobInstance> job = query.from(JobInstance.class);
            return query.multiselect(job.get(JobInstance.PROPERTY_ID), job.get("name"),
                    job.get(JobInstance.PROPERTY_START_TIME), job.get(JobInstance.PROPERTY_END_TIME),
                    job.get("location"), job.get(JobInstance.PROPERTY_STATUS), job.get("totalVirtualUsers"),
                    job.get("rampTime"), job.get("rampTimeSeconds"),
                    job.get("simulationTime"), job.get("simulationTimeSeconds"))
                    .where(cb.notEqual(job.get(JobInstance.PROPERTY_STATUS), JobQueueStatus.Deleted))
                    .orderBy(cb.desc(job.get(JobInstance.PROPERTY_CREATE)), cb.desc(job.get(JobInstance.PROPERTY_ID)));
        });
        return rows.stream().map(row -> new JobSummary(
                row.get(0, Integer.class),
                row.get(1, String.class),
                row.get(2, Date.class),
                row.get(3, Date.class),
                row.get(4, String.class),
                row.get(5, JobQueueStatus.class),
                row.get(6, Integer.class),
                // same fallback to the legacy seconds columns as BaseJob.getRampTime and getSimulationTime
                row.get(7, Long.class) != null ? row.get(7, Long.class) : row.get(8, Integer.class) * 1000L,
                row.get(9, Long.class) != null ? row.get(9, Long.class) : row.get(10, Integer.class) * 1000L))
                .toList();
    }

    /**
     * Runs the query that {@code build} makes, in a transaction of its own.
     */
    private <R> List<R> list(Function<CriteriaBuilder, CriteriaQuery<R>> build) {
        List<R> results = null;
        try {
            begin();
            results = getEntityManager().createQuery(build.apply(getEntityManager().getCriteriaBuilder()))
                    .getResultList();
            commit();
        } catch (Exception e) {
            rollback();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        return results;
    }

}
