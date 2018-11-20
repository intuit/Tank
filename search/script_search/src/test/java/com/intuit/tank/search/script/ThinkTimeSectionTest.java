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

import org.junit.jupiter.api.*;

import com.intuit.tank.search.script.ThinkTimeSection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ThinkTimeSectionTest</code> contains tests for the class <code>{@link ThinkTimeSection}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:33 AM
 */
public class ThinkTimeSectionTest {
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
        ThinkTimeSection fixture = ThinkTimeSection.maxTime;

        String result = fixture.getDisplay();

        assertEquals("Max Time", result);
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
        ThinkTimeSection fixture = ThinkTimeSection.maxTime;

        String result = fixture.getValue();

        assertEquals("maxTime", result);
    }
}