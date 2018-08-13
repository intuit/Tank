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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;

/**
 * The class <code>TestPlanTest</code> contains tests for the class <code>{@link TestPlan}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class TestPlanTest {
    /**
     * Run the TestPlan() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testTestPlan_1()
        throws Exception {
        TestPlan result = new TestPlan();
        assertNotNull(result);
    }

    /**
     * Run the void addScriptGroup(ScriptGroup) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddScriptGroup_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        ScriptGroup scriptGroup = new ScriptGroup();

        fixture.addScriptGroup(scriptGroup);
    }

    /**
     * Run the void addScriptGroupAt(ScriptGroup,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddScriptGroupAt_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        ScriptGroup scriptGroup = new ScriptGroup();
        int index = -1;

        fixture.addScriptGroupAt(scriptGroup, index);
    }

    /**
     * Run the void addScriptGroupAt(ScriptGroup,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddScriptGroupAt_2()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        ScriptGroup scriptGroup = new ScriptGroup();
        int index = 1;

        fixture.addScriptGroupAt(scriptGroup, index);
    }

    /**
     * Run the void addScriptGroupAt(ScriptGroup,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddScriptGroupAt_3()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        ScriptGroup scriptGroup = new ScriptGroup();
        int index = 1;

        fixture.addScriptGroupAt(scriptGroup, index);
    }

    /**
     * Run the TestPlan.Builder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        TestPlan.Builder result = TestPlan.builder();

        assertNotNull(result);
    }

    /**
     * Run the TestPlan.Builder builderFrom(TestPlan) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        TestPlan plan = new TestPlan();

        TestPlan.Builder result = TestPlan.builderFrom(plan);

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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Object obj = new TestPlan();

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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Object obj = new TestPlan();

        boolean result = fixture.equals(obj);
        assertTrue(result);
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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        Integer result = fixture.getPosition();
        assertNotNull(result);
    }

    /**
     * Run the List<ScriptGroup> getScriptGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScriptGroups_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        List<ScriptGroup> result = fixture.getScriptGroups();
        assertNotNull(result);
    }

    /**
     * Run the int getUserPercentage() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetUserPercentage_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        int result = fixture.getUserPercentage();
    }

    /**
     * Run the Workload getWorkload() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetWorkload_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        Workload result = fixture.getWorkload();
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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        int result = fixture.hashCode();
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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        String name = "";

        fixture.setName(name);
    }

    /**
     * Run the void setParent(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetParent_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Workload w = new Workload();

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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Integer position = new Integer(1);

        fixture.setPosition(position);
    }

    /**
     * Run the void setScriptGroups(List<ScriptGroup>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScriptGroups_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        List<ScriptGroup> scriptGroups = new LinkedList();

        fixture.setScriptGroups(scriptGroups);
    }

    /**
     * Run the void setUserPercentage(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetUserPercentage_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        int userPercentage = 1;

        fixture.setUserPercentage(userPercentage);
    }

    /**
     * Run the void setWorkload(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetWorkload_1()
        throws Exception {
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));
        Workload workload = new Workload();

        fixture.setWorkload(workload);
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
        TestPlan fixture = new TestPlan();
        fixture.setWorkload(new Workload());
        fixture.setScriptGroups(new LinkedList());
        fixture.setUserPercentage(1);
        fixture.setName("");
        fixture.setPosition(new Integer(1));

        String result = fixture.toString();
        assertNotNull(result);
    }
}