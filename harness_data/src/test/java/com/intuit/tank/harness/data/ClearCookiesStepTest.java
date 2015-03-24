package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.harness.data.ClearCookiesStep;

/**
 * The class <code>ClearCookiesStepTest</code> contains tests for the class <code>{@link ClearCookiesStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class ClearCookiesStepTest {
    /**
     * Run the ClearCookiesStep() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testClearCookiesStep_1()
            throws Exception {
        ClearCookiesStep result = new ClearCookiesStep();
        assertNotNull(result);
    }

    /**
     * Run the String getDummy() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetDummy_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.setDummy("");
        fixture.stepIndex = 1;

        String result = fixture.getDummy();

        assertEquals("", result);
    }

    /**
     * Run the String getInfo() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetInfo_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.setDummy("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("Clear HTTP Session", result);
    }

    /**
     * Run the void setDummy(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetDummy_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.setDummy("");
        fixture.stepIndex = 1;
        String dummyvalue = "";

        fixture.setDummy(dummyvalue);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.setDummy("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("ClearCookiesStep", result);
    }
}