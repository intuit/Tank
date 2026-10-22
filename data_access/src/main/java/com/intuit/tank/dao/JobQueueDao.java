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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.project.JobQueue;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class JobQueueDao extends BaseDao<JobQueue> {
    private static final Logger LOG = LogManager.getLogger(JobQueueDao.class);

    public JobQueueDao() {
        super();
        setReloadEntities(true);
    }

    /**
     * 
     * @param projectId
     * @return
     */
    public synchronized JobQueue findOrCreateForProjectId(@Nonnull int projectId) {
        JobQueue result = null;
        EntityManager em = getEntityManager();
        try {
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JobQueue> query = cb.createQuery(JobQueue.class);
            Root<JobQueue> root = query.from(JobQueue.class);
            root.fetch(JobQueue.PROPERTY_JOBS, JoinType.INNER);
            query.select(root)
                    .where(cb.equal(root.<String>get(JobQueue.PROPERTY_PROJECT_ID), projectId));
            try {
                result = em.createQuery(query).getSingleResult();
            } catch (NoResultException e) {
                result = new JobQueue(projectId);
                result = saveOrUpdate(result);
            }
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        return result;
    }

    /**
     * 
     * @param projectIds
     * @return
     */
    public List<JobQueue> getForProjectIds(@Nonnull List<Integer> projectIds) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(JobQueue.PROPERTY_PROJECT_ID, "pId", projectIds);
        return super.listWithJQL(buildQlSelect(prefix) + startWhere() + buildWhereClause(Operation.IN, prefix, parameter), parameter);
    }
    
    /**
     * 
     * @return List of JobQueue
     */
    public List<JobQueue> findRecent(Date since) {
    	List<JobQueue> results = null;
    	EntityManager em = getEntityManager();
    	try {
    		begin();
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<JobQueue> query = cb.createQuery(JobQueue.class);
	        Root<JobQueue> root = query.from(JobQueue.class);
	        query.select(root)
                    .where(cb.greaterThan(root.<Date>get(JobQueue.PROPERTY_MODIFIED), since))
                    .orderBy(cb.desc(root.get(JobQueue.PROPERTY_PROJECT_ID)));
	        results = em.createQuery(query).getResultList();
	        commit();
        } catch (Exception e) {
        	rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
    	} finally {
    		cleanup();
    	}
    	return results;
    }

    /**
     * The jobs the job tree shows, read as columns so that no queue's whole job history is loaded. Selects the
     * jobs of one project's queue, or of every queue modified after since, that are not deleted and, unless
     * includeFinished, have not ended; plus any of jobIds in those queues whatever their state.
     *
     * @param projectId the project whose queue to read, or null to read the queues modified after since
     * @param since     used when projectId is null
     * @param jobIds    jobs to include even when deleted or ended, such as those agents still report
     * @return the jobs, in no particular order
     */
    public List<QueuedJob> findQueuedJobs(Integer projectId, Date since, boolean includeFinished,
                                          @Nonnull Collection<Integer> jobIds) {
        List<QueuedJob> results = null;
        EntityManager em = getEntityManager();
        try {
            begin();
            String jql = "SELECT NEW " + QueuedJob.class.getName() + "(q.projectId, j.id, j.name, j.status,"
                    + " j.incrementStrategy, j.totalVirtualUsers, j.startTime, j.endTime, j.useTwoStep,"
                    + " j.reportingMode, j.baselineVirtualUsers, j.targetRampRate, j.numAgents, j.rampTimeSeconds,"
                    + " j.rampTime)"
                    + " FROM " + JobQueue.class.getName() + " q JOIN q.jobs j"
                    + " WHERE " + (projectId != null ? "q.projectId = :projectId" : "q.modified > :since")
                    + " AND ((j.status <> :deleted" + (includeFinished ? "" : " AND j.endTime IS NULL") + ")"
                    + (jobIds.isEmpty() ? "" : " OR j.id IN :jobIds") + ")";
            TypedQuery<QueuedJob> query = em.createQuery(jql, QueuedJob.class)
                    .setParameter("deleted", JobQueueStatus.Deleted);
            if (projectId != null) {
                query.setParameter("projectId", projectId);
            } else {
                query.setParameter("since", since);
            }
            if (!jobIds.isEmpty()) {
                query.setParameter("jobIds", jobIds);
            }
            results = query.getResultList();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        return results;
    }

    /**
     * @param jobId
     * @return the project id of the queue holding the job, or null when no queue holds it
     */
    public Integer findProjectIdForJobId(int jobId) {
        List<Integer> results = null;
        EntityManager em = getEntityManager();
        try {
            begin();
            results = em.createQuery("SELECT q.projectId FROM " + JobQueue.class.getName()
                            + " q JOIN q.jobs j WHERE j.id = :jobId", Integer.class)
                    .setParameter("jobId", jobId)
                    .setMaxResults(1)
                    .getResultList();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     *
     * @param jobId
     * @return JobQueue
     */
    public JobQueue findForJobId(Integer jobId) {
        try {
            String string = "select x.test_id from test_instance_jobs x where x.job_id = ?";
            Query q = getEntityManager().createNativeQuery(string);
            q.setParameter(1, jobId);
            Integer result = null;
            try {
            	result = (Integer) q.getSingleResult();
            } catch (NoResultException nre) {
            	return null;
            }
            return findById(result);
        } catch (Exception e) {
            LOG.error("Error finding for Job ID: " + e, e);
        }
        return null;
    }
}
