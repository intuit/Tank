package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import com.intuit.tank.harness.StopBehavior;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>StopBehaviorCpTest</code> contains tests for the class <code>{@link StopBehavior}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class StopBehaviorCpTest {
    /**
     * Run the StopBehavior fromString(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testFromString_1()
            throws Exception {
        String stopBehavior = "";

        StopBehavior result = StopBehavior.fromString(stopBehavior);

        assertNotNull(result);
        assertEquals("Stop after current script group completes.", result.getDescription());
        assertEquals("Script Group", result.getDisplay());
        assertEquals("END_OF_SCRIPT_GROUP", result.name());
        assertEquals("END_OF_SCRIPT_GROUP", result.toString());
        assertEquals(2, result.ordinal());
    }

    /**
     * Run the String getDescription() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDescription_1()
            throws Exception {
        StopBehavior fixture = StopBehavior.END_OF_SCRIPT;

        String result = fixture.getDescription();

        assertEquals("Stop after current script completes.", result);
    }

    /**
     * Run the String getDisplay() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDisplay_1()
            throws Exception {
        StopBehavior fixture = StopBehavior.END_OF_SCRIPT;

        String result = fixture.getDisplay();

        assertEquals("Script", result);
    }
}