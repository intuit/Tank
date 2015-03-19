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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.harness.functions.TaxFunctions;
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

}