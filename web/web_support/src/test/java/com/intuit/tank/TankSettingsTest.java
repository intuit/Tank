package com.intuit.tank;

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

import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.TankSettings;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>TankSettingsTest</code> contains tests for the class <code>{@link TankSettings}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class TankSettingsTest {
    /**
     * Run the TankSettings() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testTankSettings_1()
        throws Exception {
        TankSettings result = new TankSettings();
        assertNotNull(result);
    }

    /**
     * Run the List<VMRegion> getVmRegions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmRegions_1()
        throws Exception {
        TankSettings fixture = new TankSettings();

        List<VMRegion> result = fixture.getVmRegions();

    }

    /**
     * Run the boolean hasRegionConfigured(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHasRegionConfigured_1()
        throws Exception {
        TankSettings fixture = new TankSettings();
        String region = "";

        boolean result = fixture.hasRegionConfigured(region);
    }

    /**
     * Run the boolean hasRegionConfigured(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHasRegionConfigured_2()
        throws Exception {
        TankSettings fixture = new TankSettings();
        String region = "";

        boolean result = fixture.hasRegionConfigured(region);
    }

    /**
     * Run the boolean hasRegionConfigured(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHasRegionConfigured_3()
        throws Exception {
        TankSettings fixture = new TankSettings();
        String region = "";

        boolean result = fixture.hasRegionConfigured(region);
        assertTrue(!result);
    }

    /**
     * Run the void init() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testInit_1()
        throws Exception {
        TankSettings fixture = new TankSettings();

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.settings.TankConfig.<clinit>(TankConfig.java:52)
        //       at com.intuit.tank.TankSettings.<init>(TankSettings.java:27)
    }

    /**
     * Run the boolean isStandalone() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsStandalone_1()
        throws Exception {
        TankSettings fixture = new TankSettings();

        boolean result = fixture.isStandalone();
        assertTrue(!result);
    }

    /**
     * Run the boolean isStandalone() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsStandalone_2()
        throws Exception {
        TankSettings fixture = new TankSettings();

        boolean result = fixture.isStandalone();
        assertTrue(!result);
    }
}