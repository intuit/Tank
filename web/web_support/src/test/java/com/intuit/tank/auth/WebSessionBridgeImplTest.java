/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.auth;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.auth.sso.TankSsoHandler;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import jakarta.enterprise.event.Event;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WebSessionBridgeImplTest {

    @InjectMocks
    private WebSessionBridgeImpl bridge;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private TankSsoHandler ssoHandler;

    @Mock
    private Event<Preferences> preferencesReloadEvent;

    @Mock
    private Event<ModifiedUserMessage> userEvent;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        when(request.getSession(anyBoolean())).thenReturn(session);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void login_success_rotatesSessionAndReturnsSessionPrincipal() {
        when(securityContext.authenticate(eq(request), eq(response), any())).thenReturn(AuthenticationStatus.SUCCESS);
        when(securityContext.getCallerPrincipal()).thenReturn(new CallerPrincipal("bob"));
        when(securityContext.getCallerRoles()).thenReturn(Set.of("admin"));

        TankPrincipal principal = bridge.login(request, response, "bob", "secret-pass");

        assertEquals("bob", principal.getName());
        assertTrue(principal.isInRole("admin"));
        assertEquals(TankPrincipal.AuthMethod.SESSION, principal.getAuthMethod());
        ArgumentCaptor<AuthenticationParameters> params = ArgumentCaptor.forClass(AuthenticationParameters.class);
        verify(securityContext).authenticate(eq(request), eq(response), params.capture());
        UsernamePasswordCredential credential = (UsernamePasswordCredential) params.getValue().getCredential();
        assertEquals("bob", credential.getCaller());
        assertEquals("secret-pass", credential.getPasswordAsString());
        verify(request).changeSessionId();
    }

    @Test
    void login_failure_returnsNullAndKeepsSessionId() {
        when(securityContext.authenticate(eq(request), eq(response), any())).thenReturn(AuthenticationStatus.SEND_FAILURE);
        assertNull(bridge.login(request, response, "bob", "wrong"));
        verify(request, never()).changeSessionId();
    }

    @Test
    void startSso_storesReturnPath() {
        when(ssoHandler.GetOnLoadAuthorizationRequest(session)).thenReturn("https://idp/authorize");
        assertEquals("https://idp/authorize", bridge.startSsoLogin(request, "/app"));
        verify(ssoHandler).setReturnPath(session, "/app");
    }

    @Test
    void completeSso_logsInRotatesSessionAndReturnsPath() throws Exception {
        when(ssoHandler.consumeReturnPath(session)).thenReturn("/app");
        assertEquals("/app", bridge.completeSsoLogin(request, "code", "state"));
        InOrder order = inOrder(ssoHandler, request);
        order.verify(ssoHandler).HandleSsoAuthorization("code", "state", session);
        order.verify(request).changeSessionId();
    }

    @Test
    void completeSso_invalidCallbackPropagates() throws Exception {
        doThrow(new IllegalArgumentException("bad state")).when(ssoHandler).HandleSsoAuthorization(any(), any(), any());
        assertThrows(IllegalArgumentException.class, () -> bridge.completeSsoLogin(request, "code", "forged"));
        verify(request, never()).changeSessionId();
    }

    @Test
    void logout_clearsPrincipalAndInvalidatesSession() {
        bridge.logout(request);
        verify(securityContext).clearCallerPrincipal();
        verify(session).invalidate();
    }

    @Test
    void preferencesChanged_firesReloadEvent() {
        Preferences preferences = new Preferences();
        bridge.preferencesChanged(preferences);
        verify(preferencesReloadEvent).fire(preferences);
    }

    @Test
    void userChanged_firesModifiedUserEvent() {
        User user = User.builder().name("carol").build();
        bridge.userChanged(user);
        ArgumentCaptor<ModifiedUserMessage> message = ArgumentCaptor.forClass(ModifiedUserMessage.class);
        verify(userEvent).fire(message.capture());
        assertSame(user, message.getValue().getModified());
        verifyNoInteractions(preferencesReloadEvent);
    }
}
