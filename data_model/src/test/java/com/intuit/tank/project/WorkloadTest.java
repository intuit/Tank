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
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;

/**
 * The class <code>WorkloadTest</code> contains tests for the class <code>{@link Workload}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class WorkloadTest {
    /**
     * Run the Workload() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testWorkload_1()
        throws Exception {
        Workload result = new Workload();
        assertNotNull(result);
    }

    /**
     * Run the void addTestGroupAt(TestPlan,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddTestGroupAt_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        TestPlan testPlan = new TestPlan();
        int index = -1;

        fixture.addTestGroupAt(testPlan, index);
    }

    /**
     * Run the void addTestGroupAt(TestPlan,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddTestGroupAt_2()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        TestPlan testPlan = new TestPlan();
        int index = 1;

        fixture.addTestGroupAt(testPlan, index);
    }

    /**
     * Run the void addTestGroupAt(TestPlan,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddTestGroupAt_3()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        TestPlan testPlan = new TestPlan();
        int index = 1;

        fixture.addTestGroupAt(testPlan, index);
    }

    /**
     * Run the void addTestPlan(TestPlan) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddTestPlan_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        TestPlan plan = new TestPlan();

        fixture.addTestPlan(plan);
    }

    /**
     * Run the Workload.Builder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        Workload.Builder result = Workload.builder();
        assertNotNull(result);
    }

    /**
     * Run the Workload.Builder builderFrom(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        Workload w = new Workload();

        Workload.Builder result = Workload.builderFrom(w);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Object obj = new Object();

        boolean result = fixture.equals(obj);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Object obj = new Workload();

        boolean result = fixture.equals(obj);
        assertTrue(result);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Object obj = new Workload();

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the JobConfiguration getJobConfiguration() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobConfiguration_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        JobConfiguration result = fixture.getJobConfiguration();
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        String result = fixture.getName();
        assertNotNull(result);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        Integer result = fixture.getPosition();
        assertNotNull(result);
    }

    /**
     * Run the Project getProject() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProject_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        Project result = fixture.getProject();
        assertNotNull(result);
    }

    /**
     * Run the List<TestPlan> getTestPlans() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTestPlans_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        List<TestPlan> result = fixture.getTestPlans();
        assertNotNull(result);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        int result = fixture.hashCode();
    }

    /**
     * Run the void setJobConfiguration(JobConfiguration) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJobConfiguration_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        JobConfiguration jobConfiguration = new JobConfiguration();

        fixture.setJobConfiguration(jobConfiguration);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        String name = "";

        fixture.setName(name);
    }

    /**
     * Run the void setParent(Project) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetParent_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Project myProject = new Project();

        fixture.setParent(myProject);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Integer position = new Integer(1);

        fixture.setPosition(position);
    }

    /**
     * Run the void setProject(Project) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetProject_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Project myProject = new Project();

        fixture.setProject(myProject);
    }

    /**
     * Run the void setTestPlan(List<TestPlan>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetTestPlan_1()
        throws Exception {
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        List<TestPlan> plans = new LinkedList();

        fixture.setTestPlan(plans);
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
        Workload fixture = new Workload();
        fixture.setTestPlan(new LinkedList());
        fixture.setJobConfiguration(new JobConfiguration());
        fixture.setParent(new Project());
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        String result = fixture.toString();
        assertNotNull(result);
    }
}