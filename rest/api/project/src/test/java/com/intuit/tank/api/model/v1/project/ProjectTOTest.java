package com.intuit.tank.api.model.v1.project;

/*
 * #%L
 * Project Rest API
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

import com.intuit.tank.api.model.v1.project.KeyPair;
import com.intuit.tank.api.model.v1.project.ProjectTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ProjectTOTest</code> contains tests for the class <code>{@link ProjectTO}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:08 PM
 */
public class ProjectTOTest {
    /**
     * Run the ProjectTO() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testProjectTO_1()
        throws Exception {
        ProjectTO result = new ProjectTO();
        assertNotNull(result);
    }

    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the Date getCreated() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetCreated_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        Date result = fixture.getCreated();

        assertNotNull(result);
    }

    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        String result = fixture.getCreator();

        assertEquals("", result);
    }

    /**
     * Run the List<Integer> getDataFileIds() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetDataFileIds_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        List<Integer> result = fixture.getDataFileIds();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Integer getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

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
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetModified_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        Date result = fixture.getModified();

        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the List<KeyPair> getVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetVariables_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        List<KeyPair> result = fixture.getVariables();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setCreated(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetCreated_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        Date created = new Date();

        fixture.setCreated(created);

    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        String creator = "";

        fixture.setCreator(creator);

    }

    /**
     * Run the void setDataFiles(List<Integer>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetDataFiles_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        List<Integer> dataFileIds = new LinkedList();

        fixture.setDataFiles(dataFileIds);

    }

    /**
     * Run the void setId(Integer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        Integer id = new Integer(1);

        fixture.setId(id);

    }

    /**
     * Run the void setModified(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetModified_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        Date modified = new Date();

        fixture.setModified(modified);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the void setVariables(List<KeyPair>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetVariables_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());
        List<KeyPair> variables = new LinkedList();

        fixture.setVariables(variables);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        ProjectTO fixture = new ProjectTO();
        fixture.setCreator("");
        fixture.setProductName("");
        fixture.setComments("");
        fixture.setDataFiles(new LinkedList());
        fixture.setId(new Integer(1));
        fixture.setCreated(new Date());
        fixture.setModified(new Date());
        fixture.setName("");
        fixture.setVariables(new LinkedList());

        String result = fixture.toString();

        assertEquals("", result);
    }
}