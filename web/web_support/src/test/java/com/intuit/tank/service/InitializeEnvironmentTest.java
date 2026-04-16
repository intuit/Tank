package com.intuit.tank.service;

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

import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.intuit.tank.service.InitializeEnvironment;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The class <code>InitializeEnvironmentTest</code> contains tests for the class
 * <code>{@link InitializeEnvironment}</code>.
 */
public class InitializeEnvironmentTest {

    @InjectMocks
    private InitializeEnvironment initializeEnvironment;

    @Mock
    private TankConfig tankConfig;

    @Mock
    private SecurityConfig securityConfig;

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
    public void testInitializeEnvironment_1() throws Exception {
        InitializeEnvironment result = new InitializeEnvironment();
        assertNotNull(result);
    }

    @Test
    public void testPing_DoesNotThrow() {
        assertDoesNotThrow(() -> initializeEnvironment.ping());
    }

    @Test
    public void testInit_WithEmptyConfig_SetsInitializeTrue() {
        when(tankConfig.getSecurityConfig()).thenReturn(securityConfig);
        when(securityConfig.getGroups()).thenReturn(Collections.emptySet());
        when(securityConfig.getDefaultUsers()).thenReturn(Collections.emptySet());
        when(securityConfig.getDefaultGroups()).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> initializeEnvironment.init());
        assertTrue(initializeEnvironment.initialize);
    }

    @Test
    public void testInit_WithGroupsConfig_CreatesGroups() {
        when(tankConfig.getSecurityConfig()).thenReturn(securityConfig);
        when(securityConfig.getGroups()).thenReturn(new java.util.HashSet<>(java.util.Set.of("admin", "user")));
        when(securityConfig.getDefaultUsers()).thenReturn(Collections.emptySet());
        when(securityConfig.getDefaultGroups()).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> initializeEnvironment.init());
        assertTrue(initializeEnvironment.initialize);
    }

    @Test
    public void testInit_WithDefaultUsers_CreatesUsers() {
        com.intuit.tank.vm.settings.DefaultUser defaultUser = mock(com.intuit.tank.vm.settings.DefaultUser.class);
        when(defaultUser.getName()).thenReturn("admin");
        when(defaultUser.getEmail()).thenReturn("admin@test.com");
        when(defaultUser.getPassword()).thenReturn("password");
        when(defaultUser.getGroups()).thenReturn(Collections.emptySet());

        when(tankConfig.getSecurityConfig()).thenReturn(securityConfig);
        when(securityConfig.getGroups()).thenReturn(Collections.emptySet());
        when(securityConfig.getDefaultUsers()).thenReturn(new java.util.HashSet<>(java.util.Set.of(defaultUser)));
        when(securityConfig.getDefaultGroups()).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> initializeEnvironment.init());
        assertTrue(initializeEnvironment.initialize);
    }
}