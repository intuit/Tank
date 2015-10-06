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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.intuit.tank.api.model.v1.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.api.service.v1.agent.AgentService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.util.ServiceConsants;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

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
        WebResource webResource = client.resource(uriBuilder.build());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }
    public TankHttpClientDefinitionContainer getClientDefinitions() {
        UriBuilder uriBuilder = UriBuilder.fromUri(urlBuilder.buildUrl(AgentService.METHOD_CLIENTS));
        WebResource webResource = client.resource(uriBuilder.build());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(TankHttpClientDefinitionContainer.class);
    }

    public InputStream getSupportFiles() {
        UriBuilder uriBuilder = UriBuilder.fromUri(urlBuilder.buildUrl(AgentService.METHOD_SUPPORT));
        WebResource webResource = client.resource(uriBuilder.build());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntityInputStream();
    }

    /**
     * 
     * @param data
     * @return
     */
    public AgentTestStartData agentReady(AgentData data) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(AgentService.METHOD_AGENT_READY));
        // webResource.entity(data);
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML).post(ClientResponse.class, data);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(AgentTestStartData.class);
    }

    /**
     * 
     * @param atailabiliity
     */
    public void standaloneAgentAvailable(AgentAvailability atailabiliity) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(AgentService.METHOD_AGENT_AVAILABILITY));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML).post(ClientResponse.class,
                atailabiliity);
        exceptionHandler.checkStatusCode(response);
    }

}
