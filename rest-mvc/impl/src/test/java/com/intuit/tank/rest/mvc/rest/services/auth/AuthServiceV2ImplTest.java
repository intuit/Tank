/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.auth;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.AuthConfig;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.LoginRequest;
import com.intuit.tank.rest.mvc.rest.security.CsrfTokens;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridge;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridgeProvider;
import com.intuit.tank.rest.mvc.rest.services.me.MeServiceV2;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceV2ImplTest {

    @InjectMocks
    private AuthServiceV2Impl service;

    @Mock
    private WebSessionBridgeProvider bridgeProvider;

    @Mock
    private WebSessionBridge bridge;

    @Mock
    private MeServiceV2 meService;

    @Mock
    private ServletContext servletContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private AutoCloseable mocks;
    private MockedConstruction<TankConfig> tankConfigs;
    private boolean ssoConfigured = true;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        when(bridgeProvider.get()).thenReturn(bridge);
        when(request.getContextPath()).thenReturn("/tank");
        when(request.getSession(false)).thenReturn(session);
        Map<String, Object> attributes = new HashMap<>();
        when(session.getAttribute(anyString())).thenAnswer(i -> attributes.get(i.getArgument(0, String.class)));
        doAnswer(i -> attributes.put(i.getArgument(0), i.getArgument(1))).when(session).setAttribute(anyString(), any());
        doAnswer(i -> attributes.remove(i.getArgument(0, String.class))).when(session).removeAttribute(anyString());
        tankConfigs = Mockito.mockConstruction(TankConfig.class, (mock, context) -> {
            OidcSsoConfig sso = mock(OidcSsoConfig.class);
            when(sso.getConfiguration()).thenAnswer(i -> ssoConfigured ? mock(HierarchicalConfiguration.class) : null);
            when(mock.getOidcSsoConfig()).thenReturn(sso);
            when(mock.getStandalone()).thenReturn(true);
            when(mock.getControllerBase()).thenReturn("https://tank.example.com/tank");
            when(mock.getTextBanner()).thenReturn("");
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        tankConfigs.close();
        mocks.close();
    }

    @Test
    void getConfig() {
        AuthConfig config = service.getConfig();
        assertTrue(config.ssoEnabled());
        assertTrue(config.standalone());
        assertEquals("https://tank.example.com/tank", config.controllerUrl());
        assertTrue(config.version().startsWith(TankConstants.TANK_BUILD_VERSION));
        assertNull(config.textBanner());
    }

    @Test
    void getConfig_ssoNotConfigured() {
        ssoConfigured = false;
        assertFalse(service.getConfig().ssoEnabled());
    }

    @Test
    void login_success_returnsUserAndIssuesFreshCsrfCookie() {
        TankPrincipal bob = new TankPrincipal("bob", Set.of(), TankPrincipal.AuthMethod.SESSION);
        CurrentUser described = new CurrentUser("bob", "bob@example.com", List.of(), false, Map.of(), false, null);
        when(bridge.login(request, response, "bob", "secret-pass")).thenReturn(bob);
        when(meService.describe(bob)).thenReturn(described);
        String before = CsrfTokens.getOrCreate(session);

        assertSame(described, service.login(new LoginRequest(" bob ", "secret-pass"), request, response));

        ArgumentCaptor<Cookie> cookie = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookie.capture());
        assertEquals(CsrfTokens.COOKIE_NAME, cookie.getValue().getName());
        assertNotEquals(before, cookie.getValue().getValue());
        assertEquals(CsrfTokens.getOrCreate(session), cookie.getValue().getValue());
    }

    @Test
    void login_invalidCredentials() {
        when(bridge.login(any(), any(), anyString(), anyString())).thenReturn(null);
        assertThrows(GenericServiceUnauthorizedException.class,
                () -> service.login(new LoginRequest("bob", "wrong"), request, response));
        verify(response, never()).addCookie(any());
    }

    @Test
    void login_missingCredentials() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.login(null, request, response));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.login(new LoginRequest(" ", "pw"), request, response));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.login(new LoginRequest("bob", ""), request, response));
        verifyNoInteractions(bridge);
    }

    @Test
    void startSso_passesValidatedReturnPath() {
        when(bridge.startSsoLogin(request, "/app/projects?id=4")).thenReturn("https://idp/authorize?x");
        assertEquals("https://idp/authorize?x", service.startSsoLogin("/app/projects?id=4", request));
    }

    @Test
    void startSso_rejectsExternalReturnPath() {
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.startSsoLogin("https://evil.example.com", request));
        verifyNoInteractions(bridge);
    }

    @Test
    void startSso_notConfigured() {
        ssoConfigured = false;
        assertThrows(GenericServiceBadRequestException.class, () -> service.startSsoLogin(null, request));
    }

    @Test
    void completeSso_redirectsToReturnPath() throws Exception {
        when(bridge.completeSsoLogin(request, "code", "state")).thenReturn("/app/jobs");
        assertEquals("/tank/app/jobs", service.completeSsoLogin("code", "state", request, response));
        verify(response).addCookie(any());
    }

    @Test
    void completeSso_defaultsToRoot() throws Exception {
        when(bridge.completeSsoLogin(request, "code", "state")).thenReturn(null);
        assertEquals("/tank/", service.completeSsoLogin("code", "state", request, response));
    }

    @Test
    void completeSso_invalidCallbackInvalidatesSession() throws Exception {
        when(bridge.completeSsoLogin(request, "code", "forged")).thenThrow(new IllegalArgumentException("bad state"));
        assertThrows(GenericServiceUnauthorizedException.class,
                () -> service.completeSsoLogin("code", "forged", request, response));
        verify(session).invalidate();
        verify(response, never()).addCookie(any());
    }

    @Test
    void logout_delegates() {
        service.logout(request);
        verify(bridge).logout(request);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/", "/app", "/app/projects/4?tab=jobs#top", "/projects/index.jsf" })
    void validReturnPaths(String path) {
        assertTrue(AuthServiceV2Impl.isValidReturnPath(path), path);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "app", "//evil.example.com", "/\\evil.example.com", "https://evil.example.com",
            "/app\\..", "/app\r\nSet-Cookie: x", "/a b" })
    void invalidReturnPaths(String path) {
        assertFalse(AuthServiceV2Impl.isValidReturnPath(path), path);
    }
}
