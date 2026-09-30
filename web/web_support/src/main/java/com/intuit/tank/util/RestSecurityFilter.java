package com.intuit.tank.util;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.security.AuthenticatedRequest;
import com.intuit.tank.rest.mvc.rest.security.CsrfTokens;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * Authenticates {@code /v2} REST requests and attaches the caller as a {@link TankPrincipal}.
 *
 * <p>Callers are identified, in order, by the agent token or a user API token in the
 * {@code Authorization: Bearer} header, or by an existing web UI session. When
 * {@code rest-security-enabled} is true, requests with no identity are rejected with 401; when false
 * they continue anonymously as before. Session-authenticated state-changing requests must carry the
 * CSRF token (see {@link CsrfTokens}).</p>
 */
@WebFilter(urlPatterns = "/v2/*", asyncSupported = true)
public class RestSecurityFilter extends HttpFilter {
    private static final Logger LOG = LogManager.getLogger(RestSecurityFilter.class);
    private static final String BEARER_PREFIX = "bearer ";

    @Inject
    private TankSecurityContext securityContext;

    @Inject
    private TankConfig tankConfig;

    @Inject
    private UserDao userDao;

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        TankPrincipal principal;
        try {
            principal = authenticate(request);
        } catch (Exception e) {
            LOG.error("Error authenticating user", e);
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        if (principal == null) {
            if (tankConfig.isRestSecurityEnabled()) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        if (principal.getAuthMethod() == TankPrincipal.AuthMethod.SESSION) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                if (!CsrfTokens.isSafeMethod(request.getMethod()) && !CsrfTokens.isValid(request, session)) {
                    LOG.warn("Rejected {} {} from {}: missing or invalid CSRF token",
                            request.getMethod(), request.getRequestURI(), principal.getName());
                    sendError(response, HttpServletResponse.SC_FORBIDDEN, "Missing or invalid CSRF token");
                    return;
                }
                CsrfTokens.ensureCookie(request, response, CsrfTokens.getOrCreate(session));
            }
        }
        chain.doFilter(new AuthenticatedRequest(request, principal), response);
    }

    /**
     * @return the caller, or null when the request carries no valid credentials
     */
    private TankPrincipal authenticate(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.toLowerCase().startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length()).trim();
            if (!token.isEmpty() && token.equals(tankConfig.getAgentConfig().getAgentToken())) {
                return TankPrincipal.agent();
            }
            User user = validateToken(token);
            if (user != null) {
                Set<String> roles = user.getGroups().stream().map(Group::getName).collect(Collectors.toSet());
                return new TankPrincipal(user.getName(), roles, TankPrincipal.AuthMethod.API_TOKEN);
            }
            // an invalid token is never upgraded to the session identity
            return null;
        }

        // only consult the session-scoped security context when a session already exists,
        // so anonymous calls do not create sessions
        if (request.getSession(false) != null) {
            Principal caller = securityContext.getCallerPrincipal();
            if (caller != null) {
                return new TankPrincipal(caller.getName(), securityContext.getCallerRoles(),
                        TankPrincipal.AuthMethod.SESSION);
            }
        }
        return null;
    }

    private User validateToken(String token) {
        if (token.isEmpty()) {
            return null;
        }
        User user = userDao.findByApiToken(token);
        if (user != null) {
            // Update last login timestamp for API token usage
            user.setLastLoginTs(Instant.now());
            userDao.saveOrUpdate(user);
            LOG.debug("Updated last login timestamp for user: {} via API token", user.getName());
        }
        return user;
    }

    private static void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
