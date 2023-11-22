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
import java.util.List;

import javax.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.QueryHint;
import jakarta.persistence.criteria.*;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.project.JobQueue;
import org.hibernate.annotations.QueryHints;

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
