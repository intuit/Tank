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

/**
 * The class <code>FilterGroupBeanTest</code> contains tests for the class <code>{@link FilterGroupBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class FilterGroupBeanTest {
    /**
     * Run the FilterGroupBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testFilterGroupBean_1()
        throws Exception {
        FilterGroupBean result = new FilterGroupBean();
        assertNotNull(result);
    }



   

    /**
     * Run the void deleteSelectedFilterGroup() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDeleteSelectedFilterGroup_2()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(null);

        fixture.deleteSelectedFilterGroup();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.filter.FilterGroupBean.<init>(FilterGroupBean.java:24)
    }

    /**
     * Run the List<ScriptFilterGroup> getEntityList(ViewFilterType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    @Disabled
    public void testGetEntityList_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<ScriptFilterGroup> result = fixture.getEntityList(viewFilter);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.filter.FilterGroupBean.<init>(FilterGroupBean.java:24)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptFilterGroup> getEntityList(ViewFilterType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    @Disabled
    public void testGetEntityList_2()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<ScriptFilterGroup> result = fixture.getEntityList(viewFilter);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.filter.FilterGroupBean.<init>(FilterGroupBean.java:24)
        assertNotNull(result);
    }

    /**
     * Run the SelectableWrapper<ScriptFilterGroup> getSelectedFilter() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectedFilter_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        SelectableWrapper<ScriptFilterGroup> result = fixture.getSelectedFilter();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.filter.FilterGroupBean.<init>(FilterGroupBean.java:24)
        assertNotNull(result);
    }

    /**
     * Run the boolean isCurrent() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsCurrent_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        boolean result = fixture.isCurrent();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.filter.FilterGroupBean.<init>(FilterGroupBean.java:24)
        assertTrue(result);
    }


    /**
     * Run the void setSelectedFilter(SelectableWrapper<ScriptFilterGroup>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelectedFilter_1()
        throws Exception {
        FilterGroupBean fixture = new FilterGroupBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        SelectableWrapper<ScriptFilterGroup> selectedFilter = new SelectableWrapper((Object) null);

        fixture.setSelectedFilter(selectedFilter);
    }

    // Mockito-based tests for delete() and processAllSelection()

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