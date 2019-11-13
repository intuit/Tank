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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.ScriptFilter;

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@Named("scriptFilterService")
@Dependent
public class FilterService implements Serializable {

    @Inject
    ScriptFilterDao scriptFilterDao;

    private static final long serialVersionUID = 1L;

    /**
     * Returns the list of all ScriptFilters
     * 
     * @return
     */
    public List<ScriptFilter> getFilters() {
        return scriptFilterDao.findAll();
    }

    /**
     * Returns the ScriptFilter with the given id.
     * 
     * @param id
     *            project id.
     * @return
     */
    public ScriptFilter getScriptFilter(int id) {
        return scriptFilterDao.findById(id);
    }

    // /**
    // * Returns list of project descriptions for all the projects.
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
    //

    /**
     * Deletes a ScriptFilter with the given id.
     * 
     * @param id
     */
    public void deleteScriptFilter(int id) {
        scriptFilterDao.delete(id);
    }

    /**
     * Saves/updates an instance of ScriptFilter to the database.
     * 
     * @param sf
     *            An instance of ScriptFitler that needs to be saved to the database.
     * @return
     */
    public ScriptFilter saveFilter(ScriptFilter sf) {
        return scriptFilterDao.saveOrUpdate(sf);
    }

}
