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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.service.v1.cloud.CloudService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.RestServiceException;
import com.intuit.tank.rest.util.ServiceConsants;

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
     * @inheritDoc
     */
    public String getSummaryStatus(String jobId) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_SUMMARY_STATUS,
                jobId));
        Response response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

    /**
     * 
     * @param jobId
     * @param minValue
     * @param maxValue
     * @return
     */
    public String userIdFromRange(String jobId, int minValue, int maxValue) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_USER_ID_FROM_RANGE, jobId,
                Integer.toString(minValue), Integer.toString(maxValue)));
        Response response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.getEntity().toString();
    }

    /**
     * @inheritDoc
     */
    public CloudVmStatus getVmStatus(String instanceId) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_INSTANCE_STATUS, instanceId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(CloudVmStatus.class);
    }

    /**
     * @inheritDoc
     */
    public CloudVmStatusContainer getVmStatusForJob(String jobId) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_JOB_STATUS, jobId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(CloudVmStatusContainer.class);
    }

    /**
     * @inheritDoc
     */
    public void setVmStatus(String instanceId, CloudVmStatus status) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_INSTANCE_STATUS, instanceId));
        Response response = webTarget.request().put(Entity.entity(status, MediaType.APPLICATION_XML_TYPE));
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @inheritDoc
     */
    public void stopInstance(String instanceId) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE, instanceId));
        Response response = webTarget.request().get();
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @inheritDoc
     */
    public void stopInstances(ArrayList<String> instanceIds) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE));
        Response response = webTarget.request().post(Entity.entity(instanceIds, MediaType.APPLICATION_XML));
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public CloudVmStatus startReportingProxy(String location) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_START, location));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(CloudVmStatus.class);
    }

    /**
     * @inheritDoc
     */
    public void stopReportingProxy(String location) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_STOP, location));
        Response response = webTarget.request().get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public CloudVmStatus getReportingProxyStatus(String location) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_REPORTING_STATUS, location));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(CloudVmStatus.class);
    }

    /**
     * @inheritDoc
     */
    public void killInstance(String instanceId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_KILL_INSTANCE, instanceId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @inheritDoc
     */
    public void killJob(String jobId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_KILL_JOB, jobId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void killInstances(List<String> instanceIds) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_KILL_INSTANCE));
        Response response = webTarget.request().post(Entity.entity(instanceIds, MediaType.APPLICATION_XML));
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void stopJob(String jobId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_STOP_JOB, jobId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);

    }

    /**
     * @inheritDoc
     */
    public void stopAgent(String instanceId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE, instanceId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void stopAgents(List<String> instanceIds) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_STOP_INSTANCE));
        Response response = webTarget.request().post(Entity.entity(instanceIds, MediaType.APPLICATION_XML));
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void pauseJob(String jobId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_JOB, jobId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void pauseAgent(String instanceId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_INSTANCE, instanceId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void pauseAgents(List<String> instanceIds) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_RESTART_INSTANCE));
        Response response = webTarget.request().post(Entity.entity(instanceIds, MediaType.APPLICATION_XML));
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void restartJob(String jobId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_RESTART_JOB, jobId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void restartAgent(String instanceId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_RESTART_INSTANCE, instanceId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void restartAgents(List<String> instanceIds) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_RESTART_INSTANCE));
        Response response = webTarget.request().post(Entity.entity(instanceIds, MediaType.APPLICATION_XML));
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void pauseRampJob(String jobId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_RAMP_JOB, jobId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void pauseRampAgent(String instanceId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_RAMP_INSTANCE,
                instanceId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void pauseRampAgents(List<String> instanceIds) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_PAUSE_RAMP_INSTANCE));
        Response response = webTarget.request().post(Entity.entity(instanceIds, MediaType.APPLICATION_XML));
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void resumeRampJob(String jobId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_RESUME_RAMP_JOB, jobId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void resumeRampAgent(String instanceId) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_RESUME_RAMP_INSTANCE,
                instanceId));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @inheritDoc
     */
    public void resumeRampAgents(List<String> instanceIds) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(CloudService.METHOD_RESUME_RAMP_INSTANCE));
        Response response = webTarget.request().post(Entity.entity(instanceIds, MediaType.APPLICATION_XML));
        exceptionHandler.checkStatusCode(response);
    }

}
