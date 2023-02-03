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

import java.util.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>StringFunctionsTest</code> contains tests for the class <code>{@link StringFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class StringFunctionsTest {

    String[] validValues;

    @BeforeEach
    public void init() {
        validValues = new String[] {"userid", "randomalphalower", "randomalphaupper", "randomalphamixed",
                "randomnumeric", "randomspecial", "randomalphamixednumeric",
                "randomalphamixedspecial", "randomalphamixednumericspecial", "concat",
                "useridFromRange", "useridFromRangeInclude", "useridFromRangeExclude",
                "useridFromRangeWithMod", "useridFromRangeWithModExclude",
                "useridFromRangeWithModInclude", "substring"};
    }

    @Test
    public void testStringFunctions_1() {
        StringFunctions result = new StringFunctions();
        assertNotNull(result);
    }

    @Test
    public void testIsValid() {
        StringFunctions result = new StringFunctions();
        String[] values = new String[] { null, null, null, ""};
        boolean valid = StringFunctions.isValid(values);
        assertFalse(valid);

        values[3] = null;
        for (String value : validValues){
            values[2] = value;
            valid = StringFunctions.isValid(values);
            assertTrue(valid);
        }

        values = new String[] { null, null, null, null };
        valid = StringFunctions.isValid(values);
        assertFalse(valid);

        values = new String[] { null, null, "test", null };
        valid = StringFunctions.isValid(values);
        assertFalse(valid);
    }

    @Test
    public void testExecuteFunction_1() {
        String[] values = new String[] { null, null, "", null, null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_2() {
        String[] values = new String[] { null, null, "", null, null, null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_3() {
        String[] values = new String[] { null, null, "", "", "", null };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_4() {
        String[] values = new String[] { null, null, "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_5() {
        String[] values = new String[] { null, null, null, "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_6() {
        String[] values = new String[] { null, null, "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_7() {
        String[] values = new String[] { null, null, "", "", "" };
        Variables variables = new Variables();
        String addtlString = "";

        String result = StringFunctions.executeFunction(values, variables, addtlString);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_8() {
        String[] values = new String[] { null, null, null, "11", "12", ""};
        Variables variables = new Variables();
        String addtlString = "2testString";
        String result;

        for (String value : validValues){
            if (value.equals("substring")){
                break;
            }
            values[2] = value;
            values[3] = String.valueOf(Integer.parseInt(values[3]) + 10);
            values[4] = String.valueOf(Integer.parseInt(values[3]) + 10);
            String initialValue = "";
            initialValue = values[5];
            values[5] = values[5] + values[3];
            result = StringFunctions.executeFunction(values, variables, addtlString);
            assertNotEquals("", result);
            values[5] = initialValue;
        }

        values = new String[] { null, null,"substring", "test", "0"};
        result = StringFunctions.executeFunction(values, variables, addtlString);
        assertNotEquals("", result);

        values = new String[] { null, null,"substring", "test", "0", "2"};
        result = StringFunctions.executeFunction(values, variables, addtlString);
        assertNotEquals("", result);

        values = new String[] { null, null,"substring", "2", "5", "test"};
        result = StringFunctions.executeFunction(values, variables, addtlString);
        assertNotEquals("", result);
    }

    @Test
    public void testGetStack_1() {
        Integer minId = 1;
        Integer maxId = 1;
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
        Integer minId = 1;
        Integer maxId = 1;
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
        Integer minId = 1;
        Integer maxId = 1;
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
    public void testShouldInclude_1()
            throws Exception {
        String value = "";
        List<String> expressions = new LinkedList<>();
        boolean include = false;

        boolean result = StringFunctions.shouldInclude(value, expressions, include);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class
        // com.intuit.tank.harness.functions.StringFunctions
        assertTrue(result);
    }

    @Test
    public void testValidFunction() {
        boolean result = FunctionHandler.validFunction("#function.string.userid.9.MM-dd-yyyy");

        assertTrue(result);
    }
}
