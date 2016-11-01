/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.rest;

import javax.ws.rs.core.Response;

/*
 * #%L
 * Rest Client Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RestExceptionHandler
 * 
 * @author dangleton
 * 
 */
public class RestExceptionHandler {

    private static final Logger LOG = LogManager.getLogger(RestExceptionHandler.class);

    public void checkStatusCode(Response response) throws RestServiceException {
        if (response.getStatus() >= 400) {
            LOG.error("Got rest response status code " + response.getStatus() + " from location "
                    + response.getLocation());
            String msg = response.readEntity(String.class);
            if (msg == null) {
                msg = getMessageForCode(response.getStatus());
            }
            throw new RestServiceException(msg, response.getStatus());

        }
    }

    /**
     * @param status
     * @return
     */
    private String getMessageForCode(int status) {
        String result = null;
        switch (status) {
        case 400:
            result = "The request could not be understood by the server due to malformed syntax.";
            break;
        case 401:
            result = "The request requires user authentication.";
            break;
        case 404:
            result = "Resource not found.";
            break;

        default:
            result = "Error processing request.";
        }
        return result;
    }
}
