package com.intuit.tank.util;

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

import java.util.Date;

import org.junit.jupiter.api.*;

import com.intuit.tank.util.BuildVersion;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>BuildVersionTest</code> contains tests for the class <code>{@link BuildVersion}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class BuildVersionTest {
    /**
     * Run the BuildVersion() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testBuildVersion_1()
            throws Exception {
        BuildVersion result = new BuildVersion();
        assertNotNull(result);
    }

    /**
     * Run the Date getBuildDate() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetBuildDate_1()
            throws Exception {
        BuildVersion fixture = new BuildVersion();
        Date result = fixture.getBuildDate();
    }

    /**
     * Run the String getVersion() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetVersion_1()
            throws Exception {
        BuildVersion fixture = new BuildVersion();

        String result = fixture.getVersion();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.BuildVersion.getVersion(BuildVersion.java:41)
        assertNotNull(result);
    }

    /**
     * Run the void init() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testInit_1()
            throws Exception {
        BuildVersion fixture = new BuildVersion();

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.BuildVersion.init(BuildVersion.java:37)
    }
}