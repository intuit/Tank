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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDVariable;

/**
 * The class <code>HDScriptGroupTest</code> contains tests for the class <code>{@link HDScriptGroup}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDScriptGroupTest {
    /**
     * Run the HDScriptGroup() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDScriptGroup_1()
            throws Exception {
        HDScriptGroup result = new HDScriptGroup();
        assertNotNull(result);
    }

    /**
     * Run the String getDescription() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetDescription_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");

        String result = fixture.getDescription();

        assertEquals("", result);
    }

    /**
     * Run the List<HDScript> getGroupSteps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetGroupSteps_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");

        List<HDScript> result = fixture.getGroupSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the int getLoop() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetLoop_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");

        int result = fixture.getLoop();

        assertEquals(1, result);
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
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the HDTestPlan getParent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetParent_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");

        HDTestPlan result = fixture.getParent();

        assertNotNull(result);
        assertEquals("null (100%)", result.toString());
        assertEquals(null, result.getVariables());
        assertEquals(100, result.getUserPercentage());
        assertEquals(null, result.getTestPlanName());
    }

    /**
     * Run the HDVariable getVariable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetVariable_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");

        HDVariable result = fixture.getVariable();

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
    }

    /**
     * Run the void setDescription(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetDescription_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");
        String description = "";

        fixture.setDescription(description);

    }

    /**
     * Run the void setLoop(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetLoop_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");
        int loop = 1;

        fixture.setLoop(loop);

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
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setParent(HDTestPlan) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetParent_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");
        HDTestPlan parent = new HDTestPlan();

        fixture.setParent(parent);

    }

    /**
     * Run the void setVariable(HDVariable) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetVariable_1()
            throws Exception {
        HDScriptGroup fixture = new HDScriptGroup();
        fixture.setVariable(new HDVariable());
        fixture.setLoop(1);
        fixture.setParent(new HDTestPlan());
        fixture.setName("");
        fixture.setDescription("");
        HDVariable variable = new HDVariable();

        fixture.setVariable(variable);

    }
}