/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.client.v1.script;

/*
 * #%L
 * Script Rest Client
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;

import com.intuit.tank.api.model.v1.script.ExternalScriptContainer;
import com.intuit.tank.api.model.v1.script.ExternalScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptDescriptionContainer;
import com.intuit.tank.api.model.v1.script.ScriptFilterRequest;
import com.intuit.tank.api.model.v1.script.ScriptStepContainer;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptUploadRequest;
import com.intuit.tank.api.service.v1.script.ScriptService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.RestServiceException;
import com.intuit.tank.rest.util.ServiceConsants;

/**
 * ScriptServiceClient
 * 
 * @author dangleton
 * 
 */
public class ScriptServiceClient extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + ScriptService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public ScriptServiceClient(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public ScriptServiceClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
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
    public ScriptTO convertScript(ScriptUploadRequest request, InputStream in) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_CONVERT_SCRIPT));
        MultiPart multiPart = new MultiPart();
        BodyPart bp = new FormDataBodyPart("file", in, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(bp);
        multiPart.bodyPart(new FormDataBodyPart("scriptUploadRequest", request, MediaType.APPLICATION_XML_TYPE));
        ClientResponse response = webTarget.request().post(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);

        String loc = response.getHeaders().getFirst("location");
        webTarget = client.target(loc);
        response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ScriptTO.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptDescriptionContainer getScriptDescriptions() throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_DESCRIPTION));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ScriptDescriptionContainer.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptStepContainer getScriptSteps(Integer id, int start, int numSteps) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_STEPS));
    	webTarget.queryParam("start", Integer.toString(start));
    	webTarget.queryParam("numSteps", Integer.toString(numSteps));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ScriptStepContainer.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptDescription getScriptDescription(Integer id) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_DESCRIPTION, id));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ScriptDescription.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptTO getScript(Integer id) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT, id));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ScriptTO.class);
    }

    /**
     * @{inheritDoc
     */
    public void deleteScript(Integer id) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT, id));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).delete(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void deleteScriptFilter(Integer id) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_FILTER, id));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).delete(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void updateScript(Integer id, ScriptTO script) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT, id));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).put(Entity.entity(script, MediaType.APPLICATION_XML_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    public String downloadHarnessXml(Integer id) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_HARNESS_DOWNLOAD, id));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

    /**
     * @{inheritDoc
     */
    public String updateTankScript(File f) throws RestServiceException {
        InputStream in = null;
        try {
            in = new FileInputStream(f);
            return updateTankScript(in);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * @{inheritDoc
     */
    public String updateTankScript(InputStream in) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_UPDATE));
        MultiPart multiPart = new MultiPart();
        BodyPart bp = new FormDataBodyPart("file", in, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(bp);
        ClientResponse response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptTO newScript(ScriptTO script) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).post(Entity.entity(script, MediaType.APPLICATION_XML_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        String loc = response.getHeaders().getFirst("location");
        client.target(loc);
        response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ScriptTO.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptTO scriptFilterRequest(ScriptFilterRequest filterRequest) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_FILTER));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).post(Entity.entity(filterRequest, MediaType.APPLICATION_XML_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        String loc = response.getHeaders().getFirst("location");
        client.target(loc);
        response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ScriptTO.class);
    }

    /**
     * 
     * @param id
     * @return
     */
    @Nullable
    public ExternalScriptTO getExternalScript(int id) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_EXTERNAL_SCRIPT, id));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ExternalScriptTO.class);
    }

    /**
     * Gets all ExternalScriptTO objects.
     * 
     * @return the ExternalScriptTO
     */
    @Nonnull
    public List<ExternalScriptTO> getExternalScripts() throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_EXTERNAL_SCRIPTS));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ExternalScriptContainer.class).getScripts();
    }

    /**
     * Saves or updates the External Script.
     * 
     * @param script
     *            the script to store or update
     * @return created status code (201) with uri to script resource.
     */
    @Nonnull
    public ExternalScriptTO saveOrUpdateExternalScript(@Nonnull ExternalScriptTO script) throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(ScriptService.METHOD_EXTERNAL_SCRIPT));
        ClientResponse response = webTarget.request(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(script, MediaType.APPLICATION_XML_TYPE), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        String loc = response.getHeaders().getFirst("location");
        webTarget = client.target(loc);
        response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(ExternalScriptTO.class);
    }
}
