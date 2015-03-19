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

import com.intuit.tank.search.script.RequestStepSection;

import static org.junit.Assert.*;

/**
 * The class <code>RequestStepSectionTest</code> contains tests for the class <code>{@link RequestStepSection}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:33 AM
 */
public class RequestStepSectionTest {
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
        RequestStepSection fixture = RequestStepSection.host;

        String result = fixture.getDisplay();

        assertEquals("Host", result);
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
        RequestStepSection fixture = RequestStepSection.host;

        String result = fixture.getValue();

        assertEquals("host", result);
    }
}