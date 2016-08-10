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

import com.intuit.tank.harness.test.data.ValidationItem;

import static org.junit.Assert.*;

/**
 * The class <code>ValidationItemTest</code> contains tests for the class <code>{@link ValidationItem}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class ValidationItemTest {
    /**
     * Run the ValidationItem(String,String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testValidationItem_1()
            throws Exception {
        String name = "";
        String value = "";
        String condition = "";

        ValidationItem result = new ValidationItem(name, value, condition);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.test.data.DataItem.<clinit>(DataItem.java:7)
        assertNotNull(result);
    }

    /**
     * Run the String getCondition() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetCondition_1()
            throws Exception {
        ValidationItem fixture = new ValidationItem("", "", "");

        String result = fixture.getCondition();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.ValidationItem
        assertNotNull(result);
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
        ValidationItem fixture = new ValidationItem("", "", "");

        String result = fixture.toString();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.ValidationItem
        assertNotNull(result);
    }
}