package com.intuit.tank.script;

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


import javax.faces.view.ViewScoped;
import javax.security.enterprise.CallerPrincipal;

import com.intuit.tank.auth.Security;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.project.Script;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.util.Messages;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableWrapper;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;


@EnableAutoWeld
@ActivateScopes(ViewScoped.class)
public class ScriptBeanTest {

    @InjectMocks
    private ScriptBean scriptBean;

    @Mock
    private ScriptLoader scriptLoader;

    @Mock
    private Security security;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private Messages messages;

    @Mock
    private ExceptionHandler exceptionHandler;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void testScriptBean() {
        assertNotNull(scriptBean);
    }

    @Test
    public void testDeleteNoPermissions() {
        Script script = new Script();
        scriptBean.delete(script);
    }

    @Test
    public void testDeleteWithPermissions() {
        when(security.hasRight(any(AccessRight.class))).thenReturn(true);
        Script script = new Script();
        scriptBean.delete(script);
    }

    @Test
    public void testDeleteAsOwner() {
        when(security.isOwner(any(Script.class))).thenReturn(true);
        Script script = new Script();
        scriptBean.delete(script);
    }

    @Test
    public void testIsCurrent() {
        when(scriptLoader.isCurrent(anyInt())).thenReturn(true);
        assertTrue(scriptBean.isCurrent());
    }

    @Test
    public void testSaveAsNoName() {
        Script script = new Script();
        scriptBean.saveAs(script);
    }

    @Test
    public void testSaveAsSameName() {
        scriptBean.setSaveAsName("Name");
        Script script = Script.builder().name("Name").build();
        scriptBean.saveAs(script);
    }

    @Test
    public void testSaveAs() {
        CallerPrincipal callerPrincipal = new CallerPrincipal("LoginName");
        when(securityContext.getCallerPrincipal()).thenReturn(callerPrincipal);
        scriptBean.setSaveAsName("New Name");
        Script script = Script.builder().name("Old Name").build();
        scriptBean.saveAs(script);
    }

    @Test
    public void testSaveAsName() {
        scriptBean.setSaveAsName("Save As Name");
        assertEquals("Save As Name", scriptBean.getSaveAsName());
    }

    @Test
    public void testStepTablePrefs() {
        TablePreferences tablePreferences = new TablePreferences(new LinkedList());
        scriptBean.setStepTablePrefs(tablePreferences);
        assertEquals(tablePreferences, scriptBean.getStepTablePrefs());
    }

    @Test
    public void testSelectedScript() {
        Script script = new Script();
        SelectableWrapper<Script> wrapper = new SelectableWrapper<>(script);
        scriptBean.setSelectedScript(wrapper);
        assertEquals(wrapper, scriptBean.getSelectedScript());
    }

    @Test
    @Disabled
    public void testEntityList() {
        List<Script> scripts = scriptBean.getEntityList(ViewFilterType.ALL);
        assertNotNull(scripts);
        assertFalse(scripts.isEmpty());
        assertEquals(1, scripts.size());
    }
}