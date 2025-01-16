/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.jobs.models.JobTO;

public class JobServiceUtil {

    /**
     * @param job
     * @return JobTO
     */
    public static JobTO jobToTO(JobInstance job) {
        return JobTO.builder()
                .withId(job.getId())
                .withName(job.getName())
                .withEndTime(job.getEndTime())
                .withStartTime(job.getStartTime())
                .withLocation(job.getLocation())
                .withStatus(job.getStatus().name())
                .withNumUsers(job.getTotalVirtualUsers())
                .withRampTimeMilis(job.getRampTime())
                .withSimulationTimeMilis(job.getSimulationTime())
                .build();
    }

}
