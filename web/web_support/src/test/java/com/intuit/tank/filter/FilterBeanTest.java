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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.filter.FilterBean;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * The class <code>FilterBeanTest</code> contains tests for the class <code>{@link FilterBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class FilterBeanTest {
    /**
     * Run the FilterBean() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testFilterBean_1()
            throws Exception {
        FilterBean result = new FilterBean();
        assertNotNull(result);
    }

    /**
     * Run the void deleteSelectedFilter() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testDeleteSelectedFilter_2()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(null);

        fixture.deleteSelectedFilter();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
    }

    /**
     * Run the List<ScriptFilter> getEntityList(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetEntityList_1()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        ViewFilterType viewFilter = ViewFilterType.ALL;

        List<ScriptFilter> result = fixture.getEntityList(viewFilter);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
        assertNotNull(result);
    }

    /**
     * Run the SelectableWrapper<ScriptFilter> getSelectedFilter() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectedFilter_1()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        SelectableWrapper<ScriptFilter> result = fixture.getSelectedFilter();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
        assertNotNull(result);
    }

    /**
     * Run the List<Integer> getSelectedFilterIds() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectedFilterIds_1()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        List<Integer> result = fixture.getSelectedFilterIds();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
        assertNotNull(result);
    }

    /**
     * Run the List<Integer> getSelectedFilterIds() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectedFilterIds_2()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        List<Integer> result = fixture.getSelectedFilterIds();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
        assertNotNull(result);
    }

    /**
     * Run the List<Integer> getSelectedFilterIds() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSelectedFilterIds_3()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        List<Integer> result = fixture.getSelectedFilterIds();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
        assertNotNull(result);
    }

    /**
     * Run the boolean isCurrent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsCurrent_1()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));

        boolean result = fixture.isCurrent();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
        assertTrue(result);
    }

    /**
     * Run the void setSelectedFilter(SelectableWrapper<ScriptFilter>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelectedFilter_1()
            throws Exception {
        FilterBean fixture = new FilterBean();
        fixture.setSelectedFilter(new SelectableWrapper((Object) null));
        SelectableWrapper<ScriptFilter> selectedFilter = new SelectableWrapper((Object) null);

        fixture.setSelectedFilter(selectedFilter);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.filter.FilterBean.<init>(FilterBean.java:30)
    }
}