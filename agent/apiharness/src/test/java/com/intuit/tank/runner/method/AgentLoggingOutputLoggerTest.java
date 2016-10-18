package com.intuit.tank.runner.method;

import static org.junit.Assert.assertNotNull;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
import org.junit.Test;

/**
 * The class <code>AgentLoggingOutputLoggerTest</code> contains tests for the class <code>{@link AgentLoggingOutputLogger}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class AgentLoggingOutputLoggerTest {
    /**
     * Run the AgentLoggingOutputLogger() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testAgentLoggingOutputLogger_1()
        throws Exception {
        AgentLoggingOutputLogger result = new AgentLoggingOutputLogger();
        assertNotNull(result);
    }

    /**
     * Run the void debug(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testDebug_1()
        throws Exception {
        AgentLoggingOutputLogger fixture = new AgentLoggingOutputLogger();
        String text = "";

        fixture.debug(text);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.method.AgentLoggingOutputLogger
    }

    /**
     * Run the void error(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testError_1()
        throws Exception {
        AgentLoggingOutputLogger fixture = new AgentLoggingOutputLogger();
        String text = "";

        fixture.error(text);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.method.AgentLoggingOutputLogger
    }

    /**
     * Run the void log(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testLog_1()
        throws Exception {
        AgentLoggingOutputLogger fixture = new AgentLoggingOutputLogger();
        String text = "";

        fixture.log(text);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.method.AgentLoggingOutputLogger
    }

    /**
     * Run the void setScrollContent(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetScrollContent_1()
        throws Exception {
        AgentLoggingOutputLogger fixture = new AgentLoggingOutputLogger();
        boolean autoScroll = true;

        fixture.setScrollContent(autoScroll);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        //       at com.intuit.tank.runner.method.AgentLoggingOutputLogger.<clinit>(AgentLoggingOutputLogger.java:21)
    }
}