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

import org.junit.jupiter.api.*;

import com.intuit.tank.harness.logging.LogEvent;
import com.intuit.tank.harness.logging.ThreadLocalLogEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ThreadLocalLogEventTest</code> contains tests for the class <code>{@link ThreadLocalLogEvent}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class ThreadLocalLogEventTest {
    /**
     * Run the LogEvent initialValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testInitialValue_1()
            throws Exception {
        ThreadLocalLogEvent fixture = new ThreadLocalLogEvent();

        LogEvent result = fixture.initialValue();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.APITestHarness.<clinit>(APITestHarness.java:57)
        // at com.intuit.tank.harness.logging.ThreadLocalLogEvent.initialValue(ThreadLocalLogEvent.java:14)
        assertNotNull(result);
    }
}