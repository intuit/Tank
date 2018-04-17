/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.user;

/*
 * #%L
 * User Rest Service
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
import java.util.stream.Collectors;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.intuit.tank.api.model.v1.user.User;
import com.intuit.tank.api.model.v1.user.UserContainer;
import com.intuit.tank.api.model.v1.user.UserCredentials;
import com.intuit.tank.api.service.v1.project.UserService;
import com.intuit.tank.dao.UserDao;

/**
 * ProjectServiceV1
 * 
 * @author dangleton
 * 
 */
@Path(UserService.SERVICE_RELATIVE_PATH)
public class UserServiceV1 implements UserService {

    /**
     * @inheritDoc
     */
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getAllUsers() {
        ResponseBuilder responseBuilder = Response.ok();
        try {
            List<User> users;
            List<com.intuit.tank.project.User> findAll = new UserDao().findAll();
            users = findAll.stream().map(UserServiceUtil::userToTransferObject).collect(Collectors.toList());
            UserContainer container = new UserContainer(users);
            responseBuilder.entity(container);
        } catch (Exception e) {
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Cannot look up users: " + e.toString());
            throw new WebApplicationException(responseBuilder.build());
        }
        return responseBuilder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getUser(String name) {
        ResponseBuilder responseBuilder = Response.ok();
        try {
            com.intuit.tank.project.User user = new UserDao().findByUserName(name);
            if (user != null) {
                responseBuilder.entity(UserServiceUtil.userToTransferObject(user));
            } else {
                responseBuilder.status(Status.NOT_FOUND);
            }
        } catch (Exception e) {
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Cannot look up user: " + e.toString());
            throw new WebApplicationException(responseBuilder.build());
        }
        return responseBuilder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response authenticate(UserCredentials credentials) {
        ResponseBuilder responseBuilder = Response.ok();
        try {
            com.intuit.tank.project.User user = new UserDao().authenticate(credentials.getName(),
                    credentials.getPass());
            if (user != null) {
                responseBuilder.entity(UserServiceUtil.userToTransferObject(user));
            } else {
                responseBuilder.status(Status.UNAUTHORIZED);
            }
        } catch (Exception e) {
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("Cannot authenticate user: " + e.toString());
            throw new WebApplicationException(responseBuilder.build());
        }
        return responseBuilder.build();
    }

}
