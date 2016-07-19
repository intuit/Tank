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

import com.intuit.tank.api.model.v1.automation.AutomationRequestV2;

import org.codehaus.enunciate.modules.jersey.ExternallyManagedLifecycle;

import com.sun.jersey.multipart.FormDataMultiPart;

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
@ExternallyManagedLifecycle
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
     * Creates a job and returns the jobid
     * 
     * @param AutomationRequest
     * @return Return JobId
     */
    @POST
    @Path("/createJob")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    @Nonnull
    public Response createJob(@Nonnull AutomationRequestV2 request);
    
    /**
     * Runs Job based on the provided jobid
     * 
     * @param JobId
     * @return SUCCESS if started
     */
    @GET
    @Path("/run"+ "/{jobId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response runJob(@PathParam("jobId") String jobId);
    
    /**
     * Checks status of provided jobid
     * 
     * @param JobId
     * @return Status of Job
     */
    @GET
    @Path("/status"+ "/{jobId}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getStatus(@PathParam("jobId") String jobId);
}
