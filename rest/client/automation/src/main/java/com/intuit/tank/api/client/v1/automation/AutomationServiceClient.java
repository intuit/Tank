/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.client.v1.automation;

/*
 * #%L
 * Automation Rest Client
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.intuit.tank.api.model.v1.automation.AutomationRequest;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.RestServiceException;

/**
 * AutomationServiceClient
 * 
 * @author dangleton
 * 
 */
public class AutomationServiceClient extends BaseRestClient {

    private static final String SERVICE_BASE_URL = "/rest/v1/automation-service";

    private static final String METHOD_RUN_JOB = "/run-job";

    /**
     * 
     * @param serviceUrl
     */
    public AutomationServiceClient(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public AutomationServiceClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
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
    public String runAutomationJob(AutomationRequest request, File xmlFile)
            throws RestServiceException {
    	WebTarget webTarget = client.target(baseUrl + METHOD_RUN_JOB);
        MultiPart multiPart = new MultiPart();
        if (xmlFile != null) {
            BodyPart bp = new FileDataBodyPart("file", xmlFile);
            multiPart.bodyPart(bp);
        }

        multiPart.bodyPart(new FormDataBodyPart("automationRequest", request,
                MediaType.APPLICATION_XML_TYPE));
        ClientResponse response = webTarget.request().post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

    /**
     * @{inheritDoc
     */
    public String runAutomationJob(AutomationRequest request,
            InputStream xmlFileStream) {
    	WebTarget webTarget = client.target(baseUrl + METHOD_RUN_JOB);
        MultiPart multiPart = new MultiPart();
        if (xmlFileStream != null) {
            BodyPart bp = new FormDataBodyPart("file", xmlFileStream,
                    MediaType.APPLICATION_OCTET_STREAM_TYPE);
            multiPart.bodyPart(bp);
        }
        multiPart.bodyPart(new FormDataBodyPart("automationRequest", request,
                MediaType.APPLICATION_XML_TYPE));
        ClientResponse response = webTarget.request().post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

}
