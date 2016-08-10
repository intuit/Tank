package com.intuit.tank.harness.test.data;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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

import com.intuit.tank.harness.test.data.DataItem;

import static org.junit.Assert.*;

/**
 * The class <code>DataItemTest</code> contains tests for the class <code>{@link DataItem}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class DataItemTest {
    /**
     * Run the DataItem(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testDataItem_1()
            throws Exception {
        String name = "";
        String value = "";

        DataItem result = new DataItem(name, value);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.test.data.DataItem.<clinit>(DataItem.java:7)
        assertNotNull(result);
    }

    /**
     * Run the DataItem(String,String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testDataItem_2()
            throws Exception {
        String name = "";
        String value = "";
        String action = "";

        DataItem result = new DataItem(name, value, action);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.DataItem
        assertNotNull(result);
    }

    /**
     * Run the String getAction() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetAction_1()
            throws Exception {
        DataItem fixture = new DataItem("", "", "");

        String result = fixture.getAction();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.DataItem
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        DataItem fixture = new DataItem("", "", "");

        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.DataItem
        assertNotNull(result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        DataItem fixture = new DataItem("", "", "");

        String result = fixture.getValue();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.DataItem
        assertNotNull(result);
    }

    /**
     * Run the void setValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testSetValue_1()
            throws Exception {
        DataItem fixture = new DataItem("", "", "");
        String newValue = "";

        fixture.setValue(newValue);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.DataItem
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        DataItem fixture = new DataItem("", "", "");

        String result = fixture.toString();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.DataItem
        assertNotNull(result);
    }
}