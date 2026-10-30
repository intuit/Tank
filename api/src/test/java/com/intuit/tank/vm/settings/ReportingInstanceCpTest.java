package com.intuit.tank.vm.settings;

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

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>ReportingInstanceCpTest</code> contains tests for the class <code>{@link ReportingInstance}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class ReportingInstanceCpTest {
    /**
     * Run the ReportingInstance(HierarchicalConfiguration,HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testReportingInstance_1()
            throws Exception {
        HierarchicalConfiguration<ImmutableNode> config = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();
        HierarchicalConfiguration<ImmutableNode> defaultInstance = new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration();

        ReportingInstance result = new ReportingInstance(config, defaultInstance);

        assertNotNull(result);
        assertNull(result.getZone());
        assertFalse(result.getReuseInstances());
        assertFalse(result.isRegionDependent());
        assertNull(result.getReportingMode());
        assertNull(result.getAmi());
        assertNull(result.getPublicIp());
        assertNull(result.getLocation());
        assertNull(result.getSecurityGroup());
//        assertNull(result.getSubnetIds().get(0));
        assertNull(result.getKeypair());
    }

    /**
     * Run the VMRegion getRegion() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRegion_1()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        VMRegion result = fixture.getRegion();

        assertNotNull(result);
        assertEquals("US East (Northern Virginia)", result.getDescription());
        assertEquals("US East (Northern Virginia)", result.toString());
        assertEquals("ec2.us-east-1.amazonaws.com", result.getEndpoint());
        assertEquals("US_EAST", result.name());
    }

    /**
     * Run the VMRegion getRegion() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRegion_2()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        VMRegion result = fixture.getRegion();

        assertNotNull(result);
        assertEquals("US East (Northern Virginia)", result.getDescription());
        assertEquals("US East (Northern Virginia)", result.toString());
        assertEquals("ec2.us-east-1.amazonaws.com", result.getEndpoint());
        assertEquals("US_EAST", result.name());
    }

    /**
     * Run the String getReportingMode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetReportingMode_1()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getReportingMode();

        assertNull(result);
    }

    /**
     * Run the String getReportingMode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetReportingMode_2()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getReportingMode();

        assertNull(result);
    }

    /**
     * Run the boolean getReuseInstances() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetReuseInstances_1()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.getReuseInstances();

        assertFalse(result);
    }

    /**
     * Run the boolean getReuseInstances() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetReuseInstances_2()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.getReuseInstances();

        assertFalse(result);
    }

    /**
     * Run the String getZone() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetZone_1()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.getZone();

        assertNull(result);
    }

    /**
     * Run the boolean isRegionDependent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsRegionDependent_1()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.isRegionDependent();

        assertFalse(result);
    }

    /**
     * Run the boolean isRegionDependent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsRegionDependent_2()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        boolean result = fixture.isRegionDependent();

        assertFalse(result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        ReportingInstance fixture = new ReportingInstance(new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration(),
                new BasicConfigurationBuilder<>(XMLConfiguration.class).getConfiguration());

        String result = fixture.toString();

        assertNotNull(result);
    }
}