/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.client.v1.project;

/*
 * #%L
 * User Rest Client
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.model.v1.user.User;
import com.intuit.tank.api.model.v1.user.UserContainer;
import com.intuit.tank.api.model.v1.user.UserCredentials;
import com.intuit.tank.api.service.v1.project.UserService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.util.ServiceConsants;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * ProjectClientV1
 * 
 * @author dangleton
 * 
 */
public class UserServiceClientV1 extends BaseRestClient {

    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + UserService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public UserServiceClientV1(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public UserServiceClientV1(String serviceUrl, final String proxyServer, final Integer proxyPort) {
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
    public List<User> getAllUsers() {
        WebResource webResource = client.resource(urlBuilder.buildUrl(UserService.METHOD_USERS));
        ClientResponse response = webResource.get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        UserContainer container = response.getEntity(UserContainer.class);
        return container.getUsers();
    }

    /**
     * @{inheritDoc
     */
    public User getUser(String name) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(UserService.METHOD_USER, name));
        ClientResponse response = webResource.get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        User user = response.getEntity(User.class);
        return user;
    }

    /**
     * @{inheritDoc
     */
    public User authenticate(UserCredentials credentials) {
        WebResource webResource = client.resource(urlBuilder.buildUrl(UserService.METHOD_AUTHENTICATE));
        ClientResponse response = webResource.post(ClientResponse.class, credentials);
        exceptionHandler.checkStatusCode(response);
        User user = response.getEntity(User.class);
        return user;
    }
}
