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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

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
    public SimpleErrorResponse handleBadRequestException(GenericServiceBadRequestException e) {
        LOGGER.error("handling an error from the " + e.getService() + " service", e);
        return genericErrorResponse(HttpStatus.BAD_REQUEST, "Incorrect request or parameter: " + e.getMessage(), e);
    }

    @ExceptionHandler
    public SimpleErrorResponse handleForbiddenAccessException(GenericServiceForbiddenAccessException e) {
        LOGGER.error("handling an error from the " + e.getService() + " service", e);
        return genericErrorResponse(HttpStatus.FORBIDDEN, "Not authorize to access " + e.getResource(), e);
    }

    @ExceptionHandler
    public SimpleErrorResponse handleInternalServerException(GenericServiceInternalServerException e) {
        LOGGER.error("handling an error from the " + e.getService() + " service", e);
        return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server error thrown by " + e.getService() + " service while processing " + e.getResource(), e);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleFileException(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>("File upload error: incorrect request or missing file parameter", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Object> handleMissingServletRequestPart(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>("File upload error: incorrect request or missing file parameter", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>("Incorrect request body", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public SimpleErrorResponse handleOtherErrors(Throwable t) {
        LOGGER.error("handling an unexpected exception", t);
        return genericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred processing this request", t);
    }

    private SimpleErrorResponse genericErrorResponse(HttpStatus status, String message, Throwable e) {
        final boolean scrubSensitiveData = !includeDebugInfo;
        return new SimpleErrorResponse(status, message, e, scrubSensitiveData);
    }
}
