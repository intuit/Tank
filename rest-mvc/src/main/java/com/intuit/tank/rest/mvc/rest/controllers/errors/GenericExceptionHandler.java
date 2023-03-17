/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@ControllerAdvice
public class GenericExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(GenericExceptionHandler.class);

    @Value("${server.include-debug-info}")
    private boolean includeDebugInfo;

    @ExceptionHandler
    public SimpleErrorResponse handleResourceNotFoundException(GenericServiceResourceNotFoundException e) {
        LOGGER.error("handling an error from the " + e.getService() + " service", e);
        return genericErrorResponse(HttpStatus.NOT_FOUND, "Resource not found: " + e.getResource(), e);
    }

    @ExceptionHandler
    public SimpleErrorResponse handleResourceCreateOrUpdateException(GenericServiceCreateOrUpdateException e) {
        LOGGER.error("handling an error from the " + e.getService() + " service", e);
        return genericErrorResponse(HttpStatus.BAD_REQUEST, "Resource could not be created or updated: " + e.getResource(), e);
    }

    @ExceptionHandler
    public SimpleErrorResponse handleResourceDeleteException(GenericServiceDeleteException e) {
        LOGGER.error("handling an error from the " + e.getService() + " service", e);
        return genericErrorResponse(HttpStatus.NOT_FOUND, "Resource could not be deleted: " + e.getResource(), e);
    }

    @ExceptionHandler
    public SimpleErrorResponse handleOtherErrors(Throwable t) {
        LOGGER.error("handling an unexpected exception", t);
        return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred processing this request", t);
    }

    private SimpleErrorResponse genericErrorResponse(HttpStatus status, String message, Throwable e) {
        final boolean scrubSensitiveData = !includeDebugInfo; // API-1890: be very explicit about the flipping of the sense of the flag
        return new SimpleErrorResponse(status, message, e, scrubSensitiveData);
    }
}
