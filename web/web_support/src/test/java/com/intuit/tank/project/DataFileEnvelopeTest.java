package com.intuit.tank.project;

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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.DataFileEnvelope;

/**
 * The class <code>DataFileEnvelopeTest</code> contains tests for the class <code>{@link DataFileEnvelope}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class DataFileEnvelopeTest {
    /**
     * Run the DataFileEnvelope() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDataFileEnvelope_1()
            throws Exception {

        DataFileEnvelope result = new DataFileEnvelope();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:18)
        assertNotNull(result);
    }

    /**
     * Run the DataFileEnvelope(DataFile) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDataFileEnvelope_2()
            throws Exception {
        DataFile df = new DataFile();

        DataFileEnvelope result = new DataFileEnvelope(df);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the String getComments() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetComments_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);
fixture.setComments("test");
        String result = fixture.getComments();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the DataFile getDataFile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetDataFile_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);

        DataFile result = fixture.getDataFile();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the String getFileName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetFileName_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setFileName("test");
        fixture.setChecked(true);

        String result = fixture.getFileName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the String getPath() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetPath_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);
        fixture.setPath("path");
        String result = fixture.getPath();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the boolean isChecked() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsChecked_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);

        boolean result = fixture.isChecked();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
        assertTrue(result);
    }

    /**
     * Run the boolean isChecked() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsChecked_2()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(false);

        boolean result = fixture.isChecked();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
        assertTrue(!result);
    }

    /**
     * Run the void setChecked(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetChecked_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);
        boolean checked = true;

        fixture.setChecked(checked);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
    }

    /**
     * Run the void setComments(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetComments_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);
        String comments = "";

        fixture.setComments(comments);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
    }

    /**
     * Run the void setDataFile(DataFile) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetDataFile_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);
        DataFile dataFile = new DataFile();

        fixture.setDataFile(dataFile);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
    }

    /**
     * Run the void setFileName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetFileName_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);
        String fileName = "";

        fixture.setFileName(fileName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
    }

    /**
     * Run the void setPath(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetPath_1()
            throws Exception {
        DataFileEnvelope fixture = new DataFileEnvelope(new DataFile());
        fixture.setChecked(true);
        String path = "";

        fixture.setPath(path);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.DataFileEnvelope.<init>(DataFileEnvelope.java:15)
    }
}