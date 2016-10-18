package com.intuit.tank.api.service.v1.filter;

/*
 * #%L
 * Filter Rest Api
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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * AutomationService Services for Automation integration.
 * 
 * @author dangleton
 * 
 */
@Path(FilterService.SERVICE_RELATIVE_PATH)
public interface FilterService {

    public static final String REST_SERVICE_CONTEXT = "/rest";
    public static final String SERVICE_RELATIVE_PATH = "/v1/filter-service";

    public static final String METHOD_PING = "/ping";
    public static final String METHOD_FILTER_GROUPS = "/groups";

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
     * Gets a list of filter all groups.
     * 
     * @return response containing a list of Filte Groups contained in a FilterGroupContainer
     */
    @Path(METHOD_FILTER_GROUPS)
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    public Response getFilterGroups();

}
