package com.intuit.tank.admin;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.admin.LogConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>LogConfigTest</code> contains tests for the class <code>{@link LogConfig}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class LogConfigTest {
    /**
     * Run the LogConfig() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testLogConfig_1()
        throws Exception {
        LogConfig result = new LogConfig();
        assertNotNull(result);
    }

    /**
     * Run the void setLogLevel(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetLogLevel_1()
        throws Exception {
        LogConfig fixture = new LogConfig();
        String level = "";

        fixture.setLogLevel(level);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogConfig.setLogLevel(LogConfig.java:37)
    }

    /**
     * Run the void setLogLevel(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetLogLevel_2()
        throws Exception {
        LogConfig fixture = new LogConfig();
        String level = "";

        fixture.setLogLevel(level);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogConfig.setLogLevel(LogConfig.java:37)
    }
}