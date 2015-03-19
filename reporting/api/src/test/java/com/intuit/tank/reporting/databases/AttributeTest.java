package com.intuit.tank.reporting.databases;

/*
 * #%L
 * Reporting API
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

import com.intuit.tank.reporting.databases.Attribute;

import static org.junit.Assert.*;

/**
 * The class <code>AttributeTest</code> contains tests for the class <code>{@link Attribute}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:30 AM
 */
public class AttributeTest {
    /**
     * Run the Attribute() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testAttribute_1()
            throws Exception {

        Attribute result = new Attribute();

        assertNotNull(result);
        assertEquals("null = null", result.toString());
        assertEquals(null, result.getName());
        assertEquals(null, result.getValue());
    }

    /**
     * Run the Attribute(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testAttribute_2()
            throws Exception {
        String name = "";
        String value = "";

        Attribute result = new Attribute(name, value);

        assertNotNull(result);
        assertEquals(" = ", result.toString());
        assertEquals("", result.getName());
        assertEquals("", result.getValue());
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        Attribute fixture = new Attribute("", "");
        Object obj = new Attribute("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        Attribute fixture = new Attribute("", "");
        Object obj = new Attribute("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        Attribute fixture = new Attribute("", "");
        Object obj = null;

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testEquals_4()
            throws Exception {
        Attribute fixture = new Attribute("", "");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        Attribute fixture = new Attribute("", "");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        Attribute fixture = new Attribute("", "");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        Attribute fixture = new Attribute("", "");

        int result = fixture.hashCode();

        assertEquals(31213, result);
    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        Attribute fixture = new Attribute("", "");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testSetValue_1()
            throws Exception {
        Attribute fixture = new Attribute("", "");
        String value = "";

        fixture.setValue(value);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        Attribute fixture = new Attribute("", "");

        String result = fixture.toString();

        assertEquals(" = ", result);
    }
}