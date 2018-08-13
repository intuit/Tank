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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.SleepTimeStep;

/**
 * The class <code>SleepTimeStepTest</code> contains tests for the class <code>{@link SleepTimeStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class SleepTimeStepTest {
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
        SleepTimeStep fixture = new SleepTimeStep();
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("Sleep()", result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        SleepTimeStep fixture = new SleepTimeStep();
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the void setValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetValue_1()
            throws Exception {
        SleepTimeStep fixture = new SleepTimeStep();
        fixture.setValue("");
        fixture.stepIndex = 1;
        String value = "";

        fixture.setValue(value);

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
        SleepTimeStep fixture = new SleepTimeStep();
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("SleepTimeStep : ", result);
    }
}