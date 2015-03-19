package com.intuit.tank.search.script;

/*
 * #%L
 * Script Search
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.search.script.SleepTimeSection;

import static org.junit.Assert.*;

/**
 * The class <code>SleepTimeSectionTest</code> contains tests for the class <code>{@link SleepTimeSection}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:33 AM
 */
public class SleepTimeSectionTest {
    /**
     * Run the String getDisplay() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testGetDisplay_1()
            throws Exception {
        SleepTimeSection fixture = SleepTimeSection.sleepTime;

        String result = fixture.getDisplay();

        assertEquals("Sleep Time", result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        SleepTimeSection fixture = SleepTimeSection.sleepTime;

        String result = fixture.getValue();

        assertEquals("sleepTime", result);
    }
}