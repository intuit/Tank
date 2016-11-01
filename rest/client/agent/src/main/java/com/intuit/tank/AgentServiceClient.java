/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank;

/*
 * #%L
 * Agent Rest Client
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.intuit.tank.api.model.v1.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.api.service.v1.agent.AgentService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.util.ServiceConsants;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;

/**
 * CloudServiceClient
 * 
 * @author dangleton
 * 
 */
public class AgentServiceClient extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + AgentService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public AgentServiceClient(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public AgentServiceClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    /**
     * 
     * @return
     */
    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }

    public String getSettings() {
        UriBuilder uriBuilder = UriBuilder.fromUri(urlBuilder.buildUrl(AgentService.METHOD_SETTINGS));
        WebTarget webTarget = client.target(uriBuilder.build());
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }
    public TankHttpClientDefinitionContainer getClientDefinitions() {
        UriBuilder uriBuilder = UriBuilder.fromUri(urlBuilder.buildUrl(AgentService.METHOD_CLIENTS));
        WebTarget webTarget = client.target(uriBuilder.build());
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(TankHttpClientDefinitionContainer.class);
    }

    public InputStream getSupportFiles() {
        UriBuilder uriBuilder = UriBuilder.fromUri(urlBuilder.buildUrl(AgentService.METHOD_SUPPORT));
        WebTarget webTarget = client.target(uriBuilder.build());
        Response response = webTarget.request(MediaType.APPLICATION_OCTET_STREAM_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(InputStream.class);
    }

    /**
     * 
     * @param data
     * @return
     */
    public AgentTestStartData agentReady(AgentData data) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(AgentService.METHOD_AGENT_READY));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).post(Entity.entity(data, MediaType.APPLICATION_XML_TYPE));
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(AgentTestStartData.class);
    }

    /**
     * 
     * @param atailabiliity
     */
    public void standaloneAgentAvailable(AgentAvailability atailabiliity) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(AgentService.METHOD_AGENT_AVAILABILITY));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).post(Entity.entity(atailabiliity, MediaType.APPLICATION_XML_TYPE));
        exceptionHandler.checkStatusCode(response);
    }

}
