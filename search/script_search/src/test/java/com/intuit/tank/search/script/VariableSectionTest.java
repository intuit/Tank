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

import com.intuit.tank.search.script.VariableSection;

import static org.junit.Assert.*;

/**
 * The class <code>VariableSectionTest</code> contains tests for the class <code>{@link VariableSection}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:34 AM
 */
public class VariableSectionTest {
    /**
     * Run the String getDisplay() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:34 AM
     */
    @Test
    public void testGetDisplay_1()
            throws Exception {
        VariableSection fixture = VariableSection.variableKey;

        String result = fixture.getDisplay();

        assertEquals("Key", result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:34 AM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        VariableSection fixture = VariableSection.variableKey;

        String result = fixture.getValue();

        assertEquals("variableKey", result);
    }
}