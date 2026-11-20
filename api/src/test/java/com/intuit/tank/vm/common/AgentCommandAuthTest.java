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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentCommandAuthTest {

    @ParameterizedTest
    @CsvSource(value = {"bearer secret", "Bearer secret", "BEARER  secret "})
    public void acceptsTheAgentsToken(String authorization) {
        assertTrue(AgentCommandAuth.isAuthorized("secret", authorization));
    }

    @ParameterizedTest
    @CsvSource(value = {"NONE", "''", "bearer", "bearer ", "bearer other", "Basic secret", "secret", "bearer secret2"},
            nullValues = "NONE")
    public void rejectsAnythingElse(String authorization) {
        assertFalse(AgentCommandAuth.isAuthorized("secret", authorization));
    }

    @ParameterizedTest
    @CsvSource(value = {"NONE", "''", "' '"}, nullValues = "NONE")
    public void anAgentWithoutATokenAcceptsEveryCommand(String expectedToken) {
        assertTrue(AgentCommandAuth.isAuthorized(expectedToken, null));
        assertTrue(AgentCommandAuth.isAuthorized(expectedToken, "bearer anything"));
    }

    @Test
    public void readsTheBearerToken() {
        assertEquals("secret", AgentCommandAuth.bearerToken("Bearer secret"));
        assertNull(AgentCommandAuth.bearerToken("bearer "));
        assertNull(AgentCommandAuth.bearerToken("Basic secret"));
        assertNull(AgentCommandAuth.bearerToken(null));
    }

    @Test
    public void buildsTheAuthorizationHeader() {
        assertEquals("bearer secret", AgentCommandAuth.authorizationHeader("secret"));
        assertNull(AgentCommandAuth.authorizationHeader(" "));
        assertNull(AgentCommandAuth.authorizationHeader(null));
    }
}
