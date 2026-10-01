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
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridgeProvider;
import com.intuit.tank.rest.mvc.rest.services.me.MeServiceV2;
import com.intuit.tank.rest.mvc.rest.util.BuildInfo;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Pattern;

@Service
public class AuthServiceV2Impl implements AuthServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceV2Impl.class);
    private static final String SERVICE = "auth";

    /** A path within this application: starts with a single slash, no scheme, host or backslash. */
    private static final Pattern RETURN_PATH = Pattern.compile("^/(?![/\\\\])[^\\\\\\s]*$");
    static final int MAX_RETURN_PATH_LENGTH = 2048;

    @Autowired
    private WebSessionBridgeProvider webSessionBridgeProvider;

    @Autowired
    private MeServiceV2 meService;

    @Autowired
    private ServletContext servletContext;

    private volatile BuildInfo buildInfo;

    @Override
    public AuthConfig getConfig() {
        TankConfig config = new TankConfig();
        BuildInfo build = getBuildInfo();
        return new AuthConfig(isSsoEnabled(config), config.getStandalone(), config.getControllerBase(),
                build.version(), build.buildDate(), StringUtils.defaultIfBlank(config.getTextBanner(), null));
    }

    @Override
    public CurrentUser login(LoginRequest login, HttpServletRequest request, HttpServletResponse response) {
        if (login == null || StringUtils.isBlank(login.username()) || StringUtils.isEmpty(login.password())) {
            throw new GenericServiceBadRequestException(SERVICE, "credentials", "username and password are required");
        }
        TankPrincipal principal = webSessionBridgeProvider.get()
                .login(request, response, login.username().trim(), login.password());
        if (principal == null) {
            throw new GenericServiceUnauthorizedException(SERVICE, "Invalid username or password");
        }
        issueCsrfToken(request, response);
        return meService.describe(principal);
    }

    @Override
    public String startSsoLogin(String returnPath, HttpServletRequest request) {
        if (!isSsoEnabled(new TankConfig())) {
            throw new GenericServiceBadRequestException(SERVICE, "sso", "single sign-on is not configured");
        }
        if (returnPath != null && !isValidReturnPath(returnPath)) {
            throw new GenericServiceBadRequestException(SERVICE, "returnTo", "must be a path within this application");
        }
        return webSessionBridgeProvider.get().startSsoLogin(request, returnPath);
    }

    @Override
    public String completeSsoLogin(String code, String state, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String returnPath;
        try {
            returnPath = webSessionBridgeProvider.get().completeSsoLogin(request, code, state);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Rejected SSO callback: {}", e.getMessage());
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            throw new GenericServiceUnauthorizedException(SERVICE, "Single sign-on failed");
        }
        issueCsrfToken(request, response);
        String contextPath = StringUtils.defaultString(request.getContextPath());
        return contextPath + (returnPath != null && isValidReturnPath(returnPath) ? returnPath : "/");
    }

    @Override
    public void logout(HttpServletRequest request) {
        webSessionBridgeProvider.get().logout(request);
    }

    static boolean isValidReturnPath(String path) {
        return path != null && path.length() <= MAX_RETURN_PATH_LENGTH && RETURN_PATH.matcher(path).matches();
    }

    private static boolean isSsoEnabled(TankConfig config) {
        OidcSsoConfig sso = config.getOidcSsoConfig();
        return sso != null && sso.getConfiguration() != null;
    }

    private static void issueCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            CsrfTokens.ensureCookie(request, response, CsrfTokens.rotate(session));
        }
    }

    private BuildInfo getBuildInfo() {
        BuildInfo info = buildInfo;
        if (info == null) {
            info = BuildInfo.read(servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"));
            buildInfo = info;
        }
        return info;
    }
}
