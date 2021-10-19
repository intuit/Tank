package com.intuit.tank;

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

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.util.Messages;

import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectLoader;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;
import com.intuit.tank.wrapper.VersionContainer;

@Named
@ViewScoped
public class ProjectDescriptionBean extends SelectableBean<Project> implements Serializable, Multiselectable<Project> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjectLoader projectLoader;

    @Inject
    private Security security;

    @Inject
    private Messages messages;

    @Inject
    @Modified
    private Event<ModifiedProjectMessage> projectEvent;

    private SelectableWrapper<Project> selectedProject;
    
    private int version;

    @Inject
    private PreferencesBean userPrefs;

    @PostConstruct
    public void init() {
        tablePrefs = new TablePreferences(userPrefs.getPreferences().getProjectTableColumns());
        tablePrefs.registerListener(userPrefs);
    }

    public void deleteSelectedProject() {
        if (selectedProject != null) {
            delete(selectedProject.getEntity());
        }
    }

    /**
     * @return the selectedProject
     */
    public SelectableWrapper<Project> getSelectedProject() {
        return selectedProject;
    }

    /**
     * @param selectedProject
     *            the selectedProject to set
     */
    public void setSelectedProject(SelectableWrapper<Project> selectedProject) {
        this.selectedProject = selectedProject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getEntityList(ViewFilterType viewFilter) {
        VersionContainer<Project> container = projectLoader.getVersionContainer(viewFilter);
        this.version = container.getVersion();
        List<Project> all = new ProjectDao().findFiltered(viewFilter);
        return all;
    }

    /**
     * @return the creatorList
     */
    public SelectItem[] getCreatorList() {
        return projectLoader.getCreatorList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrent() {
        return projectLoader.isCurrent(version);
    }

    /**
     * deletes a project
     * 
     * @param project
     */
    public void delete(Project project) {
        if (!security.hasRight(AccessRight.DELETE_PROJECT) && !security.isOwner(project)) {
            messages.warn("You do not have permission to delete this project.");
        } else {
            try {
                new ProjectDao().delete(project);
                messages.info("Project " + project.getName() + " has been removed.");
                projectEvent.fire(new ModifiedProjectMessage(project, this));
                refresh();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 
     * @param project
     */
    public void updateProject(Project project) {
        new ProjectDao().saveOrUpdateProject(project);
        messages.info("Project " + project.getName() + " has been updated.");
        projectEvent.fire(new ModifiedProjectMessage(project, this));
    }

}
