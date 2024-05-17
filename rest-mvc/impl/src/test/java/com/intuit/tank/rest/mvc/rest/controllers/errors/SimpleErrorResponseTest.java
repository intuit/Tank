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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class SimpleErrorResponseTest {
    @Test
    public void testConstructor() {
        String msg = "friendly msg";
        SimpleErrorResponse simpleErrorResponse = new SimpleErrorResponse(msg, true);
        assertTrue(simpleErrorResponse.getBody().toString().contains(msg));
    }

    @Test
    public void testConstructorWithThrowable() {
        String msg = "friendly msg";
        SimpleErrorResponse simpleErrorResponse = new SimpleErrorResponse(msg, mock(Throwable.class), true);
        assertTrue(simpleErrorResponse.getBody().toString().contains(msg));
    }

    @Test
    public void testConstructorWithStatusCode() {
        String msg = "friendly msg";
        SimpleErrorResponse simpleErrorResponse = new SimpleErrorResponse(HttpStatus.I_AM_A_TEAPOT, msg, true);
        assertTrue(simpleErrorResponse.getBody().toString().contains(msg));
        assertEquals(HttpStatus.I_AM_A_TEAPOT, simpleErrorResponse.getStatusCode());
    }

    @Test
    public void testConstructorWithStatusCodeAndThrowable() {
        String msg = "friendly msg";
        SimpleErrorResponse simpleErrorResponse = new SimpleErrorResponse(HttpStatus.I_AM_A_TEAPOT, msg, mock(Throwable.class), true);
        assertTrue(simpleErrorResponse.getBody().toString().contains(msg));
        assertEquals(HttpStatus.I_AM_A_TEAPOT, simpleErrorResponse.getStatusCode());
    }
}
