/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.auth;

import com.intuit.tank.ModifiedUserMessage;
import com.intuit.tank.admin.Deleted;
import com.intuit.tank.auth.sso.TankSsoHandler;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.User;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridge;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Lets the REST API log the web UI session in and out the same way the JSF login page does, through
 * {@link TankSecurityContext} and {@link TankSsoHandler}.
 */
@ApplicationScoped
public class WebSessionBridgeImpl implements WebSessionBridge {

    @Inject
    private TankSecurityContext securityContext;

    @Inject
    private TankSsoHandler ssoHandler;

    @Inject
    @Deleted
    private Event<Preferences> preferencesReloadEvent;

    @Inject
    @Modified
    private Event<ModifiedUserMessage> userEvent;

    @Override
    public TankPrincipal login(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        request.getSession(true);
        AuthenticationStatus status = securityContext.authenticate(request, response,
                AuthenticationParameters.withParams().credential(new UsernamePasswordCredential(username, password)));
        if (status != AuthenticationStatus.SUCCESS) {
            return null;
        }
        // new session id after login prevents session fixation
        request.changeSessionId();
        return sessionPrincipal();
    }

    @Override
    public String startSsoLogin(HttpServletRequest request, String returnPath) {
        HttpSession session = request.getSession(true);
        String url = ssoHandler.GetOnLoadAuthorizationRequest(session);
        ssoHandler.setReturnPath(session, returnPath);
        return url;
    }

    @Override
    public String completeSsoLogin(HttpServletRequest request, String authorizationCode, String state) throws IOException {
        HttpSession session = request.getSession(false);
        ssoHandler.HandleSsoAuthorization(authorizationCode, state, session);
        request.changeSessionId();
        return ssoHandler.consumeReturnPath(session);
    }

    @Override
    public void logout(HttpServletRequest request) {
        securityContext.clearCallerPrincipal();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public void preferencesChanged(Preferences preferences) {
        // PreferencesBean observes this and reloads the preferences cached in the session
        preferencesReloadEvent.fire(preferences);
    }

    @Override
    public void userChanged(User user) {
        // UserLoader observes this and reloads the user list for the admin pages
        userEvent.fire(new ModifiedUserMessage(user, this));
    }

    private TankPrincipal sessionPrincipal() {
        return new TankPrincipal(securityContext.getCallerPrincipal().getName(), securityContext.getCallerRoles(),
                TankPrincipal.AuthMethod.SESSION);
    }
}
