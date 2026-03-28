/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v2/auth", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Auth")
public class AuthController {

    private static final Logger LOG = LogManager.getLogger(AuthController.class);

    @RequestMapping(value = "/token", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Exchange username and password for an API token", summary = "Get API token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    public ResponseEntity<Map<String, String>> getToken(@RequestBody LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserDao userDao = new UserDao();
        User user = userDao.authenticate(request.getUsername(), request.getPassword());
        if (user == null) {
            LOG.warn("Failed login attempt for user: {}", request.getUsername());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Ensure the user has an API token
        if (user.getApiToken() == null) {
            user.generateApiToken();
            userDao.saveOrUpdate(user);
            LOG.debug("Generated new API token for user: {}", user.getName());
        }

        String roles = user.getGroups().stream()
                .map(Group::getName)
                .collect(Collectors.joining(","));

        Map<String, String> response = Map.of("token", user.getApiToken(), "name", user.getName(), "role", roles);

        LOG.debug("Issued API token to user: {}", user.getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
