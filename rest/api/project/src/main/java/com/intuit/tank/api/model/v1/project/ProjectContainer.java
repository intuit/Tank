/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.project;

/*
 * #%L
 * Project Rest API
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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * ProjectContainer jaxb container for ProjectTo
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "ProjectContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "projects"
})
public class ProjectContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "projects", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "project", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ProjectTO> projects = new ArrayList<ProjectTO>();

    public ProjectContainer() {

    }

    /**
     * @param projects
     */
    public ProjectContainer(List<ProjectTO> projects) {
        this.projects = projects;
    }

    /**
     * @return the scripts
     */
    public List<ProjectTO> getProjects() {
        return projects;
    }

    /**
     * @param projects
     *            the scripts to set
     */
    public void setProjects(List<ProjectTO> projects) {
        this.projects = projects;
    }

}
