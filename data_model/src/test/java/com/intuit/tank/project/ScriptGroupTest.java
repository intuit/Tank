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

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;

/**
 * The class <code>ScriptGroupTest</code> contains tests for the class <code>{@link ScriptGroup}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptGroupTest {
    /**
     * Run the ScriptGroup() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptGroup_1()
        throws Exception {
        ScriptGroup result = new ScriptGroup();
        assertNotNull(result);
    }

    /**
     * Run the void addScriptGroupStep(ScriptGroupStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddScriptGroupStep_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        ScriptGroupStep step = new ScriptGroupStep();

        fixture.addScriptGroupStep(step);

    }

    /**
     * Run the ScriptGroup.Builder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        ScriptGroup.Builder result = ScriptGroup.builder();

        assertNotNull(result);
    }

    /**
     * Run the ScriptGroup.Builder builderFrom(ScriptGroup) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        ScriptGroup group = new ScriptGroup();

        ScriptGroup.Builder result = ScriptGroup.builderFrom(group);

        assertNotNull(result);
    }

    /**
     * Run the int compareTo(ScriptGroup) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        ScriptGroup o = null;

        int result = fixture.compareTo(o);

        assertEquals(1, result);
    }

    /**
     * Run the int compareTo(ScriptGroup) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_2()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        ScriptGroup o = new ScriptGroup();
        o.setPosition(new Integer(1));

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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        Object obj = new ScriptGroup();

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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        Object obj = new ScriptGroup();

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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");

        int result = fixture.getLoop();

        assertEquals(1, result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");

        String result = fixture.getName();

        assertEquals("", result);
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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");

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
     * Run the List<ScriptGroupStep> getScriptGroupSteps() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptGroupSteps_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");

        List<ScriptGroupStep> result = fixture.getScriptGroupSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the TestPlan getTestPlan() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTestPlan_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");

        TestPlan result = fixture.getTestPlan();

        assertNotNull(result);
        assertEquals(null, result.getPosition());
        assertEquals(null, result.getName());
        assertEquals(100, result.getUserPercentage());
        assertEquals(null, result.getWorkload());
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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");

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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        int loop = 1;

        fixture.setLoop(loop);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setParent(TestPlan) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetParent_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        TestPlan w = new TestPlan();

        fixture.setParent(w);

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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        Integer position = new Integer(1);

        fixture.setPosition(position);

    }

    /**
     * Run the void setScriptGroupSteps(List<ScriptGroupStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScriptGroupSteps_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        List<ScriptGroupStep> scripts = new LinkedList();

        fixture.setScriptGroupSteps(scripts);

    }

    /**
     * Run the void setTestPlan(TestPlan) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetTestPlan_1()
        throws Exception {
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");
        TestPlan testPlan = new TestPlan();

        fixture.setTestPlan(testPlan);

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
        ScriptGroup fixture = new ScriptGroup();
        fixture.setLoop(1);
        fixture.setScriptGroupSteps(new LinkedList());
        fixture.setPosition(new Integer(1));
        fixture.setTestPlan(new TestPlan());
        fixture.setName("");

        String result = fixture.toString();

    }
}