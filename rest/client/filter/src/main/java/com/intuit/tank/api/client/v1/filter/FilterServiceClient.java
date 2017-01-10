/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.client.v1.filter;

/*
 * #%L
 * Filter Rest Client
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
import javax.ws.rs.core.Response;

import com.intuit.tank.api.model.v1.filter.FilterGroupContainer;
import com.intuit.tank.api.model.v1.filter.FilterGroupTO;
import com.intuit.tank.api.service.v1.filter.FilterService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.RestServiceException;
import com.intuit.tank.rest.util.ServiceConsants;

/**
 * AutomationServiceClient
 * 
 * @author dangleton
 * 
 */
public class FilterServiceClient extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + FilterService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public FilterServiceClient(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public FilterServiceClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
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
     * Get a list of filter groups.
     */
    public List<FilterGroupTO> getFilterGroups() throws RestServiceException {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(FilterService.METHOD_FILTER_GROUPS));
        Response response = webTarget.request(MediaType.APPLICATION_XML_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        FilterGroupContainer container = response.readEntity(FilterGroupContainer.class);
        return container.getFilterGroups();
    }

}
