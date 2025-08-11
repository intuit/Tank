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
import java.time.Instant;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.project.User;
import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.vm.settings.TankConfig;

@WebFilter(urlPatterns = "/v2/*", asyncSupported = true)
public class RestSecurityFilter extends HttpFilter {
    private static final Logger LOG = LogManager.getLogger(RestSecurityFilter.class);

    @Inject
    private TankSecurityContext securityContext;

    @Inject
    private TankConfig tankConfig;

    @Inject
    private UserDao userDao;

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (tankConfig.isRestSecurityEnabled()) {
            // check bearer token
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    // check agent token
                    if (token.equals(tankConfig.getAgentConfig().getAgentToken())) {
                        chain.doFilter(request, response);
                        return;
                    }
                    // check user token
                    if (validateToken(token)) {
                        chain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    LOG.error("Error authenticating user", e);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized access");
                }
            }

            // check if user is logged in
            if (securityContext.getCallerPrincipal() != null) {
                chain.doFilter(request, response);
                return;
            }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized access");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean validateToken(String token) {
        User user = userDao.findByApiToken(token);
        if (user != null) {
            // Update last login timestamp for API token usage
            user.setLastLoginTs(Instant.now());
            userDao.saveOrUpdate(user);
            LOG.debug("Updated last login timestamp for user: {} via API token", user.getName());
            return true;
        }
        return false;
    }
}
