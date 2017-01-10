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

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.List;

import com.intuit.tank.vm.api.enumerated.VMSize;
import com.intuit.tank.vm.settings.InstanceDescriptionDefaults;

/**
 * The class <code>InstanceDescriptionDefaultsCpTest</code> contains tests for the class
 * <code>{@link InstanceDescriptionDefaults}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class InstanceDescriptionDefaultsCpTest {
    /**
     * Run the InstanceDescriptionDefaults(HierarchicalConfiguration,HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testInstanceDescriptionDefaults_1()
            throws Exception {
        HierarchicalConfiguration config = new HierarchicalConfiguration();
        HierarchicalConfiguration defaultInstance = new HierarchicalConfiguration();

        InstanceDescriptionDefaults result = new InstanceDescriptionDefaults(config, defaultInstance);

        assertNotNull(result);
        assertEquals(null, result.getZone());
        assertEquals(null, result.getLocation());
        assertEquals(null, result.getSecurityGroup());
        assertEquals(null, result.getSubnetIds().get(0));
        assertEquals(null, result.getKeypair());
    }

    /**
     * Run the String get(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGet_1()
            throws Exception {
        InstanceDescriptionDefaults fixture = new InstanceDescriptionDefaults(new HierarchicalConfiguration(),
                (HierarchicalConfiguration) null);
        String key = "";

        String result = fixture.get(key);

        assertEquals(null, result);
    }

    /**
     * Run the String get(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGet_2()
            throws Exception {
        InstanceDescriptionDefaults fixture = new InstanceDescriptionDefaults(new HierarchicalConfiguration(),
                new HierarchicalConfiguration());
        String key = "";

        String result = fixture.get(key);

        assertEquals(null, result);
    }

    /**
     * Run the String getKeypair() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetKeypair_1()
            throws Exception {
        InstanceDescriptionDefaults fixture = new InstanceDescriptionDefaults(new HierarchicalConfiguration(),
                new HierarchicalConfiguration());

        String result = fixture.getKeypair();

        assertEquals(null, result);
    }

    /**
     * Run the String getLocation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetLocation_1()
            throws Exception {
        InstanceDescriptionDefaults fixture = new InstanceDescriptionDefaults(new HierarchicalConfiguration(),
                new HierarchicalConfiguration());

        String result = fixture.getLocation();

        assertEquals(null, result);
    }

    /**
     * Run the String getSecurityGroup() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSecurityGroup_1()
            throws Exception {
        InstanceDescriptionDefaults fixture = new InstanceDescriptionDefaults(new HierarchicalConfiguration(),
                new HierarchicalConfiguration());

        String result = fixture.getSecurityGroup();

        assertEquals(null, result);
    }

    /**
     * Run the String getSubnetId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSubnetId_1()
            throws Exception {
        InstanceDescriptionDefaults fixture = new InstanceDescriptionDefaults(new HierarchicalConfiguration(),
                new HierarchicalConfiguration());

        List<String> result = fixture.getSubnetIds();

        assertEquals(null, result.get(0));
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
        InstanceDescriptionDefaults fixture = new InstanceDescriptionDefaults(new HierarchicalConfiguration(),
                new HierarchicalConfiguration());

        String result = fixture.getZone();

        assertEquals(null, result);
    }
}