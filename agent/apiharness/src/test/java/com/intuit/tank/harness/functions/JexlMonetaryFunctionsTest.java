/**
 * 
 */
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

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.test.TestGroups;

/**
 * @author rchalmela
 * 
 */
public class JexlMonetaryFunctionsTest {

    private Variables variables;

    @BeforeTest
    public void init() {
        variables = new Variables();
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testRandomPositiveNumber() {
        String random = variables.evaluate("#{monetaryFunctions.randomPositive(4)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 7);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testRandomNegativeNumber() {
        String random = variables.evaluate("#{monetaryFunctions.randomNegative(5)}");
        Assert.assertNotNull(random);
        Assert.assertTrue(random.length() == 9);
    }

}
