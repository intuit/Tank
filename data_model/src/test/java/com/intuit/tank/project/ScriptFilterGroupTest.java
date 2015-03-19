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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;

/**
 * The class <code>ScriptFilterGroupTest</code> contains tests for the class <code>{@link ScriptFilterGroup}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptFilterGroupTest {
    /**
     * Run the ScriptFilterGroup() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptFilterGroup_1()
            throws Exception {
        ScriptFilterGroup result = new ScriptFilterGroup();
        assertNotNull(result);
    }

    /**
     * Run the void addFilter(ScriptFilter) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddFilter_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");
        ScriptFilter filter = new ScriptFilter();

        fixture.addFilter(filter);

    }

    /**
     * Run the int compareTo(ScriptFilterGroup) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("d");
        fixture.setProductName("");
        ScriptFilterGroup other = new ScriptFilterGroup();
        other.setName("");
        int result = fixture.compareTo(other);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");
        Object obj = new ScriptFilterGroup();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");
        Object obj = new ScriptFilterGroup();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the Set<ScriptFilter> getFilters() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetFilters_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");

        Set<ScriptFilter> result = fixture.getFilters();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getProductName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProductName_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");

        int result = fixture.hashCode();

        assertEquals(1311, result);
    }

    /**
     * Run the void setFilters(Set<ScriptFilter>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetFilters_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");
        Set<ScriptFilter> filters = new HashSet();

        fixture.setFilters(filters);

    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setProductName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetProductName_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        ScriptFilterGroup fixture = new ScriptFilterGroup();
        fixture.setFilters(new HashSet());
        fixture.setName("");
        fixture.setProductName("");

        String result = fixture.toString();

    }
}