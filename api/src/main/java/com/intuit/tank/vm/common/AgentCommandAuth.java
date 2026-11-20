package com.intuit.tank.vm.common;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2026 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;

/**
 * The {@code Authorization: Bearer} token on controller commands to an agent, over WebSocket or HTTP.
 * An agent accepts a command only when it carries the token the agent itself uses to call the controller;
 * an agent without a token accepts every command, as before tokens were checked.
 */
public final class AgentCommandAuth {

    private static final String BEARER_PREFIX = "bearer ";

    private AgentCommandAuth() {
    }

    /**
     * @param expectedToken the agent's token; blank means commands aren't checked
     * @param authorization the request's {@code Authorization} header, or null
     * @return whether the command may run
     */
    public static boolean isAuthorized(String expectedToken, String authorization) {
        if (StringUtils.isBlank(expectedToken)) {
            return true;
        }
        String token = bearerToken(authorization);
        return token != null && MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                expectedToken.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @return the token in a {@code Bearer} authorization header, or null when there is none
     */
    public static String bearerToken(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    /**
     * @return the {@code Authorization} header value that carries the token, or null for a blank token
     */
    public static String authorizationHeader(String token) {
        return StringUtils.isBlank(token) ? null : BEARER_PREFIX + token;
    }
}
