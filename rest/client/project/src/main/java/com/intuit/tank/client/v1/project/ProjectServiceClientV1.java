/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.client.v1.project;

/*
 * #%L
 * Project Rest Client
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientResponse;

import com.intuit.tank.api.model.v1.project.ProjectContainer;
import com.intuit.tank.api.model.v1.project.ProjectTO;
import com.intuit.tank.api.service.v1.project.ProjectService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.RestServiceException;
import com.intuit.tank.rest.util.ServiceConsants;

/**
 * ProjectClientV1
 * 
 * @author dangleton
 * 
 */
public class ProjectServiceClientV1 extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + ProjectService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public ProjectServiceClientV1(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public ProjectServiceClientV1(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    /**
     * 
     * @return
     */
    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }

    /**
     * @{inheritDoc
     */
    public void deleteProject(int projectId) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(
                ProjectService.METHOD_DELETE, projectId));
        ClientResponse response = webTarget.request().delete(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * 
     * @return
     */
    public List<ProjectTO> getProjects() {
    	WebTarget webTarget = client.target(urlBuilder
                .buildUrl(ProjectService.METHOD_PROJECTS));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML)
                .get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        ProjectContainer container = response.readEntity(ProjectContainer.class);
        return container.getProjects();
    }

    public String downloadTestScriptForProject(Integer projectId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ProjectService.METHOD_PROJECT_SCRIPT_DOWNLOAD,
                projectId));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

}
