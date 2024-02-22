/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.logs.LogServiceV2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping(value = "/v2/logs")
@Tag(name = "Logs")
public class LogController {
    @Resource
    private LogServiceV2 logServiceV2;

    @RequestMapping(value = "/{filename}", method = RequestMethod.GET)
    @Operation(description = "Retrieves a specific log file from logs", summary = "Get streaming log file output", hidden = true)
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable String filename, @RequestParam(required = false) String from) throws IOException {
        StreamingResponseBody response = logServiceV2.getFile(filename, from);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response);
    }
}
