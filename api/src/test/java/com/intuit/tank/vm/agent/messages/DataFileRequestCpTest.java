package com.intuit.tank.vm.agent.messages;

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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>DataFileRequestCpTest</code> contains tests for the class <code>{@link DataFileRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class DataFileRequestCpTest {
    /**
     * Run the DataFileRequest() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testDataFileRequest_1()
            throws Exception {

        DataFileRequest result = new DataFileRequest();

        assertNotNull(result);
        assertFalse(result.isDefaultDataFile());
        assertNull(result.getFileName());
        assertNull(result.getFileUrl());
    }

    /**
     * Run the DataFileRequest(String,boolean,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testDataFileRequest_2()
            throws Exception {
        String fileName = "";
        boolean isDefaultDataFile = true;
        String fileUrl = "";

        DataFileRequest result = new DataFileRequest(fileName, isDefaultDataFile, fileUrl);

        assertNotNull(result);
        assertTrue(result.isDefaultDataFile());
        assertEquals("", result.getFileName());
        assertEquals("", result.getFileUrl());
    }

    /**
     * Run the String getFileName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetFileName_1()
            throws Exception {
        DataFileRequest fixture = new DataFileRequest("", true, "");

        String result = fixture.getFileName();

        assertEquals("", result);
    }

    /**
     * Run the String getFileUrl() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetFileUrl_1()
            throws Exception {
        DataFileRequest fixture = new DataFileRequest("", true, "");

        String result = fixture.getFileUrl();

        assertEquals("", result);
    }

    /**
     * Run the boolean isDefaultDataFile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testisDefaultDataFile_1()
            throws Exception {
        DataFileRequest fixture = new DataFileRequest("", true, "");

        boolean result = fixture.isDefaultDataFile();

        assertTrue(result);
    }

    /**
     * Run the boolean isDefaultDataFile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testisDefaultDataFile_2()
            throws Exception {
        DataFileRequest fixture = new DataFileRequest("", false, "");

        boolean result = fixture.isDefaultDataFile();

        assertFalse(result);
    }
}