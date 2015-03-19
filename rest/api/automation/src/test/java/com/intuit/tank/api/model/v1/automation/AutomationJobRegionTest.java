package com.intuit.tank.api.model.v1.automation;

/*
 * #%L
 * Automation Rest Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.api.model.v1.automation.AutomationJobRegion;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>AutomationJobRegionTest</code> contains tests for the class <code>{@link AutomationJobRegion}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:49 PM
 */
public class AutomationJobRegionTest {
    /**
     * Run the AutomationJobRegion() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testAutomationJobRegion_1()
        throws Exception {

        AutomationJobRegion result = new AutomationJobRegion();
        assertNotNull(result);
    }

    /**
     * Run the AutomationJobRegion(VMRegion,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testAutomationJobRegion_2()
        throws Exception {
        VMRegion region = null;
        String users = "";

        AutomationJobRegion result = new AutomationJobRegion(region, users);
        assertNotNull(result);
    }

    /**
     * Run the AutomationJobRegion(VMRegion,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testAutomationJobRegion_3()
        throws Exception {
        VMRegion region = VMRegion.ASIA_1;
        String users = "";

        AutomationJobRegion result = new AutomationJobRegion(region, users);
        assertNotNull(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        AutomationJobRegion fixture = new AutomationJobRegion(VMRegion.ASIA_1, "");
        Object obj = new AutomationJobRegion(VMRegion.ASIA_1, "");

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        AutomationJobRegion fixture = new AutomationJobRegion(VMRegion.ASIA_1, "");
        Object obj = new AutomationJobRegion(VMRegion.ASIA_1, "");

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        AutomationJobRegion fixture = new AutomationJobRegion(VMRegion.ASIA_1, "");
        Object obj = new AutomationJobRegion(VMRegion.ASIA_3, "");

        boolean result = fixture.equals(obj);
        assertFalse(result);
    }

    /**
     * Run the VMRegion getRegion() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testGetRegion_1()
        throws Exception {
        AutomationJobRegion fixture = new AutomationJobRegion(VMRegion.ASIA_1, "");

        VMRegion result = fixture.getRegion();
        assertNotNull(result);
    }

    /**
     * Run the String getUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testGetUsers_1()
        throws Exception {
        AutomationJobRegion fixture = new AutomationJobRegion(VMRegion.ASIA_1, "");

        String result = fixture.getUsers();
        assertNotNull(result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        AutomationJobRegion fixture = new AutomationJobRegion(VMRegion.ASIA_1, "");

        int result = fixture.hashCode();
    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:49 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        AutomationJobRegion fixture = new AutomationJobRegion(VMRegion.ASIA_1, "");

        String result = fixture.toString();
        assertNotNull(result);
    }
}