package com.intuit.tank.vm.common.util;

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

import org.apache.log4j.Logger;
import org.junit.*;

import com.intuit.tank.vm.common.util.MethodTimer;

import static org.junit.Assert.*;

/**
 * The class <code>MethodTimerCpTest</code> contains tests for the class <code>{@link MethodTimer}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class MethodTimerCpTest {
    /**
     * Run the MethodTimer(Logger,Class,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testMethodTimer_1()
            throws Exception {
        Logger log = Logger.getRootLogger();
        Class clazz = Object.class;
        String methodName = "";

        MethodTimer result = new MethodTimer(log, clazz, methodName);

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer(Logger,Class,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testMethodTimer_2()
            throws Exception {
        Logger log = Logger.getRootLogger();
        Class clazz = Object.class;
        String methodName = null;

        MethodTimer result = new MethodTimer(log, clazz, methodName);

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer end() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEnd_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.end();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer endAndLog() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testEndAndLog_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.endAndLog();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the String getMarkTimeMessage(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetMarkTimeMessage_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();
        String message = "";

        String result = fixture.getMarkTimeMessage(message);

        assertEquals(" ::  took 0 ms.", result);
    }

    /**
     * Run the String getMarkTimeMessage(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetMarkTimeMessage_2()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();
        String message = null;

        String result = fixture.getMarkTimeMessage(message);

        assertEquals(" ::  took 0 ms.", result);
    }

    /**
     * Run the String getNaturalTimeMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNaturalTimeMessage_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        String result = fixture.getNaturalTimeMessage();

        assertEquals(" took 0", result);
    }

    /**
     * Run the String getNaturalTimeMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNaturalTimeMessage_2()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        String result = fixture.getNaturalTimeMessage();

        assertEquals(" took 0", result);
    }

    /**
     * Run the String getTimeMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetTimeMessage_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        String result = fixture.getTimeMessage();

        assertEquals(" took 0 ms.", result);
    }

    /**
     * Run the String getTimeMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetTimeMessage_2()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        String result = fixture.getTimeMessage();

        assertEquals(" took 0 ms.", result);
    }

    /**
     * Run the MethodTimer logMark(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testLogMark_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer((Logger) null, Object.class, "");
        fixture.mark();
        String message = "";

        MethodTimer result = fixture.logMark(message);

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer logMark(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testLogMark_2()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();
        String message = "";

        MethodTimer result = fixture.logMark(message);

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer logTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testLogTime_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer((Logger) null, Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.logTime();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer logTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testLogTime_2()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.logTime();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer mark() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testMark_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.mark();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer mark() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testMark_2()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.mark();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer markAndLog() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testMarkAndLog_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.markAndLog();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer markAndLog(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testMarkAndLog_2()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();
        String message = "";

        MethodTimer result = fixture.markAndLog(message);

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }

    /**
     * Run the MethodTimer start() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testStart_1()
            throws Exception {
        MethodTimer fixture = new MethodTimer(Logger.getRootLogger(), Object.class, "");
        fixture.mark();

        MethodTimer result = fixture.start();

        assertNotNull(result);
        assertEquals(" took 0 ms.", result.getTimeMessage());
        assertEquals(" took 0", result.getNaturalTimeMessage());
    }
}