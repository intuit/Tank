package com.intuit.tank.filter;

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

import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intuit.tank.filter.ScriptFilterActionBean;
import com.intuit.tank.filter.ScriptFilterCreationBean;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.script.FailureTypes;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import com.intuit.tank.vm.api.enumerated.ValidationType;
import com.intuit.tank.vm.script.util.AddActionScope;
import com.intuit.tank.vm.script.util.RemoveActionScope;
import com.intuit.tank.vm.script.util.ReplaceActionScope;

public class ScriptFilterActionBeanTest {

    @InjectMocks
    private ScriptFilterActionBean bean;

    @Mock
    private ScriptFilterCreationBean sfcb;

    private AutoCloseable closeable;

    @BeforeEach
    void initMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testScriptFilterActionBean_1()
        throws Exception {

        ScriptFilterActionBean result = new ScriptFilterActionBean();

        assertNotNull(result);
    }

    @Test
    public void testScriptFilterActionBean_2()
        throws Exception {

        ScriptFilterActionBean result = new ScriptFilterActionBean();

        assertNotNull(result);
    }

    @Test
    public void testScriptFilterActionBean_3()
        throws Exception {

        ScriptFilterActionBean result = new ScriptFilterActionBean();

        assertNotNull(result);
    }

    @Test
    public void testDone_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_2()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_3()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_4()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_5()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_6()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_7()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_8()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_9()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testDone_10()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.done();
    }

    @Test
    public void testEditAction_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        ScriptFilterAction action = new ScriptFilterAction();
        action.setKey("");
        action.setValue("");
        action.setAction(ScriptFilterActionType.add);
        action.setScope("");

        fixture.editAction(action);
    }

    @Test
    public void testEditAction_2()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        ScriptFilterAction action = new ScriptFilterAction();
        action.setKey("");
        action.setValue("");
        action.setAction(ScriptFilterActionType.add);
        action.setScope("");

        fixture.editAction(action);
    }

    @Test
    public void testEditAction_3()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        ScriptFilterAction action = new ScriptFilterAction();
        action.setKey("");
        action.setValue("");
        action.setAction(ScriptFilterActionType.add);
        action.setScope("");

        fixture.editAction(action);
    }

    @Test
    public void testEditAction_4()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        ScriptFilterAction action = new ScriptFilterAction();
        action.setKey("");
        action.setValue("");
        action.setAction(ScriptFilterActionType.add);
        action.setScope("");

        fixture.editAction(action);
    }

    @Test
    public void testEditAction_5()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        ScriptFilterAction action = new ScriptFilterAction();
        action.setKey("");
        action.setValue("");
        action.setAction(ScriptFilterActionType.add);
        action.setScope("");

        fixture.editAction(action);
    }

    @Test
    public void testGetAction_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        ScriptFilterAction result = fixture.getAction();

        assertNotNull(result);
    }

    @Test
    public void testGetActionScopeValues_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionScopeValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionScopeValues_2()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionScopeValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionScopeValues_3()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionScopeValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionScopeValues_4()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionScopeValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionScopeValues_5()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionScopeValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionScopeValues_6()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionScopeValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionScopeValues_7()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionScopeValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionType_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        ScriptFilterActionType result = fixture.getActionType();

        assertNotNull(result);
    }

    @Test
    public void testGetActionValues_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionValues();

        assertNotNull(result);
    }

    @Test
    public void testGetActionValues_2()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        List<String> result = fixture.getActionValues();

        assertNotNull(result);
    }

    @Test
    public void testGetKey_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        String result = fixture.getKey();

        assertNotNull(result);
    }

    @Test
    public void testGetOnFailOptions_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        FailureTypes[] result = fixture.getOnFailOptions();

        assertNotNull(result);
    }

    @Test
    public void testGetScope_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        String result = fixture.getScope();

        assertNotNull(result);
    }

    @Test
    public void testGetValidationValues_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        ValidationType[] result = fixture.getValidationValues();

        assertNotNull(result);
    }

    @Test
    public void testGetValue_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        String result = fixture.getValue();

        assertNotNull(result);
    }

    @Test
    public void testGetValuePrefix_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        String result = fixture.getValuePrefix();

        assertNotNull(result);
    }

    @Test
    public void testIsAssignmentScope_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isAssignmentScope();

        assertTrue(!result);
    }

    @Test
    public void testIsKeyBoxRendered_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isKeyBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsKeyBoxRendered_2()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isKeyBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsKeyBoxRendered_3()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isKeyBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsKeyBoxRendered_4()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isKeyBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsKeyBoxRendered_5()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isKeyBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsOnFailOptionsRendered_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isOnFailOptionsRendered();

        assertFalse(result);
    }

    @Test
    public void testIsValidationScope_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isValidationScope();

        assertFalse(result);
    }

    @Test
    public void testIsValueBoxRendered_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isValueBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsValueBoxRendered_2()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isValueBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsValueBoxRendered_3()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isValueBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsValueBoxRendered_4()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isValueBoxRendered();

        assertTrue(result);
    }

    @Test
    public void testIsValuePrefixRendered_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        boolean result = fixture.isValuePrefixRendered();

        assertFalse(result);
    }

    @Test
    public void testSetAction_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        ScriptFilterAction action = new ScriptFilterAction();

        fixture.setAction(action);
    }

    @Test
    public void testSetActionType_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        ScriptFilterActionType actionType = ScriptFilterActionType.add;

        fixture.setActionType(actionType);
    }

    @Test
    public void testSetKey_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        String key = "";

        fixture.setKey(key);
    }

    @Test
    public void testSetScope_1()
            throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        String scope = "";

        fixture.setScope(scope);
    }

    @Test
    public void testSetValue_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        String value = "";

        fixture.setValue(value);
    }

    @Test
    public void testSetValuePrefix_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");
        String valuePrefix = "";

        fixture.setValuePrefix(valuePrefix);
    }

    @Test
    public void testGetActionScopeValues_WithRemoveType() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.remove);
        List<String> values = bean.getActionScopeValues();
        assertNotNull(values);
        assertFalse(values.isEmpty());
    }

    @Test
    public void testGetActionScopeValues_WithReplaceType() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.replace);
        List<String> values = bean.getActionScopeValues();
        assertNotNull(values);
        assertFalse(values.isEmpty());
    }

    @Test
    public void testEditAction_WithAssignmentScope() {
        ScriptFilterAction action = new ScriptFilterAction();
        action.setAction(ScriptFilterActionType.add);
        action.setScope(AddActionScope.assignment.getValue());
        action.setKey("key1");
        action.setValue("=someValue");
        bean.editAction(action);
        assertEquals(AddActionScope.assignment.getValue(), bean.getScope());
    }

    @Test
    public void testEditAction_WithValidationScope() {
        ScriptFilterAction action = new ScriptFilterAction();
        action.setAction(ScriptFilterActionType.add);
        action.setScope(AddActionScope.validation.getValue());
        action.setKey("key2");
        action.setValue("==expectedValue");
        bean.editAction(action);
        assertEquals(AddActionScope.validation.getValue(), bean.getScope());
    }

    @Test
    public void testDone_WithAssignmentScope_SetsValuePrefixPlusValue() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.add);
        bean.setScope(AddActionScope.assignment.getValue());
        bean.setKey("myKey");
        bean.setValuePrefix("=");
        bean.setValue("myValue");
        // editMode is false, so sfcb.getFilter().addAction() is called
        ScriptFilter filter = new ScriptFilter();
        when(sfcb.getFilter()).thenReturn(filter);
        bean.done();
        assertEquals("=myValue", bean.getAction().getValue());
    }

    @Test
    public void testDone_WithEditModeFalse_AddsActionToFilter() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.remove);
        bean.setScope(RemoveActionScope.request.getValue());
        bean.setKey("k");
        bean.setValue("v");
        ScriptFilter filter = new ScriptFilter();
        when(sfcb.getFilter()).thenReturn(filter);
        bean.done();
        verify(sfcb).getFilter();
    }

    @Test
    public void testIsKeyBoxRendered_WithReplaceAndOnfailScope() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.replace);
        bean.setScope(ReplaceActionScope.onfail.getValue());
        assertFalse(bean.isKeyBoxRendered());
    }

    @Test
    public void testIsValuePrefixRendered_WithAssignmentScope() {
        bean.setupNewAction();
        bean.setScope(AddActionScope.assignment.getValue());
        assertTrue(bean.isValuePrefixRendered());
    }

    @Test
    public void testIsValuePrefixRendered_WithValidationScope() {
        bean.setupNewAction();
        bean.setScope(AddActionScope.validation.getValue());
        assertTrue(bean.isValuePrefixRendered());
    }

    @Test
    public void testIsAssignmentScope_WithAddAssignmentScope() {
        bean.setupNewAction();
        bean.setScope(AddActionScope.assignment.getValue());
        assertTrue(bean.isAssignmentScope());
        assertEquals("=", bean.getValuePrefix());
    }

    @Test
    public void testIsAssignmentScope_WithReplaceAssignmentScope() {
        bean.setupNewAction();
        bean.setScope(ReplaceActionScope.assignment.getValue());
        assertTrue(bean.isAssignmentScope());
    }

    @Test
    public void testIsValidationScope_WithAddValidationScope() {
        bean.setupNewAction();
        bean.setScope(AddActionScope.validation.getValue());
        assertTrue(bean.isValidationScope());
    }

    @Test
    public void testIsValidationScope_WithReplaceValidationScope() {
        bean.setupNewAction();
        bean.setScope(ReplaceActionScope.validation.getValue());
        assertTrue(bean.isValidationScope());
    }

    @Test
    public void testIsValueBoxRendered_WithRemoveType() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.remove);
        bean.setScope(RemoveActionScope.request.getValue());
        assertFalse(bean.isValueBoxRendered());
    }

    @Test
    public void testIsValueBoxRendered_WithReplaceAndOnfailScope() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.replace);
        bean.setScope(ReplaceActionScope.onfail.getValue());
        assertFalse(bean.isValueBoxRendered());
    }

    @Test
    public void testIsOnFailOptionsRendered_WithReplaceAndOnfailScope() {
        bean.setupNewAction();
        bean.setActionType(ScriptFilterActionType.replace);
        bean.setScope(ReplaceActionScope.onfail.getValue());
        assertTrue(bean.isOnFailOptionsRendered());
    }

    @Test
    public void testSetupNewAction_1()
        throws Exception {
        ScriptFilterActionBean fixture = new ScriptFilterActionBean();
        fixture.setKey("");
        fixture.setValuePrefix("");
        fixture.setScope("");
        ScriptFilterAction scriptFilterAction = new ScriptFilterAction();
        scriptFilterAction.setKey("");
        scriptFilterAction.setValue("");
        scriptFilterAction.setAction(ScriptFilterActionType.add);
        scriptFilterAction.setScope("");
        fixture.editAction(scriptFilterAction);
        fixture.setActionType(ScriptFilterActionType.add);
        fixture.setValue("");

        fixture.setupNewAction();
    }
}