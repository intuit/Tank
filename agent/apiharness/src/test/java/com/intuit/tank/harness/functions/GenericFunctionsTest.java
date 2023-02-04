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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>GenericFunctionsTest</code> contains tests for the class <code>{@link GenericFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class GenericFunctionsTest {

    @Test
    public void testGenericFunctions_1() {
        GenericFunctions result = new GenericFunctions();
        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_1() {
        String[] values = new String[] { null, null, null};
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_2() {
        String[] values = new String[] { null, null, "getfiledata", "testfile", "1"};
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);
        assertEquals("", result);
    }

    @Test
    public void testExecuteFunction_3() {
        String[] values = new String[] { null, null, "getcsv", "1"};
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);
        assertEquals("", result);
    }
}
