package com.intuit.tank.project;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import com.intuit.tank.project.AddTestPlan;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>AddTestPlanTest</code> contains tests for the class <code>{@link AddTestPlan}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class AddTestPlanTest {
    /**
     * Run the AddTestPlan() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAddTestPlan_1()
        throws Exception {
        AddTestPlan result = new AddTestPlan();
        assertNotNull(result);
    }

    /**
     * Run the void clear() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testClear_1()
        throws Exception {
        AddTestPlan fixture = new AddTestPlan();
        fixture.setPercentage(1);
        fixture.setName("");

        fixture.clear();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.AddTestPlan.setPercentage(AddTestPlan.java:48)
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        AddTestPlan fixture = new AddTestPlan();
        fixture.setPercentage(1);
        fixture.setName("");

        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.AddTestPlan.setPercentage(AddTestPlan.java:48)
        assertNotNull(result);
    }

    /**
     * Run the int getPercentage() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetPercentage_1()
        throws Exception {
        AddTestPlan fixture = new AddTestPlan();
        fixture.setPercentage(1);
        fixture.setName("");

        int result = fixture.getPercentage();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.AddTestPlan.setPercentage(AddTestPlan.java:48)
        assertEquals(1, result);
    }

    /**
     * Run the boolean isHasErrors() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsHasErrors_1()
        throws Exception {
        AddTestPlan fixture = new AddTestPlan();
        fixture.setPercentage(1);
        fixture.setName("");

        boolean result = fixture.isHasErrors();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.AddTestPlan.setPercentage(AddTestPlan.java:48)
        assertTrue(!result);
    }

    /**
     * Run the boolean isHasErrors() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsHasErrors_2()
        throws Exception {
        AddTestPlan fixture = new AddTestPlan();
        fixture.setPercentage(1);
        fixture.setName("");

        boolean result = fixture.isHasErrors();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.AddTestPlan.setPercentage(AddTestPlan.java:48)
        assertTrue(!result);
    }

   

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        AddTestPlan fixture = new AddTestPlan();
        fixture.setPercentage(1);
        fixture.setName("");
        String name = "";

        fixture.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.AddTestPlan.setPercentage(AddTestPlan.java:48)
    }

    /**
     * Run the void setPercentage(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetPercentage_1()
        throws Exception {
        AddTestPlan fixture = new AddTestPlan();
        fixture.setPercentage(1);
        fixture.setName("");
        int percentage = 1;

        fixture.setPercentage(percentage);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.AddTestPlan.setPercentage(AddTestPlan.java:48)
    }
}