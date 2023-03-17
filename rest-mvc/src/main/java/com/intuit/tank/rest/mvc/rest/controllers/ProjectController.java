/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.projects.AutomationRequest;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/v2/projects", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Projects")
public class ProjectController {

    @Resource
    private ProjectServiceV2 projectService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings project service", summary = "Check if project service is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project Service is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<String>(projectService.ping(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns all project descriptions", summary = "Get all project descriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all project descriptions"),
            @ApiResponse(responseCode = "404", description = "All project descriptions could not be found", content = @Content)
    })
    public ResponseEntity<ProjectContainer> getAllProjects() {
        return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    @Operation(description = "Gets a specific project description by project ID", summary = "Get a specific project description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found project"),
            @ApiResponse(responseCode = "404", description = "Project could not be found", content = @Content)
    })
    public ResponseEntity<ProjectTO> getProject(@PathVariable @Parameter(description = "The project ID associated with project", required = true) Integer projectId) {
        return new ResponseEntity<>(projectService.getProject(projectId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Given a project request payload, creates a new project and returns projectId and created status in response on success \n\n" +
                             "Note: Make sure you provide a new project name in request to avoid conflict \n\n" +
                             "Parameters: \n\n" +
                             "  - name, productName, comments, and variable key/values are accepted as strings \n\n" +
                             "  - rampTime and simulationTime are accepted as time strings i.e 60s, 12m, 24h \n\n" +
                             "  - location, workloadType, stopBehavior, and terminationPolicy are matched against accepted values (see corresponding keys in schema) \n\n" +
                             "  - userIntervalIncrement and dataFileIds are accepted as an integer and a list of integers (datafile IDs to add to project) \n\n" +
                             "  - jobRegions.regions correspond to AWS regions in uppercase i.e US_WEST_2, US_EAST_2 \n\n" +
                             "  - jobRegions.users are accepted as integer strings i.e \"100\", \"4000\" \n\n", summary = "Create a new project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created project", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<Map<String, String>> createProject(
            @RequestBody @Parameter(description = "request", required = true) AutomationRequest request) {
        Map<String, String> response = projectService.createProject(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().scheme("https").path("/{id}").buildAndExpand(response.get("ProjectId")).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Given an existing project's projectId and request payload, updates project and returns projectId and updated status in response on success \n\n" +
                             "Parameters: \n\n" +
                             "  - name, productName, comments, and variable key/values are accepted as strings (can be same name as original project, but new variable k/v are added to variable list) \n\n" +
                             "  - rampTime and simulationTime are accepted as time strings i.e 60s, 12m, 24h \n\n" +
                             "  - location, workloadType, stopBehavior, and terminationPolicy are matched against accepted values (see corresponding keys in schema) \n\n" +
                             "  - userIntervalIncrement and dataFileIds are accepted as an integer and a list of integers (datafile IDs to add to project) \n\n" +
                             "  - jobRegions.regions correspond to AWS regions in uppercase i.e US_WEST_2, US_EAST_2 \n\n" +
                             "  - jobRegions.users are accepted as integer strings i.e \"100\", \"4000\" \n\n", summary = "Update a specific project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated project", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<Map<String, String>> updateProject(
            @PathVariable @Parameter(description = "The project ID associated with project", required = true) Integer projectId,
            @RequestBody @Parameter(description = "request", required = true) AutomationRequest request) {
        Map<String, String> response = projectService.updateProject(projectId, request);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Deletes a specific project by project ID", summary = "Delete a project")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No content (project delete successful)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found",  content = @Content) })
    public ResponseEntity<String> deleteProject(
            @PathVariable @Parameter(description = "The project ID", required = true) Integer projectId) {
        String response = projectService.deleteProject(projectId);
        if (Objects.equals(response, "")) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
