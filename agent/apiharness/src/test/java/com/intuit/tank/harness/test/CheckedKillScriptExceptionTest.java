package com.intuit.tank.harness.test;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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

import com.intuit.tank.harness.test.CheckedKillScriptException;

import static org.junit.Assert.*;

/**
 * The class <code>CheckedKillScriptExceptionTest</code> contains tests for the class
 * <code>{@link CheckedKillScriptException}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class CheckedKillScriptExceptionTest {
    /**
     * Run the CheckedKillScriptException(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testCheckedKillScriptException_1()
            throws Exception {
        String msg = "";

        CheckedKillScriptException result = new CheckedKillScriptException(msg);

        assertNotNull(result);
        assertEquals(null, result.getCause());
        assertEquals("com.intuit.tank.harness.test.CheckedKillScriptException: ", result.toString());
        assertEquals("", result.getMessage());
        assertEquals("", result.getLocalizedMessage());
    }
}