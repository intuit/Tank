/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.job;

/*
 * #%L
 * Job Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.api.model.v1.job.JobTO;
import com.intuit.tank.project.JobInstance;

/**
 * FilterServiceUtil
 * 
 * @author dangleton
 * 
 */
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
