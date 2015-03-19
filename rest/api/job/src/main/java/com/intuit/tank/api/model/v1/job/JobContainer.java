/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.job;

/*
 * #%L
 * Job Rest Api
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
 * JobContainer jaxb container for JobTO
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "FilterContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "jobs"
})
public class JobContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "jobs", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "job", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<JobTO> jobs = new ArrayList<JobTO>();

    /**
     * @param jobs
     */
    public JobContainer(List<JobTO> jobs) {
        super();
        this.jobs = jobs;
    }

    /**
     * 
     */
    public JobContainer() {
        super();
    }

    /**
     * @return the jobs
     */
    public List<JobTO> getJobs() {
        return jobs;
    }

}
