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

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.harness.functions.StringFunctions;
import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>StringFunctionsTest</code> contains tests for the class <code>{@link StringFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class StringFunctionsTest {
    /**
     * Run the StringFunctions() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testStringFunctions_1()
            throws Exception {
        StringFunctions result = new StringFunctions();
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_1()
            throws Exception {
        String[] values = new String[] { null, null, "", null, null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class org.apache.log4j.LogManager
        // at org.apache.log4j.Logger.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.test.data.Variables.<clinit>(Variables.java:36)
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_2()
            throws Exception {
        String[] values = new String[] { null, null, "", null, null, null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_3()
            throws Exception {
        String[] values = new String[] { null, null, "", null, null, null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_4()
            throws Exception {
        String[] values = new String[] { null, null, "", null, null, null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_5()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "", null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
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
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_7()
            throws Exception {
        String[] values = new String[] { null, null, null, "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_8()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_9()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_10()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_11()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_12()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_13()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_14()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_15()
            throws Exception {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String executeFunction(String[],Variables,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testExecuteFunction_16()
            throws Exception {
        String[] values = new String[] { null, null, "", "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the Stack<Integer> getStack(Integer,Integer,String,boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetStack_1()
            throws Exception {
        Integer minId = Integer.valueOf(1);
        Integer maxId = Integer.valueOf(1);
        String exclusions = "";
        boolean include = true;

        Stack<Integer> result = StringFunctions.getStack(minId, maxId, exclusions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertNotNull(result);
    }

    /**
     * Run the Stack<Integer> getStack(Integer,Integer,String,boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetStack_2()
            throws Exception {
        Integer minId = Integer.valueOf(1);
        Integer maxId = Integer.valueOf(1);
        String exclusions = "";
        boolean include = false;

        Stack<Integer> result = StringFunctions.getStack(minId, maxId, exclusions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertNotNull(result);
    }

    /**
     * Run the Stack<Integer> getStack(Integer,Integer,String,boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetStack_3()
            throws Exception {
        Integer minId = Integer.valueOf(1);
        Integer maxId = Integer.valueOf(1);
        String exclusions = "";
        boolean include = true;

        Stack<Integer> result = StringFunctions.getStack(minId, maxId, exclusions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertNotNull(result);
    }

    /**
     * Run the Stack<Integer> getStack(Integer,Integer,String,boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetStack_4()
            throws Exception {
        Integer minId = Integer.valueOf(1);
        Integer maxId = Integer.valueOf(1);
        String exclusions = "";
        boolean include = true;

        Stack<Integer> result = StringFunctions.getStack(minId, maxId, exclusions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertNotNull(result);
    }

    /**
     * Run the Stack<Integer> getStack(Integer,Integer,String,boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetStack_5()
            throws Exception {
        Integer minId = new Integer(1);
        Integer maxId = new Integer(1);
        String exclusions = "";
        boolean include = true;

        Stack<Integer> result = StringFunctions.getStack(minId, maxId, exclusions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertNotNull(result);
    }

    /**
     * Run the boolean shouldInclude(String,List<String>,boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testShouldInclude_2()
            throws Exception {
        String value = "";
        List<String> expressions = new LinkedList();
        boolean include = false;

        boolean result = StringFunctions.shouldInclude(value, expressions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertTrue(result);
    }

    /**
     * Run the boolean shouldInclude(String,List<String>,boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testShouldInclude_4()
            throws Exception {
        String value = "";
        List<String> expressions = new LinkedList();
        boolean include = false;

        boolean result = StringFunctions.shouldInclude(value, expressions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertTrue(result);
    }
}