/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.project.Preferences;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Access to the web UI session for the REST layer.
 *
 * <p>Logins made through REST must establish the same session as the JSF login page, so that both UIs
 * work side by side during the migration. The session lives in the web module ({@code TankSecurityContext}),
 * which this module cannot depend on; the web module provides this interface as a CDI bean instead.</p>
 */
public interface WebSessionBridge {

    /**
     * Validates the credentials and, on success, logs the session in (rotating the session id).
     *
     * @return the logged in caller, or null when the credentials are invalid
     */
    TankPrincipal login(HttpServletRequest request, HttpServletResponse response, String username, String password);

    /**
     * Starts an SSO login for this session.
     *
     * @param returnPath an already validated path within this application to send the browser to after
     *                   the login completes, or null for the configured default
     * @return the identity provider URL to redirect the browser to
     */
    String startSsoLogin(HttpServletRequest request, String returnPath);

    /**
     * Completes an SSO login from the identity provider callback and logs the session in.
     *
     * @return the return path given to {@link #startSsoLogin}, or null when none was given
     * @throws IllegalArgumentException when the callback is invalid (state, nonce or claims)
     */
    String completeSsoLogin(HttpServletRequest request, String authorizationCode, String state) throws IOException;

    /**
     * Logs the session out and invalidates it.
     */
    void logout(HttpServletRequest request);

    /**
     * Tells the web UI that the user's table preferences were changed or deleted, so the copy cached in
     * this request's session is reloaded.
     */
    void preferencesChanged(Preferences preferences);
}
