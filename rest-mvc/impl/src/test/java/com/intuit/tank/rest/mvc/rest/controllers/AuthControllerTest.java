/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.AuthConfig;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.LoginRequest;
import com.intuit.tank.rest.mvc.rest.services.auth.AuthServiceV2;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController controller;

    @Mock
    private AuthServiceV2 authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void getConfig() {
        AuthConfig config = new AuthConfig(false, false, "url", "v", null, null);
        when(authService.getConfig()).thenReturn(config);
        assertSame(config, controller.getConfig().getBody());
    }

    @Test
    void login() {
        LoginRequest login = new LoginRequest("bob", "pw");
        CurrentUser me = new CurrentUser("bob", "e", List.of(), false, Map.of(), false, null);
        when(authService.login(login, request, response)).thenReturn(me);
        ResponseEntity<CurrentUser> result = controller.login(login, request, response);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(me, result.getBody());
    }

    @Test
    void startSso_redirectsToIdentityProvider() {
        when(authService.startSsoLogin("/app", request)).thenReturn("https://idp.example.com/authorize?state=s");
        ResponseEntity<Void> result = controller.startSso("/app", request);
        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertEquals(URI.create("https://idp.example.com/authorize?state=s"), result.getHeaders().getLocation());
    }

    @Test
    void completeSso_redirectsIntoApplication() throws Exception {
        when(authService.completeSsoLogin("c", "s", request, response)).thenReturn("/tank/app");
        ResponseEntity<Void> result = controller.completeSso("c", "s", request, response);
        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertEquals(URI.create("/tank/app"), result.getHeaders().getLocation());
    }

    @Test
    void logout() {
        assertEquals(HttpStatus.NO_CONTENT, controller.logout(request).getStatusCode());
        verify(authService).logout(request);
    }
}
