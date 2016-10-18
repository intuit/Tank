package com.intuit.tank.harness.logging;

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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.harness.logging.LogEvent;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;

/**
 * The class <code>LogUtilTest</code> contains tests for the class <code>{@link LogUtil}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class LogUtilTest {
    /**
     * Run the LogEvent getLogEvent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetLogEvent_1()
            throws Exception {

        LogEvent result = LogUtil.getLogEvent();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.logging.LogUtil.<clinit>(LogUtil.java:20)
        assertNotNull(result);
    }

    /**
     * Run the String getLogMessage(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetLogMessage_1()
            throws Exception {
        String msg = "";

        String result = LogUtil.getLogMessage(msg);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertNotNull(result);
    }

    /**
     * Run the String getLogMessage(String,LogEventType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetLogMessage_2()
            throws Exception {
        String msg = "";
        LogEventType type = null;

        String result = LogUtil.getLogMessage(msg, type);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertNotNull(result);
    }

    /**
     * Run the String getLogMessage(String,LogEventType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetLogMessage_3()
            throws Exception {
        String msg = "";
        LogEventType type = LogEventType.Http;

        String result = LogUtil.getLogMessage(msg, type);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertNotNull(result);
    }

    /**
     * Run the String getLogMessage(String,LogEventType,LoggingProfile) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetLogMessage_4()
            throws Exception {
        String msg = "";
        LogEventType type = LogEventType.Http;
        LoggingProfile profile = LoggingProfile.STANDARD;

        String result = LogUtil.getLogMessage(msg, type, profile);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertNotNull(result);
    }

    /**
     * Run the String getLogMessage(String,LogEventType,LoggingProfile) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testGetLogMessage_5()
            throws Exception {
        String msg = "";
        LogEventType type = LogEventType.Http;
        LoggingProfile profile = null;

        String result = LogUtil.getLogMessage(msg, type, profile);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertNotNull(result);
    }

    /**
     * Run the boolean isTextMimeType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsTextMimeType_1()
            throws Exception {
        String mimeType = "application/xml";

        boolean result = LogUtil.isTextMimeType(mimeType);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertTrue(result);
    }

    /**
     * Run the boolean isTextMimeType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsTextMimeType_2()
            throws Exception {
        String mimeType = "text/html";

        boolean result = LogUtil.isTextMimeType(mimeType);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertTrue(result);
    }

    /**
     * Run the boolean isTextMimeType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:21 PM
     */
    @Test
    public void testIsTextMimeType_3()
            throws Exception {
        String mimeType = "application/json";

        boolean result = LogUtil.isTextMimeType(mimeType);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.logging.LogUtil
        assertTrue(result);
    }

}