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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.function.Function;
import java.util.stream.Stream;

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

    static Stream<Arguments> tools() {
        return Stream.of(
                Arguments.of("Tank-Debugger-all.jar", (Function<ToolsController, ResponseEntity<Resource>>) ToolsController::getDebugger),
                Arguments.of("Tank-Script-Runner-all.jar", (Function<ToolsController, ResponseEntity<Resource>>) ToolsController::getScriptRunner),
                Arguments.of("Tank-Proxy-pkg.zip", (Function<ToolsController, ResponseEntity<Resource>>) ToolsController::getProxy),
                Arguments.of("apiharness-1.0-all.jar", (Function<ToolsController, ResponseEntity<Resource>>) ToolsController::getApiHarness));
    }

    @ParameterizedTest
    @MethodSource("tools")
    void userTokenDownloadsTheTool(String fileName, Function<ToolsController, ResponseEntity<Resource>> endpoint) throws IOException {
        Path file = Files.writeString(toolsDir.resolve(fileName), "tool bytes");
        when(servletContext.getRealPath("/tools/" + fileName)).thenReturn(file.toString());
        SecurityTestSupport.actAs(SecurityTestSupport.user("jenkins"));

        ResponseEntity<Resource> response = endpoint.apply(toolsController);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=\"" + fileName + "\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(10, response.getHeaders().getContentLength());
        assertEquals("tool bytes", response.getBody().getContentAsString(StandardCharsets.UTF_8));
    }

    @ParameterizedTest
    @MethodSource("tools")
    void missingToolIsNotFound(String fileName, Function<ToolsController, ResponseEntity<Resource>> endpoint) {
        when(servletContext.getRealPath("/tools/" + fileName)).thenReturn(toolsDir.resolve(fileName).toString());
        SecurityTestSupport.actAs(SecurityTestSupport.user("jenkins"));

        assertThrows(GenericServiceResourceNotFoundException.class, () -> endpoint.apply(toolsController));
    }

    @Test
    void anonymousCallerIsRejected() {
        assertThrows(GenericServiceUnauthorizedException.class, () -> toolsController.getProxy());
    }

    @Test
    void agentTokenIsRejected() {
        SecurityTestSupport.actAs(TankPrincipal.agent());

        assertThrows(GenericServiceForbiddenAccessException.class, () -> toolsController.getDebugger());
    }
}
