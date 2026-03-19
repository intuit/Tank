package com.intuit.tank.auth;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.security.enterprise.CallerPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityTest {

    @InjectMocks
    private Security security;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private TankConfig tankConfig;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testIsOwner_WhenOwner_ReturnsTrue() {
        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);

        OwnableEntity entity = mock(OwnableEntity.class);
        when(entity.getCreator()).thenReturn("alice");

        assertTrue(security.isOwner(entity));
    }

    @Test
    public void testIsOwner_WhenNotOwner_ReturnsFalse() {
        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);

        OwnableEntity entity = mock(OwnableEntity.class);
        when(entity.getCreator()).thenReturn("bob");

        assertFalse(security.isOwner(entity));
    }

    @Test
    public void testIsOwner_WhenNoPrincipal_ReturnsFalse() {
        when(securityContext.getCallerPrincipal()).thenReturn(null);

        OwnableEntity entity = mock(OwnableEntity.class);
        when(entity.getCreator()).thenReturn("alice");

        assertFalse(security.isOwner(entity));
    }

    @Test
    public void testIsOwner_WhenEmptyCreator_ReturnsFalse() {
        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);

        OwnableEntity entity = mock(OwnableEntity.class);
        when(entity.getCreator()).thenReturn("");

        assertFalse(security.isOwner(entity));
    }

    @Test
    public void testIsAdmin_WhenAdmin_ReturnsTrue() {
        when(securityContext.isCallerInRole(TankConstants.TANK_GROUP_ADMIN)).thenReturn(true);
        assertTrue(security.isAdmin());
    }

    @Test
    public void testIsAdmin_WhenNotAdmin_ReturnsFalse() {
        when(securityContext.isCallerInRole(TankConstants.TANK_GROUP_ADMIN)).thenReturn(false);
        assertFalse(security.isAdmin());
    }

    @Test
    public void testHasRight_WhenAdmin_ReturnsTrue() {
        when(securityContext.isCallerInRole(TankConstants.TANK_GROUP_ADMIN)).thenReturn(true);
        assertTrue(security.hasRight(AccessRight.DELETE_PROJECT));
    }

    @Test
    public void testHasRight_WhenGroupHasRight_ReturnsTrue() {
        when(securityContext.isCallerInRole(TankConstants.TANK_GROUP_ADMIN)).thenReturn(false);

        SecurityConfig securityConfig = mock(SecurityConfig.class);
        Map<String, List<String>> restrictionMap = new HashMap<>();
        restrictionMap.put(AccessRight.DELETE_PROJECT.name(), List.of("admin", "powerUser"));
        when(securityConfig.getRestrictionMap()).thenReturn(restrictionMap);
        when(tankConfig.getSecurityConfig()).thenReturn(securityConfig);
        when(securityContext.isCallerInRole("powerUser")).thenReturn(true);

        assertTrue(security.hasRight(AccessRight.DELETE_PROJECT));
    }

    @Test
    public void testHasRight_WhenNoGroupMapping_ReturnsFalse() {
        when(securityContext.isCallerInRole(TankConstants.TANK_GROUP_ADMIN)).thenReturn(false);

        SecurityConfig securityConfig = mock(SecurityConfig.class);
        when(securityConfig.getRestrictionMap()).thenReturn(new HashMap<>());
        when(tankConfig.getSecurityConfig()).thenReturn(securityConfig);

        assertFalse(security.hasRight(AccessRight.DELETE_PROJECT));
    }

    @Test
    public void testGetName_WhenPrincipalExists_ReturnsName() {
        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        assertEquals("alice", security.getName());
    }

    @Test
    public void testGetName_WhenNoPrincipal_ReturnsEmpty() {
        when(securityContext.getCallerPrincipal()).thenReturn(null);
        assertEquals("", security.getName());
    }
}
