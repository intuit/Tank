/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.project.Script;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.AccessRight;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class RestAuthorizationTest {

    @AfterEach
    void tearDown() {
        reset();
    }

    private static Script scriptOwnedBy(String creator) {
        Script script = new Script();
        script.setCreator(creator);
        return script;
    }

    @Test
    void anonymous_allowedOnlyWhenRestSecurityDisabled() {
        useConfig(false, Map.of());
        assertDoesNotThrow(() -> RestAuthorization.requireRight(AccessRight.DELETE_PROJECT, "projects"));
        assertDoesNotThrow(() -> RestAuthorization.requireAdmin("logs"));
        assertEquals(TankConstants.TANK_USER_SYSTEM, RestAuthorization.currentUserName());

        useConfig(true, Map.of());
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> RestAuthorization.requireRight(AccessRight.DELETE_PROJECT, "projects"));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> RestAuthorization.requireAdmin("logs"));
    }

    @Test
    void authenticatedUser_isCheckedEvenWhenRestSecurityDisabled() {
        useConfig(false, Map.of());
        actAs(user("bob"));
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> RestAuthorization.requireRight(AccessRight.CREATE_SCRIPT, "scripts"));
        assertEquals("bob", RestAuthorization.currentUserName());
    }

    @Test
    void user_withGroupRight_isAllowed() {
        useConfig(true, Map.of(AccessRight.EDIT_SCRIPT, List.of("scripters")));
        actAs(user("bob", "scripters"));
        assertDoesNotThrow(() -> RestAuthorization.requireRightOrOwner(AccessRight.EDIT_SCRIPT, scriptOwnedBy("alice"), "scripts"));
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, scriptOwnedBy("alice"), "scripts"));
    }

    @Test
    void owner_isAllowedWithoutRight() {
        useConfig(true, Map.of());
        actAs(user("alice"));
        assertDoesNotThrow(() -> RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, scriptOwnedBy("alice"), "scripts"));
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, scriptOwnedBy("carol"), "scripts"));
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, (Script) null, "scripts"));
    }

    @Test
    void admin_isAllowedEverything() {
        useConfig(true, Map.of());
        actAs(user("root", TankConstants.TANK_GROUP_ADMIN));
        assertDoesNotThrow(() -> RestAuthorization.requireAdmin("logs"));
        assertDoesNotThrow(() -> RestAuthorization.requireRightOrOwner(AccessRight.DELETE_SCRIPT, scriptOwnedBy("alice"), "scripts"));
    }

    @Test
    void agentToken_isPrivilegedButNeverAnOwner() {
        useConfig(true, Map.of());
        actAs(TankPrincipal.agent());
        assertDoesNotThrow(() -> RestAuthorization.requireRight(AccessRight.CONTROL_JOB, "jobs"));
        assertDoesNotThrow(() -> RestAuthorization.requireAgentOrRight(AccessRight.CONTROL_JOB, "agent"));
        assertFalse(RestAuthorization.isOwner(scriptOwnedBy(TankConstants.TANK_USER_SYSTEM)));
        assertEquals(TankConstants.TANK_USER_SYSTEM, RestAuthorization.currentUserName());
    }

    @Test
    void agentEndpoints_deniedForPlainUsers() {
        useConfig(true, Map.of(AccessRight.CONTROL_JOB, List.of("operators")));
        actAs(user("bob"));
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> RestAuthorization.requireAgentOrRight(AccessRight.CONTROL_JOB, "agent"));
        actAs(user("olivia", "operators"));
        assertDoesNotThrow(() -> RestAuthorization.requireAgentOrRight(AccessRight.CONTROL_JOB, "agent"));
    }

    @Test
    void requireUser_rejectsAnonymousEvenWhenRestSecurityDisabled() {
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, () -> RestAuthorization.requireUser("me"));
    }

    @Test
    void requireUser_rejectsAgentToken() {
        useConfig(true, Map.of());
        actAs(TankPrincipal.agent());
        assertThrows(GenericServiceForbiddenAccessException.class, () -> RestAuthorization.requireUser("me"));
    }

    @Test
    void requireUser_returnsUser() {
        useConfig(true, Map.of());
        TankPrincipal bob = user("bob");
        actAs(bob);
        assertSame(bob, RestAuthorization.requireUser("me"));
    }
}
