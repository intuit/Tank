/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenericExceptionHandlerTest {
    @Test
    public void testHandleResourceNotFoundException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        GenericServiceResourceNotFoundException genericServiceResourceNotFoundException = mock(GenericServiceResourceNotFoundException.class);
        SimpleErrorResponse response = genericExceptionHandler.handleResourceNotFoundException(genericServiceResourceNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testHandleResourceCreateOrUpdateException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        GenericServiceCreateOrUpdateException genericServiceCreateOrUpdateException = mock(GenericServiceCreateOrUpdateException.class);
        SimpleErrorResponse response = genericExceptionHandler.handleResourceCreateOrUpdateException(genericServiceCreateOrUpdateException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testHandleResourceDeleteException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        GenericServiceDeleteException genericServiceDeleteException = mock(GenericServiceDeleteException.class);
        SimpleErrorResponse response = genericExceptionHandler.handleResourceDeleteException(genericServiceDeleteException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testHandleConflictException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        GenericServiceConflictException conflictException = new GenericServiceConflictException("jobs", "job", "Cannot delete active job");
        SimpleErrorResponse response = genericExceptionHandler.handleConflictException(conflictException);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testHandleOtherErrors() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        Throwable e = mock(Throwable.class);
        SimpleErrorResponse response = genericExceptionHandler.handleOtherErrors(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testHandleBadRequestException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        GenericServiceBadRequestException e = mock(GenericServiceBadRequestException.class);
        SimpleErrorResponse response = genericExceptionHandler.handleBadRequestException(e);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testHandleForbiddenAccessException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        GenericServiceForbiddenAccessException e = mock(GenericServiceForbiddenAccessException.class);
        SimpleErrorResponse response = genericExceptionHandler.handleForbiddenAccessException(e);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testHandleInternalServerException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        GenericServiceInternalServerException e = mock(GenericServiceInternalServerException.class);
        SimpleErrorResponse response = genericExceptionHandler.handleInternalServerException(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testHandleClientException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        ClientException e = mock(ClientException.class);
        when(e.getStatusCode()).thenReturn(422);
        when(e.getErrorMessage()).thenReturn("Unprocessable");
        SimpleErrorResponse response = genericExceptionHandler.handleClientException(e);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    public void testHandleFileException() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        org.springframework.web.multipart.MultipartException ex = mock(org.springframework.web.multipart.MultipartException.class);
        org.springframework.http.ResponseEntity<Object> response = genericExceptionHandler.handleFileException(request, ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testHandleNoResourceFound() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        org.springframework.web.servlet.resource.NoResourceFoundException ex = mock(org.springframework.web.servlet.resource.NoResourceFoundException.class);
        org.springframework.http.ResponseEntity<Object> response = genericExceptionHandler.handleNoResourceFound(request, ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
