package com.intuit.tank.vm.common;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.common.ScriptProgressContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ScriptProgressContainerCpTest</code> contains tests for the class
 * <code>{@link ScriptProgressContainer}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class ScriptProgressContainerCpTest {
    /**
     * Run the ScriptProgressContainer() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testScriptProgressContainer_1()
            throws Exception {

        ScriptProgressContainer result = new ScriptProgressContainer();

        assertNotNull(result);
        assertEquals(null, result.getErrorMessage());
        assertEquals(0, result.getPercentComplete());
    }

    /**
     * Run the ScriptProgressContainer(int,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testScriptProgressContainer_2()
            throws Exception {
        int percentComplete = 1;
        String errorMessage = "";

        ScriptProgressContainer result = new ScriptProgressContainer(percentComplete, errorMessage);

        assertNotNull(result);
        assertEquals("", result.getErrorMessage());
        assertEquals(1, result.getPercentComplete());
    }

    /**
     * Run the String getErrorMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetErrorMessage_1()
            throws Exception {
        ScriptProgressContainer fixture = new ScriptProgressContainer(1, "");

        String result = fixture.getErrorMessage();

        assertEquals("", result);
    }

    /**
     * Run the int getPercentComplete() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetPercentComplete_1()
            throws Exception {
        ScriptProgressContainer fixture = new ScriptProgressContainer(1, "");

        int result = fixture.getPercentComplete();

        assertEquals(1, result);
    }

    /**
     * Run the void setErrorMessage(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetErrorMessage_1()
            throws Exception {
        ScriptProgressContainer fixture = new ScriptProgressContainer(1, "");
        String errorMessage = "";

        fixture.setErrorMessage(errorMessage);

    }

    /**
     * Run the void setPercentComplete(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetPercentComplete_1()
            throws Exception {
        ScriptProgressContainer fixture = new ScriptProgressContainer(1, "");
        int percentComplete = 1;

        fixture.setPercentComplete(percentComplete);

    }
}