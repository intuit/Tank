package com.intuit.tank.api.service.v1.agent;

/*
 * #%L
 * Agent Rest API
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

import org.codehaus.enunciate.jaxrs.TypeHint;
import org.codehaus.enunciate.modules.jersey.ExternallyManagedLifecycle;

import com.intuit.tank.api.model.v1.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.Headers;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@Path(AgentService.SERVICE_RELATIVE_PATH)
@ExternallyManagedLifecycle
public interface AgentService {
    public static final String SERVICE_RELATIVE_PATH = "/v1/agent-service";

    public static final String METHOD_PING = "/ping";
    public static final String METHOD_SETTINGS = "/settings";
    public static final String METHOD_SUPPORT = "/supportFiles";
    public static final String METHOD_HEADERS = "/headers";
    public static final String METHOD_AGENT_READY = "/agent/ready";
    public static final String METHOD_CLIENTS = "/agent/clients";
    public static final String METHOD_AGENT_AVAILABILITY = "/agent/availability";

    /**
     * Test method to test if the service is up.
     * 
     * @return non-null String value.
     */
    @Path(AgentService.METHOD_PING)
    @GET
    @Nonnull
    public String ping();

    /**
     * Endpoint for standalone agent to indicate its availability.
     * 
     * @param availability
     *            the current availability of the agent.
     * @return no content response
     */
    @Path(AgentService.METHOD_AGENT_AVAILABILITY)
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @TypeHint(TypeHint.NO_CONTENT.class)
    public Response standaloneAgentAvailable(AgentAvailability availability);

    /**
     * Indicate that the agent is ready to start.
     * 
     * @param data
     *            The agent data
     * @return a AgentTestStartData.
     */
    @Path(AgentService.METHOD_AGENT_READY)
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @TypeHint(AgentTestStartData.class)
    public Response agentReady(AgentData data);

    /**
     * gets the settings file
     * 
     * @return string of the settings file.
     */
    @Path(AgentService.METHOD_CLIENTS)
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Nonnull
    @TypeHint(TankHttpClientDefinitionContainer.class)
    public Response getClients();

    /**
     * gets the settings file
     * 
     * @return string of the settings file.
     */
    @Path(AgentService.METHOD_SETTINGS)
    @GET
    @Produces({ MediaType.APPLICATION_XML })
    @Nonnull
    @TypeHint(String.class)
    public Response getSettings();

    /**
     * gets the settings file
     * 
     * @return string of the settings file.
     */
    @Path(AgentService.METHOD_HEADERS)
    @GET
    @Produces({ MediaType.APPLICATION_XML })
    @Nonnull
    @TypeHint(Headers.class)
    public Response getHeaders();

    /**
     * gets the support files (jar files)
     * 
     * @return Streaming output of a gzipped archive of the support files.
     */
    @Path(AgentService.METHOD_SUPPORT)
    @GET
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    @Nonnull
    public Response getSupportFiles();

}
