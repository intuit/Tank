/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Downloads of the tools on the Tools page, for callers that use an API token instead of a web session.
 */
@RestController
@RequestMapping(value = "/v2/tools")
@Tag(name = "Tools")
public class ToolsController {

    private static final String HEADLESS_DEBUGGER_JAR = "Headless-Debugger-all.jar";

    @Autowired
    private ServletContext servletContext;

    @GetMapping(value = "/headless-debugger", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @Operation(description = "Returns the Headless-Debugger-all.jar built with this Tank version. Requires a user API token.",
            summary = "Download the headless debugger jar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the jar", content = @Content),
            @ApiResponse(responseCode = "401", description = "No user is signed in or the token is invalid", content = @Content),
            @ApiResponse(responseCode = "403", description = "The agent token cannot download tools", content = @Content),
            @ApiResponse(responseCode = "404", description = "This Tank was built without the release profile, so it has no tools", content = @Content)
    })
    public ResponseEntity<Resource> getHeadlessDebugger() {
        RestAuthorization.requireUser("tools");
        String path = servletContext.getRealPath("/tools/" + HEADLESS_DEBUGGER_JAR);
        File jar = path == null ? null : new File(path);
        if (jar == null || !jar.isFile()) {
            throw new GenericServiceResourceNotFoundException("tools", HEADLESS_DEBUGGER_JAR, null);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + HEADLESS_DEBUGGER_JAR + "\"")
                .contentLength(jar.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(jar));
    }
}
