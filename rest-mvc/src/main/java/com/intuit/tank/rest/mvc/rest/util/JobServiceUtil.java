/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobTO;

public class JobServiceUtil {

    /**
     * @param g
     * @return
     */
    public static JobTO jobToTO(JobInstance job) {
        JobTO ret = new JobTO();
        ret.setId(job.getId());
        ret.setName(job.getName());
        ret.setEndTime(job.getEndTime());
        ret.setStartTime(job.getStartTime());
        ret.setLocation(job.getLocation());
        ret.setStatus(job.getStatus().name());
        ret.setNumUsers(job.getTotalVirtualUsers());
        ret.setRampTimeMilis(job.getRampTime());
        ret.setSimulationTimeMilis(job.getSimulationTime());
        return ret;
    }

}
