/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CsrfTokensTest {

    private static HttpSession fakeSession() {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(anyString())).thenAnswer(i -> attributes.get(i.getArgument(0, String.class)));
        doAnswer(i -> attributes.put(i.getArgument(0), i.getArgument(1))).when(session).setAttribute(anyString(), any());
        return session;
    }

    @Test
    void safeMethods() {
        assertTrue(CsrfTokens.isSafeMethod("GET"));
        assertTrue(CsrfTokens.isSafeMethod("head"));
        assertFalse(CsrfTokens.isSafeMethod("POST"));
        assertFalse(CsrfTokens.isSafeMethod("DELETE"));
        assertFalse(CsrfTokens.isSafeMethod(null));
    }

    @Test
    void token_isStablePerSessionAndRandomAcrossSessions() {
        HttpSession session = fakeSession();
        String token = CsrfTokens.getOrCreate(session);
        assertEquals(token, CsrfTokens.getOrCreate(session));
        assertNotEquals(token, CsrfTokens.getOrCreate(fakeSession()));
        assertTrue(token.length() >= 40);
    }

    @Test
    void isValid_requiresMatchingHeader() {
        HttpSession session = fakeSession();
        String token = CsrfTokens.getOrCreate(session);
        HttpServletRequest request = mock(HttpServletRequest.class);

        assertFalse(CsrfTokens.isValid(request, session));
        when(request.getHeader(CsrfTokens.HEADER_NAME)).thenReturn("wrong");
        assertFalse(CsrfTokens.isValid(request, session));
        when(request.getHeader(CsrfTokens.HEADER_NAME)).thenReturn(token);
        assertTrue(CsrfTokens.isValid(request, session));
        assertFalse(CsrfTokens.isValid(request, fakeSession()));
    }

    @Test
    void ensureCookie_skipsWhenBrowserHasCurrentToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie(CsrfTokens.COOKIE_NAME, "abc") });

        CsrfTokens.ensureCookie(request, response, "abc");
        verify(response, never()).addCookie(any());

        CsrfTokens.ensureCookie(request, response, "new-token");
        verify(response).addCookie(argThat(c -> "new-token".equals(c.getValue()) && "/".equals(c.getPath())
                && "Lax".equals(c.getAttribute("SameSite"))));
    }
}
