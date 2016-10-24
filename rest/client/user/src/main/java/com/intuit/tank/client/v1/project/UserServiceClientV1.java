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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientResponse;

import com.intuit.tank.api.model.v1.user.User;
import com.intuit.tank.api.model.v1.user.UserContainer;
import com.intuit.tank.api.model.v1.user.UserCredentials;
import com.intuit.tank.api.service.v1.project.UserService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.util.ServiceConsants;

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
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(UserService.METHOD_USERS));
        ClientResponse response = webTarget.request().get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        UserContainer container = response.readEntity(UserContainer.class);
        return container.getUsers();
    }

    /**
     * @{inheritDoc
     */
    public User getUser(String name) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(UserService.METHOD_USER, name));
        ClientResponse response = webTarget.request().get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(User.class);
    }

    /**
     * @{inheritDoc
     */
    public User authenticate(UserCredentials credentials) {
    	WebTarget webTarget = client.target(urlBuilder.buildUrl(UserService.METHOD_AUTHENTICATE));
        ClientResponse response = webTarget.request().post(Entity.entity(credentials, MediaType.APPLICATION_XML), ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(User.class);
    }
}
