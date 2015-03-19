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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.filter.FilterServiceBean;
import com.intuit.tank.project.ScriptFilter;

/**
 * The class <code>FilterServiceBeanTest</code> contains tests for the class <code>{@link FilterServiceBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class FilterServiceBeanTest {
    /**
     * Run the FilterServiceBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testFilterServiceBean_1()
        throws Exception {
        FilterServiceBean result = new FilterServiceBean();
        assertNotNull(result);
    }

    /**
     * Run the void edit() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEdit_1()
        throws Exception {
        FilterServiceBean fixture = new FilterServiceBean();

        fixture.edit();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.FilterServiceBean.edit(FilterServiceBean.java:49)
    }

    /**
     * Run the void edit(ScriptFilter) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEdit_2()
        throws Exception {
        FilterServiceBean fixture = new FilterServiceBean();
        ScriptFilter filter = new ScriptFilter();

        fixture.edit(filter);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.FilterServiceBean.edit(FilterServiceBean.java:45)
    }

    /**
     * Run the List<ScriptFilter> getScriptFilters() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetScriptFilters_1()
        throws Exception {
        FilterServiceBean fixture = new FilterServiceBean();

        List<ScriptFilter> result = fixture.getScriptFilters();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.FilterServiceBean.getScriptFilters(FilterServiceBean.java:35)
        assertNotNull(result);
    }

    /**
     * Run the void handleInvalidEvent(InvalidScriptFilter) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testHandleInvalidEvent_1()
        throws Exception {
        FilterServiceBean fixture = new FilterServiceBean();
        FilterServiceBean.InvalidScriptFilter filterEvent = null;

        fixture.handleInvalidEvent(filterEvent);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.FilterServiceBean.handleInvalidEvent(FilterServiceBean.java:57)
    }

    /**
     * Run the void handleModifyEvent(ScriptFilter) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testHandleModifyEvent_1()
        throws Exception {
        FilterServiceBean fixture = new FilterServiceBean();
        ScriptFilter filter = new ScriptFilter();

        fixture.handleModifyEvent(filter);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.FilterServiceBean.handleModifyEvent(FilterServiceBean.java:66)
    }
}