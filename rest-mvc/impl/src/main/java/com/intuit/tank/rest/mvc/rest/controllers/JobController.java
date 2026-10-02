/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.jobs.models.CreateJobRequest;
import com.intuit.tank.jobs.models.JobContainer;
import com.intuit.tank.rest.mvc.rest.models.JobActionResult;
import com.intuit.tank.rest.mvc.rest.models.JobDetails;
import com.intuit.tank.rest.mvc.rest.models.JobTree;
import com.intuit.tank.rest.mvc.rest.models.Timeseries;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobQueueServiceV2;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobServiceV2;
import com.intuit.tank.jobs.models.JobTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Resource;

@RestController
@RequestMapping(value = "/v2/jobs", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Jobs")
public class JobController {

    @Resource
    private JobServiceV2 jobService;

    @Resource
    private JobQueueServiceV2 jobQueueService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings job service", summary = "Check if job service is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job Service is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<String>(jobService.ping(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns all jobs descriptions", summary = "Get all job descriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all job descriptions"),
            @ApiResponse(responseCode = "404", description = "All job descriptions could not be found", content = @Content)
    })
    public ResponseEntity<JobContainer> getAllJobs() {
        return new ResponseEntity<>(jobService.getAllJobs(), HttpStatus.OK);
    }

    @RequestMapping(value = "{jobId}", method = RequestMethod.GET)
    @Operation(description = "Returns a specific job description by job id", summary = "Get a specific job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found job"),
            @ApiResponse(responseCode = "404", description = "Job could not be found", content = @Content)
    })
    public ResponseEntity<JobTO> getJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        return new ResponseEntity<>(jobService.getJob(jobId), HttpStatus.OK);
    }

    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    @Operation(description = "Returns all jobs under a specific project via project ID", summary = "Get all jobs from a specific project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found jobs for specific project"),
            @ApiResponse(responseCode = "404", description = "Jobs could not be found for that project ID", content = @Content)
    })
    public ResponseEntity<JobContainer> getJobsByProject(@PathVariable @Parameter(description = "The project ID associated with jobs", required = true) Integer projectId) {
        return new ResponseEntity<>(jobService.getJobsByProject(projectId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Given a job request payload, creates a new job under an existing project and returns new jobId and created status in response \n\n" +
            "Note: Make sure projectId matches an existing project to successfully create the job for that project \n\n" +
            "Parameters: \n\n" +
            "  - jobInstanceName and projectName are accepted as strings (both optional) \n\n" +
            "  - jobInstanceName overrides projectName for naming jobs \n\n" +
            "  - passing only projectName creates jobs named:   '{projectName}_{total_users}\\_users\\_{timestamp}' \n\n" +
            "  - rampTime and simulationTime are accepted as time strings i.e 60s, 12m, 24h \n\n" +
            "  - stopBehavior is matched against accepted values ( END_OF_STEP,  END_OF_SCRIPT,  END_OF_SCRIPT_GROUP,  END_OF_TEST ) \n\n" +
            "  - vmInstance matches against AWS EC2 Instance Types i.e m8g.large, m8g.xlarge, etc \n\n"+
            "  - workloadType can be set to increasing (linear workload) or standard (nonlinear workload)  \n\n"+
            "  - targetRampRate, targetRatePerAgent, and jobRegions.percentage fields apply to standard workloadType jobs (nonlinear) \n\n"+
            "  - projectId, userIntervalIncrement and numUsersPerAgent are accepted as integers \n\n" +
            "  - jobRegions.regions correspond to AWS regions in lowercase i.e us-west-2, us-east-2 \n\n" +
            "  - jobRegions.users and jobRegions.percentage are accepted as integer strings i.e \"100\", \"4000\" \n\n", summary =  "Create a new job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created job", content = @Content),
            @ApiResponse(responseCode = "400", description = "Could not create job due to bad request", content = @Content)
    })
    public ResponseEntity<Map<String, String>> createJob(
            @RequestBody @Parameter(description = "request", required = true) CreateJobRequest request) {
        Map<String, String> response = jobService.createJob(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().scheme("https").path("/{id}").buildAndExpand(response.get("JobId")).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @Operation(description = "Returns all current job statuses", summary = "Get all job statuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all job statuses", content = @Content),
            @ApiResponse(responseCode = "404", description = "All job statuses could not be found", content = @Content)
    })
    public ResponseEntity<List<Map<String, String>>> getAllJobStatus() {
        List<Map<String, String>> status = jobService.getAllJobStatus();
        if (status == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/status/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE })
    @Operation(description = "Returns a specific job status by job id", summary = "Get a specific job status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found specific job status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job status could not be found", content = @Content)
    })
    public ResponseEntity<String> getJobStatus(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.getJobStatus(jobId);
        if (status == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/instance-status/{jobId}", method = RequestMethod.GET)
    @Operation(description = "Returns list of agent/instance statuses for an existing job", summary = "Get list of instance statuses for job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found specific job instance statuses"),
            @ApiResponse(responseCode = "404", description = "Job instance statuses could not be found", content = @Content)
    })
    public ResponseEntity<CloudVmStatusContainer> getJobVMStatuses(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) String jobId) {
        CloudVmStatusContainer status = jobService.getJobVMStatus(jobId);
        if (status == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/script/{jobId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Gets streaming output of job's harness XML file", summary = "Get job's harness file", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned job's harness file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job's harness file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> getTestScriptForJob(@PathVariable @Parameter(description = "Job ID", required = true) Integer jobId) throws IOException {
        StreamingResponseBody response = jobService.getTestScriptForJob(jobId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(response);
    }

    @RequestMapping(value = "/download/{jobId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Downloads a job's harness XML file", summary = "Download the job's harness file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded job's harness file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job's harness file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadTestScriptForJob(@PathVariable @Parameter(description = "Job ID", required = true) Integer jobId) throws IOException {
        Map<String, StreamingResponseBody> response = jobService.downloadTestScriptForJob(jobId);
        if (response == null) return ResponseEntity.notFound().build();

        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .body(responseBody);
    }

    // Job Status Setters

    @RequestMapping(value = "/start/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(deprecated = true, description = "Deprecated: use POST /v2/jobs/{jobId}/<action>. Starts a specific job by job id", summary = "Start a specific job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully started job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> startJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.startJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/stop/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(deprecated = true, description = "Deprecated: use POST /v2/jobs/{jobId}/<action>. Stops a specific job by job id", summary = "Stop a specific job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully stopped job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> stopJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.stopJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/pause/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(deprecated = true, description = "Deprecated: use POST /v2/jobs/{jobId}/<action>. Pauses a specific job by job id", summary = "Pause a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully paused job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> pauseJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.pauseJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/resume/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(deprecated = true, description = "Deprecated: use POST /v2/jobs/{jobId}/<action>. Resumes a specific job by job id", summary = "Resume a paused job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully resumed job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> resumeJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.resumeJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/kill/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(deprecated = true, description = "Deprecated: use POST /v2/jobs/{jobId}/<action>. Terminates a specific job by job id", summary = "Terminate a specific job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully terminated job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> killJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.killJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    // Job queue pages

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @Operation(description = "Recent and running jobs grouped by project, with each agent's live status and the actions "
            + "the caller may take. Live figures come from the controller serving the request. Poll every 10 seconds or more",
            summary = "Get the live job tree")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the tree")
    })
    public ResponseEntity<JobTree> getJobTree(
            @RequestParam(required = false) @Parameter(description = "Only this project's jobs; default every project with activity in the last week") Integer projectId,
            @RequestParam(defaultValue = "false") @Parameter(description = "Include jobs that have ended") boolean includeFinished) {
        return ResponseEntity.ok(jobQueueService.getJobTree(projectId, includeFinished));
    }

    @RequestMapping(value = "/{jobId}/{action}", method = RequestMethod.POST)
    @Operation(description = "Sends an action to a job: start, start-load (two-step jobs), pause, resume, pause-ramp, "
            + "resume-ramp, stop or kill. Actions are asynchronous", summary = "Control a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action sent; returns the job's status"),
            @ApiResponse(responseCode = "400", description = "Unknown action", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to control the job", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such job", content = @Content),
            @ApiResponse(responseCode = "409", description = "The job's status does not allow the action", content = @Content)
    })
    public ResponseEntity<JobActionResult> controlJob(
            @PathVariable @Parameter(description = "The job ID", required = true) Integer jobId,
            @PathVariable @Parameter(description = "The action", required = true) String action) {
        return ResponseEntity.ok(jobQueueService.controlJob(jobId, action));
    }

    @RequestMapping(value = "/instances/{instanceId}/{action}", method = RequestMethod.POST)
    @Operation(description = "Sends an action to one agent of a job: pause, resume, pause-ramp, resume-ramp, stop or kill",
            summary = "Control an agent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action sent; returns the agent's status"),
            @ApiResponse(responseCode = "400", description = "Unknown action, or one for whole jobs only", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to control the job", content = @Content),
            @ApiResponse(responseCode = "404", description = "No agent with that instance ID reports to this controller", content = @Content),
            @ApiResponse(responseCode = "409", description = "The agent's status does not allow the action", content = @Content)
    })
    public ResponseEntity<JobActionResult> controlAgent(
            @PathVariable @Parameter(description = "The agent's instance ID", required = true) String instanceId,
            @PathVariable @Parameter(description = "The action", required = true) String action) {
        return ResponseEntity.ok(jobQueueService.controlAgent(instanceId, action));
    }

    @RequestMapping(value = "/{jobId}", method = RequestMethod.DELETE)
    @Operation(description = "Deletes a job that has not started", summary = "Delete a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to control the job", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such job", content = @Content),
            @ApiResponse(responseCode = "409", description = "The job has already started", content = @Content)
    })
    public ResponseEntity<Void> deleteJob(@PathVariable @Parameter(description = "The job ID", required = true) Integer jobId) {
        jobQueueService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{jobId}/details", method = RequestMethod.GET)
    @Operation(description = "The job details recorded when it was queued, with live user and failure totals",
            summary = "Get job details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the details"),
            @ApiResponse(responseCode = "404", description = "No such job", content = @Content)
    })
    public ResponseEntity<JobDetails> getJobDetails(@PathVariable @Parameter(description = "The job ID", required = true) Integer jobId) {
        return ResponseEntity.ok(jobQueueService.getJobDetails(jobId));
    }

    @RequestMapping(value = "/{jobId}/users-timeseries", method = RequestMethod.GET)
    @Operation(description = "Users per script over time, as the job's agents reported them to this controller",
            summary = "Get the users chart data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the series"),
            @ApiResponse(responseCode = "404", description = "No such job", content = @Content)
    })
    public ResponseEntity<Timeseries> getUserSeries(@PathVariable @Parameter(description = "The job ID", required = true) Integer jobId) {
        return ResponseEntity.ok(jobQueueService.getUserSeries(jobId));
    }

    @RequestMapping(value = "/{jobId}/tps-timeseries", method = RequestMethod.GET)
    @Operation(description = "Transactions per second per request over time, with a Total TPS series first",
            summary = "Get the TPS chart data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the series"),
            @ApiResponse(responseCode = "404", description = "No such job", content = @Content)
    })
    public ResponseEntity<Timeseries> getTpsSeries(
            @PathVariable @Parameter(description = "The job ID", required = true) Integer jobId,
            @RequestParam(required = false) @Parameter(description = "Only this agent") String instanceId,
            @RequestParam(required = false) @Parameter(description = "Only samples after this time (epoch milliseconds)") Long since) {
        return ResponseEntity.ok(jobQueueService.getTpsSeries(jobId, instanceId, since));
    }
}
