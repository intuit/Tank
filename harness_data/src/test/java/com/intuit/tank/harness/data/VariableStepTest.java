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

import com.intuit.tank.harness.data.VariableStep;

/**
 * The class <code>VariableStepTest</code> contains tests for the class <code>{@link VariableStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class VariableStepTest {
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
        VariableStep fixture = new VariableStep();
        fixture.setValue("");
        fixture.setKey("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("Set_Variable(->)", result);
    }

    /**
     * Run the String getKey() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetKey_1()
            throws Exception {
        VariableStep fixture = new VariableStep();
        fixture.setValue("");
        fixture.setKey("");
        fixture.stepIndex = 1;

        String result = fixture.getKey();

        assertEquals("", result);
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
        VariableStep fixture = new VariableStep();
        fixture.setValue("");
        fixture.setKey("");
        fixture.stepIndex = 1;

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the void setKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetKey_1()
            throws Exception {
        VariableStep fixture = new VariableStep();
        fixture.setValue("");
        fixture.setKey("");
        fixture.stepIndex = 1;
        String key = "";

        fixture.setKey(key);

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
        VariableStep fixture = new VariableStep();
        fixture.setValue("");
        fixture.setKey("");
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
        VariableStep fixture = new VariableStep();
        fixture.setValue("");
        fixture.setKey("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("VariableStep :  = ", result);
    }
}