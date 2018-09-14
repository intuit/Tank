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

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.Item;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ItemTest</code> contains tests for the class <code>{@link Item}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:30 AM
 */
public class ItemTest {
    /**
     * Run the Item() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testItem_1()
            throws Exception {

        Item result = new Item();

        assertNotNull(result);
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(null, result.getAttributes());
    }

    /**
     * Run the Item(String,List<Attribute>) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testItem_2()
            throws Exception {
        String name = "";
        List<Attribute> attributes = new LinkedList();

        Item result = new Item(name, attributes);

        assertNotNull(result);
        assertEquals("", result.toString());
        assertEquals("", result.getName());
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
        Item fixture = new Item("", new LinkedList());
        Object obj = new Item("", new LinkedList());

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
        Item fixture = new Item("", new LinkedList());
        Object obj = new Item("", new LinkedList());

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
        Item fixture = new Item("", new LinkedList());
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
        Item fixture = new Item("", new LinkedList());
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the List<Attribute> getAttributes() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testGetAttributes_1()
            throws Exception {
        Item fixture = new Item("", new LinkedList());

        List<Attribute> result = fixture.getAttributes();

        assertNotNull(result);
        assertEquals(0, result.size());
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
        Item fixture = new Item("", new LinkedList());

        String result = fixture.getName();

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
        Item fixture = new Item("", new LinkedList());

        int result = fixture.hashCode();

        assertEquals(0, result);
    }

    /**
     * Run the void setAttributes(List<Attribute>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testSetAttributes_1()
            throws Exception {
        Item fixture = new Item("", new LinkedList());
        List<Attribute> attributes = new LinkedList();

        fixture.setAttributes(attributes);

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
        Item fixture = new Item("", new LinkedList());
        String name = "";

        fixture.setName(name);

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
        Item fixture = new Item("", new LinkedList());

        String result = fixture.toString();

        assertEquals("", result);
    }
}