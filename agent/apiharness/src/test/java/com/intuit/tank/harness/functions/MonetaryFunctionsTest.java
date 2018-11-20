package com.intuit.tank.harness.functions;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.harness.functions.MonetaryFunctions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>MonetaryFunctionsTest</code> contains tests for the class <code>{@link MonetaryFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class MonetaryFunctionsTest {
    /**
     * Run the String executeFunction(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testExecuteFunction_1()
            throws Exception {
        String[] values = new String[] { null, null, null, "" };

        String result = MonetaryFunctions.executeFunction(values);

        assertEquals("", result);
    }

    /**
     * Run the String executeFunction(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testExecuteFunction_2()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };

        String result = MonetaryFunctions.executeFunction(values);

        assertEquals("", result);
    }

    /**
     * Run the String executeFunction(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testExecuteFunction_3()
            throws Exception {
        String[] values = new String[] { null, null, "" };

        String result = MonetaryFunctions.executeFunction(values);

        assertEquals("", result);
    }

    /**
     * Run the String executeFunction(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testExecuteFunction_4()
            throws Exception {
        String[] values = new String[] { null, null, null, "" };

        String result = MonetaryFunctions.executeFunction(values);

        assertEquals("", result);
    }

    /**
     * Run the String executeFunction(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testExecuteFunction_5()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };

        String result = MonetaryFunctions.executeFunction(values);

        assertEquals("", result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testIsValid_1()
            throws Exception {
        String[] values = new String[] {};

        boolean result = MonetaryFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testIsValid_2()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };

        boolean result = MonetaryFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testIsValid_3()
            throws Exception {
        String[] values = new String[] { null, null, "" };

        boolean result = MonetaryFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testIsValid_4()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };

        boolean result = MonetaryFunctions.isValid(values);

        assertEquals(false, result);
    }
}