package com.intuit.tank.script;

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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptStepFactory;

/**
 * The class <code>ScriptStepFactoryTest</code> contains tests for the class <code>{@link ScriptStepFactory}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ScriptStepFactoryTest {
    /**
     * Run the ScriptStep createClearCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateClearCookies_1()
        throws Exception {

        ScriptStep result = ScriptStepFactory.createClearCookies();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptStepFactory.createClearCookies(ScriptStepFactory.java:112)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep createCookie(String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateCookie_1()
        throws Exception {
        String name = "";
        String value = "";
        String domain = "";
        String path = "";

        ScriptStep result = ScriptStepFactory.createCookie(name, value, domain, path);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptStepFactory.createCookie(ScriptStepFactory.java:104)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep createCookie(String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateCookie_2()
        throws Exception {
        String name = "";
        String value = "";
        String domain = "";
        String path = "";

        ScriptStep result = ScriptStepFactory.createCookie(name, value, domain, path);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptStepFactory.createCookie(ScriptStepFactory.java:104)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep createLogic(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateLogic_1()
        throws Exception {
        String name = "";
        String script = "";

        ScriptStep result = ScriptStepFactory.createLogic(name, script);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptStepFactory.createLogic(ScriptStepFactory.java:75)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep createSleepTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateSleepTime_1()
        throws Exception {
        String delay = "";

        ScriptStep result = ScriptStepFactory.createSleepTime(delay);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptStepFactory.createSleepTime(ScriptStepFactory.java:60)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep createThinkTime(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateThinkTime_1()
        throws Exception {
        String minTime = "";
        String maxTime = "";

        ScriptStep result = ScriptStepFactory.createThinkTime(minTime, maxTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptStepFactory.createThinkTime(ScriptStepFactory.java:46)
        assertNotNull(result);
    }

    /**
     * Run the ScriptStep createVariable(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testCreateVariable_1()
        throws Exception {
        String key = "";
        String value = "";

        ScriptStep result = ScriptStepFactory.createVariable(key, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ScriptStepFactory.createVariable(ScriptStepFactory.java:23)
        assertNotNull(result);
    }
}