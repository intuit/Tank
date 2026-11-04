/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PublicEndpointsTest {

    @ParameterizedTest
    @CsvSource({
            "/tank/v2/auth/config, true",
            "/tank/v2/auth/login, true",
            "/tank/v2/auth/login/, true",
            "/tank/v2/auth/sso/authorize, true",
            "/tank/v2/auth/sso/callback, true",
            "/tank/v2/auth/logout, false",
            "/tank/v2/me, false",
            "/tank/v2/auth/login/../../me, false",
            "/tank/v2/auth/login;x=1, false",
            "/v2/auth/login, false",
            "/tank/v2/auth/configx, false"
    })
    void onlyLoginEndpointsArePublic(String uri, boolean expected) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/tank");
        when(request.getRequestURI()).thenReturn(uri);
        assertEquals(expected, PublicEndpoints.isPublic(request), uri);
    }

    @ParameterizedTest
    @CsvSource({ "/v2/auth/login, true", "/v2/me, false" })
    void rootContextPath(String uri, boolean expected) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn(uri);
        assertEquals(expected, PublicEndpoints.isPublic(request), uri);
    }
}
