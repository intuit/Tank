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
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.Workload;


/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class WorkloadDao extends BaseDao<Workload> {
    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(WorkloadDao.class);

    private final ScriptDao scriptDao = new ScriptDao();

    public WorkloadDao() {
        super();
        setReloadEntities(true);
    }
    
    
    /**
     * This is an override of the BaseEntity to initiate eager loading when needed.
     * 
     * @param id
     *            the primary key
     * @return the entity or null
     */
    @Nullable
    @Override
    public Workload findById(@Nonnull Integer id) {
    	Workload workload = null;
    	try {
   		begin();
    		workload = getEntityManager().find(Workload.class, id);
    		if (workload != null) {
    			workload.getJobConfiguration();
    			// Force-initialize testPlans (LAZY) and its eagerly-loaded children
    			// in one query to avoid N+1 lazy-load round-trips per TestPlan
    			CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    			CriteriaQuery<Workload> query = cb.createQuery(Workload.class);
    			Root<Workload> root = query.from(Workload.class);
    			root.fetch(Workload.PROPERTY_TEST_PLANS, JoinType.LEFT)
    			    .fetch("scriptGroups", JoinType.LEFT)
    			    .fetch("steps", JoinType.LEFT);
    			query.select(root).distinct(true)
    			     .where(cb.equal(root.get("id"), id));
    			getEntityManager().createQuery(query).getSingleResult();
    		}
    		commit();
        } catch (Exception e) {
    	    rollback();
            LOG.error("Error loading workload {}: {}", id, e.toString(), e);
            throw new RuntimeException(e);
		} finally {
			cleanup();
		}
		return workload;
    }

    public Workload loadScriptsForWorkload(Workload workload) {
        List<Script> scripts = workload.getTestPlans().stream()
                .flatMap(testPlan -> testPlan.getScriptGroups().stream())
                .flatMap(scriptGroup -> scriptGroup.getScriptGroupSteps().stream())
                .map(ScriptGroupStep::getScript)
                .collect(java.util.stream.Collectors.toList());
        scriptDao.loadScriptStepsBulk(scripts);
        return workload;
    }

}
