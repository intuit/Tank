package com.intuit.tank.api.service.v1.job;

/*
 * #%L
 * Job Rest Api
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * JobService Services for Job instances.
 * 
 * @author dangleton
 * 
 */
@Path(JobService.SERVICE_RELATIVE_PATH)
public interface JobService {

    public static final String REST_SERVICE_CONTEXT = "/rest";
    public static final String SERVICE_RELATIVE_PATH = "/v1/job-service";

    public static final String METHOD_PING = "/ping";
    public static final String METHOD_JOB = "/job";
    public static final String METHOD_JOBS = "/jobs/project";

    /**
     * Test method to test if the service is up.
     * 
     * @return non-null String value.
     */
    @Path("/ping")
    @Produces({ MediaType.TEXT_PLAIN })
    @GET
    @Nonnull
    public String ping();

    /**
     * Gets the specified Job form the database. Returns a JobTO object
     * 
     * @param jobId
     *            the id of the job to fetch
     */
    @Path(METHOD_JOB + "/{jobId}")
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    public Response getJob(@PathParam("jobId") int jobId);

    /**
     * Gets the specified Job form the database. Returns a JobTO object
     * 
     * @param jobId
     *            the id of the job to fetch
     */
    @Path(METHOD_JOBS + "/{projectId}")
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    public Response getJobsForProject(@PathParam("projectId") int projectId);

}
