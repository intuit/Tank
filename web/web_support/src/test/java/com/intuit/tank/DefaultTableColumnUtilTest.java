package com.intuit.tank;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.DefaultTableColumnUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>DefaultTableColumnUtilTest</code> contains tests for the class <code>{@link DefaultTableColumnUtil}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class DefaultTableColumnUtilTest {

    @Test
    public void testProjectColPrefsNotNull() {
        assertNotNull(DefaultTableColumnUtil.PROJECT_COL_PREFS);
        assertFalse(DefaultTableColumnUtil.PROJECT_COL_PREFS.isEmpty());
    }

    @Test
    public void testScriptsColPrefsNotNull() {
        assertNotNull(DefaultTableColumnUtil.SCRIPTS_COL_PREFS);
        assertFalse(DefaultTableColumnUtil.SCRIPTS_COL_PREFS.isEmpty());
    }

    @Test
    public void testDataFilesColPrefsNotNull() {
        assertNotNull(DefaultTableColumnUtil.DATA_FILES_COL_PREFS);
        assertFalse(DefaultTableColumnUtil.DATA_FILES_COL_PREFS.isEmpty());
    }

    @Test
    public void testJobsColPrefsNotNull() {
        assertNotNull(DefaultTableColumnUtil.JOBS_COL_PREFS);
        assertFalse(DefaultTableColumnUtil.JOBS_COL_PREFS.isEmpty());
    }

    @Test
    public void testScriptStepsColPrefsNotNull() {
        assertNotNull(DefaultTableColumnUtil.SCRIPT_STEPS_COL_PREFS);
        assertFalse(DefaultTableColumnUtil.SCRIPT_STEPS_COL_PREFS.isEmpty());
    }

    @Test
    public void testProjectColPrefsSize() {
        assertEquals(9, DefaultTableColumnUtil.PROJECT_COL_PREFS.size());
    }

    @Test
    public void testJobsColPrefsSize() {
        assertEquals(12, DefaultTableColumnUtil.JOBS_COL_PREFS.size());
    }
}