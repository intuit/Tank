package com.intuit.tank.api.service.v1.project;

/*
 * #%L
 * Project Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.annotation.Nonnull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@Path(ProjectService.SERVICE_RELATIVE_PATH)
public interface ProjectService {

    public static final String REST_SERVICE_CONTEXT = "/rest";
    public static final String SERVICE_RELATIVE_PATH = "/v1/project-service";

    public static final String METHOD_PING = "/ping";

    public static final String METHOD_DELETE = "/delete";
    public static final String METHOD_SCRIPT = "/script";
    public static final String METHOD_PROJECT_SCRIPT = "/script/project";
    public static final String METHOD_SCRIPT_DOWNLOAD = "/download/script";
    public static final String METHOD_PROJECT_SCRIPT_DOWNLOAD = "/download/script/project";

    public static final String METHOD_PROJECTS = "/projects";
    public static final String METHOD_RUN = "/project/run";

    /**
     * Test method to test if the service is up.
     * 
     * @return non-null String value.
     */
    @Path(ProjectService.METHOD_PING)
    @Produces({ MediaType.TEXT_PLAIN })
    @GET
    @Nonnull
    public String ping();

    /**
     * Deletes The specified project.
     * 
     * @param projectId
     *            the id of the project to delete.
     * @return Response containing a status code 204 (no content) if successful and 400 (bad request) if id cannot be
     *         found.
     */
    @Path(ProjectService.METHOD_DELETE + "/{projectId}")
    @Produces({ MediaType.TEXT_PLAIN })
    @DELETE
    @Nonnull
    public Response deleteProject(@PathParam("projectId") int projectId);

    /**
     * Gets a list of all projects in the system.
     * 
     * @param projectId
     *            the id of the project to delete.
     * @return Response containing a ProjectContainer with the list of ProjectTo.
     */
    @Path(ProjectService.METHOD_PROJECTS)
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    @Nonnull
    public Response getProjectNames();

    /**
     * Deletes The specified project.
     * 
     * @param projectId
     *            the id of the project to delete.
     * @return Response containing a status code 204 (no content) if successful and 400 (bad request) if id cannot be
     *         found.
     * @deprecated Added for flex hack. May be removed when we aren't using flex
     */
    @Path(ProjectService.METHOD_DELETE + "/{projectId}")
    @Produces({ MediaType.TEXT_PLAIN })
    @POST
    @Nonnull
    public Response deleteProjectPost(@PathParam("projectId") int projectId);

    @Path(METHOD_SCRIPT + "/{jobId}")
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    public StreamingOutput getTestScriptForJob(@PathParam("jobId") String jobId);

    @Path(METHOD_PROJECT_SCRIPT + "/{projectId}")
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    public StreamingOutput getTestScriptForProject(
            @PathParam("projectId") Integer projectId);

    @Path(METHOD_SCRIPT_DOWNLOAD + "/{jobId}")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    @GET
    public Response downloadTestScriptForJob(@PathParam("jobId") String jobId);

    @Path(METHOD_PROJECT_SCRIPT_DOWNLOAD + "/{projectId}")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    @GET
    public Response downloadTestScriptForProject(
            @PathParam("projectId") Integer projectId);

    @Path(METHOD_RUN + "/{projectId}")
    @Produces({ MediaType.TEXT_PLAIN })
    @GET
    public Response runProject(
            @PathParam("projectId") Integer projectId);

}