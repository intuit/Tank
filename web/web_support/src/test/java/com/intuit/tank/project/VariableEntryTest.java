package com.intuit.tank.project;

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

import com.intuit.tank.project.VariableEntry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>VariableEntryTest</code> contains tests for the class <code>{@link VariableEntry}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class VariableEntryTest {
    /**
     * Run the VariableEntry() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testVariableEntry_1()
        throws Exception {

        VariableEntry result = new VariableEntry();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:30)
        assertNotNull(result);
    }

    /**
     * Run the VariableEntry(String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testVariableEntry_2()
        throws Exception {
        String key = "";
        String value = "";

        VariableEntry result = new VariableEntry(key, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
        assertNotNull(result);
    }

    /**
     * Run the int compareTo(VariableEntry) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");
        VariableEntry o = new VariableEntry("", "");

        int result = fixture.compareTo(o);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
        assertEquals(0, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");
        Object arg = null;

        boolean result = fixture.equals(arg);

        assertTrue(!result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");
        Object arg = new Object();

        boolean result = fixture.equals(arg);
        assertTrue(!result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");
        Object arg = new VariableEntry("", "");

        boolean result = fixture.equals(arg);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_4()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");
        Object arg = new VariableEntry("", "");

        boolean result = fixture.equals(arg);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
        assertTrue(result);
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");

        String result = fixture.getKey();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
        assertNotNull(result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");

        String result = fixture.getValue();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
        assertNotNull(result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");

        int result = fixture.hashCode();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");
        String key = "";

        fixture.setKey(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");
        String value = "";

        fixture.setValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        VariableEntry fixture = new VariableEntry("", "");

        String result = fixture.toString();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.VariableEntry.<init>(VariableEntry.java:40)
        assertNotNull(result);
    }
}