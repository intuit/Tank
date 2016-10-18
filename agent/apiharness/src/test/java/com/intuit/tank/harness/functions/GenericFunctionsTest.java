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

import com.intuit.tank.harness.functions.GenericFunctions;
import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>GenericFunctionsTest</code> contains tests for the class <code>{@link GenericFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class GenericFunctionsTest {
    /**
     * Run the GenericFunctions() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGenericFunctions_1()
            throws Exception {
        GenericFunctions result = new GenericFunctions();
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
        String[] values = new String[] { null, null, null, "0" };
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class org.apache.log4j.LogManager
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
        String[] values = new String[] { null, null, "" };
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);

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

        String result = GenericFunctions.executeFunction(values, variables);

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
        String[] values = new String[] { null, null, "" };
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);

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
        String[] values = new String[] { null, null, null, "" };
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

   

}