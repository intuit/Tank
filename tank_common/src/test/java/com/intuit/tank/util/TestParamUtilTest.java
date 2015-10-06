package com.intuit.tank.util;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

import static org.junit.Assert.*;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.test.TestGroups;

public class TestParamUtilTest {
    private static final long TEST_VALUE = 30000L;

    @DataProvider(name = "data")
    private Object[][] testData() {
        return new Object[][] {
                { "1.5ET", 45000L },
                { ".5ET", 15000L },
                { "3 * ET + 10", 90010L },
                { "3ET + 10", 90010L },
                { "10", 10L },
                { "4 * RT / 1000", 120L },
                { "100 * RT / 1000", 3000L },
                { "10h 3m 4S", 36184000L },
                { "(3ET + 2RT) / 2", 75000L },
                { "(ET3 + RT2) / ST", 5L },
                { "(3ET + RT2) / (2 * ST)", 3L },
                { "(3ET + RT2) / (2 * 2ST)", 1L }
        };
    }

    @DataProvider(name = "invalidData")
    private Object[][] invalidData() {
        return new Object[][] {
                { "3 * XT + 10" },
                { "" },
                { "10X" },
                { "((3ET + 2RT) / 2" },
                { "22% * 5" }
        };
    }

    @BeforeTest
    public void before() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "data")
    public void testEvaluateExpression(String expression, long expectedResult) {
        Assert.assertEquals(expectedResult,
                TestParamUtil.evaluateExpression(expression, TEST_VALUE, TEST_VALUE, TEST_VALUE));
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "data")
    public void testValidExpression(String expression, long result) {
        Assert.assertTrue(TestParamUtil.isValidExpression(expression));
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "invalidData")
    public void testInvalidExpression(String expression) {
        Assert.assertFalse(TestParamUtil.isValidExpression(expression));
    }

}
