package com.intuit.tank.api.model.v1.cloud;

/*
 * #%L
 * Cloud Rest API
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

import com.intuit.tank.api.model.v1.cloud.ValidationStatus;

import static org.junit.Assert.*;

/**
 * The class <code>ValidationStatusTest</code> contains tests for the class <code>{@link ValidationStatus}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:57 PM
 */
public class ValidationStatusTest {
    /**
     * Run the ValidationStatus() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testValidationStatus_1()
        throws Exception {

        ValidationStatus result = new ValidationStatus();

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getValidationSkips());
        assertEquals(0, result.getValidationRestarts());
        assertEquals(0, result.getValidationGotos());
        assertEquals(0, result.getValidationAborts());
        assertEquals(0, result.getValidationKills());
        assertEquals(0, result.getValidationSkipGroups());
    }

    /**
     * Run the ValidationStatus(int,int,int,int,int,int) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testValidationStatus_2()
        throws Exception {
        int kills = 1;
        int aborts = 1;
        int gotos = 1;
        int skips = 1;
        int skipGroups = 1;
        int restarts = 1;

        ValidationStatus result = new ValidationStatus(kills, aborts, gotos, skips, skipGroups, restarts);

        assertNotNull(result);
        assertEquals(6, result.getTotal());
        assertEquals(1, result.getValidationSkips());
        assertEquals(1, result.getValidationRestarts());
        assertEquals(1, result.getValidationGotos());
        assertEquals(1, result.getValidationAborts());
        assertEquals(1, result.getValidationKills());
        assertEquals(1, result.getValidationSkipGroups());
    }

    /**
     * Run the void addAbort() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddAbort_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        fixture.addAbort();

    }

    /**
     * Run the void addFailures(ValidationStatus) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddFailures_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);
        ValidationStatus other = new ValidationStatus(1, 1, 1, 1, 1, 1);

        fixture.addFailures(other);

    }

    /**
     * Run the void addGoto() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddGoto_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        fixture.addGoto();

    }

    /**
     * Run the void addKill() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddKill_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        fixture.addKill();

    }

    /**
     * Run the void addRestart() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddRestart_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        fixture.addRestart();

    }

    /**
     * Run the void addSkip() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddSkip_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        fixture.addSkip();

    }

    /**
     * Run the void addSkipGroup() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddSkipGroup_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        fixture.addSkipGroup();

    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);
        Object obj = new ValidationStatus();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);
        Object obj = new ValidationStatus();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the int getTotal() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTotal_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.getTotal();

        assertEquals(6, result);
    }

    /**
     * Run the int getValidationAborts() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetValidationAborts_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.getValidationAborts();

        assertEquals(1, result);
    }

    /**
     * Run the int getValidationGotos() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetValidationGotos_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.getValidationGotos();

        assertEquals(1, result);
    }

    /**
     * Run the int getValidationKills() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetValidationKills_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.getValidationKills();

        assertEquals(1, result);
    }

    /**
     * Run the int getValidationRestarts() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetValidationRestarts_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.getValidationRestarts();

        assertEquals(1, result);
    }

    /**
     * Run the int getValidationSkipGroups() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetValidationSkipGroups_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.getValidationSkipGroups();

        assertEquals(1, result);
    }

    /**
     * Run the int getValidationSkips() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetValidationSkips_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.getValidationSkips();

        assertEquals(1, result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        int result = fixture.hashCode();

        assertEquals(1462892007, result);
    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        ValidationStatus fixture = new ValidationStatus(1, 1, 1, 1, 1, 1);

        String result = fixture.toString();

    }
}