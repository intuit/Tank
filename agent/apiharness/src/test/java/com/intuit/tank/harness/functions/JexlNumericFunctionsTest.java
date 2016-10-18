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

import org.apache.commons.lang3.math.NumberUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.test.TestGroups;

public class JexlNumericFunctionsTest {

    private Variables variables;

    @BeforeTest
    public void init() {
        variables = new Variables();
        variables.addVariable("two_int", "2");
        variables.addVariable("three_float", "3.0");
        variables.addVariable("four_float", "4.0");
        variables.addVariable("authId", "10");
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testRandomPositiveWhole() {
        String random = variables.evaluate("#{numericFunctions.randomPositiveWhole(4)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 4);
        random = variables.evaluate("#{numericFunctions.randomPositiveWhole(two_int)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 2);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testRandomNegativeWhole() {
        String random = variables.evaluate("#{numericFunctions.randomNegativeWhole(5)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 6);
        random = variables.evaluate("#{numericFunctions.randomNegativeWhole(two_int)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 3);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testRandomPositiveFloat() {
        String random = variables.evaluate("#{numericFunctions.randomPositiveFloat(5, 3)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 9);
        random = variables.evaluate("#{numericFunctions.randomPositiveFloat(two_int, three_float)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 6);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testRandomNegativeFloat() {
        String random = variables.evaluate("#{numericFunctions.randomNegativeFloat(6, 4)}");
        Assert.assertNotNull(random);
        Assert.assertEquals(random.length(), 12);
        random = variables.evaluate("#{numericFunctions.randomNegativeFloat(two_int, three_float)}");
        Assert.assertNotNull(random);
        Assert.assertEquals(random.length(), 7);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testAdd() {
        String sum = variables.evaluate("#{numericFunctions.add(4, 8)}");
        Assert.assertNotNull(sum);
        Assert.assertEquals("12.0", sum);
        sum = variables.evaluate("#{numericFunctions.add(two_int, three_float)}");
        Assert.assertNotNull(sum);
        Assert.assertEquals("5.0", sum);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testAdd3Params() {
        String sum = variables.evaluate("#{numericFunctions.add(4, 8, 13)}");
        Assert.assertNotNull(sum);
        Assert.assertEquals("25.0", sum);
        sum = variables.evaluate("#{numericFunctions.add(two_int, three_float, 13)}");
        Assert.assertNotNull(sum);
        Assert.assertEquals("18.0", sum);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testSubtract() {
        String result = variables.evaluate("#{numericFunctions.subtract(9, 3)}");
        Assert.assertNotNull(result);
        Assert.assertEquals("6.0", result);
        result = variables.evaluate("#{numericFunctions.subtract(two_int, three_float)}");
        Assert.assertNotNull(result);
        Assert.assertEquals("-1.0", result);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testSubtract3Params() {
        String result = variables.evaluate("#{numericFunctions.subtract(9, 3, 2)}");
        Assert.assertNotNull(result);
        Assert.assertEquals("4.0", result);
        result = variables.evaluate("#{numericFunctions.subtract(9, two_int, three_float)}");
        Assert.assertNotNull(result);
        Assert.assertEquals("4.0", result);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testToInt() {
        String result = variables.evaluate("#{numericFunctions.toInt(numericFunctions.add(authId, 2))}");
        Assert.assertNotNull(result);
        Assert.assertEquals("12", result);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testRandom() {
        String result = variables.evaluate("#{numericFunctions.random(two_int, four_float)}");
        Assert.assertNotNull(result);
        Assert.assertTrue(NumberUtils.createNumber(result).doubleValue() < 4D
                && NumberUtils.createNumber(result).doubleValue() >= 2D);
        result = variables.evaluate("#{numericFunctions.random(four_float)}");
        Assert.assertNotNull(result);
        Assert.assertTrue(NumberUtils.createNumber(result).doubleValue() < 4D
                && NumberUtils.createNumber(result).doubleValue() >= 0D);
    }
}