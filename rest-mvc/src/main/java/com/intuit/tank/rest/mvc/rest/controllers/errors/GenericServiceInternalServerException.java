/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

public class GenericServiceInternalServerException extends GenericServiceException {

    private static final long serialVersionUID = 1L;

    private final String service;

    private final String resource;


    /**
     * Construct with a message {@code String} that is returned by the inherited
     * {@code Throwable.getMessage}.
     *
     * @param service
     *            the service impacted
     *
     * @param resource
     *            the specific service related resource
     *
     * @param cause
     *  the cause that is returned by the inherited
     * {@code Throwable.getCause}
     */
    public GenericServiceInternalServerException(String service, String resource, Throwable cause) {
        super("Server error caught for " + service + " service while handling " + resource + " caused by " + cause);
        this.service = service;
        this.resource = resource;
    }

    public String getService() {
        return service;
    }

    public String getResource() {
        return resource;
    }
}
