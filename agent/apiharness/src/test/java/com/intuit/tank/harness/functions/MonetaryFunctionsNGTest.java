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

import junit.framework.TestCase;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MonetaryFunctionsNGTest {
    Variables variables = new Variables();

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testIsValid() {
        // Generic Tests
        TestCase.assertFalse(FunctionHandler.validFunction("#function.monetary"));
        TestCase.assertFalse(FunctionHandler.validFunction("#function.monetary.bogusFunction"));

        TestCase.assertTrue(FunctionHandler.validFunction("#function.monetary.randomPositive.5"));

        // Random Negative Whole Number
        TestCase.assertFalse(FunctionHandler.validFunction("#function.monetary.randomNegative"));
        TestCase.assertTrue(FunctionHandler.validFunction("#function.monetary.randomNegative.5"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomPositiveNumber() {
        String command = "#function.monetary.randomPositive.4";

        String random = FunctionHandler.executeFunction(command, variables);
        TestCase.assertNotNull(random);
        TestCase.assertTrue(random.length() == 7);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomNegativeNumber() {
        String command = "#function.monetary.randomNegative.5";

        String random = FunctionHandler.executeFunction(command, variables);
        TestCase.assertNotNull(random);
        TestCase.assertTrue(random.length() == 9);
    }
}
