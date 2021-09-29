package com.intuit.tank.api.service.v1.project;

/*
 * #%L
 * User Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.intuit.tank.api.model.v1.user.UserCredentials;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * UserService
 * 
 * @author dangleton
 * 
 */
@Path(UserService.SERVICE_RELATIVE_PATH)
public interface UserService {

    public static final String SERVICE_RELATIVE_PATH = "/v1/user-service";
    public static final String METHOD_PING = "/ping";
    public static final String METHOD_AUTHENTICATE = "/authenticate";
    public static final String METHOD_USER = "/user";
    public static final String METHOD_USER_EMAIL = "/user/email";
    public static final String METHOD_USERS = "/users";

    /**
     * Test method to test if the service is up.
     * 
     * @return non-null String value.
     */
    @Path(UserService.METHOD_PING)
    @Produces({ MediaType.TEXT_PLAIN })
    @GET
    @Nonnull
    public String ping();

    /**
     * Get All Basic User information
     */
    @Path(UserService.METHOD_USERS)
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    @Nonnull
    public Response getAllUsers();

    /**
     * Get Basic USER information by USERNAME
     * @param username Username
     * @return User information
     */
    @Path(UserService.METHOD_USER + "/{username}")
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    @Nullable
    public Response getUserByUsername(@PathParam("username") String username);

    /**
     * Get Basic USER information by EMAIL
     * @param email User email
     * @return User information
     */
    @Path(UserService.METHOD_USER_EMAIL + "/{email}")
    @Produces({ MediaType.APPLICATION_XML })
    @GET
    @Nullable
    public Response getUserByEmail(@PathParam("email") String email);

    /**
     * Authenticate user given credentials
     * @param credentials User Credentials
     * @return User information
     */
    @Path(UserService.METHOD_AUTHENTICATE)
    @Produces({ MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_XML })
    @POST
    @Nonnull
    public Response authenticate(UserCredentials credentials);

}
