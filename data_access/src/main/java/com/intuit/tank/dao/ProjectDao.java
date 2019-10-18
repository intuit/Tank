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

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class ProjectDao extends OwnableDao<Project> {

    public ProjectDao() {
        super();
        setReloadEntities(true);
    }

    /**
     * 
     * @param name
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Project findByName(@Nonnull String name) {
    	Project project = null;
    	EntityManager em = getEntityManager();
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Project> query = cb.createQuery(Project.class);
    	Root<Project> root = query.from(Project.class);
    	Fetch<Project, Workload>  wl = root.fetch(Project.PROPERTY_WORKLOADS);
    	wl.fetch("jobConfiguration");
    	query.select(root);
    	query.where(cb.equal(root.<String>get(Project.PROPERTY_NAME), name));
    	project = em.createQuery(query).getSingleResult();
    	Hibernate.initialize(project.getWorkloads().get(0).getJobConfiguration());
    	Hibernate.initialize(project.getWorkloads().get(0).getTestPlans());
    	return project;
    }

    /**
     * @param project
     * @return
     */
    public synchronized Project saveOrUpdateProject(Project project) {
        boolean saveAs = project.getId() == 0;
        for (Workload w : project.getWorkloads()) {
            w.setParent(project);
            if (saveAs) {
                w.setId(0);
            }
            JobConfiguration jobConfiguration = w.getJobConfiguration();
            jobConfiguration.setParent(w);
            jobConfiguration.setJobRegions(JobRegionDao.cleanRegions(jobConfiguration.getJobRegions()));
            // for (TestPl sg : w.getTestPlans()) {
            // sg.setParent(w);
            // if (saveAs) {
            // sg.setId(0);
            // }
            // for (ScriptGroupStep sgs : sg.getScriptGroupSteps()) {
            // sgs.setParent(sg);
            // if (saveAs) {
            // sgs.setId(0);
            // }
            // }
            // }
        }
        project = saveOrUpdate(project);
        return project;
    }

    @Override
    public Project saveOrUpdate(Project entity) throws HibernateException {
        entity.setModified(new Date());
        return super.saveOrUpdate(entity);
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Project findById(@Nonnull Integer id) {
    	Project project = null;
    	project = getEntityManager().find(Project.class, id);
    	Hibernate.initialize(project.getWorkloads().get(0).getJobConfiguration());
    	Hibernate.initialize(project.getWorkloads().get(0).getTestPlans());
    	return project;
    }

    /**
     * Finds all Objects of type T_ENTITY
     * 
     * @return the nonnull list of entities
     * @throws HibernateException
     *             if there is an error in persistence
     */
    @Nonnull
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Project> findAll() throws HibernateException {
    	List<Project> results = null;
    	EntityManager em = getEntityManager();
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Project> query = cb.createQuery(Project.class);
    	Root<Project> root = query.from(Project.class);
    	Fetch<Project, Workload> wl = root.fetch(Project.PROPERTY_WORKLOADS);
    	wl.fetch("jobConfiguration");
    	query.select(root);
    	results = em.createQuery(query).getResultList();
    	for (Project project : results) {
    	    Hibernate.initialize(project.getWorkloads().get(0).getJobConfiguration());
    	}
    	return results;
    }
    
    @Nullable
    public Project loadScripts(Integer ProjectId) {
    	Project project = findById(ProjectId);
        ScriptDao sd = new ScriptDao();
        for (TestPlan testPlan : project.getWorkloads().get(0).getTestPlans()) {
            for (ScriptGroup scriptGroup : testPlan.getScriptGroups()) {
                for (ScriptGroupStep scriptGroupStep : scriptGroup.getScriptGroupSteps()) {
                    sd.loadScriptSteps(scriptGroupStep.getScript());
                }
            }
        }
        return project;
    }
}
