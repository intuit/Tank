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

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.api.model.v1.datafile.DataFileDescriptorContainer;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.util.ServiceConsants;
import com.intuit.tank.service.api.v1.DataFileService;

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
     * @inheritDoc
     */
    public List<DataFileDescriptor> getDataFiles() {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(DataFileDescriptorContainer.class).getDataFiles();

    }

    /**
     * @inheritDoc
     */
    public DataFileDescriptor getDataFile(Integer id) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(DataFileDescriptor.class);
    }

    /**
     * @inheritDoc
     */
    public String getDataFileDataOffset(Integer id, int offset, int numLines) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data/offset" + offset));
    	webTarget.queryParam("offset", Integer.toString(offset));
    	webTarget.queryParam("num-lines", Integer.toString(numLines));
        Response response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

    /**
     * @inheritDoc "/datafile/{id}/data"
     */
    public String getDataFileData(Integer id) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data"));
        Response response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }

    /**
     * @inheritDoc
     */
    public InputStream getDataFileDataStream(Integer id) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data"));
        Response response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(InputStream.class);
    }

    /**
     * @inheritDoc
     */
    public InputStream getDataFileDataOffsetStream(Integer id, int offset, int numLines) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(
                DataFileService.METHOD_GET_DATA_FILES, id, "data/offset" + offset));
    	webTarget.queryParam("offset", Integer.toString(offset));
    	webTarget.queryParam("num-lines", Integer.toString(numLines));
        Response response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(InputStream.class);
    }

}
