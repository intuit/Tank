/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank;

/*
 * #%L
 * Cloud Rest Client
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.service.v1.cloud.CloudService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.RestServiceException;
import com.intuit.tank.rest.util.ServiceConsants;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * CloudServiceClient
 * 
 * @author dangleton
 * 
 */
public class CloudServiceClient extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + CloudService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public CloudServiceClient(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public CloudServiceClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
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
    public String getSummaryStatus(String jobId) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_SUMMARY_STATUS,
                jobId));
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }

    /**
     * 
     * @param jobId
     * @param minValue
     * @param maxValue
     * @return
     */
    public String userIdFromRange(String jobId, int minValue, int maxValue) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_USER_ID_FROM_RANGE, jobId,
                Integer.toString(minValue), Integer.toString(maxValue)));
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }

    /**
     * @{inheritDoc
     */
    public CloudVmStatus getVmStatus(String instanceId) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_INSTANCE_STATUS, instanceId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(CloudVmStatus.class);
    }

    /**
     * @{inheritDoc
     */
    public CloudVmStatusContainer getVmStatusForJob(String jobId) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_JOB_STATUS, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(CloudVmStatusContainer.class);
    }

    /**
     * @{inheritDoc
     */
    public void setVmStatus(String instanceId, CloudVmStatus status) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_INSTANCE_STATUS, instanceId));
        ClientResponse response = webResource.entity(status, MediaType.APPLICATION_XML_TYPE).put(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @{inheritDoc
     */
    public void stopInstance(String instanceId) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE, instanceId));
        ClientResponse response = webResource.post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @{inheritDoc
     */
    public void stopInstances(ArrayList<String> instanceIds) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE));
        ClientResponse response = webResource.entity(instanceIds, MediaType.APPLICATION_XML).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public CloudVmStatus startReportingProxy(String location) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_START, location));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(CloudVmStatus.class);
    }

    /**
     * @{inheritDoc
     */
    public void stopReportingProxy(String location) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_STOP, location));
        ClientResponse response = webResource.post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public CloudVmStatus getReportingProxyStatus(String location) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_STATUS, location));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(CloudVmStatus.class);
    }

    /**
     * @{inheritDoc
     */
    public void killInstance(String instanceId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_KILL_INSTANCE, instanceId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @{inheritDoc
     */
    public void killJob(String jobId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_KILL_JOB, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void killInstances(List<String> instanceIds) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_KILL_INSTANCE));
        ClientResponse response = webResource.entity(instanceIds, MediaType.APPLICATION_XML).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void stopJob(String jobId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_STOP_JOB, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @{inheritDoc
     */
    public void stopAgent(String instanceId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE, instanceId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void stopAgents(List<String> instanceIds) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE));
        ClientResponse response = webResource.entity(instanceIds, MediaType.APPLICATION_XML).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void pauseJob(String jobId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_JOB, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void pauseAgent(String instanceId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_INSTANCE, instanceId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void pauseAgents(List<String> instanceIds) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_RESTART_INSTANCE));
        ClientResponse response = webResource.entity(instanceIds, MediaType.APPLICATION_XML).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void restartJob(String jobId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_RESTART_JOB, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void restartAgent(String instanceId) {
        WebResource webResource = client
                .resource(urlBuilder.buildUrl(CloudService.METHOD_RESTART_INSTANCE, instanceId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void restartAgents(List<String> instanceIds) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_RESTART_INSTANCE));
        ClientResponse response = webResource.entity(instanceIds, MediaType.APPLICATION_XML).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void pauseRampJob(String jobId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_RAMP_JOB, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void pauseRampAgent(String instanceId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_RAMP_INSTANCE,
                instanceId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void pauseRampAgents(List<String> instanceIds) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_RAMP_INSTANCE));
        ClientResponse response = webResource.entity(instanceIds, MediaType.APPLICATION_XML).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void resumeRampJob(String jobId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_RESUME_RAMP_JOB, jobId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void resumeRampAgent(String instanceId) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_RESUME_RAMP_INSTANCE,
                instanceId));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void resumeRampAgents(List<String> instanceIds) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(CloudService.METHOD_RESUME_RAMP_INSTANCE));
        ClientResponse response = webResource.entity(instanceIds, MediaType.APPLICATION_XML).post(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

}
