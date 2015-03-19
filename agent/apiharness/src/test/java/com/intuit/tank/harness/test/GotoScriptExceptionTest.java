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

import com.intuit.tank.harness.test.GotoScriptException;

import static org.junit.Assert.*;

/**
 * The class <code>GotoScriptExceptionTest</code> contains tests for the class <code>{@link GotoScriptException}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class GotoScriptExceptionTest {
    /**
     * Run the GotoScriptException(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGotoScriptException_1()
            throws Exception {
        String msg = "";
        String gotoTarget = "";

        GotoScriptException result = new GotoScriptException(msg, gotoTarget);

        assertNotNull(result);
        assertEquals("", result.getGotoTarget());
        assertEquals(null, result.getCause());
        assertEquals("com.intuit.tank.harness.test.GotoScriptException: ", result.toString());
        assertEquals("", result.getMessage());
        assertEquals("", result.getLocalizedMessage());
    }

    /**
     * Run the String getGotoTarget() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetGotoTarget_1()
            throws Exception {
        GotoScriptException fixture = new GotoScriptException("", "");
        fixture.addSuppressed(new Throwable());

        String result = fixture.getGotoTarget();

        assertEquals("", result);
    }
}