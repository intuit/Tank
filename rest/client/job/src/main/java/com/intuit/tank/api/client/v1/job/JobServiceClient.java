package com.intuit.tank.api.client.v1.job;

/*
 * #%L
 * Job Rest Client
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.intuit.tank.api.model.v1.job.JobContainer;
import com.intuit.tank.api.model.v1.job.JobTO;
import com.intuit.tank.api.service.v1.job.JobService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.RestServiceException;
import com.intuit.tank.rest.util.ServiceConsants;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * JobClient
 * 
 * @author dangleton
 * 
 */
public class JobServiceClient extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + JobService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public JobServiceClient(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public JobServiceClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
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
    @Nullable
    public JobTO getJob(int jobId) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(JobService.METHOD_JOB, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
            return null;
        }
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(JobTO.class);
    }

    /**
     * @{inheritDoc
     */
    @Nullable
    public List<JobTO> getJobsForProject(int projectId) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(JobService.METHOD_JOBS, projectId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
            return Collections.emptyList();
        }
        exceptionHandler.checkStatusCode(response);
        JobContainer jobContainer = response.getEntity(JobContainer.class);
        return jobContainer.getJobs();
    }
}
