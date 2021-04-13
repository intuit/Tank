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

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.test.TestGroups;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumericFunctionsNGTest {

    Variables variables = new Variables();

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testIsValid() {
        // Generic Tests
        assertFalse(FunctionHandler.validFunction("#function.numeric"));
        assertFalse(FunctionHandler.validFunction("#function.numeric.bogusFunction"));

        // Random Positive Whole Number
        assertFalse(FunctionHandler.validFunction("#function.numeric.randomPositiveWhole"));
        assertTrue(FunctionHandler.validFunction("#function.numeric.randomPositiveWhole.5"));

        // Random Negative Whole Number
        assertFalse(FunctionHandler.validFunction("#function.numeric.randomNegativeWhole"));
        assertTrue(FunctionHandler.validFunction("#function.numeric.randomNegativeWhole.5"));

        // Random Positive Float Number
        assertFalse(FunctionHandler.validFunction("#function.numeric.randomPositiveFloat"));
        assertTrue(FunctionHandler.validFunction("#function.numeric.randomPositiveFloat.5.3"));

        // Random Negative Float Number
        assertFalse(FunctionHandler.validFunction("#function.numeric.randomNegativeFloat"));
        assertTrue(FunctionHandler.validFunction("#function.numeric.randomNegativeFloat.5.4"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomPositiveWhole() {
        String command = "#function.numeric.randomPositiveWhole.4";

        String random = FunctionHandler.executeFunction(command, variables);
        assertNotNull(random);
        assertEquals(random.length(), 4);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomNegativeWhole() {
        String command = "#function.numeric.randomNegativeWhole.5";

        String random = FunctionHandler.executeFunction(command, variables);
        assertNotNull(random);
        assertEquals(random.length(), 6);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomPositiveFloat() {
        String command = "#function.numeric.randomPositiveFloat.5.3";

        String random = FunctionHandler.executeFunction(command, variables);
        assertNotNull(random);
        assertEquals(random.length(), 9);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomNegativeFloat() {
        String command = "#function.numeric.randomNegativeFloat.6.4";

        String random = FunctionHandler.executeFunction(command, variables);
        assertNotNull(random);
        assertEquals(random.length(), 12);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testAdd() {
        String command = "#function.numeric.add.4.8";

        String sum = FunctionHandler.executeFunction(command, variables);
        assertNotNull(sum);
        assertEquals("12.0", sum);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testAdd3Params() {
        String command = "#function.numeric.add.4.8.13";

        String sum = FunctionHandler.executeFunction(command, variables);
        assertNotNull(sum);
        assertEquals("25.0", sum);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSubtract() {
        String command = "#function.numeric.subtract.9.3";

        String result = FunctionHandler.executeFunction(command, variables);
        assertNotNull(result);
        assertEquals("6.0", result);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testSubtract3Params() {
        String command = "#function.numeric.subtract.9.3.2";

        String result = FunctionHandler.executeFunction(command, variables);
        assertNotNull(result);
        assertEquals("4.0", result);
    }
}
