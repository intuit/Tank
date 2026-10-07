/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.security.Principal;

/**
 * Request wrapper that exposes the authenticated {@link TankPrincipal} through the standard servlet
 * security API ({@code getUserPrincipal}, {@code isUserInRole}, {@code getRemoteUser}).
 */
public class AuthenticatedRequest extends HttpServletRequestWrapper {

    private final TankPrincipal principal;

    public AuthenticatedRequest(HttpServletRequest request, TankPrincipal principal) {
        super(request);
        this.principal = principal;
        request.setAttribute(RestAuthorization.PRINCIPAL_ATTRIBUTE, principal);
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return principal.isInRole(role);
    }

    @Override
    public String getRemoteUser() {
        return principal.getName();
    }

    @Override
    public String getAuthType() {
        return principal.getAuthMethod() == TankPrincipal.AuthMethod.SESSION ? FORM_AUTH : "BEARER";
    }

    public TankPrincipal getTankPrincipal() {
        return principal;
    }
}
