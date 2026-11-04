/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

/**
 * Thrown when a request needs an authenticated user and has none, or when login credentials are
 * rejected. Mapped to HTTP 401.
 */
public class GenericServiceUnauthorizedException extends GenericServiceException {

    private static final long serialVersionUID = 1L;

    private final String service;

    public GenericServiceUnauthorizedException(String service, String message) {
        super(message);
        this.service = service;
    }

    public String getService() {
        return service;
    }
}
