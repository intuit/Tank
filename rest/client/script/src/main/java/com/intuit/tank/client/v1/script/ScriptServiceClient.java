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
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.MultiPart;

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
    public ScriptTO convertScript(ScriptUploadRequest request, InputStream in) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_CONVERT_SCRIPT));
        MultiPart multiPart = new MultiPart();
        BodyPart bp = new FormDataBodyPart("file", in, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(bp);
        multiPart.bodyPart(new FormDataBodyPart("scriptUploadRequest", request, MediaType.APPLICATION_XML_TYPE));
        ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, multiPart);
        exceptionHandler.checkStatusCode(response);

        String loc = response.getHeaders().getFirst("location");
        client.resource(loc);
        response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ScriptTO.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptDescriptionContainer getScriptDescriptions() throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_DESCRIPTION));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ScriptDescriptionContainer.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptStepContainer getScriptSteps(Integer id, int start, int numSteps) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_STEPS));
        webResource.queryParam("start", Integer.toString(start));
        webResource.queryParam("numSteps", Integer.toString(numSteps));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ScriptStepContainer.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptDescription getScriptDescription(Integer id) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_DESCRIPTION, id));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ScriptDescription.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptTO getScript(Integer id) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT, id));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ScriptTO.class);
    }

    /**
     * @{inheritDoc
     */
    public void deleteScript(Integer id) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT, id));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).delete(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void deleteScriptFilter(Integer id) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_FILTER, id));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).delete(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    /**
     * @{inheritDoc
     */
    public void updateScript(Integer id, ScriptTO script) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT, id));
        webResource.entity(script);
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).put(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
    }

    public String downloadHarnessXml(Integer id) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_HARNESS_DOWNLOAD, id));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }

    /**
     * @{inheritDoc
     */
    public String updateTankScript(File f) throws RestServiceException, UniformInterfaceException {
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
    public String updateTankScript(InputStream in) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_UPDATE));
        MultiPart multiPart = new MultiPart();
        BodyPart bp = new FormDataBodyPart("file", in, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(bp);
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE)
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, multiPart);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptTO newScript(ScriptTO script) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class, script);
        exceptionHandler.checkStatusCode(response);
        String loc = response.getHeaders().getFirst("location");
        client.resource(loc);
        response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ScriptTO.class);
    }

    /**
     * @{inheritDoc
     */
    public ScriptTO scriptFilterRequest(ScriptFilterRequest filterRequest) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_SCRIPT_FILTER));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class,
                filterRequest);
        exceptionHandler.checkStatusCode(response);
        String loc = response.getHeaders().getFirst("location");
        client.resource(loc);
        response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ScriptTO.class);
    }

    /**
     * 
     * @param id
     * @return
     */
    @Nullable
    public ExternalScriptTO getExternalScript(int id) throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_EXTERNAL_SCRIPT, id));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ExternalScriptTO.class);
    }

    /**
     * Gets all ExternalScriptTO objects.
     * 
     * @return the ExternalScriptTO
     */
    @Nonnull
    public List<ExternalScriptTO> getExternalScripts() throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_EXTERNAL_SCRIPTS));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ExternalScriptContainer.class).getScripts();
    }

    /**
     * Saves or updates the External Script.
     * 
     * @param script
     *            the script to store or update
     * @return created status code (201) with uri to script resource.
     */
    @Nonnull
    public ExternalScriptTO saveOrUpdateExternalScript(@Nonnull ExternalScriptTO script) throws RestServiceException,
            UniformInterfaceException {
        WebResource webResource = client.resource(urlBuilder.buildUrl(ScriptService.METHOD_EXTERNAL_SCRIPT));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, script);
        exceptionHandler.checkStatusCode(response);
        String loc = response.getHeaders().getFirst("location");
        webResource = client.resource(loc);
        response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(ExternalScriptTO.class);
    }
}
