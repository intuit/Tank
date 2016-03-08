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
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectDTO;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class ProjectDao extends OwnableDao<Project> {

    /**
     * @param entityClass
     */
    public ProjectDao() {
        super();
        setReloadEntities(true);
    }

    /**
     * @param id
     * @return
     */
    @Nonnull
    public List<Workload> getWorkloadsForProject(@Nonnull Integer projectId) {
        return findById(projectId).getWorkloads();
    }

    /**
     * 
     * @param name
     * @return
     */
    public Project findByName(String name) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(Project.PROPERTY_NAME, "n", name);
        StringBuilder sb = new StringBuilder();
        sb.append(buildQlSelect(prefix)).append(startWhere())
                .append(buildWhereClause(Operation.EQUALS, prefix, parameter));
        return super.findOneWithJQL(sb.toString(), parameter);
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
    public Project findById(@Nonnull Integer id) {
    	Project project = null;
    	try {
    		begin();
    		project = getEntityManager().find(Project.class, id);
    		if( project != null) {
    			project.getWorkloads().get(0).getJobConfiguration().readConfig();
    			project.getWorkloads().get(0).getJobConfiguration().getJobRegions();
    			project.getWorkloads().get(0).getJobConfiguration().getVariables();
    			project.getWorkloads().get(0).getJobConfiguration().getDataFileIds();
    			project.getWorkloads().get(0).getJobConfiguration().getNotifications();
    			for ( TestPlan tp : project.getWorkloads().get(0).getTestPlans() ) {
    				for (ScriptGroup sg : tp.getScriptGroups() ) {
    					sg.getScriptGroupSteps();
    				}
    			}
    		}
    		commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
    	} finally {
    		cleanup();
    	}
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
    public List<Project> findAll() throws HibernateException {
    	List<Project> results = null;
    	EntityManager em = getEntityManager();
    	try {
    		begin();
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Project> query = cb.createQuery(Project.class);
	        Root<Project> root = query.from(Project.class);
	        Fetch<Project, Workload>  wl = root.fetch(Project.PROPERTY_WORKLOADS);
	        wl.fetch("jobConfiguration");
	        query.select(root);
	        results = em.createQuery(query).getResultList();
	        for (Project project : results) {
	        	project.getWorkloads().get(0).getJobConfiguration().getVariables();
	        	project.getWorkloads().get(0).getJobConfiguration().getDataFileIds();	        	
	        }
	        commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
    	} finally {
    		cleanup();
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
    
    /**
     * Finds all Objects of type T_ENTITY
     * 
     * @return the nonnull list of entities
     * @throws HibernateException
     *             if there is an error in persistence
     */
    @Nonnull
    public List<ProjectDTO> findAllProjectNames() throws HibernateException {
    	List<ProjectDTO> results = null;
    	EntityManager em = getEntityManager();
    	try {
    		begin();
	    	Session session = em.unwrap(Session.class);
	    	Criteria cr = session.createCriteria(Project.class)
	    			.setProjection(Projections.projectionList()
	    					.add( Projections.property("id"), "id")
	    					.add( Projections.property("created"), "created")
	    					.add( Projections.property("modified"), "modified")
	    					.add( Projections.property("creator"), "creator")
	    					.add( Projections.property("name"), "name"))
	    			.setResultTransformer(Transformers.aliasToBean(ProjectDTO.class));
	
	        results = cr.list();
	        commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
    	} finally {
    		cleanup();
    	}
        return results;
    }

}
