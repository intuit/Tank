/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.client.v1.datafile;

/*
 * #%L
 * Datafile Rest Client
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
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.api.model.v1.datafile.DataFileDescriptorContainer;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.util.ServiceConsants;
import com.intuit.tank.service.api.v1.DataFileService;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * DataFileClient
 * 
 * @author dangleton
 * 
 */
public class DataFileClient extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + DataFileService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public DataFileClient(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public DataFileClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
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
    public List<DataFileDescriptor> getDataFiles() {
        WebResource webResource = client.resource(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(DataFileDescriptorContainer.class).getDataFiles();

    }

    /**
     * @{inheritDoc
     */
    public DataFileDescriptor getDataFile(Integer id) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id));
        ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(DataFileDescriptor.class);
    }

    /**
     * @{inheritDoc
     */
    public String getDataFileDataOffset(Integer id, int offset, int numLines) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data/offset" + offset));
        webResource.queryParam("offset", Integer.toString(offset));
        webResource.queryParam("num-lines", Integer.toString(numLines));
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }

    /**
     * @{inheritDoc "/datafile/{id}/data"
     */
    public String getDataFileData(Integer id) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data"));
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }

    /**
     * @{inheritDoc
     */
    public InputStream getDataFileDataStream(Integer id) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data"));
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntityInputStream();
    }

    /**
     * @{inheritDoc
     */
    public InputStream getDataFileDataOffsetStream(Integer id, int offset, int numLines) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data/offset" + offset));
        webResource.queryParam("offset", Integer.toString(offset));
        webResource.queryParam("num-lines", Integer.toString(numLines));
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntityInputStream();
    }

}
