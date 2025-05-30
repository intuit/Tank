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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import org.hibernate.jpa.QueryHints;

/**
 * ProductDao
 *
 * @author dangleton
 */
public class ProjectDao extends OwnableDao<Project> {
    private static final Logger LOG = LogManager.getLogger(ProjectDao.class);

    public ProjectDao() {
        super();
        setReloadEntities(true);
    }

    /**
     * Shallow project lookup used to avoid name collision
     *
     * @param name
     * @return
     */
    public Project findByName(@Nonnull String name) {
        Project project = null;
        EntityManager em = getEntityManager();
        try {
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Project> query = cb.createQuery(Project.class);
            Root<Project> root = query.from(Project.class);
            query.select(root);
            query.where(cb.equal(root.<String>get(Project.PROPERTY_NAME), name));
            project = em.createQuery(query).getSingleResult();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
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
        }
        project = saveOrUpdate(project);
        return project;
    }

    @Override
    public Project saveOrUpdate(@Nonnull Project entity) throws HibernateException {
        entity.setModified(new Date());
        return super.saveOrUpdate(entity);
    }

    /**
     * Deep lookup of full project, initiate eager loading when needed.
     *
     * @param id the primary key
     * @return the entity or null
     */
    @Nullable
    public Project findByIdEager(@Nonnull Integer id) {
        Project project = null;
        try {
            begin();
            project = getEntityManager().find(Project.class, id);
            Hibernate.initialize(project.getWorkloads().get(0).getJobConfiguration());
            Hibernate.initialize(project.getWorkloads().get(0).getTestPlans());
            commit();
        } catch (Exception e) {
            rollback();
            LOG.info("No entities for Project id " + id);
        } finally {
            cleanup();
        }
        return project;
    }

    /**
     * Override BaseDao to deep lookup finaAll Projects
     * This is very slow, thousands of queries, don't use this.
     *
     * @return the nonnull list of entities
     * @throws HibernateException if there is an error in persistence
     */
    @Nonnull
    @Override
    public List<Project> findAll() throws HibernateException {
        List<Project> results = Collections.emptyList();
        EntityManager em = getEntityManager();
        try {
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Project> query = cb.createQuery(Project.class);
            Root<Project> root = query.from(Project.class);
            Fetch<Project, Workload> wl = root.fetch(Project.PROPERTY_WORKLOADS);
            wl.fetch(Workload.PROPERTY_JOB_CONFIGURATION);
            query.select(root);
            results = em.createQuery(query).getResultList();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            LOG.info("No entities found at all for Project");
        } finally {
            cleanup();
        }
        return results;
    }

    /**
     * Shallow find of all Projects, used for debugger request.
     *
     * @return the nonnull list of entities
     * @throws HibernateException if there is an error in persistence
     */
    @Nonnull
    public List<Project> findAllFast() throws HibernateException {
        List<Project> results = Collections.emptyList();
        EntityManager em = getEntityManager();
        try {
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Project> query = cb.createQuery(Project.class);
            Root<Project> root = query.from(Project.class);
            query.select(root);
            results = em.createQuery(query).getResultList();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            LOG.info("No entities found at all for Project");
        } finally {
            cleanup();
        }
        return results;
    }

    @Nullable
    public Project loadScripts(Integer ProjectId) {
        Project project = findByIdEager(ProjectId);
        if (project != null) {
            ScriptDao dao = new ScriptDao();
            for (TestPlan testPlan : project.getWorkloads().get(0).getTestPlans()) {
                for (ScriptGroup scriptGroup : testPlan.getScriptGroups()) {
                    for (ScriptGroupStep scriptGroupStep : scriptGroup.getScriptGroupSteps()) {
                        dao.loadScriptSteps(scriptGroupStep.getScript());
                    }
                }
            }
        }
        return project;
    }
}
