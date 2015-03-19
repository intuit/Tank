package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.vm.vmManager.JobRequest;

/**
 * 
 * WorkLoadModel
 * 
 * 
 */
public class JobInstanceAgentModel {
    private JobRequest job;
    private int startTime = 0;

    public JobInstanceAgentModel(JobRequest job) {
        this.job = job;
    }

    public int getStartTime() {
        return startTime;
    }

    /**
     * @return the job
     */
    public JobRequest getJob() {
        return job;
    }

    public int getNumberConcurrent() {
        return job.getTotalVirtualUsers();
    }

    public long getRampTime() {
        return job.getRampTime();
    }

    public String getScriptsXmlUrl() {
        return job.getScriptsXmlUrl();
    }

}
