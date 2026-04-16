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
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.util.Messages;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import jakarta.enterprise.context.Conversation;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FilterGroupCreationBeanTest {

    @InjectMocks
    private FilterGroupCreationBean filterGroupCreationBean;

    @Mock
    private Messages messages;

    @Mock
    private Conversation conversation;

    @Mock
    private TankSecurityContext securityContext;

    @Mock
    private Security security;

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
    public void testGetSfg_WhenNull_ReturnsNewGroup() {
        ScriptFilterGroup sfg = filterGroupCreationBean.getSfg();
        assertNotNull(sfg);
    }

    @Test
    public void testSetGetSfg() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("TestGroup");
        filterGroupCreationBean.setSfg(group);
        assertEquals(group, filterGroupCreationBean.getSfg());
    }

    @Test
    public void testGetName_ReturnsSfgName() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("GroupName");
        filterGroupCreationBean.setSfg(group);
        assertEquals("GroupName", filterGroupCreationBean.getName());
    }

    @Test
    public void testSetName_SetsSfgName() {
        filterGroupCreationBean.setName("NewName");
        assertEquals("NewName", filterGroupCreationBean.getName());
    }

    @Test
    public void testGetSetSaveAsName() {
        filterGroupCreationBean.setSaveAsName("SavedName");
        assertEquals("SavedName", filterGroupCreationBean.getSaveAsName());
    }

    @Test
    public void testIsEditing_InitiallyFalse() {
        assertFalse(filterGroupCreationBean.isEditing());
    }

    @Test
    public void testCancel_EndsConversation() {
        filterGroupCreationBean.cancel();
        verify(conversation).end();
    }

    @Test
    public void testIsCurrent_ReturnsTrue() {
        assertTrue(filterGroupCreationBean.isCurrent());
    }

    @Test
    public void testDelete_DoesNothing() {
        ScriptFilter filter = new ScriptFilter();
        // delete is a no-op in FilterGroupCreationBean
        filterGroupCreationBean.delete(filter);
        // No exception should be thrown
    }

    @Test
    public void testCanCreateFilterGroup_WhenHasRight_ReturnsTrue() {
        when(security.hasRight(AccessRight.CREATE_FILTER)).thenReturn(true);
        assertTrue(filterGroupCreationBean.canCreateFilterGroup());
    }

    @Test
    public void testCanCreateFilterGroup_WhenNoRight_ReturnsFalse() {
        when(security.hasRight(AccessRight.CREATE_FILTER)).thenReturn(false);
        assertFalse(filterGroupCreationBean.canCreateFilterGroup());
    }

    @Test
    public void testCanEditFilterGroup_WhenHasEditRight_ReturnsTrue() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        filterGroupCreationBean.setSfg(group);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(true);
        assertTrue(filterGroupCreationBean.canEditFilterGroup());
    }

    @Test
    public void testCanEditFilterGroup_WhenOwner_ReturnsTrue() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        filterGroupCreationBean.setSfg(group);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(false);
        when(security.isOwner(group)).thenReturn(true);
        assertTrue(filterGroupCreationBean.canEditFilterGroup());
    }

    @Test
    public void testCanEditFilterGroup_WhenNoRightOrOwner_ReturnsFalse() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        filterGroupCreationBean.setSfg(group);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(false);
        when(security.isOwner(group)).thenReturn(false);
        assertFalse(filterGroupCreationBean.canEditFilterGroup());
    }

    @Test
    public void testSaveAs_WhenNameEmpty_ShowsError() {
        filterGroupCreationBean.setSaveAsName("");
        filterGroupCreationBean.saveAs();
        verify(messages).error(anyString());
    }

    @Test
    public void testSaveAs_WhenNullName_ShowsError() {
        filterGroupCreationBean.setSaveAsName(null);
        filterGroupCreationBean.saveAs();
        verify(messages).error(anyString());
    }

    @Test
    public void testNewFilterGroup_SetsEditingFalseAndBeginsConversation() {
        jakarta.security.enterprise.CallerPrincipal principal = new jakarta.security.enterprise.CallerPrincipal("testuser");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);

        filterGroupCreationBean.newFilterGroup();

        assertFalse(filterGroupCreationBean.isEditing());
        verify(conversation).begin();
        assertEquals("testuser", filterGroupCreationBean.getSfg().getCreator());
    }

    @Test
    public void testGetEntityList_ReturnsNonNull() {
        // Will call H2 DB; should return a list
        java.util.List<ScriptFilter> result = filterGroupCreationBean.getEntityList(ViewFilterType.ALL);
        assertNotNull(result);
    }

    @Test
    public void testSave_WithNoSelectedFilters_CallsDao() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("TestGroup");
        group.setCreator("testuser");
        filterGroupCreationBean.setSfg(group);

        // save() calls DAO - with H2 should complete without NPE
        assertDoesNotThrow(() -> filterGroupCreationBean.save());
    }

    @Test
    public void testSaveAs_WhenSameNameAsSfg_CallsSave() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("SameName");
        group.setCreator("testuser");
        filterGroupCreationBean.setSfg(group);
        filterGroupCreationBean.setSaveAsName("SameName");

        // When same name, calls save() which calls DAO
        assertDoesNotThrow(() -> filterGroupCreationBean.saveAs());
    }

    @Test
    public void testSaveAs_WhenDifferentNameFromSfg_SavesNewGroup() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("OriginalName");
        group.setCreator("testuser");
        filterGroupCreationBean.setSfg(group);
        filterGroupCreationBean.setSaveAsName("NewName");

        jakarta.security.enterprise.CallerPrincipal principal = new jakarta.security.enterprise.CallerPrincipal("testuser");
        when(securityContext.getCallerPrincipal()).thenReturn(principal);
        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(true);

        assertDoesNotThrow(() -> filterGroupCreationBean.saveAs());
        verify(messages).info(anyString());
    }

    @Test
    public void testEditFilterGroup_SetsEditingTrue() {
        // Create and save a group to H2 first
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("EditableGroup");
        group.setCreator("testuser");
        ScriptFilterGroup saved = new com.intuit.tank.dao.ScriptFilterGroupDao().saveOrUpdate(group);

        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(true);

        filterGroupCreationBean.editFilterGroup(saved);

        assertTrue(filterGroupCreationBean.isEditing());
        verify(conversation).begin();
        assertNotNull(filterGroupCreationBean.getSfg());
    }

    @Test
    public void testEditFilterGroup_WhenNoPermission_ShowsWarning() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("ProtectedGroup");
        group.setCreator("otheruser");
        ScriptFilterGroup saved = new com.intuit.tank.dao.ScriptFilterGroupDao().saveOrUpdate(group);

        when(security.hasRight(AccessRight.EDIT_FILTER)).thenReturn(false);
        when(security.isOwner(any())).thenReturn(false);

        filterGroupCreationBean.editFilterGroup(saved);

        verify(messages).warn(anyString());
    }
}
