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

import org.hibernate.HibernateException;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.Project;
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

}
