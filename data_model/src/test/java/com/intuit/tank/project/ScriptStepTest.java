package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.project.ScriptStep;

/**
 * The class <code>ScriptStepTest</code> contains tests for the class <code>{@link ScriptStep}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptStepTest {
    /**
     * Run the ScriptStep() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptStep_1()
        throws Exception {
        ScriptStep result = new ScriptStep();
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep.Builder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        ScriptStep.Builder result = ScriptStep.builder();

        assertNotNull(result);
    }

    /**
     * Run the ScriptStep.Builder builderFrom(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        ScriptStep step = new ScriptStep();

        ScriptStep.Builder result = ScriptStep.builderFrom(step);

        assertNotNull(result);
    }

    /**
     * Run the int compareTo(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");
        ScriptStep o = new ScriptStep();

        int result = fixture.compareTo(o);

        assertEquals(0, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");
        Object obj = new ScriptStep();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");
        Object obj = new ScriptStep();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the String getScriptGroupName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptGroupName_1()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");

        String result = fixture.getScriptGroupName();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");

        int result = fixture.hashCode();

    }

    /**
     * Run the void setScriptGroupName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScriptGroupName_1()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");
        String scriptGroupName = "";

        fixture.setScriptGroupName(scriptGroupName);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        ScriptStep fixture = new ScriptStep();
        fixture.setScriptGroupName("");

        String result = fixture.toString();

    }
}