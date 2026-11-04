/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.Project;
import com.intuit.tank.vm.settings.AccessRight;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Job control checks. Matches the JSF job tree: a caller may control a job when they have
 * {@link AccessRight#CONTROL_JOB} or own the job's project.
 */
public final class JobAuthorization {

    private static final String SERVICE = "jobs";

    private JobAuthorization() {
    }

    public static void requireJobControl(Integer jobId) {
        if (RestAuthorization.hasRight(AccessRight.CONTROL_JOB)) {
            return;
        }
        RestAuthorization.requireRightOrOwner(AccessRight.CONTROL_JOB, findProjectForJob(jobId), SERVICE);
    }

    public static void requireJobControl(String jobId) {
        requireJobControl(NumberUtils.isDigits(jobId) ? Integer.valueOf(jobId) : null);
    }

    private static Project findProjectForJob(Integer jobId) {
        if (jobId == null) {
            return null;
        }
        JobQueue queue = new JobQueueDao().findForJobId(jobId);
        return queue != null ? new ProjectDao().findById(queue.getProjectId()) : null;
    }
}
