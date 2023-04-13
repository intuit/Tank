/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

public class GenericServiceBadRequestException extends GenericServiceException {

    private static final long serialVersionUID = 1L;

    private final String service;

    private final String resource;

    private final String message;


    /**
     * Construct with a message {@code String} that is returned by the inherited
     * {@code Throwable.getMessage}.
     *
     * @param service
     *            the service impacted
     *
     * @param resource
     *            the specific service related resource
     */
    public GenericServiceBadRequestException(String service, String resource, String message) {
        super("Incorrect request or parameter received for " + service + " service for " + resource);
        this.service = service;
        this.resource = resource;
        this.message = message;
    }

    public String getService() {
        return service;
    }

    public String getResource() {
        return resource;
    }

    public String getMessage() {
        return message;
    }
}
