package com.intuit.tank.vm.api.enumerated;

/*
 * #%L
 * Intuit Tank Api
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

import com.intuit.tank.vm.api.enumerated.VMRegion;

import static org.junit.Assert.*;

/**
 * The class <code>VMRegionCpTest</code> contains tests for the class <code>{@link VMRegion}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class VMRegionCpTest {
    /**
     * Run the String getDescription() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetDescription_1()
            throws Exception {
        VMRegion fixture = VMRegion.ASIA_1;

        String result = fixture.getDescription();

        assertEquals("Asia Pacific (Singapore)", result);
        assertEquals("Singapore", fixture.getName());
    }

    /**
     * Run the String getEndpoint() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetEndpoint_1()
            throws Exception {
        VMRegion fixture = VMRegion.ASIA_1;

        String result = fixture.getEndpoint();

        assertEquals("ec2.ap-southeast-1.amazonaws.com", result);
    }

    /**
     * Run the VMRegion getRegionFromZone(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRegionFromZone_1()
            throws Exception {
        String zone = "";

        VMRegion result = VMRegion.getRegionFromZone(zone);

        assertNotNull(result);
        assertEquals("US East (Northern Virginia)", result.getDescription());
        assertEquals("US East (Northern Virginia)", result.toString());
        assertEquals("ec2.us-east-1.amazonaws.com", result.getEndpoint());
        assertEquals("US_EAST", result.name());
    }

    /**
     * Run the VMRegion getRegionFromZone(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRegionFromZone_2()
            throws Exception {
        String zone = "";

        VMRegion result = VMRegion.getRegionFromZone(zone);

        assertNotNull(result);
        assertEquals("US East (Northern Virginia)", result.getDescription());
        assertEquals("US East (Northern Virginia)", result.toString());
        assertEquals("ec2.us-east-1.amazonaws.com", result.getEndpoint());
        assertEquals("US_EAST", result.name());
    }

    /**
     * Run the VMRegion getRegionFromZone(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRegionFromZone_3()
            throws Exception {
        String zone = "";

        VMRegion result = VMRegion.getRegionFromZone(zone);

        assertNotNull(result);
        assertEquals("US East (Northern Virginia)", result.getDescription());
        assertEquals("US East (Northern Virginia)", result.toString());
        assertEquals("ec2.us-east-1.amazonaws.com", result.getEndpoint());
        assertEquals("US_EAST", result.name());
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        VMRegion fixture = VMRegion.ASIA_1;

        String result = fixture.toString();

        assertEquals("Asia Pacific (Singapore)", result);
    }
}