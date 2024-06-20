/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.jobs;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.jobs.models.CreateJobRequest;
import com.intuit.tank.jobs.models.JobContainer;
import com.intuit.tank.jobs.models.JobTO;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Map;

public interface JobServiceV2 {

    /**
     * Test method to test if the service is up.
     *
     * @return non-null String value.
     */
    public String ping();

    /**
     * Gets a job by jobId
     *
     * @param jobId Job ID
     *
     * @throws GenericServiceResourceNotFoundException
     *         if an error occurs when returning a job
     *
     * @return Job JSON response
     */
    public JobTO getJob(Integer jobId);

    /**
     * Gets streaming output of the harness XML script file for a specific job
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors returning harness XML script content
     *
     * @return jobs's harness XML file
     */
    public StreamingResponseBody getTestScriptForJob(Integer jobId);


    /**
     * Downloads harness XML script file for a specific job
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors downloading harness XML script
     *
     * @return jobs's harness XML file
     */
    public Map<String, StreamingResponseBody> downloadTestScriptForJob(Integer jobId);

    /**
     * Gets all jobs for a specific project
     *
     * @param projectId Project ID
     *
     * @throws GenericServiceResourceNotFoundException
     *         if an error occurs when returning a project's jobs
     *
     * @return list of jobs for a specific project
     */
    public JobContainer getJobsByProject(Integer projectId);

    /**
     * Gets all jobs
     *
     * @throws GenericServiceResourceNotFoundException
     *         if an error occurs when returning all jobs
     *
     * @return list of jobs JSON response
     */
    public JobContainer getAllJobs();

    /**
     * Creates a job and returns the jobId
     *
     * @param request Job Request JSON payload
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if an error occurs when creating a job
     *
     * @return corresponding job id and created status
     */
    public Map<String, String> createJob(CreateJobRequest request);

    /**
     * Gets the status of a specific job
     *
     * @param jobId jobId for job
     *
     * @throws GenericServiceResourceNotFoundException
     *         if an error occurs when returning job status
     *
     * @return Status of job
     */
    public String getJobStatus(Integer jobId);

    /**
     * Returns job statuses for all jobs
     *
     * @return list of job statuses
     */
    public List<Map<String, String>> getAllJobStatus();

    /**
     * Returns AWS Instance/VM statuses for job
     *
     * @param jobId
     *         job id
     *
     * @throws GenericServiceResourceNotFoundException
     *         if an error occurs when returning job instance status
     *
     * @return VM statuses JSON response of job
     */
    public CloudVmStatusContainer getJobVMStatus(String jobId);


    // Job Status operations

    /**
     * Starts a job based on the provided jobId
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error starting the job
     *
     * @param jobId jobId for job
     *
     * @return job status
     */
    public String startJob(Integer jobId);

    /**
     * Stops a job based on the provided jobId
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error stopping the job
     *
     * @param jobId jobId for job
     *
     * @return job status
     */
    public String stopJob(Integer jobId);

    /**
     * Pauses a running job based on the provided jobId
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error pausing the job
     *
     * @param jobId jobId for job
     *
     * @return job status
     */
    public String pauseJob(Integer jobId);


    /**
     * Resumes a paused job based on the provided jobId
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error resuming the job
     *
     * @param jobId jobId for job
     *
     * @return job status
     */
    public String resumeJob(Integer jobId);

    /**
     * Kills a job based on the provided jobId
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error terminating the job
     *
     * @param jobId jobId for job
     *
     * @return job status
     */
    public String killJob(Integer jobId);
}
