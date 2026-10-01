/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.auth.sso;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TankSsoHandlerReturnPathTest {

    private final TankSsoHandler handler = new TankSsoHandler();

    private static HttpSession fakeSession() {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(anyString())).thenAnswer(i -> attributes.get(i.getArgument(0, String.class)));
        doAnswer(i -> attributes.put(i.getArgument(0), i.getArgument(1))).when(session).setAttribute(anyString(), any());
        doAnswer(i -> attributes.remove(i.getArgument(0, String.class))).when(session).removeAttribute(anyString());
        return session;
    }

    @Test
    void returnPath_isSingleUse() {
        HttpSession session = fakeSession();
        handler.setReturnPath(session, "/app/jobs");
        assertEquals("/app/jobs", handler.consumeReturnPath(session));
        assertNull(handler.consumeReturnPath(session));
    }

    @Test
    void nullReturnPath_clearsPreviousOne() {
        HttpSession session = fakeSession();
        handler.setReturnPath(session, "/app/jobs");
        handler.setReturnPath(session, null);
        assertNull(handler.consumeReturnPath(session));
    }

    @Test
    void noSession() {
        assertNull(handler.consumeReturnPath(null));
    }
}
