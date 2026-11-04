/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.jobs;

import com.intuit.tank.rest.mvc.rest.models.JobActionResult;
import com.intuit.tank.rest.mvc.rest.models.JobDetails;
import com.intuit.tank.rest.mvc.rest.models.JobLaunchRequest;
import com.intuit.tank.rest.mvc.rest.models.JobPreview;
import com.intuit.tank.rest.mvc.rest.models.JobTree;
import com.intuit.tank.rest.mvc.rest.models.QueuedJob;
import com.intuit.tank.rest.mvc.rest.models.Timeseries;

/**
 * Queueing jobs from projects, the live job tree, and job and agent control for the web UI.
 * Every method requires a user caller.
 */
public interface JobQueueServiceV2 {

    /**
     * Shows what a job queued from the saved project now would run, without queueing it.
     */
    JobPreview previewJob(Integer projectId, JobLaunchRequest request);

    /**
     * Queues a job from the saved project. Needs {@code CONTROL_JOB} or ownership of the project, and a
     * project that passes validation.
     */
    QueuedJob queueJob(Integer projectId, JobLaunchRequest request);

    /**
     * @param projectId       one project's jobs, or null for every project with activity in the last week
     * @param includeFinished whether to include jobs that have ended
     */
    JobTree getJobTree(Integer projectId, boolean includeFinished);

    /**
     * Sends an action to a job. Needs {@code CONTROL_JOB} or ownership of the job's project.
     *
     * @throws com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException when the
     *         job's status does not allow the action
     */
    JobActionResult controlJob(Integer jobId, String action);

    /**
     * Sends an action to one agent of a job. {@code start} and {@code start-load} apply to whole jobs only.
     */
    JobActionResult controlAgent(String instanceId, String action);

    /**
     * Deletes a job that has not started. Deleted jobs are hidden but kept.
     */
    void deleteJob(Integer jobId);

    JobDetails getJobDetails(Integer jobId);

    /**
     * Users per script over time, as reported by the job's agents to this controller.
     */
    Timeseries getUserSeries(Integer jobId);

    /**
     * Transactions per second per request over time.
     *
     * @param instanceId only this agent, or null for the whole job
     * @param sinceMs    only samples after this time (epoch milliseconds), or null for all
     */
    Timeseries getTpsSeries(Integer jobId, String instanceId, Long sinceMs);
}
