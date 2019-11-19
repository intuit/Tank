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

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author rchalmela
 * 
 */
public class JexlMonetaryFunctionsTest {

    private Variables variables;

    @BeforeEach
    public void init() {
        variables = new Variables();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomPositiveNumber() {
        String random = variables.evaluate("#{monetaryFunctions.randomPositive(4)}");
        assertNotNull(random);
        assertTrue(random.length() == 7);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRandomNegativeNumber() {
        String random = variables.evaluate("#{monetaryFunctions.randomNegative(5)}");
        assertNotNull(random);
        assertTrue(random.length() == 9);
    }

}
