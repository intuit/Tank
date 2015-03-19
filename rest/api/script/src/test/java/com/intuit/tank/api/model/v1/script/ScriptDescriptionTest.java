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

import org.junit.*;

import com.intuit.tank.api.model.v1.script.ScriptDescription;

import static org.junit.Assert.*;

/**
 * The class <code>ScriptDescriptionTest</code> contains tests for the class <code>{@link ScriptDescription}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class ScriptDescriptionTest {
    /**
     * Run the ScriptDescription() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testScriptDescription_1()
        throws Exception {

        ScriptDescription result = new ScriptDescription();

        assertNotNull(result);
        assertEquals(null, result.toString());
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

        int result = fixture.getRuntime();

        assertEquals(1, result);
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
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
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));
        int runtime = 1;

        fixture.setRuntime(runtime);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        ScriptDescription fixture = new ScriptDescription();
        fixture.setRuntime(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setName("");
        fixture.setCreator("");
        fixture.setModified(new Date());
        fixture.setProductName("");
        fixture.setId(new Integer(1));

        String result = fixture.toString();

        assertEquals("", result);
    }
}