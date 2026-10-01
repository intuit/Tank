/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.projects.models.AutomationRequest;
import com.intuit.tank.projects.models.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ProjectCopyRequest;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectSummary;
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation;

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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.util.List;
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

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @Operation(description = "Returns all project names with corresponding project IDs", summary = "Get all project names with project IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all project names with IDs", content = @Content),
            @ApiResponse(responseCode = "404", description = "All project names with IDs could not be found", content = @Content)
    })
    public ResponseEntity<Map<Integer, String>> getAllProjectNames() {
        return new ResponseEntity<>(projectService.getAllProjectNames(), HttpStatus.OK);
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
                             "**Note**: Make sure you provide a new project name in request to avoid conflict \n\n" +
                             "Parameters: \n\n" +
                             "  - name, productName, comments, and variable key/values are accepted as strings \n\n" +
                             "  - rampTime and simulationTime are accepted as time strings i.e 60s, 12m, 24h \n\n" +
                             "  - location, workloadType, stopBehavior, and terminationPolicy are matched against the schema (see corresponding keys in schema) \n\n" +
                             "  - userIntervalIncrement and dataFileIds are accepted as an integer and a list of integers (datafile IDs to add to project) \n\n" +
                             "  - testPlans, scriptGroups and scripts are matched against the schema - if testPlans is left empty or excluded from payload, " +
                                  "the project defaults to an empty 'Main' test plan with 100% User Percentage \n\n" +
                             "  - testPlans, scriptGroups and scripts are added in the order in which they appear in the payload; their position is determined by their list index \n\n" +
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
                             "**Note**: Make sure you provide a new project name in request to avoid conflict \n\n" +
                             "Parameters: \n\n" +
                             "  - name, productName, comments, and variable key/values are accepted as strings (can be same name as original project, but new variable k/v are added to variable list) \n\n" +
                             "  - rampTime and simulationTime are accepted as time strings i.e 60s, 12m, 24h \n\n" +
                             "  - location, workloadType, stopBehavior, and terminationPolicy are matched against the schema (see corresponding keys in schema) \n\n" +
                             "  - userIntervalIncrement and dataFileIds are accepted as an integer and a list of integers (datafile IDs to add to project) \n\n" +
                             "  - testPlans, scriptGroups and scripts are matched against the schema, and if there are any entries in testPlans, " +
                             "    it will  **overwrite** the existing test plans - passing an empty list of test plans or excluding it from the payload will keep the current test plans as is \n\n" +
                             "  - testPlans, scriptGroups and scripts are added in the order in which they appear in the payload; their position is determined by their list index \n\n" +
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

    @RequestMapping(value = "/download/{projectId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Downloads a project's harness XML file", summary = "Download the project's harness file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded project's harness file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project's harness file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadTestScriptForProject(@PathVariable @Parameter(description = "Project ID", required = true) Integer projectId) throws IOException {
        Map<String, StreamingResponseBody> response = projectService.downloadTestScriptForProject(projectId);
        if (response == null) return ResponseEntity.notFound().build();

        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .body(responseBody);
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

    @RequestMapping(method = RequestMethod.GET, params = "page")
    @Operation(description = "Lists projects one page at a time. The page parameter selects this form; without it "
            + "GET /v2/projects returns every project unpaged",
            summary = "List projects (paged)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the page"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size or sort", content = @Content)
    })
    public ResponseEntity<PageResponse<ProjectSummary>> listProjects(
            @RequestParam @Parameter(description = "Zero-based page number") Integer page,
            @RequestParam(required = false) @Parameter(description = "Page size, 1 to 200 (default 25)") Integer size,
            @RequestParam(required = false) @Parameter(description = "id, name, productName, owner, created or modified, "
                    + "optionally followed by ,asc or ,desc (default modified,desc)") String sort,
            @RequestParam(required = false) @Parameter(description = "Only projects owned by this user") String owner,
            @RequestParam(required = false) @Parameter(description = "Text the name, product or comments contain") String q) {
        return ResponseEntity.ok(projectService.listProjects(page, size, sort, owner, q));
    }

    @RequestMapping(method = RequestMethod.DELETE, params = "ids")
    @Operation(description = "Deletes several projects. Nothing is deleted unless the caller may delete every one that exists",
            summary = "Delete projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns which ids were deleted and which did not exist"),
            @ApiResponse(responseCode = "400", description = "No ids, or more than 100", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to delete one of the projects", content = @Content)
    })
    public ResponseEntity<BulkDeleteResult> deleteProjects(
            @RequestParam @Parameter(description = "Project IDs, comma separated", required = true) List<Integer> ids) {
        return ResponseEntity.ok(projectService.deleteProjects(ids));
    }

    @RequestMapping(value = "/{projectId}/full", method = RequestMethod.GET)
    @Operation(description = "Returns everything the project editor shows: settings, regions, test plans, variables, "
            + "data files, and what the caller may do", summary = "Get a project for editing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the project"),
            @ApiResponse(responseCode = "404", description = "No such project", content = @Content)
    })
    public ResponseEntity<ProjectDetail> getProjectDetail(
            @PathVariable @Parameter(description = "The project ID", required = true) Integer projectId) {
        return ResponseEntity.ok(projectService.getProjectDetail(projectId));
    }

    @RequestMapping(value = "/{projectId}/full", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Replaces the whole project with the body. Send modified from the GET; if the project was "
            + "saved since, the request is rejected with 409", summary = "Save a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved; returns the project as stored"),
            @ApiResponse(responseCode = "400", description = "Invalid project, unknown script or data file", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to edit the project or change its owner", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such project", content = @Content),
            @ApiResponse(responseCode = "409", description = "Saved by someone else since it was loaded, or the name is taken", content = @Content)
    })
    public ResponseEntity<ProjectDetail> updateProjectDetail(
            @PathVariable @Parameter(description = "The project ID", required = true) Integer projectId,
            @RequestBody ProjectDetail detail) {
        return ResponseEntity.ok(projectService.updateProjectDetail(projectId, detail));
    }

    @RequestMapping(value = "/{projectId}/copy", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Copies a project under a new name, owned by the caller", summary = "Copy a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Copied; returns the new project"),
            @ApiResponse(responseCode = "400", description = "Name missing or too long", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to create projects", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such project", content = @Content),
            @ApiResponse(responseCode = "409", description = "A project with that name exists", content = @Content)
    })
    public ResponseEntity<ProjectDetail> copyProject(
            @PathVariable @Parameter(description = "The project ID to copy", required = true) Integer projectId,
            @RequestBody ProjectCopyRequest request) {
        ProjectDetail copy = projectService.copyProject(projectId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v2/projects/{id}/full").buildAndExpand(copy.id()).toUri();
        return ResponseEntity.created(location).body(copy);
    }

    @RequestMapping(value = "/{projectId}/validate", method = RequestMethod.GET)
    @Operation(description = "Checks whether the saved project is ready to run: users, times, test plan percentages, "
            + "scripts, and variable usage", summary = "Validate a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns errors, warnings and estimates"),
            @ApiResponse(responseCode = "404", description = "No such project", content = @Content)
    })
    public ResponseEntity<ProjectValidation> validateProject(
            @PathVariable @Parameter(description = "The project ID", required = true) Integer projectId) {
        return ResponseEntity.ok(projectService.validateProject(projectId));
    }
}
