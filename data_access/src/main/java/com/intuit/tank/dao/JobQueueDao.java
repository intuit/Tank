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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LockOptions;

import com.intuit.tank.project.JobQueue;

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
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(JobQueue.PROPERTY_PROJECT_ID, "pId", projectId);
        List<JobQueue> resultList = super.listWithJQL(buildQlSelect(prefix) + startWhere() + buildWhereClause(Operation.EQUALS, prefix, parameter), parameter);
        if (resultList.size() > 1) {
            LOG.warn("Have " + resultList.size() + " queues for project " + projectId);
        }
        if (!resultList.isEmpty()) {
            result = resultList.get(0);
        }
        if (result == null) {
            result = new JobQueue(projectId);
            result = saveOrUpdate(result);
        } else {
            getHibernateSession().refresh(result, LockOptions.READ);
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
    public List<JobQueue> findRecent() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -5);
    	List<JobQueue> results = null;
    	EntityManager em = getEntityManager();
    	try {
    		begin();
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<JobQueue> query = cb.createQuery(JobQueue.class);
	        Root<JobQueue> root = query.from(JobQueue.class);
	        query.select(root);
	        query.where(cb.greaterThan(root.<Date>get(JobQueue.PROPERTY_MODIFIED), c.getTime()));
	        query.orderBy(cb.desc(root.get(JobQueue.PROPERTY_PROJECT_ID)));
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
