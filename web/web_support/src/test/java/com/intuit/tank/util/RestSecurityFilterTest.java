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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.security.AuthenticatedRequest;
import com.intuit.tank.rest.mvc.rest.security.CsrfTokens;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
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
    private HttpSession session;

    @Mock
    private FilterChain chain;

    @Mock
    private AgentConfig agentConfig;

    private StringWriter body;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        when(agentConfig.getAgentToken()).thenReturn("agent-secret-token");
        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn("/tank");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private TankPrincipal forwardedPrincipal() throws IOException, ServletException {
        ArgumentCaptor<ServletRequest> forwarded = ArgumentCaptor.forClass(ServletRequest.class);
        verify(chain).doFilter(forwarded.capture(), eq(response));
        assertInstanceOf(AuthenticatedRequest.class, forwarded.getValue());
        return ((AuthenticatedRequest) forwarded.getValue()).getTankPrincipal();
    }

    private void givenSessionUser(String name, Set<String> roles) {
        when(request.getSession(false)).thenReturn(session);
        when(securityContext.getCallerPrincipal()).thenReturn(new CallerPrincipal(name));
        when(securityContext.getCallerRoles()).thenReturn(roles);
    }

    @Test
    public void testDoFilter_SecurityDisabled_Anonymous_PassesThroughUnwrapped() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verifyNoInteractions(securityContext);
    }

    @Test
    public void testDoFilter_SecurityDisabled_SessionUser_StillAttachesIdentity() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(false);
        givenSessionUser("alice", Set.of("user"));

        filter.doFilter(request, response, chain);

        TankPrincipal principal = forwardedPrincipal();
        assertEquals("alice", principal.getName());
        assertEquals(TankPrincipal.AuthMethod.SESSION, principal.getAuthMethod());
    }

    @Test
    public void testDoFilter_SecurityEnabled_WithLoggedInUser_AttachesSessionPrincipal() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        givenSessionUser("alice", Set.of("user", "admin"));

        filter.doFilter(request, response, chain);

        TankPrincipal principal = forwardedPrincipal();
        assertEquals("alice", principal.getName());
        assertTrue(principal.isInRole("admin"));
    }

    @Test
    public void testDoFilter_SecurityEnabled_NoAuthHeader_NoSession_Returns401() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(body.toString().contains("Unauthorized"));
        verify(chain, never()).doFilter(any(), any());
        verifyNoInteractions(securityContext);
    }

    @Test
    public void testDoFilter_WithAgentBearerToken_AttachesAgentPrincipal() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer agent-secret-token");

        filter.doFilter(request, response, chain);

        assertTrue(forwardedPrincipal().isAgent());
        verifyNoInteractions(userDao);
    }

    @Test
    public void testDoFilter_WithValidUserApiToken_AttachesUserWithGroups() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer user-api-token");
        User user = new User();
        user.setName("alice");
        Group group = new Group();
        group.setName("scripters");
        user.addGroup(group);
        when(userDao.findByApiToken("user-api-token")).thenReturn(user);

        filter.doFilter(request, response, chain);

        TankPrincipal principal = forwardedPrincipal();
        assertEquals("alice", principal.getName());
        assertEquals(TankPrincipal.AuthMethod.API_TOKEN, principal.getAuthMethod());
        assertTrue(principal.isInRole("scripters"));
        verify(userDao).saveOrUpdate(user);
    }

    @Test
    public void testDoFilter_SecurityEnabled_WithInvalidBearerToken_Returns401EvenWithSession() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-token");
        when(userDao.findByApiToken("invalid-token")).thenReturn(null);
        givenSessionUser("alice", Set.of());

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void testDoFilter_TokenLookupFails_Returns401Once() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer some-token");
        when(userDao.findByApiToken("some-token")).thenThrow(new RuntimeException("db down"));

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void testDoFilter_SessionGet_IssuesCsrfCookie() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        givenSessionUser("alice", Set.of());

        filter.doFilter(request, response, chain);

        ArgumentCaptor<Cookie> cookie = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookie.capture());
        assertEquals(CsrfTokens.COOKIE_NAME, cookie.getValue().getName());
        assertFalse(cookie.getValue().isHttpOnly());
        assertEquals("/tank", cookie.getValue().getPath());
    }

    @Test
    public void testDoFilter_SessionPost_WithoutCsrfToken_Returns403() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getMethod()).thenReturn("POST");
        givenSessionUser("alice", Set.of());

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void testDoFilter_SessionDelete_WithCsrfToken_PassesThrough() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getMethod()).thenReturn("DELETE");
        givenSessionUser("alice", Set.of());
        when(session.getAttribute(anyString())).thenReturn("csrf-value");
        when(request.getHeader(CsrfTokens.HEADER_NAME)).thenReturn("csrf-value");

        filter.doFilter(request, response, chain);

        assertEquals("alice", forwardedPrincipal().getName());
    }

    @Test
    public void testDoFilter_TokenPost_DoesNotRequireCsrfToken() throws IOException, ServletException {
        when(tankConfig.isRestSecurityEnabled()).thenReturn(true);
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer agent-secret-token");

        filter.doFilter(request, response, chain);

        assertTrue(forwardedPrincipal().isAgent());
        verify(response, never()).addCookie(any());
    }
}
