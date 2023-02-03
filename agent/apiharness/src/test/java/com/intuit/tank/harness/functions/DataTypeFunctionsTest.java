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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>DataTypeFunctionsTest</code> contains tests for the class <code>{@link DataTypeFunctions}</code>.
 *
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class DataTypeFunctionsTest {
    String[] values;
    Map<String, String> ValidValues;

    @BeforeEach
    public void init() {
        values = new String[] { null, null, null, null };
        ValidValues = new HashMap<>();
        ValidValues.put("BYTE_Max", String.valueOf(Byte.MAX_VALUE));
        ValidValues.put("BYTE_Min", String.valueOf(Byte.MIN_VALUE));
        ValidValues.put("BYTE_Max_Plus", String.valueOf(Byte.MAX_VALUE + 1));
        ValidValues.put("BYTE_Min_Minus", String.valueOf(Byte.MIN_VALUE - 1));
        ValidValues.put("SHORT_Max", String.valueOf(Short.MAX_VALUE));
        ValidValues.put("SHORT_Min", String.valueOf(Short.MIN_VALUE));
        ValidValues.put("SHORT_Max_Plus", String.valueOf(Short.MAX_VALUE + 1));
        ValidValues.put("SHORT_Min_Minus", String.valueOf(Short.MIN_VALUE - 1));
        ValidValues.put("INT_Max", String.valueOf(Integer.MAX_VALUE));
        ValidValues.put("INT_Min", String.valueOf(Integer.MIN_VALUE));
        ValidValues.put("INT_Max_Plus", String.valueOf(Integer.MAX_VALUE + 1));
        ValidValues.put("INT_Min_Minus", String.valueOf(Integer.MIN_VALUE - 1));
        ValidValues.put("LONG_Max", String.valueOf(Long.MAX_VALUE));
        ValidValues.put("LONG_Min", String.valueOf(Long.MIN_VALUE));
        ValidValues.put("LONG_Max_Plus", String.valueOf("9223372036854775808"));
        ValidValues.put("LONG_Min_Minus", String.valueOf("-9223372036854775809"));
    }

    @Test
    public void testExecuteFunction_1() {
        String result = DataTypeFunctions.executeFunction(values);
        assertEquals("", result);
    }

    @Test
    public void testExecuteFunction_2() {
        values[2] = "";
        String result = DataTypeFunctions.executeFunction(values);
        assertEquals("", result);
    }

    @Test
    public void testExecuteFunction_3() {
        Set<String> keys = ValidValues.keySet();
        for (String value : keys){
            values[2] = value;
            String result = DataTypeFunctions.executeFunction(values);
            assertEquals(ValidValues.get(value), result);
        }
    }

    @Test
    public void testIsValid_1() {
        values = new String[] {};
        boolean result = DataTypeFunctions.isValid(values);
        assertFalse(result);
    }

    @Test
    public void testIsValid_2() {
        values[3] = "";
        boolean result = DataTypeFunctions.isValid(values);
        assertFalse(result);
    }

    @Test
    public void testIsValid_3() {
        values[2] = "testValue";
        boolean result = DataTypeFunctions.isValid(values);
        assertFalse(result);
    }

    @Test
    public void testIsValid_4() {
        Set<String> keys = ValidValues.keySet();
        for (String value : keys){
            values[2] = value;
            boolean result = DataTypeFunctions.isValid(values);
            assertTrue(result);
        }
    }

    @Test
    public void testValidFunction() {
        boolean result = FunctionHandler.validFunction("#function.datatype.BYTE_Max");
        assertTrue(result);
    }
}
