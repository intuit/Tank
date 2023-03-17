/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Default")
public class DefaultController {

    @GetMapping(value ="/v2/ping",  produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings Tank V2 API default service", summary = "Check if Tank V2 API is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tank V2 API is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("PONG Tank V2 API", HttpStatus.OK);
    }

}
