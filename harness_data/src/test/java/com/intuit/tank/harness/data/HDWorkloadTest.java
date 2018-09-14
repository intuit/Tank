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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

public class HDWorkloadTest {

    @Test(groups = TestGroups.FUNCTIONAL)
    public void test() throws Exception {
        HDWorkload workload = new HDWorkload();
        workload.setName("Test Workload");
        workload.setDescription("Desc of TestWorkload");
        List<HDTestPlan> plans = workload.getPlans();
        plans.add(getTestPlan(1, 50));
        plans.add(getTestPlan(2, 50));
        String marshall = JaxbUtil.marshall(workload);
        Assert.assertNotNull(marshall);
        System.out.println(marshall);
        HDWorkload unmarshall = JaxbUtil.unmarshall(marshall, HDWorkload.class);
        Assert.assertEquals(workload, unmarshall);

    }

    private HDTestPlan getTestPlan(int i, int userPercent) {
        HDTestPlan plan = new HDTestPlan();
        plan.setTestPlanName("Test Plan " + i);
        plan.setUserPercentage(userPercent);
        return plan;
    }

    /**
     * Run the HDWorkload() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDWorkload_1()
            throws Exception {
        HDWorkload result = new HDWorkload();
        assertNotNull(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");
        HDWorkload obj = new HDWorkload();
        obj.setName("");
        obj.setDescription("");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");
        HDWorkload obj = new HDWorkload();
        obj.setName("");
        obj.setDescription("");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getDescription() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetDescription_1()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");

        String result = fixture.getDescription();

        assertEquals("", result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the List<HDTestPlan> getPlans() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetPlans_1()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");

        List<HDTestPlan> result = fixture.getPlans();

        assertNotNull(result);
        assertEquals(0, result.size());
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
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");

        HDTestVariables result = fixture.getVariables();

        assertNotNull(result);
        assertEquals(false, result.isAllowOverride());
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");

        int result = fixture.hashCode();

        assertEquals(861102, result);
    }

    /**
     * Run the void setDescription(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetDescription_1()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");
        String desc = "";

        fixture.setDescription(desc);

    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");
        String name = "";

        fixture.setName(name);

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
        HDWorkload fixture = new HDWorkload();
        fixture.setVariables(new HDTestVariables());
        fixture.setName("");
        fixture.setDescription("");
        HDTestVariables variables = new HDTestVariables();

        fixture.setVariables(variables);

    }
}
