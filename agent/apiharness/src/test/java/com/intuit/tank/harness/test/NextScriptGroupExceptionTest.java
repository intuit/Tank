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

import org.junit.jupiter.api.*;

import com.intuit.tank.harness.test.NextScriptGroupException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>NextScriptGroupExceptionTest</code> contains tests for the class
 * <code>{@link NextScriptGroupException}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class NextScriptGroupExceptionTest {
    /**
     * Run the NextScriptGroupException(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testNextScriptGroupException_1()
            throws Exception {
        String msg = "";

        NextScriptGroupException result = new NextScriptGroupException(msg);

        assertNotNull(result);
        assertEquals(null, result.getCause());
        assertEquals("com.intuit.tank.harness.test.NextScriptGroupException: ", result.toString());
        assertEquals("", result.getMessage());
        assertEquals("", result.getLocalizedMessage());
    }
}