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

import java.util.List;

import org.junit.*;

import com.intuit.tank.admin.LogViewer;

import static org.junit.Assert.*;

/**
 * The class <code>LogViewerTest</code> contains tests for the class <code>{@link LogViewer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class LogViewerTest {
    /**
     * Run the LogViewer() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testLogViewer_1()
        throws Exception {
        LogViewer result = new LogViewer();
        assertNotNull(result);
    }

    /**
     * Run the String getCurrentLogFile() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetCurrentLogFile_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        String result = fixture.getCurrentLogFile();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
        assertNotNull(result);
    }

    /**
     * Run the String getLogFileUrl() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetLogFileUrl_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        String result = fixture.getLogFileUrl();
    }

    /**
     * Run the String getLogFileUrl() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetLogFileUrl_2()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        String result = fixture.getLogFileUrl();

    }

    /**
     * Run the List<String> getLogFiles() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetLogFiles_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        List<String> result = fixture.getLogFiles();

    }

    /**
     * Run the int getNumLines() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetNumLines_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        int result = fixture.getNumLines();
    }

    /**
     * Run the int getPollSeconds() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPollSeconds_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        int result = fixture.getPollSeconds();
    }

    /**
     * Run the void init() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInit_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void init() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInit_2()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void init() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInit_3()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void init() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInit_4()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void init() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInit_5()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void init() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInit_6()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");

        fixture.init();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void setCurrentLogFile(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetCurrentLogFile_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");
        String currentLogFile = "";

        fixture.setCurrentLogFile(currentLogFile);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void setNumLines(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetNumLines_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");
        int numLines = 1;

        fixture.setNumLines(numLines);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }

    /**
     * Run the void setPollSeconds(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetPollSeconds_1()
        throws Exception {
        LogViewer fixture = new LogViewer();
        fixture.setNumLines(1);
        fixture.setPollSeconds(1);
        fixture.setCurrentLogFile("");
        int pollSeconds = 1;

        fixture.setPollSeconds(pollSeconds);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.admin.LogViewer.setNumLines(LogViewer.java:119)
    }
}