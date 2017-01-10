package com.intuit.tank.api.service.v1.automation;

/*
 * #%L
 * Automation Rest Api
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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.intuit.tank.api.model.v1.automation.CreateJobRequest;
import com.intuit.tank.api.model.v1.automation.ApplyFiltersRequest;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * AutomationService Services for Automation integration.
 * 
 * @author dangleton
 * 
 */
@Path("/v1/automation-service")
public interface AutomationService {

    public static final String VAR_NAME = "";

    /**
     * Test method to test if the service is up.
     * 
     * @return non-null String value.
     */
    @GET
    @Path("/ping")
    @Produces({ MediaType.TEXT_PLAIN })
    @Nonnull
    public String ping();

    /**
     * Creates an in memory job and runs it.
     * 
     * @param formData
     *            Multi-part form data should contain a AutomationRequest with the formKey of automationRequest and the
     *            file data with formKey of file
     * @return Response status code 201 (created) if successful or an error code
     */
    @POST
    @Path("/run-job")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.TEXT_PLAIN })
    @Nonnull
    public Response runAutomationJob(@Nonnull FormDataMultiPart formData);

    /**
     * SaveAs an exiting script to a new name and scriptId
     * 
     * @param scriptId
     * @param name
     * @return Status of Job
     */
    @GET
    @Path("/saveAs"+ "/{scriptId}"+ "/{name}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Nonnull
    public Response saveAs(@PathParam("scriptId") String scriptId, @PathParam("name") String name);
    
    /**
     * Uploads a script to an existing script 
     * 
     * @param formData
     *            Multi-part form data should contain a scriptId with the formKey of scriptId and the
     *            file data with formKey of file
     *            Example: curl -X POST -F "scriptId=x" -F "scriptName=xx" -F "file=@tank-script.xml" http://xxx/rest/v1/automation-service/uploadScript
     * @return Response status code 201 (created) if successful or an error code
     */
    @POST
    @Path("/uploadScript")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    @Nonnull
    public Response uploadScript(@Nonnull FormDataMultiPart formData);
    
    /**
     * Applies Filters to an existing Script
     * 
     * @param request
     * 				Example: curl -X POST -H "Content-Type: application/json" -d '{"scriptId":"x","filterIds":[xx],"filterGroupIds":[x,x,x]}' http://xxx/rest/v1/automation-service/applyFilters
     * @return Response status code 201 (created) if successful or an error code
     */
    @POST
    @Path("/applyFilters")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Nonnull
    public Response applyFilters(@Nonnull ApplyFiltersRequest request);
    
    /**
     * Creates a job and returns the jobid
     * 
     * @param request
     * 				Example: curl -X POST -H "Content-Type: application/json" -d '{"name":"xx",
     * 				"rampTime":"12m","simulationTime":"0","jobRegions":[{"region":"us-west-1","users":"100"},
     * 				{"region":"us-east-1","users":"300"}]}' http://xxx/rest/v1/automation-service/createJob
     * @return Respones with JobId
     */
    @POST
    @Path("/createJob")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Nonnull
    public Response createJob(@Nonnull CreateJobRequest request);
    
    /**
     * Runs Job based on the provided jobid
     * 
     * @param jobId
     * @return Response with SUCCESS if started
     */
    @GET
    @Path("/run" + "/{jobId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response runJob(@PathParam("jobId") String jobId);
    
    /**
     * Checks status of provided jobid
     * 
     * @param jobId
     * @return Status of Job
     */
    @GET
    @Path("/status" + "/{jobId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getStatus(@PathParam("jobId") String jobId);
}
