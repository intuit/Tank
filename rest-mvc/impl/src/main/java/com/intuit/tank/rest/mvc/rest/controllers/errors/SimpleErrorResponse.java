/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SimpleErrorResponse extends ResponseEntity<Object> {

    // No status or cause provided, default to 500
    public SimpleErrorResponse(String userFriendlyMessage, boolean scrubSensitiveData) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, userFriendlyMessage, null, scrubSensitiveData);
    }

    // No status provided, default to 500
    public SimpleErrorResponse(String userFriendlyMessage, Throwable cause, boolean scrubSensitiveData) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, userFriendlyMessage, cause, scrubSensitiveData);
    }

    // No cause provided
    public SimpleErrorResponse(HttpStatus status, String userFriendlyMessage, boolean scrubSensitiveData) {
        this(status, userFriendlyMessage, null, scrubSensitiveData);
    }

    public SimpleErrorResponse(HttpStatus status, String userFriendlyMessage, Throwable cause,
            boolean scrubSensitiveData) {
        super(new SimpleErrorResponseBody(userFriendlyMessage, cause, scrubSensitiveData), status);
    }
}
