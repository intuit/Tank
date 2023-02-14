/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.jobs;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
