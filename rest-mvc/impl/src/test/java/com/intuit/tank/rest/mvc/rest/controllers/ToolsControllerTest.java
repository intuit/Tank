/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ToolsControllerTest {

    @InjectMocks
    private ToolsController toolsController;

    @Mock
    private ServletContext servletContext;

    @TempDir
    Path toolsDir;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void reset() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void userTokenDownloadsTheHeadlessDebuggerJar() throws IOException {
        Path jar = Files.writeString(toolsDir.resolve("Headless-Debugger-all.jar"), "jar bytes");
        when(servletContext.getRealPath("/tools/Headless-Debugger-all.jar")).thenReturn(jar.toString());
        SecurityTestSupport.actAs(SecurityTestSupport.user("jenkins"));

        ResponseEntity<Resource> response = toolsController.getHeadlessDebugger();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=\"Headless-Debugger-all.jar\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(9, response.getHeaders().getContentLength());
        assertEquals("jar bytes", response.getBody().getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    void anonymousCallerIsRejected() {
        assertThrows(GenericServiceUnauthorizedException.class, () -> toolsController.getHeadlessDebugger());
    }

    @Test
    void agentTokenIsRejected() {
        SecurityTestSupport.actAs(TankPrincipal.agent());

        assertThrows(GenericServiceForbiddenAccessException.class, () -> toolsController.getHeadlessDebugger());
    }

    @Test
    void missingJarIsNotFound() {
        when(servletContext.getRealPath("/tools/Headless-Debugger-all.jar"))
                .thenReturn(toolsDir.resolve("Headless-Debugger-all.jar").toString());
        SecurityTestSupport.actAs(SecurityTestSupport.user("jenkins"));

        assertThrows(GenericServiceResourceNotFoundException.class, () -> toolsController.getHeadlessDebugger());
    }
}
