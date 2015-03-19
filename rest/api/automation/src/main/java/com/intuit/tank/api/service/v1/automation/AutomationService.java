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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @Path("/ping")
    @Produces({ MediaType.TEXT_PLAIN })
    @GET
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
    @Path("/run-job")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.TEXT_PLAIN })
    @POST
    @Nonnull
    public Response runAutomationJob(@Nonnull FormDataMultiPart formData);

}
