/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.projects;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.projects.models.ProjectContainer;
import com.intuit.tank.projects.models.AutomationRequest;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Map;

public interface ProjectServiceV2 {

    /**
     * Test method to test if the service is up.
     *
     * @return non-null String value.
     */
    public String ping();

    /**
     * Gets all projects
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error returning all projects
     *
     * @return list of projects description JSON response
     */
    public ProjectContainer getAllProjects();

    /**
     * Gets all project names along with projectId
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error returning all project names
     *
     * @return map of project < projectName, projectId > JSON response
     */
    public Map<Integer, String> getAllProjectNames();

    /**
     * Gets all specific project
     *
     * @param projectId Project ID
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error returning the project
     *
     * @return project description JSON response
     */
    public ProjectTO getProject(Integer projectId);

    /**
     * Creates a new project
     *
     * @param request Project create/update request JSON payload
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error creating the project
     *
     * @return corresponding project id and status
     */
    public Map<String, String> createProject(AutomationRequest request);

    /**
     * Downloads a project's harness XML file
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors downloading harness file
     *
     * @return project's harness XML file
     */
    public Map<String, StreamingResponseBody> downloadTestScriptForProject(Integer projectId);


    /**
     * Updates an existing project
     *
     * @param projectId Project ID
     *
     * @param request Project create/update request JSON payload
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error updating the project
     *
     * @return corresponding project id and status
     */
    public Map<String, String> updateProject(Integer projectId, AutomationRequest request);


    /**
     * Deletes a specific project
     *
     * @param projectId Project ID
     *
     * @throws GenericServiceDeleteException
     *         if there is an error deleting the project
     *
     * @return 204 No Content or error string if project does not exist
     */
    public String deleteProject(Integer projectId);
}
