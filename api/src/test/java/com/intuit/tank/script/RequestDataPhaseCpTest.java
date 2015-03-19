package com.intuit.tank.script;

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

import org.junit.*;

import com.intuit.tank.script.RequestDataPhase;

import static org.junit.Assert.*;

/**
 * The class <code>RequestDataPhaseCpTest</code> contains tests for the class <code>{@link RequestDataPhase}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class RequestDataPhaseCpTest {
    /**
     * Run the String getDisplay() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetDisplay_1()
            throws Exception {
        RequestDataPhase fixture = RequestDataPhase.POST_REQUEST;

        String result = fixture.getDisplay();

        assertEquals("Post Request", result);
    }
}