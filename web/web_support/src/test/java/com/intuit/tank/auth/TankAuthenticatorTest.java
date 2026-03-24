package com.intuit.tank.auth;

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

import jakarta.security.enterprise.CallerPrincipal;

import com.intuit.tank.auth.sso.TankSsoHandler;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TankAuthenticatorTest {

    @InjectMocks
    private TankAuthenticator authenticator;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private Messages messages;

    @Mock
    private TankConfig tankConfig;

    @Mock
    private TankSsoHandler tankSsoHandler;

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
    public void testGetUsername_SetAndGet() {
        authenticator.setUsername("testuser");
        assertEquals("testuser", authenticator.getUsername());
    }

    @Test
    public void testGetPassword_SetAndGet() {
        authenticator.setPassword("secret123");
        assertEquals("secret123", authenticator.getPassword());
    }

    @Test
    public void testIsLoggedIn_WhenNoPrincipal_ReturnsFalse() {
        when(securityContext.getCallerPrincipal()).thenReturn(null);
        assertFalse(authenticator.isloggedIn());
    }

    @Test
    public void testIsLoggedIn_WhenPrincipalExists_ReturnsTrue() {
        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        assertTrue(authenticator.isloggedIn());
    }

    @Test
    public void testLogin_WhenFacesContextNull_ReturnsFailed() throws Exception {
        // FacesContext.getCurrentInstance() returns null in unit tests
        // continueAuthentication() catches the NPE and returns SEND_FAILURE
        // login() then calls messages.error and returns "failure"
        authenticator.setUsername("testuser");
        authenticator.setPassword("testpass1");
        String result = authenticator.login();
        assertEquals("failure", result);
        verify(messages).error(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsssoLogin_WhenConfigNotNull_ReturnsTrue() {
        OidcSsoConfig oidcSsoConfig = mock(OidcSsoConfig.class);
        when(tankConfig.getOidcSsoConfig()).thenReturn(oidcSsoConfig);
        HierarchicalConfiguration<ImmutableNode> config = mock(HierarchicalConfiguration.class);
        when(oidcSsoConfig.getConfiguration()).thenReturn(config);
        assertTrue(authenticator.isssoLogin());
    }

    @Test
    public void testIsssoLogin_WhenConfigNull_ReturnsFalse() {
        OidcSsoConfig oidcSsoConfig = mock(OidcSsoConfig.class);
        when(tankConfig.getOidcSsoConfig()).thenReturn(oidcSsoConfig);
        when(oidcSsoConfig.getConfiguration()).thenReturn(null);
        assertFalse(authenticator.isssoLogin());
    }

    @Test
    public void testIsssoLogin_WhenOidcSsoConfigNull_ThrowsNPE() {
        when(tankConfig.getOidcSsoConfig()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> authenticator.isssoLogin());
    }
}
