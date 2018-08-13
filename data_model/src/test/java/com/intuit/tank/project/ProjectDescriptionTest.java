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

import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectDescription;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;

/**
 * The class <code>ProjectDescriptionTest</code> contains tests for the class <code>{@link ProjectDescription}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
@org.testng.annotations.Ignore
public class ProjectDescriptionTest {
    /**
     * Run the ProjectDescription() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testProjectDescription_1()
        throws Exception {

        ProjectDescription result = new ProjectDescription();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getWorkloadNames());
        assertEquals(null, result.getCreated());
        assertEquals(null, result.getCreator());
        assertEquals(null, result.getProductName());
        assertEquals(null, result.getComments());
    }

    /**
     * Run the ProjectDescription(Project) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testProjectDescription_2()
        throws Exception {
        Project p = new Project();
        p.setWorkloads(new LinkedList());
        p.setComments("");
        p.setScriptDriver(ScriptDriver.SilkPerformer);
        p.setName("");
        p.setProductName("");

        ProjectDescription result = new ProjectDescription(p);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
        assertEquals(null, result.getCreator());
        assertEquals("", result.getProductName());
        assertEquals("", result.getComments());
    }

    /**
     * Run the ProjectDescription(Project) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testProjectDescription_3()
        throws Exception {
        Project p = new Project();
        p.setWorkloads(new LinkedList());
        p.setComments("");
        p.setScriptDriver(ScriptDriver.SilkPerformer);
        p.setName("");
        p.setProductName("");

        ProjectDescription result = new ProjectDescription(p);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
        assertEquals(null, result.getCreator());
        assertEquals("", result.getProductName());
        assertEquals("", result.getComments());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
        Object obj = new ProjectDescription();

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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
        Object obj = new ProjectDescription();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());

        String result = fixture.getCreator();

        assertEquals("", result);
    }

    /**
     * Run the int getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());

        int result = fixture.getId();

        assertEquals(1, result);
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the ScriptDriver getScriptDriver() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptDriver_1()
        throws Exception {
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());

        ScriptDriver result = fixture.getScriptDriver();

        assertNotNull(result);
        assertEquals("SilkPerformer", result.name());
        assertEquals("SilkPerformer", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the List<String> getWorkloadNames() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetWorkloadNames_1()
        throws Exception {
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());

        List<String> result = fixture.getWorkloadNames();

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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());

        int result = fixture.hashCode();

        assertEquals(1306, result);
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
        String creator = "";

        fixture.setCreator(creator);

    }

    /**
     * Run the void setId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
        int id = 1;

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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the void setScriptDriver(ScriptDriver) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScriptDriver_1()
        throws Exception {
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
        ScriptDriver scriptDriver = ScriptDriver.SilkPerformer;

        fixture.setScriptDriver(scriptDriver);

    }

    /**
     * Run the void setWorkloadNames(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetWorkloadNames_1()
        throws Exception {
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());
        List<String> workloadNames = new LinkedList();

        fixture.setWorkloadNames(workloadNames);

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
        ProjectDescription fixture = new ProjectDescription();
        fixture.setCreator("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(1);
        fixture.setCreated(new Date());
        fixture.setComments("");
        fixture.setWorkloadNames(new LinkedList());
        fixture.setModified(new Date());

        String result = fixture.toString();

    }
}