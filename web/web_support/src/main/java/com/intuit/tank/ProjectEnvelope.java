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

import org.apache.commons.lang.time.DateFormatUtils;

import com.intuit.tank.project.Project;

public class ProjectEnvelope {

    private boolean checked;
    private Project project;

    public ProjectEnvelope(boolean checked, Project project) {
        this.checked = checked;
        this.project = project;
    }

    public ProjectEnvelope(Project project) {
        this(false, project);
    }

    public ProjectEnvelope() {
        this(null);
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked
     *            the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    public String getCreated() {
        return DateFormatUtils.format(project.getCreated(), "dd/MM/yy HH:mm");
    }

    public String getModified() {
        return DateFormatUtils.format(project.getModified(), "dd/MM/yy HH:mm");
    }

}
