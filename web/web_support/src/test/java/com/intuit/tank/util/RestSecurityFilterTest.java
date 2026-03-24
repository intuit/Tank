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

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class RestSecurityFilterTest {

    @InjectMocks
    private RestSecurityFilter filter;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private TankConfig tankConfig;

    @Mock
    private UserDao userDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private AgentConfig agentConfig;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testDoFilter_SecurityDisabled_PassesThrough() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testDoFilter_SecurityEnabled_WithLoggedInUser_PassesThrough() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testDoFilter_SecurityEnabled_NoAuthHeader_NoSession_Returns401() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(securityContext.getCallerPrincipal()).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized access");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testDoFilter_SecurityEnabled_WithAgentBearerToken_PassesThrough() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer agent-secret-token");
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        when(agentConfig.getAgentToken()).thenReturn("agent-secret-token");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testDoFilter_SecurityEnabled_WithValidUserApiToken_PassesThrough() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer user-api-token");
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        when(agentConfig.getAgentToken()).thenReturn("different-token");

        User user = new User();
        user.setName("alice");
        when(userDao.findByApiToken("user-api-token")).thenReturn(user);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(userDao).saveOrUpdate(user);
    }

    @Test
    public void testDoFilter_SecurityEnabled_WithInvalidBearerToken_Returns401() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-token");
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        when(agentConfig.getAgentToken()).thenReturn("different-token");
        when(userDao.findByApiToken("invalid-token")).thenReturn(null);
        when(securityContext.getCallerPrincipal()).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized access");
    }
}
