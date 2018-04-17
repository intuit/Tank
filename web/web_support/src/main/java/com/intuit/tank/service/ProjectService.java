/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.PropertyComparer;
import com.intuit.tank.PropertyComparer.SortOrder;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectDescription;

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
@Named
public class ProjectService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjectDao prjDao;

    /**
     * Returns the list of projects
     * 
     * @return
     */
    public List<Project> getProjectList() {
        List<Project> projects = prjDao.findAll();
        projects.sort(new PropertyComparer<Project>("name", SortOrder.ASCENDING));
        return projects;
    }

    /**
     * Returns the Project with the given id.
     * 
     * @param id
     *            project id.
     * @return
     */
    public Project getProject(int id) {
        return prjDao.findById(id);
    }

    /**
     * Returns list of project descriptions for all the projects.
     * 
     * @return
     */
    public List<ProjectDescription> getProjectDescriptions() {
        return getProjectList().stream().map(ProjectDescription::new).collect(Collectors.toList());
    }

    /**
     * Deletes a project with the given id.
     * 
     * @param id
     */
    public void deleteProject(int id) {
        prjDao.delete(id);
    }

}
