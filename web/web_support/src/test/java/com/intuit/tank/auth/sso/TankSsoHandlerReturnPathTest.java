/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.auth.sso;

import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TankSsoHandlerReturnPathTest {

    @InjectMocks
    private TankSsoHandler handler;

    @Mock
    private TankConfig tankConfig;

    @Mock
    private OidcSsoConfig oidcSsoConfig;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(tankConfig.getOidcSsoConfig()).thenReturn(oidcSsoConfig);
        when(oidcSsoConfig.getConfiguration()).thenReturn(mock(HierarchicalConfiguration.class));
        when(oidcSsoConfig.getAuthorizationUrl()).thenReturn("https://idp.example.com/auth");
        when(oidcSsoConfig.getClientId()).thenReturn("client-id");
        when(oidcSsoConfig.getRedirectUrl()).thenReturn("https://tank.example.com/projects/");
    }

    private static HttpSession fakeSession() {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(anyString())).thenAnswer(i -> attributes.get(i.getArgument(0, String.class)));
        doAnswer(i -> attributes.put(i.getArgument(0), i.getArgument(1))).when(session).setAttribute(anyString(), any());
        doAnswer(i -> attributes.remove(i.getArgument(0, String.class))).when(session).removeAttribute(anyString());
        return session;
    }

    @Test
    void returnPath_isSingleUse() {
        HttpSession session = fakeSession();
        handler.setReturnPath(session, "/app/jobs");
        assertEquals("/app/jobs", handler.consumeReturnPath(session));
        assertNull(handler.consumeReturnPath(session));
    }

    @Test
    void nullReturnPath_clearsPreviousOne() {
        HttpSession session = fakeSession();
        handler.setReturnPath(session, "/app/jobs");
        handler.setReturnPath(session, null);
        assertNull(handler.consumeReturnPath(session));
    }

    @Test
    void noSession() {
        assertNull(handler.consumeReturnPath(null));
    }

    @Test
    void newLogin_clearsReturnPathOfAbandonedLogin() {
        HttpSession session = fakeSession();
        handler.GetOnLoadAuthorizationRequest(session);
        handler.setReturnPath(session, "/app/abandoned");

        // the user later signs in from the JSF login page, which never sets a return path
        handler.GetOnLoadAuthorizationRequest(session);

        assertNull(handler.consumeReturnPath(session));
    }

    @Test
    void returnPathSetAfterStartingSurvives() {
        HttpSession session = fakeSession();
        handler.GetOnLoadAuthorizationRequest(session);
        handler.setReturnPath(session, "/app/jobs");
        assertEquals("/app/jobs", handler.consumeReturnPath(session));
    }
}
