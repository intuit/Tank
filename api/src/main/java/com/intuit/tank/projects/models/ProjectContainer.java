/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.projects.models;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "ProjectContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "projects"
})
public class ProjectContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Singular(ignoreNullCollections = true)
    @XmlElementWrapper(name = "projects", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "project", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ProjectTO> projects;

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
