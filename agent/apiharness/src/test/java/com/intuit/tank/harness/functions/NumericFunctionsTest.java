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

import org.apache.commons.lang.math.NumberUtils;
import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.harness.functions.NumericFunctions;
import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>NumericFunctionsTest</code> contains tests for the class <code>{@link NumericFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class NumericFunctionsTest {
    /**
     * Run the NumericFunctions() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testNumericFunctions_1()
            throws Exception {
        NumericFunctions result = new NumericFunctions();
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_1()
            throws Exception {
        String[] values = new String[] { null, null, null, "", "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.Logger.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.test.data.Variables.<clinit>(Variables.java:36)
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_2()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_3()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_4()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_5()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_6()
            throws Exception {
        String[] values = new String[] { null, null, "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_7()
            throws Exception {
        String[] values = new String[] { null, null, "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_8()
            throws Exception {
        String[] values = new String[] { null, null, "" };
        Variables variables = new Variables();

        String result = NumericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_1()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_2()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_3()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_4()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_5()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_6()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_7()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_8()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isValid(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsValid_9()
            throws Exception {
        String[] values = new String[] { null, null, "" };

        boolean result = NumericFunctions.isValid(values);

        assertEquals(false, result);
    }

    /**
     * Run the String randomNegativeFloat(int,int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testRandomNegativeFloat_1()
            throws Exception {
        int whole = 1;
        int decimal = 1;

        String result = NumericFunctions.randomNegativeFloat(whole, decimal);

        assertTrue(NumberUtils.isNumber(result));
        assertTrue(NumberUtils.toDouble(result) < 0);
    }

    /**
     * Run the String randomNegativeWhole(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testRandomNegativeWhole_1()
            throws Exception {
        int length = 1;

        String result = NumericFunctions.randomNegativeWhole(length);

        assertTrue(NumberUtils.isNumber(result));
        assertTrue(NumberUtils.toDouble(result) < 0);
    }

    /**
     * Run the String randomPositiveFloat(int,int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testRandomPositiveFloat_1()
            throws Exception {
        int whole = 1;
        int decimal = 1;

        String result = NumericFunctions.randomPositiveFloat(whole, decimal);

        assertTrue(NumberUtils.isNumber(result));
    }

    /**
     * Run the String randomPositiveFloat(int,int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testRandomPositiveFloat_2()
            throws Exception {
        int whole = 1;
        int decimal = 0;

        String result = NumericFunctions.randomPositiveFloat(whole, decimal);

        assertTrue(NumberUtils.isNumber(result));
    }

    /**
     * Run the String randomPositiveWhole(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testRandomPositiveWhole_1()
            throws Exception {
        int length = 0;

        String result = NumericFunctions.randomPositiveWhole(length);

        assertEquals("", result);
    }

    /**
     * Run the String randomPositiveWhole(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testRandomPositiveWhole_2()
            throws Exception {
        int length = 1;

        String result = NumericFunctions.randomPositiveWhole(length);

        assertTrue(NumberUtils.isNumber(result));
    }

    /**
     * Run the String randomPositiveWhole(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testRandomPositiveWhole_3()
            throws Exception {
        int length = 1;

        String result = NumericFunctions.randomPositiveWhole(length);

        assertTrue(NumberUtils.isNumber(result));
    }
}