package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
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

import java.util.List;

import org.junit.Test;

import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;

/**
 * The class <code>HDTestPlanTest</code> contains tests for the class <code>{@link HDTestPlan}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDTestPlanTest {
    /**
     * Run the HDTestPlan() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDTestPlan_1()
            throws Exception {
        HDTestPlan result = new HDTestPlan();
        assertNotNull(result);
    }

    /**
     * Run the List<HDScriptGroup> getGroup() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetGroup_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);

        List<HDScriptGroup> result = fixture.getGroup();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getTestPlanName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetTestPlanName_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);

        String result = fixture.getTestPlanName();

        assertEquals("", result);
    }

    /**
     * Run the int getUserPercentage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetUserPercentage_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);

        int result = fixture.getUserPercentage();

        assertEquals(1, result);
    }

    /**
     * Run the HDTestVariables getVariables() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetVariables_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);

        HDTestVariables result = fixture.getVariables();

        assertNotNull(result);
        assertEquals(false, result.isAllowOverride());
    }

    /**
     * Run the void setTestPlanName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetTestPlanName_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);
        String testPlanName = "";

        fixture.setTestPlanName(testPlanName);

    }

    /**
     * Run the void setUserPercentage(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetUserPercentage_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);
        int userPercentage = 1;

        fixture.setUserPercentage(userPercentage);

    }

    /**
     * Run the void setVariables(HDTestVariables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetVariables_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);
        HDTestVariables variables = new HDTestVariables();

        fixture.setVariables(variables);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        HDTestPlan fixture = new HDTestPlan();
        fixture.setTestPlanName("");
        fixture.setVariables(new HDTestVariables());
        fixture.setUserPercentage(1);

        String result = fixture.toString();

        assertEquals(" (1%)", result);
    }
}