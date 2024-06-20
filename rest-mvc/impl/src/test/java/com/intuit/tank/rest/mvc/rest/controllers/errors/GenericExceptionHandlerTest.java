/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

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
    public void testHandleOtherErrors() {
        GenericExceptionHandler genericExceptionHandler = new GenericExceptionHandler();
        Throwable e = mock(Throwable.class);
        SimpleErrorResponse response = genericExceptionHandler.handleOtherErrors(e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
