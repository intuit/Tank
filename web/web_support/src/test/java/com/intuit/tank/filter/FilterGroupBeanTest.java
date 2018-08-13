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

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.filter.FilterGroupBean.<init>(FilterGroupBean.java:24)
    }

}