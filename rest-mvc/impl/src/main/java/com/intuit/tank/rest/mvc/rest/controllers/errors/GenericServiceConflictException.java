/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

/**
 * Thrown when a request conflicts with the current state of a resource: it was changed since the caller
 * read it, or a name is already taken. Mapped to HTTP 409.
 */
public class GenericServiceConflictException extends GenericServiceException {

    private static final long serialVersionUID = 1L;

    private final String service;

    public GenericServiceConflictException(String service, String message) {
        super(message);
        this.service = service;
    }

    public String getService() {
        return service;
    }
}
