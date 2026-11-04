/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.JobLaunchRequest;
import com.intuit.tank.rest.mvc.rest.models.JobPreview;
import com.intuit.tank.rest.mvc.rest.models.QueuedJob;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobQueueServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Queueing jobs from a project. The job takes its settings from the saved project, as the web UI's
 * "Add to job queue" dialog does after saving.
 */
@RestController
@RequestMapping(value = "/v2/projects/{projectId}/jobs", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Jobs")
public class ProjectJobController {

    @Resource
    private JobQueueServiceV2 jobQueueService;

    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    @Operation(description = "Shows the job that queueing the saved project now would create: its name, users, times, "
            + "errors that would block queueing, warnings, and the job details", summary = "Preview a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the preview"),
            @ApiResponse(responseCode = "400", description = "Invalid name, or times that cannot be evaluated", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such project", content = @Content)
    })
    public ResponseEntity<JobPreview> previewJob(
            @PathVariable @Parameter(description = "The project ID", required = true) Integer projectId,
            @RequestBody(required = false) JobLaunchRequest request) {
        return ResponseEntity.ok(jobQueueService.previewJob(projectId, request));
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(description = "Adds a job for the saved project to its queue. The job does not start until it is started",
            summary = "Queue a job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Queued; returns the job ID"),
            @ApiResponse(responseCode = "400", description = "The project fails validation; the message lists why", content = @Content),
            @ApiResponse(responseCode = "403", description = "Needs CONTROL_JOB or ownership of the project", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such project", content = @Content)
    })
    public ResponseEntity<QueuedJob> queueJob(
            @PathVariable @Parameter(description = "The project ID", required = true) Integer projectId,
            @RequestBody(required = false) JobLaunchRequest request) {
        QueuedJob job = jobQueueService.queueJob(projectId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v2/jobs/{id}/details").buildAndExpand(job.jobId()).toUri();
        return ResponseEntity.created(location).body(job);
    }
}
