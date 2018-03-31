/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.report;

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
import java.util.Date;

import com.intuit.tank.project.JobInstance;

/**
 * JobReportData
 * 
 * @author dangleton
 * 
 */
public class JobReportData implements Serializable {

    private static final long serialVersionUID = 1L;

    public String projectName;
    public long duration;
    public JobInstance jobInstance;

    /**
     * @param projectName
     * @param duration
     * @param jobInstance
     */
    public JobReportData(String projectName, JobInstance jobInstance) {
        super();
        this.projectName = projectName;
        this.duration = calculateDuration(jobInstance);
        this.jobInstance = jobInstance;
    }

    public String getStatus() {
        return jobInstance.getStatus().name();
    }

    /**
     * @param jobInstance2
     * @return
     */
    private long calculateDuration(JobInstance job) {
        long end = job.getEndTime().getTime() / 60000;
        long start = job.getStartTime().getTime() / 60000;
        return (end - start) * 60;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    public Date getStartTime() {
        return jobInstance.getStartTime();
    }

    /**
     * @return the jobInstance
     */
    public JobInstance getJobInstance() {
        return jobInstance;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof JobReportData) {
            return jobInstance.equals(((JobReportData) obj).jobInstance);
        }
        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return jobInstance.hashCode();
    }

}
