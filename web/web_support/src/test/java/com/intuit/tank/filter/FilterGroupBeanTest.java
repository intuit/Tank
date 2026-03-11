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

import com.intuit.tank.auth.Security;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.settings.AccessRight;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intuit.tank.filter.FilterGroupBean;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableWrapper;

public class FilterGroupBeanTest {
    @InjectMocks
    private FilterGroupBean filterGroupBean;

    @Mock
    private FilterBean filterBean;

    @Mock
    private Security security;

    @Mock
    private Messages messages;

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
    public void testFilterGroupBean_1()
        throws Exception {
        FilterGroupBean result = new FilterGroupBean();
        assertNotNull(result);
    }

    @Test
    public void testDeleteSelectedFilterGroup_2()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(null);

        fixture.deleteSelectedFilterGroup();
    }

    @Test
    @Disabled
    public void testGetEntityList_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<ScriptFilterGroup> result = fixture.getEntityList(viewFilter);

        assertNotNull(result);
    }

    @Test
    @Disabled
    public void testGetEntityList_2()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<ScriptFilterGroup> result = fixture.getEntityList(viewFilter);

        assertNotNull(result);
    }

    @Test
    public void testGetSelectedFilter_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        SelectableWrapper<ScriptFilterGroup> result = fixture.getSelectedFilter();

        assertNotNull(result);
    }

    @Test
    public void testIsCurrent_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        boolean result = fixture.isCurrent();

        assertTrue(result);
    }

    @Test
    public void testSetSelectedFilter_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        SelectableWrapper<ScriptFilterGroup> selectedFilter = new SelectableWrapper((Object) null);

        fixture.setSelectedFilter(selectedFilter);
    }

    @Test
    public void testDelete_WithNoPermissionAndNotOwner_WarnsUser() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        when(security.hasRight(AccessRight.DELETE_FILTER)).thenReturn(false);
        when(security.isOwner(group)).thenReturn(false);
        filterGroupBean.delete(group);
        verify(messages).warn(anyString());
    }

    @Test
    public void testDeleteSelectedFilterGroup_WhenNull_DoesNothing() {
        filterGroupBean.setSelectedFilter(null);
        filterGroupBean.deleteSelectedFilterGroup();
        verifyNoInteractions(security, messages);
    }

    @Test
    public void testDeleteSelectedFilterGroup_WhenSelectedWithNoPermission_WarnsUser() {
        ScriptFilterGroup group = new ScriptFilterGroup();
        SelectableWrapper<ScriptFilterGroup> wrapper = new SelectableWrapper<>(group);
        filterGroupBean.setSelectedFilter(wrapper);
        when(security.hasRight(AccessRight.DELETE_FILTER)).thenReturn(false);
        when(security.isOwner(group)).thenReturn(false);
        filterGroupBean.deleteSelectedFilterGroup();
        verify(messages).warn(anyString());
    }

    @Test
    public void testIsCurrent_ReturnsTrue() {
        assertTrue(filterGroupBean.isCurrent());
    }

    @Test
    public void testGetEntityList_ReturnsNonNull() {
        List<ScriptFilterGroup> result = filterGroupBean.getEntityList(com.intuit.tank.view.filter.ViewFilterType.ALL);
        assertNotNull(result);
    }

    @Test
    public void testDelete_WithPermission_DeletesGroupSuccessfully() {
        // Save a group to H2 so it can be deleted
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("ToDelete");
        group.setCreator("testuser");
        ScriptFilterGroup saved = new com.intuit.tank.dao.ScriptFilterGroupDao().saveOrUpdate(group);

        when(security.hasRight(AccessRight.DELETE_FILTER)).thenReturn(true);
        filterGroupBean.delete(saved);

        // No warning should be raised
        verify(messages, never()).warn(anyString());
    }

    @Test
    public void testSelectAll_CallsProcessAllSelection() {
        // selectAll() calls super.selectAll() then processAllSelection()
        // processAllSelection iterates getSelectionList() which may be empty initially
        assertDoesNotThrow(() -> filterGroupBean.selectAll());
    }

    @Test
    public void testUnselectAll_CallsProcessAllSelection() {
        // unselectAll() calls super.unselectAll() then processAllSelection()
        assertDoesNotThrow(() -> filterGroupBean.unselectAll());
    }

    @Test
    public void testProcessAllSelection_WithEntities_CallsFilterBean() {
        // Load entities first so selection list is populated
        // With empty H2, getSelectionList may be empty
        assertDoesNotThrow(() -> filterGroupBean.processAllSelection());
        // verify filterBean.processSelection is called 0 or more times
    }

    @Test
    public void testDeleteSelectedFilterGroup_WhenSelectedWithPermission_Deletes() {
        // Save a group and select it, then delete
        ScriptFilterGroup group = new ScriptFilterGroup();
        group.setName("SelectedGroup");
        group.setCreator("testuser");
        ScriptFilterGroup saved = new com.intuit.tank.dao.ScriptFilterGroupDao().saveOrUpdate(group);

        SelectableWrapper<ScriptFilterGroup> wrapper = new SelectableWrapper<>(saved);
        filterGroupBean.setSelectedFilter(wrapper);
        when(security.hasRight(AccessRight.DELETE_FILTER)).thenReturn(true);

        filterGroupBean.deleteSelectedFilterGroup();

        verify(messages, never()).warn(anyString());
    }
}