/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.AdminGroup;
import com.intuit.tank.rest.mvc.rest.models.AdminUser;
import com.intuit.tank.rest.mvc.rest.models.AdminUserRequest;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.LogLevelSetting;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.services.admin.AdminServiceV2;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @InjectMocks
    private AdminController controller;

    @Mock
    private AdminServiceV2 adminService;

    @Mock
    private HttpServletRequest request;

    private AutoCloseable mocks;

    private static final AdminUser CAROL = new AdminUser(7, "carol", "carol@example.com", List.of("user"), false, null,
            null, null, null);

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(443);
        when(request.getContextPath()).thenReturn("/tank");
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://localhost/tank/v2/admin/users"));
    }

    @AfterEach
    void tearDown() throws Exception {
        RequestContextHolder.resetRequestAttributes();
        mocks.close();
    }

    @Test
    void listAndGetUsers() {
        PageResponse<AdminUser> page = new PageResponse<>(List.of(CAROL), 1, 0, 25);
        when(adminService.listUsers(0, 25, "name", "c")).thenReturn(page);
        when(adminService.getUser(7)).thenReturn(CAROL);

        assertSame(page, controller.listUsers(0, 25, "name", "c").getBody());
        assertSame(CAROL, controller.getUser(7).getBody());
    }

    @Test
    void createUser_returnsCreatedWithLocation() {
        AdminUserRequest body = new AdminUserRequest("carol", "carol@example.com", "long-enough", null);
        when(adminService.createUser(body)).thenReturn(CAROL);

        ResponseEntity<AdminUser> result = controller.createUser(body);

        assertEquals(201, result.getStatusCode().value());
        assertTrue(result.getHeaders().getLocation().toString().endsWith("/tank/v2/admin/users/7"));
    }

    @Test
    void updateDeleteAndTokens() {
        AdminUserRequest body = new AdminUserRequest(null, "c2@example.com", null, null);
        when(adminService.updateUser(7, body)).thenReturn(CAROL);
        when(adminService.createApiToken(7)).thenReturn(new ApiTokenResponse("tok"));

        assertEquals(200, controller.updateUser(7, body).getStatusCode().value());
        assertEquals(204, controller.deleteUser(7).getStatusCode().value());
        assertEquals("tok", controller.createApiToken(7).getBody().apiToken());
        assertEquals(204, controller.deleteApiToken(7).getStatusCode().value());
        assertEquals(204, controller.resetPreferences(7).getStatusCode().value());
        verify(adminService).deleteUser(7);
        verify(adminService).deleteApiToken(7);
        verify(adminService).resetPreferences(7);
    }

    @Test
    void groupsLogsAndLogLevel() {
        when(adminService.getGroups()).thenReturn(List.of(new AdminGroup("user", true)));
        when(adminService.listLogFiles()).thenReturn(List.of("tank.log"));
        LogLevelSetting level = new LogLevelSetting("INFO", "node-1");
        when(adminService.getLogLevel()).thenReturn(level);
        LogLevelSetting change = new LogLevelSetting("DEBUG", null);
        when(adminService.setLogLevel(change)).thenReturn(new LogLevelSetting("DEBUG", "node-1"));

        assertEquals("user", controller.getGroups().getBody().get(0).name());
        assertEquals(List.of("tank.log"), controller.listLogFiles().getBody());
        assertSame(level, controller.getLogLevel().getBody());
        assertEquals("DEBUG", controller.setLogLevel(change).getBody().level());
    }
}
