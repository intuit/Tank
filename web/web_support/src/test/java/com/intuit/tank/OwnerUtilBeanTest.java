package com.intuit.tank;

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
import java.util.ArrayList;

import com.intuit.tank.admin.UserAdmin;
import com.intuit.tank.auth.Security;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.project.Script;
import com.intuit.tank.view.filter.ViewFilterType;
import jakarta.security.enterprise.CallerPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OwnerUtilBeanTest {

    @InjectMocks
    private OwnerUtilBean ownerUtilBean;

    @Mock
    private UserAdmin userAdmin;

    @Mock
    private Security security;

    @Mock
    private TankSecurityContext securityContext;

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
    public void testIsOwnable_WithOwnableEntity_ReturnsTrue() {
        Script script = new Script();
        assertTrue(ownerUtilBean.isOwnable(script));
    }

    @Test
    public void testIsOwnable_WithNonOwnableObject_ReturnsFalse() {
        assertFalse(ownerUtilBean.isOwnable("not ownable"));
        assertFalse(ownerUtilBean.isOwnable(42));
    }

    @Test
    public void testGetOwnerList_DelegatesToUserAdmin() {
        when(userAdmin.getEntityList(ViewFilterType.ALL)).thenReturn(new ArrayList<>());
        assertNotNull(ownerUtilBean.getOwnerList());
        verify(userAdmin).getEntityList(ViewFilterType.ALL);
    }

    @Test
    public void testCanChangeOwner_NonOwnableObject_ReturnsFalse() {
        assertFalse(ownerUtilBean.canChangeOwner("not ownable"));
    }

    @Test
    public void testCanChangeOwner_WhenOwner_ReturnsTrue() {
        Script script = new Script();
        script.setCreator("alice");

        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        when(security.isOwner(script)).thenReturn(true);
        when(security.isAdmin()).thenReturn(false);

        assertTrue(ownerUtilBean.canChangeOwner(script));
    }

    @Test
    public void testCanChangeOwner_WhenAdmin_ReturnsTrue() {
        Script script = new Script();
        script.setCreator("alice");

        CallerPrincipal principal = new CallerPrincipal("bob");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        when(security.isOwner(script)).thenReturn(false);
        when(security.isAdmin()).thenReturn(true);

        assertTrue(ownerUtilBean.canChangeOwner(script));
    }

    @Test
    public void testCanChangeOwner_WhenNotOwnerNotAdmin_ReturnsFalse() {
        Script script = new Script();
        script.setCreator("alice");

        CallerPrincipal principal = new CallerPrincipal("bob");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        when(security.isOwner(script)).thenReturn(false);
        when(security.isAdmin()).thenReturn(false);

        assertFalse(ownerUtilBean.canChangeOwner(script));
    }

    @Test
    public void testCanChangeOwner_WhenEmptyCreator_SetsCreatorFromPrincipal() {
        Script script = new Script();
        script.setCreator("");

        CallerPrincipal principal = new CallerPrincipal("alice");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        when(security.isOwner(any(OwnableEntity.class))).thenReturn(true);

        ownerUtilBean.canChangeOwner(script);
        assertEquals("alice", script.getCreator());
    }
}
