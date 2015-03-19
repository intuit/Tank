package com.intuit.tank.common;

/*
 * #%L
 * Common
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

import com.intuit.tank.common.ScriptAssignment;

import static org.junit.Assert.*;

/**
 * The class <code>ScriptAssignmentTest</code> contains tests for the class <code>{@link ScriptAssignment}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:34 AM
 */
public class ScriptAssignmentTest {
    /**
     * Run the ScriptAssignment(String,String,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:34 AM
     */
    @Test
    public void testScriptAssignment_1()
            throws Exception {
        String variable = "";
        String assignemnt = "";
        int scriptIndex = 1;

        ScriptAssignment result = new ScriptAssignment(variable, assignemnt, scriptIndex);

        assertNotNull(result);
        assertEquals(1, result.getScriptIndex());
        assertEquals("", result.getAssignemnt());
        assertEquals("", result.getVariable());
    }

    /**
     * Run the String getAssignemnt() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:34 AM
     */
    @Test
    public void testGetAssignemnt_1()
            throws Exception {
        ScriptAssignment fixture = new ScriptAssignment("", "", 1);

        String result = fixture.getAssignemnt();

        assertEquals("", result);
    }

    /**
     * Run the int getScriptIndex() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:34 AM
     */
    @Test
    public void testGetScriptIndex_1()
            throws Exception {
        ScriptAssignment fixture = new ScriptAssignment("", "", 1);

        int result = fixture.getScriptIndex();

        assertEquals(1, result);
    }

    /**
     * Run the String getVariable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:34 AM
     */
    @Test
    public void testGetVariable_1()
            throws Exception {
        ScriptAssignment fixture = new ScriptAssignment("", "", 1);

        String result = fixture.getVariable();

        assertEquals("", result);
    }
}