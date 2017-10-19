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

import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;

/**
 * The class <code>ProjectTest</code> contains tests for the class <code>{@link Project}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ProjectTest {
    /**
     * Run the Project() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    @org.junit.Ignore
    public void testProject_1()
        throws Exception {

        Project result = new Project();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(null, result.getWorkloadNames());
        assertEquals(null, result.getProductName());
        assertEquals(null, result.getComments());
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the void addWorkload(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddWorkload_1()
        throws Exception {
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
        Workload userType = new Workload();

        fixture.addWorkload(userType);
    }

    /**
     * Run the Project.Builder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        Project.Builder result = Project.builder();

        assertNotNull(result);
    }

    /**
     * Run the Project.Builder builderFrom(Project) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        Project p = new Project();

        Project.Builder result = Project.builderFrom(p);

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
        Object obj = new Project();

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
        Object obj = new Project();

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
    @org.junit.Ignore
    public void testGetComments_1()
        throws Exception {
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

        ScriptDriver result = fixture.getScriptDriver();

        assertNotNull(result);
        assertEquals("SilkPerformer", result.name());
        assertEquals("SilkPerformer", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the String getWorkloadNames() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetWorkloadNames_1()
        throws Exception {
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

        String result = fixture.getWorkloadNames();

        assertEquals("", result);
    }

    /**
     * Run the List<Workload> getWorkloads() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetWorkloads_1()
        throws Exception {
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

        List<Workload> result = fixture.getWorkloads();

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
        ScriptDriver driver = ScriptDriver.SilkPerformer;

        fixture.setScriptDriver(driver);

    }

    /**
     * Run the void setWorkloadNames(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetWorkloadNames_1()
        throws Exception {
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
        String workloadNames = "";

        fixture.setWorkloadNames(workloadNames);

    }

    /**
     * Run the void setWorkloads(List<Workload>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetWorkloads_1()
        throws Exception {
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");
        List<Workload> workloads = new LinkedList();

        fixture.setWorkloads(workloads);

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
        Project fixture = new Project();
        fixture.setWorkloadNames("");
        fixture.setName("");
        fixture.setScriptDriver(ScriptDriver.SilkPerformer);
        fixture.setWorkloads(new LinkedList());
        fixture.setComments("");
        fixture.setProductName("");

        String result = fixture.toString();

    }
}