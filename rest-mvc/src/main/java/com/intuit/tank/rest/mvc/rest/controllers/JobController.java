/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.common.CloudVmStatusContainer;
import com.intuit.tank.rest.mvc.rest.models.jobs.CreateJobRequest;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobContainer;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobServiceV2;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobTO;

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
import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/v2/jobs", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Jobs")
public class JobController {

    @Resource
    private JobServiceV2 jobService;

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
            "  - vmInstance matches against AWS EC2 Instance Types i.e c5.large, c5.xlarge, etc \n\n"+
            "  - projectId, userIntervalIncrement and numUsersPerAgent are accepted as integers \n\n" +
            "  - jobRegions.regions correspond to AWS regions in lowercase i.e us-west-2, us-east-2 \n\n" +
            "  - jobRegions.users are accepted as integer strings i.e \"100\", \"4000\" \n\n", summary =  "Create a new job")
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
        if (status != null){
            return new ResponseEntity<>(status, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/status/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE })
    @Operation(description = "Returns a specific job status by job id", summary = "Get a specific job status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found specific job status", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job status could not be found", content = @Content)
    })
    public ResponseEntity<String> getJobStatus(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.getJobStatus(jobId);
        if (status != null){
            return new ResponseEntity<>(status, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/instance-status/{jobId}", method = RequestMethod.GET)
    @Operation(description = "Returns list of agent/instance statuses for an existing job", summary = "Get list of instance statuses for job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found specific job instance statuses"),
            @ApiResponse(responseCode = "404", description = "Job instance statuses could not be found", content = @Content)
    })
    public ResponseEntity<CloudVmStatusContainer> getJobVMStatuses(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) String jobId) {
        CloudVmStatusContainer status = jobService.getJobVMStatus(jobId);
        if (status != null){
            return new ResponseEntity<>(status, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/script/{jobId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Gets streaming output of job's harness XML file", summary = "Get job's harness file", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned job's harness file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job's harness file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> getTestScriptForJob(@PathVariable @Parameter(description = "Job ID", required = true) Integer jobId) throws IOException {
        StreamingResponseBody response = jobService.getTestScriptForJob(jobId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/download/{jobId}", method = RequestMethod.GET)
    @Operation(description = "Downloads a job's harness XML file", summary = "Download the job's harness file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded job's harness file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job's harness file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadTestScriptForJob(@PathVariable @Parameter(description = "Job ID", required = true) Integer jobId) throws IOException {
        Map<String, StreamingResponseBody> response = jobService.downloadTestScriptForJob(jobId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    // Job Status Setters

    @RequestMapping(value = "/start/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Starts a specific job by job id", summary = "Start a specific job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully started job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> startJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.startJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/stop/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Stops a specific job by job id", summary = "Stop a specific job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully stopped job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> stopJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.stopJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/pause/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pauses a specific job by job id", summary = "Pause a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully paused job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> pauseJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.pauseJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/resume/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Resumes a specific job by job id", summary = "Resume a paused job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully resumed job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> resumeJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.resumeJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/kill/{jobId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Terminates a specific job by job id", summary = "Terminate a specific job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully terminated job"),
            @ApiResponse(responseCode = "400", description = "Could not update job status due to invalid jobId", content = @Content)
    })
    public ResponseEntity<String> killJob(@PathVariable @Parameter(description = "The job ID associated with the job", required = true) Integer jobId) {
        String status = jobService.killJob(jobId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
