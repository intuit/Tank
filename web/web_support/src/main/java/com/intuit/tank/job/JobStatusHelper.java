package com.intuit.tank.job;

import com.intuit.tank.api.model.v1.cloud.VMStatus;

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

import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

public class JobStatusHelper {

    public static boolean canBeRun(String status) {
        return (status.equals(JobQueueStatus.Created.toString())
        		|| status.equals(JobQueueStatus.Queued.toString())
        		|| status.equals(JobQueueStatus.Paused.toString())
        		|| status.equals(JobQueueStatus.RampPaused.toString()))
        		? true : false;
    }

    public static boolean canBePaused(String status) {
        return (status.equalsIgnoreCase(JobQueueStatus.Running.toString())
        		|| status.equals(JobQueueStatus.Starting.toString())
        		|| status.equals(JobQueueStatus.RampPaused.toString()))
        		? true : false;
    }

    public static boolean canRampBePaused(String status) {
    	return (status.equalsIgnoreCase(JobQueueStatus.Running.toString())
    			|| status.equals(JobQueueStatus.Starting.toString()))
            	? true : false;
    }

    public static boolean canBeStopped(String status) {
    	return (status.equalsIgnoreCase(JobQueueStatus.Running.toString())
    			|| status.equals(JobQueueStatus.Starting.toString())
    			|| status.equals(JobQueueStatus.Paused.toString())
    			|| status.equals(JobQueueStatus.RampPaused.toString()))
            	? true : false;
    }

    public static boolean canBeKilled(String status) {
        return (status.equalsIgnoreCase(JobQueueStatus.Running.toString())
        		|| status.equalsIgnoreCase(JobQueueStatus.Stopped.toString())
        		|| status.equalsIgnoreCase(JobQueueStatus.Starting.toString())
        		|| status.equals(VMStatus.stopping.toString())
        		|| status.equals(JobQueueStatus.Paused.toString())
        		|| status.equals(JobQueueStatus.RampPaused.toString()))
            	? true : false;
    }
}
