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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>TaxFunctionsTest</code> contains tests for the class <code>{@link TaxFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class TaxFunctionsTest {
    /**
     * Run the TaxFunctions() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testTaxFunctions_1()
            throws Exception {
        TaxFunctions result = new TaxFunctions();
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
        String[] values = new String[] {};
        Variables variables = new Variables();

        String result = TaxFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
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
        String[] values = new String[] {};
        Variables variables = new Variables();

        String result = TaxFunctions.executeFunction(values, variables);

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
        String[] values = new String[] {};
        Variables variables = new Variables();

        String result = TaxFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getSsn(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetSsn_1()
            throws Exception {
        long startSsn = 700010002L;

        String result = TaxFunctions.getSsn(startSsn);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.StringIndexOutOfBoundsException: String index out of range: 9
        // at java.lang.String.substring(String.java:1907)
        // at com.intuit.tank.harness.functions.TaxFunctions.getSsn(TaxFunctions.java:74)
        assertNotNull(result);
    }

    /**
     * Run the String getSsn(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetSsn_2()
            throws Exception {
        long startSsn = 700010002L;

        String result = TaxFunctions.getSsn(startSsn);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.StringIndexOutOfBoundsException: String index out of range: 9
        // at java.lang.String.substring(String.java:1907)
        // at com.intuit.tank.harness.functions.TaxFunctions.getSsn(TaxFunctions.java:74)
        assertNotNull(result);
    }

    /**
     * Run the String getSsn(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetSsn_3()
            throws Exception {
        long startSsn = 700010002L;

        String result = TaxFunctions.getSsn(startSsn);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.StringIndexOutOfBoundsException: String index out of range: 9
        // at java.lang.String.substring(String.java:1907)
        // at com.intuit.tank.harness.functions.TaxFunctions.getSsn(TaxFunctions.java:74)
        assertNotNull(result);
    }

    @Test
    public void testValidFunction() {
        boolean result = FunctionHandler.validFunction("#function.tax.getssn");
        assertTrue(result);
    }

    @Test
    public void testValidFunctionFail() {
        boolean result = FunctionHandler.validFunction("#function.tax.getbbn");
        assertFalse(result);
        result = FunctionHandler.validFunction("#function.tax.$5932");
    }

    @Test
    public void testIsValidWithoutFunctionName() {
        assertThrows(UnsupportedOperationException.class,
                () -> TaxFunctions.isValid(new String[] { "#function", "tax", null }));
    }

    @Test
    public void testExecuteFunctionUnknownReturnsEmpty() {
        assertEquals("", TaxFunctions.executeFunction(new String[] { "#function", "tax", "getbbn" }, new Variables()));
    }

    @Test
    public void testExecuteFunctionGetSsnStartsAtVariable() throws Exception {
        // the start value is only read on the first call, so reset it to make the test independent of order
        resetLastSsn();
        Variables variables = new Variables();
        variables.addVariable("SSN_START", "12345678");

        String[] values = new String[] { "#function", "tax", "getssn" };
        // numbers shorter than nine digits are left padded with zeros
        assertEquals("012-34-5678", TaxFunctions.executeFunction(values, variables));
        assertEquals("012-34-5679", TaxFunctions.executeFunction(values, variables));
    }

    @Test
    public void testGetSsnPadsSevenDigitNumbers() throws Exception {
        resetLastSsn();
        // the lowest valid ssn has seven digits and needs two leading zeros
        assertEquals("001-01-0001", TaxFunctions.getSsn(0L));
        assertEquals("001-01-0002", TaxFunctions.getSsn(0L));
    }

    @Test
    public void testIsValidWithTooFewArgumentsIsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> TaxFunctions.isValid(new String[] { "#function", "tax" }));
    }

    @Test
    public void testGetSsnSkipsInvalidRange() throws Exception {
        resetLastSsn();
        assertEquals("699-99-9999", TaxFunctions.getSsn(699999999L));
        // 700000000 through 700010000 are not valid ssns
        assertEquals("700-01-0001", TaxFunctions.getSsn(699999999L));
    }

    private static void resetLastSsn() throws IllegalAccessException {
        FieldUtils.writeStaticField(TaxFunctions.class, "lastSSN", -1L, true);
    }
}
