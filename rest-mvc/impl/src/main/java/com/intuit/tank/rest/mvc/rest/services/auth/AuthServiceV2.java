/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.auth;

import com.intuit.tank.rest.mvc.rest.models.AuthConfig;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Signing in and out of the web UI session. Sessions created here are shared with the JSF pages.
 */
public interface AuthServiceV2 {

    AuthConfig getConfig();

    /**
     * Logs the session in with a username and password and issues a fresh CSRF token cookie.
     */
    CurrentUser login(LoginRequest login, HttpServletRequest request, HttpServletResponse response);

    /**
     * @param returnPath where to send the browser after the login, a path within this application
     * @return the identity provider URL to redirect to
     */
    String startSsoLogin(String returnPath, HttpServletRequest request);

    /**
     * @return the URL to redirect the browser to: the return path given at the start of the login, or
     *         the application root
     */
    String completeSsoLogin(String code, String state, HttpServletRequest request, HttpServletResponse response)
            throws IOException;

    void logout(HttpServletRequest request);
}
