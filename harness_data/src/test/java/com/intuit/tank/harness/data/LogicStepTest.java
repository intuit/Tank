package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
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

import com.intuit.tank.harness.data.LogicStep;

/**
 * The class <code>LogicStepTest</code> contains tests for the class <code>{@link LogicStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class LogicStepTest {
    /**
     * Run the LogicStep() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testLogicStep_1()
            throws Exception {
        LogicStep result = new LogicStep();
        assertNotNull(result);
    }

    /**
     * Run the String getInfo() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetInfo_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("Logic Step: ", result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getOnFail() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetOnFail_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getOnFail();

        assertEquals("", result);
    }

    /**
     * Run the String getScript() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetScript_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getScript();

        assertEquals("", result);
    }

    /**
     * Run the String getScriptGroupName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetScriptGroupName_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.getScriptGroupName();

        assertEquals("", result);
    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setOnFail(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetOnFail_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String onFail = "";

        fixture.setOnFail(onFail);

    }

    /**
     * Run the void setScript(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetScript_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String script = "";

        fixture.setScript(script);

    }

    /**
     * Run the void setScriptGroupName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetScriptGroupName_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;
        String scriptGroupName = "";

        fixture.setScriptGroupName(scriptGroupName);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        LogicStep fixture = new LogicStep();
        fixture.setScript("");
        fixture.setName("");
        fixture.setOnFail("");
        fixture.setScriptGroupName("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("LogicStep : ", result);
    }
}