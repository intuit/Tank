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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

/**
 * Sign in and out of the web UI. {@code /config}, {@code /login} and the SSO endpoints are callable
 * without credentials.
 */
@RestController
@RequestMapping(value = "/v2/auth", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Auth")
public class AuthController {

    @Resource
    private AuthServiceV2 authService;

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @Operation(description = "Returns what the login page needs: whether SSO is configured, the version and the banner",
            summary = "Get login page configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the configuration")
    })
    public ResponseEntity<AuthConfig> getConfig() {
        return ResponseEntity.ok(authService.getConfig());
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Logs in with a username and password. Sets the session cookie and a new XSRF-TOKEN cookie, "
            + "whose value must be sent in the X-XSRF-TOKEN header on later state-changing requests",
            summary = "Log in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged in; returns the current user"),
            @ApiResponse(responseCode = "400", description = "Username or password missing", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content)
    })
    public ResponseEntity<CurrentUser> login(@RequestBody LoginRequest login, HttpServletRequest request,
                                             HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(login, request, response));
    }

    @RequestMapping(value = "/sso/authorize", method = RequestMethod.GET, produces = { MediaType.ALL_VALUE })
    @Operation(description = "Redirects the browser to the identity provider to start a single sign-on login",
            summary = "Start SSO login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to the identity provider", content = @Content),
            @ApiResponse(responseCode = "400", description = "SSO not configured or invalid returnTo", content = @Content)
    })
    public ResponseEntity<Void> startSso(@RequestParam(required = false) String returnTo, HttpServletRequest request) {
        return redirect(authService.startSsoLogin(returnTo, request));
    }

    @RequestMapping(value = "/sso/callback", method = RequestMethod.GET, produces = { MediaType.ALL_VALUE })
    @Operation(description = "Completes a single sign-on login when the identity provider redirects back, then redirects "
            + "to the returnTo path given at the start. Used when this URL is the configured SSO redirect URL",
            summary = "Complete SSO login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Logged in; redirect into the application", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid or expired SSO response", content = @Content)
    })
    public ResponseEntity<Void> completeSso(@RequestParam(required = false) String code,
                                            @RequestParam(required = false) String state,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        return redirect(authService.completeSsoLogin(code, state, request, response));
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @Operation(description = "Logs out and ends the session", summary = "Log out")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logged out", content = @Content)
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    private static ResponseEntity<Void> redirect(String location) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(location));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
