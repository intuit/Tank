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

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.api.enumerated.VMRegion;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * The class <code>TankSettingsTest</code> contains tests for the class <code>{@link TankSettings}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
@EnableAutoWeld
public class TankSettingsTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(TankSettings.class).activate(ApplicationScoped.class).build();

    @Inject
    private TankSettings tankSettings;
    
    /**
     * Run the TankSettings() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testTankSettings_1()
        throws Exception {
        assertNotNull(tankSettings);
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
        List<VMRegion> result = tankSettings.getVmRegions();
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
        String region = "";

        boolean result = tankSettings.hasRegionConfigured(region);
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
        String region = "";

        boolean result = tankSettings.hasRegionConfigured(region);
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
        String region = "";

        boolean result = tankSettings.hasRegionConfigured(region);
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

        tankSettings.init();

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

        boolean result = tankSettings.isStandalone();
        assertFalse(result);
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

        boolean result = tankSettings.isStandalone();
        assertFalse(result);
    }
}