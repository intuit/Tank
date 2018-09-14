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

import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.service.FilterService;

/**
 * The class <code>FilterServiceTest</code> contains tests for the class <code>{@link FilterService}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class FilterServiceTest {
    /**
     * Run the FilterService() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testFilterService_1()
            throws Exception {
        FilterService result = new FilterService();
        assertNotNull(result);
    }

    /**
     * Run the void deleteScriptFilter(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDeleteScriptFilter_1()
            throws Exception {
        FilterService fixture = new FilterService();
        int id = 1;

        fixture.deleteScriptFilter(id);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.service.FilterService.deleteScriptFilter(FilterService.java:66)
    }

    /**
     * Run the void deleteScriptFilter(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testDeleteScriptFilter_2()
            throws Exception {
        FilterService fixture = new FilterService();
        int id = 1;

        fixture.deleteScriptFilter(id);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.service.FilterService.deleteScriptFilter(FilterService.java:66)
    }

    /**
     * Run the List<ScriptFilter> getFilters() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetFilters_1()
            throws Exception {
        FilterService fixture = new FilterService();

        List<ScriptFilter> result = fixture.getFilters();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.service.FilterService.getFilters(FilterService.java:32)
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptFilter> getFilters() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetFilters_2()
            throws Exception {
        FilterService fixture = new FilterService();

        List<ScriptFilter> result = fixture.getFilters();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.service.FilterService.getFilters(FilterService.java:32)
        assertNotNull(result);
    }

    /**
     * Run the ScriptFilter getScriptFilter(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetScriptFilter_1()
            throws Exception {
        FilterService fixture = new FilterService();
        int id = 1;

        ScriptFilter result = fixture.getScriptFilter(id);
    }

}