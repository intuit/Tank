package com.intuit.tank.job;

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
        boolean retVal = false;
        if (status.equals(JobQueueStatus.Created.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Queued.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Paused.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.RampPaused.toString())) {
            retVal = true;
        }
        return retVal;
    }

    public static boolean canBePaused(String status) {
        boolean retVal = false;
        if (status.equals(JobQueueStatus.Running.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Starting.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.RampPaused.toString())) {
            retVal = true;
        }
        return retVal;
    }

    public static boolean canRampBePaused(String status) {
        boolean retVal = false;
        if (status.equals(JobQueueStatus.Running.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Starting.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Starting.toString())) {
            retVal = true;
        }
        return retVal;
    }

    public static boolean canBeStopped(String status) {
        boolean retVal = false;
        if (status.equals(JobQueueStatus.Starting.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Running.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Paused.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.RampPaused.toString())) {
            retVal = true;
        }

        return retVal;
    }

    public static boolean canBeKilled(String status) {
        boolean retVal = false;
        if (status.equals(JobQueueStatus.Starting.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Running.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Paused.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.Stopped.toString())) {
            retVal = true;
        } else if (status.equals(JobQueueStatus.RampPaused.toString())) {
            retVal = true;
        }
        return retVal;
    }
}
