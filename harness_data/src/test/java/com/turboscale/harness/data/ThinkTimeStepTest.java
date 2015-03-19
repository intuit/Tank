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

import com.intuit.tank.harness.data.ThinkTimeStep;

/**
 * The class <code>ThinkTimeStepTest</code> contains tests for the class <code>{@link ThinkTimeStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class ThinkTimeStepTest {
    /**
     * Run the ThinkTimeStep() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testThinkTimeStep_1()
            throws Exception {
        ThinkTimeStep result = new ThinkTimeStep();
        assertNotNull(result);
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
        ThinkTimeStep fixture = new ThinkTimeStep();
        fixture.setMaxTime("");
        fixture.setMinTime("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("ThinkTime(, )", result);
    }

    /**
     * Run the String getMaxTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetMaxTime_1()
            throws Exception {
        ThinkTimeStep fixture = new ThinkTimeStep();
        fixture.setMaxTime("");
        fixture.setMinTime("");
        fixture.stepIndex = 1;

        String result = fixture.getMaxTime();

        assertEquals("", result);
    }

    /**
     * Run the String getMinTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetMinTime_1()
            throws Exception {
        ThinkTimeStep fixture = new ThinkTimeStep();
        fixture.setMaxTime("");
        fixture.setMinTime("");
        fixture.stepIndex = 1;

        String result = fixture.getMinTime();

        assertEquals("", result);
    }

    /**
     * Run the void setMaxTime(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetMaxTime_1()
            throws Exception {
        ThinkTimeStep fixture = new ThinkTimeStep();
        fixture.setMaxTime("");
        fixture.setMinTime("");
        fixture.stepIndex = 1;
        String maxTime = "";

        fixture.setMaxTime(maxTime);

    }

    /**
     * Run the void setMinTime(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetMinTime_1()
            throws Exception {
        ThinkTimeStep fixture = new ThinkTimeStep();
        fixture.setMaxTime("");
        fixture.setMinTime("");
        fixture.stepIndex = 1;
        String minTime = "";

        fixture.setMinTime(minTime);

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
        ThinkTimeStep fixture = new ThinkTimeStep();
        fixture.setMaxTime("");
        fixture.setMinTime("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("ThinkTimeStep : , ", result);
    }
}