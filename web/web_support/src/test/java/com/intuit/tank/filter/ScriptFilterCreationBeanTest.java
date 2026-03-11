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

import com.intuit.tank.auth.Security;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.util.Messages;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import jakarta.enterprise.context.Conversation;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScriptFilterCreationBeanTest {

    @InjectMocks
    private ScriptFilterCreationBean bean;

    @Mock
    private Messages messages;

    @Mock
    private Conversation conversation;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private Security security;

    @Mock
    private ExceptionHandler exceptionHandler;

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
    public void testConstructor() {
        assertNotNull(bean);
    }

    @Test
    public void testGetSetName() {
        bean.setName("TestFilter");
        assertEquals("TestFilter", bean.getName());
    }

    @Test
    public void testGetSetProductName() {
        bean.setProductName("ProductA");
        assertEquals("ProductA", bean.getProductName());
    }

    @Test
    public void testGetSetCreationMode() {
        bean.setCreationMode(ScriptFilterType.EXTERNAL);
        assertEquals(ScriptFilterType.EXTERNAL, bean.getCreationMode());
    }

    @Test
    public void testDefaultCreationMode() {
        assertEquals(ScriptFilterType.INTERNAL, bean.getCreationMode());
    }

    @Test
    public void testGetSetSaveAsName() {
        bean.setSaveAsName("NewName");
        assertEquals("NewName", bean.getSaveAsName());
    }

    @Test
    public void testGetSetConditionProcessed() {
        bean.setConditionProcessed(true);
        assertTrue(bean.isConditionProcessed());
    }

    @Test
    public void testIsEditing_InitiallyFalse() {
        assertFalse(bean.isEditing());
    }

    @Test
    public void testGetSetFilter() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);
        assertEquals(filter, bean.getFilter());
    }

    @Test
    public void testGetSetSelectedExternalScript() {
        bean.setSelectedExternalScript(42);
        assertEquals(42, bean.getSelectedExternalScript());
    }

    @Test
    public void testGetScriptFilterTypeList_ReturnsAllValues() {
        ScriptFilterType[] types = bean.getScriptFilterTypeList();
        assertNotNull(types);
        assertTrue(types.length > 0);
    }

    @Test
    public void testCancel_EndsConversation() {
        bean.cancel();
        verify(conversation).end();
    }

    @Test
    public void testCanCreateFilter_WhenHasRight_ReturnsTrue() {
        when(security.hasRight(AccessRight.CREATE_FILTER)).thenReturn(true);
        assertTrue(bean.canCreateFilter());
    }

    @Test
    public void testCanCreateFilter_WhenNoRight_ReturnsFalse() {
        when(security.hasRight(AccessRight.CREATE_FILTER)).thenReturn(false);
        assertFalse(bean.canCreateFilter());
    }

    @Test
    public void testCanEditFilter_WhenHasEditRight_ReturnsTrue() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(true);
        assertTrue(bean.canEditFilter());
    }

    @Test
    public void testCanEditFilter_WhenOwner_ReturnsTrue() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(false);
        when(security.isOwner(filter)).thenReturn(true);
        assertTrue(bean.canEditFilter());
    }

    @Test
    public void testEditFilter_SetsEditingTrue() {
        ScriptFilter filter = new ScriptFilter();
        filter.setName("TestFilter");
        filter.setFilterType(ScriptFilterType.INTERNAL);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(true);

        bean.editFilter(filter);

        assertTrue(bean.isEditing());
        assertEquals(filter, bean.getFilter());
        verify(conversation).begin();
    }

    @Test
    public void testEditFilter_WhenNoPermission_ShowsWarning() {
        ScriptFilter filter = new ScriptFilter();
        filter.setName("TestFilter");
        filter.setFilterType(ScriptFilterType.INTERNAL);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(false);
        when(security.isOwner(filter)).thenReturn(false);

        bean.editFilter(filter);

        verify(messages).warn(anyString());
    }

    @Test
    public void testRemoveCondition() {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition condition = new ScriptFilterCondition();
        filter.addCondition(condition);
        bean.setFilter(filter);

        bean.removeCondition(condition);

        assertFalse(filter.getConditions().contains(condition));
    }

    @Test
    public void testRemoveAction() {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterAction action = new ScriptFilterAction();
        filter.addAction(action);
        bean.setFilter(filter);

        bean.removeAction(action);

        assertFalse(filter.getActions().contains(action));
    }

    @Test
    public void testGetConditions_ReturnsFilterConditions() {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterCondition condition = new ScriptFilterCondition();
        filter.addCondition(condition);
        bean.setFilter(filter);

        List<ScriptFilterCondition> conditions = bean.getConditions();
        assertEquals(1, conditions.size());
    }

    @Test
    public void testGetActions_ReturnsFilterActions() {
        ScriptFilter filter = new ScriptFilter();
        ScriptFilterAction action = new ScriptFilterAction();
        filter.addAction(action);
        bean.setFilter(filter);

        List<ScriptFilterAction> actions = bean.getActions();
        assertEquals(1, actions.size());
    }

    @Test
    public void testSetAllConditionsPass_UpdatesFilter() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);

        bean.setAllConditionsPass(true);

        assertTrue(bean.isAllConditionsPass());
        assertTrue(filter.getAllConditionsMustPass());
    }

    @Test
    public void testSaveAs_WhenEmptyName_ShowsError() {
        bean.setSaveAsName("");
        bean.saveAs();
        verify(messages).error(anyString());
    }

    @Test
    public void testSaveAs_WhenNullName_ShowsError() {
        bean.setSaveAsName(null);
        bean.saveAs();
        verify(messages).error(anyString());
    }

    @Test
    public void testSave_WhenMessagesEmpty_AttemptsSave() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);
        bean.setName("TestFilter");
        when(messages.isEmpty()).thenReturn(true);

        // save will attempt DAO call which may succeed with H2 or fail
        // but we just verify the behavior up to that point
        assertDoesNotThrow(() -> bean.save());
    }

    @Test
    public void testSave_WhenMessagesNotEmpty_ReturnsFail() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);
        when(messages.isEmpty()).thenReturn(false);

        String result = bean.save();
        assertEquals("fail", result);
    }

    @Test
    public void testSetExternalScripts() {
        List<com.intuit.tank.project.ExternalScript> scripts = java.util.Collections.emptyList();
        bean.setExternalScripts(scripts);
        // No exception should be thrown
    }

    @Test
    public void testNewFilter_SetsEditingFalseAndBeginsConversation() {
        jakarta.security.enterprise.CallerPrincipal principal = new jakarta.security.enterprise.CallerPrincipal("testuser");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);

        bean.newFilter();

        assertFalse(bean.isEditing());
        verify(conversation).begin();
        assertNotNull(bean.getFilter());
        assertEquals("testuser", bean.getFilter().getCreator());
    }

    @Test
    public void testSave_WithExternalMode_WhenMessagesEmpty_AttemptsSave() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);
        bean.setName("ExternalFilter");
        bean.setCreationMode(ScriptFilterType.EXTERNAL);
        bean.setSelectedExternalScript(1);
        when(messages.isEmpty()).thenReturn(true);

        // save with EXTERNAL mode - attempts DAO call
        assertDoesNotThrow(() -> bean.save());
    }

    @Test
    public void testSave_WithExternalMode_WhenMessagesNotEmpty_ReturnsFail() {
        ScriptFilter filter = new ScriptFilter();
        bean.setFilter(filter);
        bean.setCreationMode(ScriptFilterType.EXTERNAL);
        when(messages.isEmpty()).thenReturn(false);

        String result = bean.save();
        assertEquals("fail", result);
    }

    @Test
    public void testSaveAs_WhenDifferentName_CopiesFilter() {
        ScriptFilter filter = new ScriptFilter();
        filter.setName("OriginalFilter");
        filter.setFilterType(ScriptFilterType.INTERNAL);
        bean.setFilter(filter);
        bean.setName("OriginalFilter");
        bean.setSaveAsName("DifferentName");

        jakarta.security.enterprise.CallerPrincipal principal = new jakarta.security.enterprise.CallerPrincipal("testuser");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(true);

        // saveAs with different name will attempt DAO saveOrUpdate - may succeed with H2
        assertDoesNotThrow(() -> bean.saveAs());
    }

    @Test
    public void testSaveAs_WhenSameName_CallsSave() {
        ScriptFilter filter = new ScriptFilter();
        filter.setName("SameFilter");
        filter.setFilterType(ScriptFilterType.INTERNAL);
        bean.setFilter(filter);
        bean.setName("SameFilter");
        bean.setSaveAsName("SameFilter");
        when(messages.isEmpty()).thenReturn(true);

        assertDoesNotThrow(() -> bean.saveAs());
    }

    @Test
    public void testGetExternalScripts_ReturnsNonNull() {
        // calls ExternalScriptDao with H2 - should return empty list or list of scripts
        List<com.intuit.tank.project.ExternalScript> scripts = bean.getExternalScripts();
        assertNotNull(scripts);
    }
}
