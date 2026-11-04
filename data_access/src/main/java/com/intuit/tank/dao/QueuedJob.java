/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.dao;

import java.util.Date;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

/**
 * The columns of a queued job the job trees show, read without loading the JobInstance, its eager collections
 * or its job_details lob.
 */
public record QueuedJob(int projectId, int jobId, String name, JobQueueStatus status,
                        IncrementStrategy incrementStrategy, int totalVirtualUsers, Date startTime, Date endTime,
                        Boolean useTwoStep, String reportingMode, int baselineVirtualUsers, Double targetRampRate,
                        int numAgents, int rampTimeSeconds, Long rampTimeMillis) {

    public boolean isUseTwoStep() {
        return useTwoStep != null && useTwoStep;
    }

    /** As {@link com.intuit.tank.project.BaseJob#getTargetRampRate()} */
    public double getTargetRampRate() {
        return targetRampRate != null ? targetRampRate : 1.0;
    }

    /** As {@link com.intuit.tank.project.BaseJob#getRampTime()} */
    public long getRampTime() {
        return rampTimeMillis != null ? rampTimeMillis : rampTimeSeconds * 1000L;
    }
}
