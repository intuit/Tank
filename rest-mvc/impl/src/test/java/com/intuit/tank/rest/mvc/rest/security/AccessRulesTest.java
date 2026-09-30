/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.project.Project;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessRulesTest {

    private final SecurityConfig config = mock(SecurityConfig.class);

    @Test
    void admin_hasEveryRight() {
        when(config.getRestrictionMap()).thenReturn(Map.of());
        for (AccessRight right : AccessRight.values()) {
            assertTrue(AccessRules.hasRight(right, Set.of("admin")::contains, config));
        }
    }

    @Test
    void right_grantedThroughConfiguredGroup() {
        when(config.getRestrictionMap()).thenReturn(Map.of(AccessRight.EDIT_SCRIPT.name(), List.of("scripters")));
        assertTrue(AccessRules.hasRight(AccessRight.EDIT_SCRIPT, Set.of("scripters")::contains, config));
        assertFalse(AccessRules.hasRight(AccessRight.DELETE_SCRIPT, Set.of("scripters")::contains, config));
        assertFalse(AccessRules.hasRight(AccessRight.EDIT_SCRIPT, Set.of("users")::contains, config));
    }

    @Test
    void right_deniedWithoutConfig() {
        assertFalse(AccessRules.hasRight(AccessRight.EDIT_SCRIPT, Set.of("users")::contains, null));
    }

    @Test
    void owner_isCreator() {
        Project project = new Project();
        project.setCreator("alice");
        assertTrue(AccessRules.isOwner("alice", project));
        assertFalse(AccessRules.isOwner("bob", project));
        assertFalse(AccessRules.isOwner(null, project));
        assertFalse(AccessRules.isOwner("alice", null));
        project.setCreator("");
        assertFalse(AccessRules.isOwner("", project));
    }
}
