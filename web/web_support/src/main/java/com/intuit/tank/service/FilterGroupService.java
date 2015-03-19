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
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import com.intuit.tank.dao.FilterGroupDao;
import com.intuit.tank.project.ScriptFilterGroup;

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
@Model
public class FilterGroupService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private FilterGroupDao dao;

    /**
     * Returns the list of projects
     * 
     * @return
     */
    public List<ScriptFilterGroup> getFilterGroups() {
        return dao.findAll();
    }

    /**
     * Returns the Project with the given id.
     * 
     * @param id
     *            project id.
     * @return
     */
    public ScriptFilterGroup getScriptFilterGroups(int id) {
        return dao.findById(id);
    }

    // /**
    // * Returns list of project descriptions for all the projects.
    // *
    // * @return
    // */
    // public List<ProjectDescription> getProjectDescriptions() {
    // List<Project> projectList = getProjectList();
    // List<ProjectDescription> result = new ArrayList<ProjectDescription>();
    // for (Project project : projectList) {
    // result.add(new ProjectDescription(project));
    // }
    // return result;
    // }

    /**
     * Deletes a project with the given id.
     * 
     * @param id
     */
    public void deleteScriptFilterGroup(int id) {
        dao.delete(id);
    }

    /**
     * Saves/Updates a ScriptFilterGroup instance to the database
     * 
     * @param sfg
     *            ScriptFilterGroup entity that needs to be saved to the database.
     * @return
     */
    public ScriptFilterGroup saveFilterGroup(ScriptFilterGroup sfg) {
        return dao.saveOrUpdate(sfg);
    }

}
