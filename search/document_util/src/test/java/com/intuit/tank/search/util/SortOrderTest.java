package com.intuit.tank.search.util;

/*
 * #%L
 * DocumentUtil
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.search.util.SortOrder;

import static org.junit.Assert.*;

/**
 * The class <code>SortOrderTest</code> contains tests for the class <code>{@link SortOrder}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:27 AM
 */
public class SortOrderTest {
    /**
     * Run the SortOrder(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testSortOrder_1()
            throws Exception {
        String field = "";

        SortOrder result = new SortOrder(field);

        assertNotNull(result);
        assertEquals("ascending", result.toString());
        assertEquals("", result.getField());
        assertEquals(false, result.isDescending());
    }

    /**
     * Run the SortOrder(String,boolean) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testSortOrder_2()
            throws Exception {
        String field = "";
        boolean descending = true;

        SortOrder result = new SortOrder(field, descending);

        assertNotNull(result);
        assertEquals("descending", result.toString());
        assertEquals("", result.getField());
        assertEquals(true, result.isDescending());
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);
        Object obj = new SortOrder("", true);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);
        Object obj = new SortOrder("", true);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_4()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);
        Object obj = new SortOrder("", true);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getField() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testGetField_1()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);

        String result = fixture.getField();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);

        int result = fixture.hashCode();

        assertEquals(735, result);
    }

    /**
     * Run the boolean isDescending() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testIsDescending_1()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);

        boolean result = fixture.isDescending();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isDescending() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testIsDescending_2()
            throws Exception {
        SortOrder fixture = new SortOrder("", false);

        boolean result = fixture.isDescending();

        assertEquals(false, result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        SortOrder fixture = new SortOrder("", false);

        String result = fixture.toString();

        assertEquals("ascending", result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testToString_2()
            throws Exception {
        SortOrder fixture = new SortOrder("", true);

        String result = fixture.toString();

        assertEquals("descending", result);
    }
}