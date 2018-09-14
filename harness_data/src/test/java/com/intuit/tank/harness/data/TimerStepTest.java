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

import com.intuit.tank.harness.data.TimerStep;

/**
 * The class <code>TimerStepTest</code> contains tests for the class <code>{@link TimerStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class TimerStepTest {
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
        TimerStep fixture = new TimerStep();
        fixture.setStart(false);
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("Stop_Timer()", result);
    }

    /**
     * Run the String getInfo() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetInfo_2()
            throws Exception {
        TimerStep fixture = new TimerStep();
        fixture.setStart(true);
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("Start_Timer()", result);
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
        TimerStep fixture = new TimerStep();
        fixture.setStart(true);
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the boolean isStart() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testIsStart_1()
            throws Exception {
        TimerStep fixture = new TimerStep();
        fixture.setStart(true);
        fixture.setValue("");
        fixture.stepIndex = 1;

        boolean result = fixture.isStart();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isStart() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testIsStart_2()
            throws Exception {
        TimerStep fixture = new TimerStep();
        fixture.setStart(false);
        fixture.setValue("");
        fixture.stepIndex = 1;

        boolean result = fixture.isStart();

        assertEquals(false, result);
    }

    /**
     * Run the void setStart(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetStart_1()
            throws Exception {
        TimerStep fixture = new TimerStep();
        fixture.setStart(true);
        fixture.setValue("");
        fixture.stepIndex = 1;
        boolean start = true;

        fixture.setStart(start);

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
        TimerStep fixture = new TimerStep();
        fixture.setStart(true);
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
        TimerStep fixture = new TimerStep();
        fixture.setStart(false);
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("TimerStepStop : ", result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testToString_2()
            throws Exception {
        TimerStep fixture = new TimerStep();
        fixture.setStart(true);
        fixture.setValue("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("TimerStepStart : ", result);
    }
}