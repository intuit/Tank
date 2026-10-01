/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.config.ConfigServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v2/users", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Users")
public class UserController {

    @Resource
    private ConfigServiceV2 configService;

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @Operation(description = "Returns the names of all users, sorted, for choosing an owner", summary = "Get user names")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the names"),
            @ApiResponse(responseCode = "401", description = "Not signed in", content = @Content)
    })
    public ResponseEntity<List<String>> getUserNames() {
        return ResponseEntity.ok(configService.getUserNames());
    }
}
