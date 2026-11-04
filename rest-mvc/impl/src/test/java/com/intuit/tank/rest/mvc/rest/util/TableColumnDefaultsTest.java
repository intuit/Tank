package com.intuit.tank.rest.mvc.rest.util;

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

import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.Preferences;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>TableColumnDefaultsTest</code> contains tests for the class <code>{@link TableColumnDefaults}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class TableColumnDefaultsTest {

    @Test
    public void testProjectColPrefsNotNull() {
        assertNotNull(TableColumnDefaults.PROJECT_COL_PREFS);
        assertFalse(TableColumnDefaults.PROJECT_COL_PREFS.isEmpty());
    }

    @Test
    public void testScriptsColPrefsNotNull() {
        assertNotNull(TableColumnDefaults.SCRIPTS_COL_PREFS);
        assertFalse(TableColumnDefaults.SCRIPTS_COL_PREFS.isEmpty());
    }

    @Test
    public void testDataFilesColPrefsNotNull() {
        assertNotNull(TableColumnDefaults.DATA_FILES_COL_PREFS);
        assertFalse(TableColumnDefaults.DATA_FILES_COL_PREFS.isEmpty());
    }

    @Test
    public void testJobsColPrefsNotNull() {
        assertNotNull(TableColumnDefaults.JOBS_COL_PREFS);
        assertFalse(TableColumnDefaults.JOBS_COL_PREFS.isEmpty());
    }

    @Test
    public void testScriptStepsColPrefsNotNull() {
        assertNotNull(TableColumnDefaults.SCRIPT_STEPS_COL_PREFS);
        assertFalse(TableColumnDefaults.SCRIPT_STEPS_COL_PREFS.isEmpty());
    }

    @Test
    public void testProjectColPrefsSize() {
        assertEquals(9, TableColumnDefaults.PROJECT_COL_PREFS.size());
    }

    @Test
    public void testJobsColPrefsSize() {
        assertEquals(12, TableColumnDefaults.JOBS_COL_PREFS.size());
    }

    @Test
    public void testEnsureDefaultsCreatesPreferencesForNewUser() {
        TableColumnDefaults.Result result = TableColumnDefaults.ensureDefaults(null, "alice");
        assertTrue(result.changed());
        assertEquals("alice", result.preferences().getCreator());
        for (TableColumnDefaults.Table table : TableColumnDefaults.Table.values()) {
            assertEquals(table.getDefaults(), table.columnsOf(result.preferences()), table.name());
        }
        assertEquals("alice", result.preferences().getJobsTableColumns().get(0).getCreator());
    }

    @Test
    public void testEnsureDefaultsKeepsSavedSettingsAndAddsMissingColumns() {
        Preferences saved = TableColumnDefaults.ensureDefaults(null, "alice").preferences();
        ColumnPreferences name = saved.getProjectTableColumns().get(2);
        name.setSize(400);
        name.setVisible(false);
        saved.getProjectTableColumns().remove(1);

        TableColumnDefaults.Result result = TableColumnDefaults.ensureDefaults(saved, "alice");

        assertTrue(result.changed());
        assertSame(saved, result.preferences());
        assertEquals(TableColumnDefaults.PROJECT_COL_PREFS, saved.getProjectTableColumns());
        assertEquals("idColumn", saved.getProjectTableColumns().get(1).getColName());
        assertEquals(400, saved.getProjectTableColumns().get(2).getSize());
        assertFalse(saved.getProjectTableColumns().get(2).isVisible());
    }

    @Test
    public void testEnsureDefaultsUnchangedWhenComplete() {
        Preferences saved = TableColumnDefaults.ensureDefaults(null, "alice").preferences();
        assertFalse(TableColumnDefaults.ensureDefaults(saved, "alice").changed());
    }
}
