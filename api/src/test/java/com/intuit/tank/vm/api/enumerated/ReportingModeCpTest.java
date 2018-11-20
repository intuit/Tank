package com.intuit.tank.vm.api.enumerated;

/*
 * #%L
 * Intuit Tank Api
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

import com.intuit.tank.vm.api.enumerated.ReportingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ReportingModeCpTest</code> contains tests for the class <code>{@link ReportingMode}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class ReportingModeCpTest {
    /**
     * Run the ReportingMode fromValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testFromValue_1()
            throws Exception {
        String s = "perf_db_results";

        ReportingMode result = ReportingMode.fromValue(s);

        assertNotNull(result);
        assertEquals("perf_db_results", result.getValue());
        assertEquals("Database", result.name());
        assertEquals("Database", result.toString());
        assertEquals(2, result.ordinal());
    }

    /**
     * Run the ReportingMode fromValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testFromValue_2()
            throws Exception {
        String s = "";

        ReportingMode result = ReportingMode.fromValue(s);

        assertNotNull(result);
        assertEquals("no_results", result.getValue());
        assertEquals("None", result.name());
        assertEquals("None", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the ReportingMode fromValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testFromValue_3()
            throws Exception {
        String s = "perf_results";

        ReportingMode result = ReportingMode.fromValue(s);

        assertNotNull(result);
        assertEquals("perf_results", result.getValue());
        assertEquals("Wily", result.name());
        assertEquals("Wily", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        ReportingMode fixture = ReportingMode.Database;

        String result = fixture.getValue();

        assertEquals("perf_db_results", result);
    }
}