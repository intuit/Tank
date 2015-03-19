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

import static org.junit.Assert.*;

/**
 * The class <code>ScriptSearchFieldTest</code> contains tests for the class <code>{@link ScriptSearchField}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:33 AM
 */
public class ScriptSearchFieldTest {
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
        ScriptSearchField fixture = ScriptSearchField.comments;

        String result = fixture.getValue();

        assertEquals("comments", result);
    }
}