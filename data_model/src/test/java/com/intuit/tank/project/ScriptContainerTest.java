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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptContainer;
import com.intuit.tank.project.ScriptStep;

/**
 * The class <code>ScriptContainerTest</code> contains tests for the class <code>{@link ScriptContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptContainerTest {
    /**
     * Run the ScriptContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptContainer_1()
        throws Exception {

        ScriptContainer result = new ScriptContainer();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(0, result.getRuntime());
        assertEquals(null, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
        assertEquals(null, result.getCreator());
        assertEquals(null, result.getProductName());
        assertEquals(null, result.getComments());
    }

    /**
     * Run the ScriptContainer(Script) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptContainer_2()
        throws Exception {
        Script script = new Script();
        script.setScriptSteps(new LinkedList());
        script.setName("");
        script.setRuntime(1);
        script.setComments("");
        script.setProductName("");

        ScriptContainer result = new ScriptContainer(script);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals(1, result.getRuntime());
        assertEquals(new Integer(0), result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
        assertEquals(null, result.getCreator());
        assertEquals("", result.getProductName());
        assertEquals("", result.getComments());
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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the Date getCreated() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetCreated_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Date result = fixture.getCreated();

        assertNotNull(result);
    }

    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        String result = fixture.getCreator();

        assertEquals("", result);
    }

    /**
     * Run the Integer getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Integer result = fixture.getId();

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
     * Run the Date getModified() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Date result = fixture.getModified();

        assertNotNull(result);
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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        int result = fixture.getRuntime();

        assertEquals(1, result);
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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        List<ScriptStep> result = fixture.getSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setCreated(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetCreated_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        Date created = new Date();

        fixture.setCreated(created);

    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        String creator = "";

        fixture.setCreator(creator);

    }

    /**
     * Run the void setId(Integer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        Integer id = new Integer(1);

        fixture.setId(id);

    }

    /**
     * Run the void setModified(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetModified_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        Date modified = new Date();

        fixture.setModified(modified);

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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        int runtime = 1;

        fixture.setRuntime(runtime);

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
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        List<ScriptStep> steps = new LinkedList();

        fixture.setSteps(steps);

    }

    /**
     * Run the Script toScript() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToScript_1()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId((Integer) null);
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Script result = fixture.toScript();

        assertNotNull(result);
        assertEquals("", result.toString());
        assertEquals("", result.getName());
        assertEquals(1, result.getRuntime());
        assertEquals(null, result.getSerializedScriptStepId());
        assertEquals("", result.getProductName());
        assertEquals("", result.getComments());
        assertEquals("", result.getCreator());
        assertEquals(0, result.getId());
    }

    /**
     * Run the Script toScript() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToScript_2()
        throws Exception {
        ScriptContainer fixture = new ScriptContainer();
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setSteps(new LinkedList());
        fixture.setCreated(new Date());
        fixture.setModified(new Date());

        Script result = fixture.toScript();

        assertNotNull(result);
        assertEquals("", result.toString());
        assertEquals("", result.getName());
        assertEquals(1, result.getRuntime());
        assertEquals(null, result.getSerializedScriptStepId());
        assertEquals("", result.getProductName());
        assertEquals("", result.getComments());
        assertEquals("", result.getCreator());
        assertEquals(1, result.getId());
    }
}