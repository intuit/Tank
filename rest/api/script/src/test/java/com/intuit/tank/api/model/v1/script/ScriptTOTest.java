package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.script.ScriptStepTO;
import com.intuit.tank.api.model.v1.script.ScriptTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ScriptTOTest</code> contains tests for the class <code>{@link ScriptTO}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class ScriptTOTest {
    /**
     * Run the ScriptTO() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptTO_1()
        throws Exception {

        ScriptTO result = new ScriptTO();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(0, result.getRuntime());
        assertEquals(null, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getProductName());
        assertEquals(null, result.getComments());
        assertEquals(null, result.getCreator());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the Date getCreated() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetCreated_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        Date result = fixture.getCreated();

        assertNotNull(result);
    }

    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        String result = fixture.getCreator();

        assertEquals("", result);
    }

    /**
     * Run the Integer getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

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
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        Date result = fixture.getModified();

        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the int getRuntime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetRuntime_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        int result = fixture.getRuntime();

        assertEquals(1, result);
    }

    /**
     * Run the List<ScriptStepTO> getSteps() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetSteps_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());

        List<ScriptStepTO> result = fixture.getSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setCreated(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetCreated_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        Date created = new Date();

        fixture.setCreated(created);

    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        String creator = "";

        fixture.setCreator(creator);

    }

    /**
     * Run the void setId(Integer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        Integer id = new Integer(1);

        fixture.setId(id);

    }

    /**
     * Run the void setModified(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetModified_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        Date modified = new Date();

        fixture.setModified(modified);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the void setRuntime(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetRuntime_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        int runtime = 1;

        fixture.setRuntime(runtime);

    }

    /**
     * Run the void setSteps(List<ScriptStepTO>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetSteps_1()
        throws Exception {
        ScriptTO fixture = new ScriptTO();
        fixture.setId(new Integer(1));
        fixture.setName("");
        fixture.setProductName("");
        fixture.setRuntime(1);
        fixture.setComments("");
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setCreator("");
        fixture.setSteps(new LinkedList());
        List<ScriptStepTO> steps = new LinkedList();

        fixture.setSteps(steps);

    }
}