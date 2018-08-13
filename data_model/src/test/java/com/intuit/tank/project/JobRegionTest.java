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

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.JobRegion;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>JobRegionTest</code> contains tests for the class <code>{@link JobRegion}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class JobRegionTest {
    /**
     * Run the JobRegion() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobRegion_1()
        throws Exception {

        JobRegion result = new JobRegion();

        assertNotNull(result);
        assertEquals(null, result.getUsers());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the JobRegion(VMRegion,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobRegion_2()
        throws Exception {
        VMRegion region = VMRegion.ASIA_1;
        String users = "";

        JobRegion result = new JobRegion(region, users);

        assertNotNull(result);
        assertEquals("", result.getUsers());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the int compareTo(JobRegion) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
        JobRegion o = new JobRegion(VMRegion.ASIA_1, "");

        int result = fixture.compareTo(o);

        assertEquals(0, result);
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
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
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
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
        Object obj = new JobRegion();

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
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
        Object obj = new JobRegion();

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
    public void testEquals_4()
        throws Exception {
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
        Object obj = new JobRegion();

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
    public void testEquals_5()
        throws Exception {
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
        Object obj = new JobRegion();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the VMRegion getRegion() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRegion_1()
        throws Exception {
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");

        VMRegion result = fixture.getRegion();

        assertNotNull(result);
        assertEquals("Asia Pacific (Singapore)", result.toString());
        assertEquals("ap-southeast-1", result.getRegion());
        assertEquals("ec2.ap-southeast-1.amazonaws.com", result.getEndpoint());
        assertEquals("Asia Pacific (Singapore)", result.getDescription());
        assertEquals("ASIA_1", result.name());
    }

    /**
     * Run the String getUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetUsers_1()
        throws Exception {
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");

        String result = fixture.getUsers();

        assertEquals("", result);
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
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");

        int result = fixture.hashCode();

    }

    /**
     * Run the void setRegion(VMRegion) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRegion_1()
        throws Exception {
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
        VMRegion region = VMRegion.ASIA_1;

        fixture.setRegion(region);

    }

    /**
     * Run the void setUsers(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetUsers_1()
        throws Exception {
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");
        String users = "";

        fixture.setUsers(users);

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
        JobRegion fixture = new JobRegion(VMRegion.ASIA_1, "");

        String result = fixture.toString();

    }
}