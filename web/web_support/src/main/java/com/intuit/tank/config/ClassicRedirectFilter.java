/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sends a request for a JSF page at its address from before the JSF UI moved under {@code /classic},
 * such as a bookmark or a configured SSO redirect URL ending in {@code /projects/index.jsf}, to the
 * page's new address. The query string goes along, so an SSO authorization code still reaches
 * {@link LoginFilter}.
 */
@WebFilter(urlPatterns = "*.jsf", asyncSupported = true)
public class ClassicRedirectFilter extends HttpFilter {

    static final String CLASSIC = "/classic";
    /** JSF serves its resources here whichever page asks for them */
    static final String FACES_RESOURCES = "/jakarta.faces.resource/";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String path = request.getServletPath();
        if (path.startsWith(CLASSIC + "/") || path.startsWith(FACES_RESOURCES)) {
            chain.doFilter(request, response);
            return;
        }
        String query = request.getQueryString();
        response.sendRedirect(request.getContextPath() + CLASSIC + path + (query == null ? "" : "?" + query));
    }
}
