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

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;

/**
 * The class <code>ScriptGroupStepTest</code> contains tests for the class <code>{@link ScriptGroupStep}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptGroupStepTest {
    /**
     * Run the ScriptGroupStep() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptGroupStep_1()
        throws Exception {
        ScriptGroupStep result = new ScriptGroupStep();
        assertNotNull(result);
    }

    /**
     * Run the ScriptGroupStep.ScriptGroupStepBuilder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        ScriptGroupStep.ScriptGroupStepBuilder result = ScriptGroupStep.builder();

        assertNotNull(result);
    }

    /**
     * Run the ScriptGroupStep.ScriptGroupStepBuilder builderFrom(ScriptGroupStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        ScriptGroupStep step = new ScriptGroupStep();

        ScriptGroupStep.ScriptGroupStepBuilder result = ScriptGroupStep.builderFrom(step);

        assertNotNull(result);
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
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
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
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
        Object obj = new ScriptGroupStep();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
        Object obj = new ScriptGroupStep();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the int getLoop() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetLoop_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());

        int result = fixture.getLoop();

        assertEquals(1, result);
    }

    /**
     * Run the Integer getPosition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPosition_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());

        Integer result = fixture.getPosition();

        assertNotNull(result);
        assertEquals("1", result.toString());
        assertEquals((byte) 1, result.byteValue());
        assertEquals((short) 1, result.shortValue());
        assertEquals(1, result.intValue());
        assertEquals(1L, result.longValue());
        assertEquals(1.0f, result.floatValue(), 1.0f);
        assertEquals(1.0, result.doubleValue(), 1.0);
    }

    /**
     * Run the Script getScript() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScript_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());

        Script result = fixture.getScript();

        assertNotNull(result);
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(0, result.getRuntime());
        assertEquals(null, result.getSerializedScriptStepId());
        assertEquals(null, result.getProductName());
        assertEquals(null, result.getComments());
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the ScriptGroup getScriptGroup() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptGroup_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());

        ScriptGroup result = fixture.getScriptGroup();

        assertNotNull(result);
        assertEquals(null, result.getPosition());
        assertEquals(null, result.getName());
        assertEquals(1, result.getLoop());
        assertEquals(null, result.getTestPlan());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
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
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void setLoop(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetLoop_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
        int loop = 1;

        fixture.setLoop(loop);

    }

    /**
     * Run the void setParent(ScriptGroup) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetParent_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
        ScriptGroup scriptGroup = new ScriptGroup();

        fixture.setParent(scriptGroup);

    }

    /**
     * Run the void setPosition(Integer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPosition_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
        Integer position = new Integer(1);

        fixture.setPosition(position);

    }

    /**
     * Run the void setScript(Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScript_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
        Script script = new Script();

        fixture.setScript(script);

    }

    /**
     * Run the void setScriptGroup(ScriptGroup) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScriptGroup_1()
        throws Exception {
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());
        ScriptGroup scriptGroup = new ScriptGroup();

        fixture.setScriptGroup(scriptGroup);

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
        ScriptGroupStep fixture = new ScriptGroupStep();
        fixture.setPosition(new Integer(1));
        fixture.setScriptGroup(new ScriptGroup());
        fixture.setLoop(1);
        fixture.setScript(new Script());

        String result = fixture.toString();

    }
}