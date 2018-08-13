package com.intuit.tank.wrapper;

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

import java.io.InputStream;
import java.io.PipedInputStream;

import org.junit.jupiter.api.*;

import com.intuit.tank.wrapper.FileInputStreamWrapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>FileInputStreamWrapperTest</code> contains tests for the class <code>{@link FileInputStreamWrapper}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class FileInputStreamWrapperTest {
    /**
     * Run the FileInputStreamWrapper(String,InputStream) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testFileInputStreamWrapper_1()
        throws Exception {
        String fileName = "";
        InputStream inputStream = new PipedInputStream();

        FileInputStreamWrapper result = new FileInputStreamWrapper(fileName, inputStream);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.FileInputStreamWrapper.<init>(FileInputStreamWrapper.java:12)
        assertNotNull(result);
    }

    /**
     * Run the String getFileName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetFileName_1()
        throws Exception {
        FileInputStreamWrapper fixture = new FileInputStreamWrapper("", new PipedInputStream());

        String result = fixture.getFileName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.FileInputStreamWrapper.<init>(FileInputStreamWrapper.java:12)
        assertNotNull(result);
    }

    /**
     * Run the InputStream getInputStream() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetInputStream_1()
        throws Exception {
        FileInputStreamWrapper fixture = new FileInputStreamWrapper("", new PipedInputStream());

        InputStream result = fixture.getInputStream();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.FileInputStreamWrapper.<init>(FileInputStreamWrapper.java:12)
        assertNotNull(result);
    }

    /**
     * Run the void setFileName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetFileName_1()
        throws Exception {
        FileInputStreamWrapper fixture = new FileInputStreamWrapper("", new PipedInputStream());
        String fileName = "";

        fixture.setFileName(fileName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.FileInputStreamWrapper.<init>(FileInputStreamWrapper.java:12)
    }

    /**
     * Run the void setInputStream(InputStream) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetInputStream_1()
        throws Exception {
        FileInputStreamWrapper fixture = new FileInputStreamWrapper("", new PipedInputStream());
        InputStream inputStream = new PipedInputStream();

        fixture.setInputStream(inputStream);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.FileInputStreamWrapper.<init>(FileInputStreamWrapper.java:12)
    }
}