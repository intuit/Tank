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
 * Each endpoint names its file, so the API cannot serve anything else in /tools.
 */
@RestController
@RequestMapping(value = "/v2/tools")
@Tag(name = "Tools")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the file", content = @Content),
        @ApiResponse(responseCode = "401", description = "No user is signed in or the token is invalid", content = @Content),
        @ApiResponse(responseCode = "403", description = "The agent token cannot download tools", content = @Content),
        @ApiResponse(responseCode = "404", description = "This Tank was built without the release profile, so it has no tools", content = @Content)
})
public class ToolsController {

    @Autowired
    private ServletContext servletContext;

    @GetMapping(value = "/debugger", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @Operation(summary = "Download the agent visual debugger", description = "Returns Tank-Debugger-all.jar built with this Tank version")
    public ResponseEntity<Resource> getDebugger() {
        return download("Tank-Debugger-all.jar");
    }

    @GetMapping(value = "/script-runner", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @Operation(summary = "Download the script filter editor", description = "Returns Tank-Script-Runner-all.jar built with this Tank version")
    public ResponseEntity<Resource> getScriptRunner() {
        return download("Tank-Script-Runner-all.jar");
    }

    @GetMapping(value = "/proxy", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @Operation(summary = "Download the Tank Proxy", description = "Returns Tank-Proxy-pkg.zip built with this Tank version")
    public ResponseEntity<Resource> getProxy() {
        return download("Tank-Proxy-pkg.zip");
    }

    @GetMapping(value = "/api-harness", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @Operation(summary = "Download the API test harness", description = "Returns apiharness-1.0-all.jar built with this Tank version")
    public ResponseEntity<Resource> getApiHarness() {
        return download("apiharness-1.0-all.jar");
    }

    private ResponseEntity<Resource> download(String fileName) {
        RestAuthorization.requireUser("tools");
        String path = servletContext.getRealPath("/tools/" + fileName);
        File file = path == null ? null : new File(path);
        if (file == null || !file.isFile()) {
            throw new GenericServiceResourceNotFoundException("tools", fileName, null);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }
}
