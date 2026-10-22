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

import java.util.List;
import java.util.function.Function;

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
