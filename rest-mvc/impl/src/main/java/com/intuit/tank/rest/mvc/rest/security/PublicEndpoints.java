/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

/**
 * The {@code /v2} endpoints that are callable without credentials: what the login page needs before a
 * user has signed in.
 */
public final class PublicEndpoints {

    public static final String AUTH_CONFIG = "/v2/auth/config";
    public static final String AUTH_LOGIN = "/v2/auth/login";
    public static final String SSO_AUTHORIZE = "/v2/auth/sso/authorize";
    public static final String SSO_CALLBACK = "/v2/auth/sso/callback";

    private static final Set<String> PATHS = Set.of(AUTH_CONFIG, AUTH_LOGIN, SSO_AUTHORIZE, SSO_CALLBACK);

    private PublicEndpoints() {
    }

    public static boolean isPublic(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (uri == null) {
            return false;
        }
        if (contextPath != null && !contextPath.isEmpty()) {
            if (!uri.startsWith(contextPath)) {
                return false;
            }
            uri = uri.substring(contextPath.length());
        }
        if (uri.length() > 1 && uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return PATHS.contains(uri);
    }
}
