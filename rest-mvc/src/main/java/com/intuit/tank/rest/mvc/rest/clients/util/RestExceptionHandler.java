/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.rest.mvc.rest.clients.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;

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
