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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.SerializedScriptStep;

/**
 * The class <code>ScriptTest</code> contains tests for the class <code>{@link Script}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptTest {
    /**
     * Run the Script() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScript_1()
        throws Exception {
        Script result = new Script();
        assertNotNull(result);
    }

    /**
     * Run the void addStep(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddStep_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        ScriptStep step = new ScriptStep();

        fixture.addStep(step);

    }

    /**
     * Run the Script.Builder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        Script.Builder result = Script.builder();

        assertNotNull(result);
    }

    /**
     * Run the Script.Builder builderFrom(Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        Script script = new Script();

        Script.Builder result = Script.builderFrom(script);

        assertNotNull(result);
    }

    /**
     * Run the int compareTo(Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        Script o = new Script();
        o.setName("");

        int result = fixture.compareTo(o);

        assertEquals(0, result);
    }

    /**
     * Run the int compareTo(Script) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_2()
        throws Exception {
        Script fixture = new Script();
        fixture.setName((String) null);
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        Script o = new Script();
        o.setName((String) null);

        int result = fixture.compareTo(o);

        assertEquals(0, result);
    }

    /**
     * Run the List<ScriptStep> deserializeBlob(SerializedScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testDeserializeBlob_1()
        throws Exception {
        SerializedScriptStep serializedScriptStep = new SerializedScriptStep();
        serializedScriptStep.setSerialzedData(new SerialBlob(new byte[] {}));
    }

    /**
     * Run the List<ScriptStep> deserializeBlob(SerializedScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testDeserializeBlob_2()
        throws Exception {
        SerializedScriptStep serializedScriptStep = null;

        List<ScriptStep> result = Script.deserializeBlob(serializedScriptStep);

        assertEquals(null, result);
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
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
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
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        Object obj = new Script();

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
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        Object obj = new Script();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        String result = fixture.getComments();

        assertEquals("", result);
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
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the int getRuntime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRuntime_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        int result = fixture.getRuntime();

        assertEquals(1, result);
    }

    /**
     * Run the List<ScriptStep> getScriptSteps() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptSteps_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        List<ScriptStep> result = fixture.getScriptSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Integer getSerializedScriptStepId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSerializedScriptStepId_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        Integer result = fixture.getSerializedScriptStepId();

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
     * Run the List<ScriptStep> getSteps() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSteps_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        List<ScriptStep> result = fixture.getSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
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
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        String comments = "";

        fixture.setComments(comments);

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
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the void setRuntime(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRuntime_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        int runtime = 1;

        fixture.setRuntime(runtime);

    }

    /**
     * Run the void setScriptSteps(List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScriptSteps_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        List<ScriptStep> steps = new LinkedList();

        fixture.setScriptSteps(steps);

    }

    /**
     * Run the void setSerializedScriptStepId(Integer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSerializedScriptStepId_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        Integer serializedScriptStepId = new Integer(1);

        fixture.setSerializedScriptStepId(serializedScriptStepId);

    }

    /**
     * Run the void setSerializedSteps(SerializedScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSerializedSteps_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        SerializedScriptStep serializedSteps = new SerializedScriptStep();

        fixture.setSerializedSteps(serializedSteps);

    }

    /**
     * Run the void setSteps(List<ScriptStep>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSteps_1()
        throws Exception {
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);
        List<ScriptStep> steps = new LinkedList();

        fixture.setSteps(steps);

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
        Script fixture = new Script();
        fixture.setName("");
        fixture.setComments("");
        fixture.setSteps(new LinkedList());
        fixture.setProductName("");
        fixture.setSerializedScriptStepId(new Integer(1));
        fixture.setRuntime(1);

        String result = fixture.toString();

        assertEquals("", result);
    }
}