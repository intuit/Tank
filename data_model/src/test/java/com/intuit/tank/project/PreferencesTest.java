package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.Preferences;

/**
 * The class <code>PreferencesTest</code> contains tests for the class <code>{@link Preferences}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class PreferencesTest {
    /**
     * Run the Preferences() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testPreferences_1()
        throws Exception {
        Preferences result = new Preferences();
        assertNotNull(result);
    }

    /**
     * Run the List<ColumnPreferences> getDatafilesTableColumns() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetDatafilesTableColumns_1()
        throws Exception {
        Preferences fixture = new Preferences();

        List<ColumnPreferences> result = fixture.getDatafilesTableColumns();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ColumnPreferences> getJobsTableColumns() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobsTableColumns_1()
        throws Exception {
        Preferences fixture = new Preferences();

        List<ColumnPreferences> result = fixture.getJobsTableColumns();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ColumnPreferences> getProjectTableColumns() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProjectTableColumns_1()
        throws Exception {
        Preferences fixture = new Preferences();

        List<ColumnPreferences> result = fixture.getProjectTableColumns();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ColumnPreferences> getScriptStepTableColumns() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptStepTableColumns_1()
        throws Exception {
        Preferences fixture = new Preferences();

        List<ColumnPreferences> result = fixture.getScriptStepTableColumns();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ColumnPreferences> getScriptsTableColumns() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptsTableColumns_1()
        throws Exception {
        Preferences fixture = new Preferences();

        List<ColumnPreferences> result = fixture.getScriptsTableColumns();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}