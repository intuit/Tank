/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.auth.LoginRequest;
import com.intuit.tank.rest.mvc.rest.models.auth.UserInfoTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping(value = "/v2/auth", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Auth")
public class AuthController {

    private static final Logger LOG = LogManager.getLogger(AuthController.class);

    /**
     * Resolve TankSecurityContext by its CDI @Named qualifier to avoid the
     * ambiguous-dependency error between TankSecurityContext and
     * SecurityContextImpl, and to avoid a circular compile-time dependency
     * (web-support → rest-mvc-impl).
     */
    private SecurityContext getSecurityContext() {
        return CDI.current()
                .select(SecurityContext.class, NamedLiteral.of("tankSecurityContext"))
                .get();
    }

    @PostMapping("/login")
    @Operation(summary = "Username/password login", description = "Authenticates with username and password, establishes a session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    public ResponseEntity<UserInfoTO> login(
            @RequestBody LoginRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            SecurityContext sc = getSecurityContext();
            AuthenticationParameters params = AuthenticationParameters.withParams()
                    .credential(new UsernamePasswordCredential(req.getUsername(), req.getPassword()));
            AuthenticationStatus status = sc.authenticate(request, response, params);
            if (status == AuthenticationStatus.SUCCESS) {
                Principal principal = sc.getCallerPrincipal();
                return ResponseEntity.ok(UserInfoTO.builder()
                        .username(principal != null ? principal.getName() : req.getUsername())
                        .build());
            }
        } catch (Exception e) {
            LOG.warn("Login failed for user {}: {}", req.getUsername(), e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates the current session")
    @ApiResponse(responseCode = "200", description = "Logged out")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inRole/{role}")
    @Operation(summary = "Get current user", description = "Returns info about the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    public ResponseEntity<Void> inRole(@PathVariable String role) {
        try {
            boolean inRole = getSecurityContext().isCallerInRole(role);
            return inRole
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/sso-redirect")
    @Operation(summary = "Get SSO redirect URL", description = "Returns the OIDC authorization URL for SSO login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSO redirect URL"),
            @ApiResponse(responseCode = "503", description = "SSO not configured", content = @Content)
    })
    public ResponseEntity<Map<String, String>> ssoRedirect() {
        try {
            // Resolve TankSsoHandler via CDI at runtime
            Object ssoHandler = CDI.current().select(
                    Class.forName("com.intuit.tank.auth.sso.TankSsoHandler")).get();
            String url = (String) ssoHandler.getClass()
                    .getMethod("GetOnLoadAuthorizationRequest")
                    .invoke(ssoHandler);
            return ResponseEntity.ok(Map.of("redirectUrl", url));
        } catch (Exception e) {
            LOG.warn("SSO redirect failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/sso-callback")
    @Operation(summary = "OIDC SSO callback", description = "Handles the OIDC authorization code callback, establishes a session, redirects to /app/")
    @ApiResponse(responseCode = "302", description = "Redirect to /app/")
    public void ssoCallback(
            @RequestParam(value = "code", required = false) String code,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (code == null || code.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/app/login?error=missing_code");
            return;
        }
        try {
            Object ssoHandler = CDI.current().select(
                    Class.forName("com.intuit.tank.auth.sso.TankSsoHandler")).get();
            ssoHandler.getClass().getMethod("HandleSsoAuthorization", String.class)
                    .invoke(ssoHandler, code);
            response.sendRedirect(request.getContextPath() + "/app/");
        } catch (Exception e) {
            LOG.warn("SSO callback failed: {}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/login?error=sso_failed");
        }
    }
}
