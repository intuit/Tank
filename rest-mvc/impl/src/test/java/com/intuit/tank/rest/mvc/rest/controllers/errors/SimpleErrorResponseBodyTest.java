/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class SimpleErrorResponseBodyTest {
    @Test
    public void testSimpleErrorResponseBody() {
        String msg = "this is a msg";
        Throwable cause = mock(Throwable.class);
        SimpleErrorResponseBody simpleErrorResponseBody = new SimpleErrorResponseBody(msg, cause, true);
        simpleErrorResponseBody.setMessage("new message");
        simpleErrorResponseBody.setDebugInfo("debug info");

        SimpleErrorResponseBody simpleErrorResponseBody2 = new SimpleErrorResponseBody(msg, cause, true);
        simpleErrorResponseBody2.setMessage("new message");
        simpleErrorResponseBody2.setDebugInfo("debug info");

        assertTrue(simpleErrorResponseBody.equals(simpleErrorResponseBody2));
        assertTrue(simpleErrorResponseBody.canEqual(simpleErrorResponseBody2));
        assertEquals(simpleErrorResponseBody.hashCode(), simpleErrorResponseBody2.hashCode());
    }
}
