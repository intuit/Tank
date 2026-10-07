/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

/**
 * Double-submit CSRF protection for REST calls that authenticate with the session cookie.
 *
 * <p>The token lives in the HTTP session and is mirrored to a script-readable {@value #COOKIE_NAME}
 * cookie. Browser clients echo it in the {@value #HEADER_NAME} header on state-changing requests
 * (the header name matches the axios and Angular defaults). Bearer-token callers are not affected.</p>
 */
public final class CsrfTokens {

    public static final String COOKIE_NAME = "XSRF-TOKEN";
    public static final String HEADER_NAME = "X-XSRF-TOKEN";
    static final String SESSION_ATTRIBUTE = CsrfTokens.class.getName();

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfTokens() {
    }

    public static boolean isSafeMethod(String method) {
        return method != null && SAFE_METHODS.contains(method.toUpperCase());
    }

    public static String getOrCreate(HttpSession session) {
        Object token = session.getAttribute(SESSION_ATTRIBUTE);
        if (token instanceof String) {
            return (String) token;
        }
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String created = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_ATTRIBUTE, created);
        return created;
    }

    /**
     * Replaces the session's token, so a token issued before login is not valid after it.
     */
    public static String rotate(HttpSession session) {
        session.removeAttribute(SESSION_ATTRIBUTE);
        return getOrCreate(session);
    }

    /**
     * @return true when the request header carries the session's token
     */
    public static boolean isValid(HttpServletRequest request, HttpSession session) {
        Object expected = session.getAttribute(SESSION_ATTRIBUTE);
        String actual = request.getHeader(HEADER_NAME);
        return expected instanceof String && actual != null
                && MessageDigest.isEqual(((String) expected).getBytes(StandardCharsets.UTF_8),
                                         actual.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sends the token cookie unless the browser already holds the current value.
     */
    public static void ensureCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName()) && token.equals(c.getValue())) {
                    return;
                }
            }
        }
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        String contextPath = request.getContextPath();
        cookie.setPath(contextPath == null || contextPath.isEmpty() ? "/" : contextPath);
        cookie.setHttpOnly(false); // must be readable by the browser client
        cookie.setSecure(request.isSecure());
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}
