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

import com.intuit.tank.search.script.ScriptSearchField;
import com.intuit.tank.search.script.ScriptStepSection;

import static org.junit.Assert.*;

/**
 * The class <code>ScriptStepSectionTest</code> contains tests for the class <code>{@link ScriptStepSection}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:34 AM
 */
public class ScriptStepSectionTest {
    /**
     * Run the ScriptSearchField getField() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:34 AM
     */
    @Test
    public void testGetField_1()
            throws Exception {
        ScriptStepSection fixture = ScriptStepSection.all;

        ScriptSearchField result = fixture.getField();

        assertNotNull(result);
        assertEquals("search", result.getValue());
        assertEquals("search", result.name());
        assertEquals("search", result.toString());
        assertEquals(21, result.ordinal());
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
        ScriptStepSection fixture = ScriptStepSection.all;

        String result = fixture.getValue();

        assertEquals("All", result);
    }
}