/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.harness.functions;

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

import java.nio.file.Files;

import com.intuit.tank.harness.test.CheckedKillScriptException;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JexlFileFunctionsTest
 * 
 * @author dangleton
 * 
 */
public class JexlIOFunctionsTest {

    @AfterEach
    public void cleanup() {
        JexlIOFunctions.resetStatics();
    }

    @Test
    public void testVisitRegistersIoFunctions() {
        JexlIOFunctions functions = new JexlIOFunctions();
        MapContext context = new MapContext();
        functions.visit(context);
        assertSame(functions, context.get("ioFunctions"));
    }

    @Test
    public void testGetCSVDataFromFileReadsColumnsThenNextLine() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            String fileName = files.write("data.csv", "One,1,I\nTwo,2,II\n");
            JexlIOFunctions functions = new JexlIOFunctions();

            assertEquals("One", functions.getCSVData(fileName));
            assertEquals("1", functions.getCSVData(fileName, 1));
            // column 0 of the current line was consumed, so the next line is read
            assertEquals("Two", functions.getCSVData(fileName, "0"));
            assertEquals("II", functions.getCSVData(fileName, 2, false));
        }
    }

    @Test
    public void testGetCSVDataFromFileThrowsWhenExhausted() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            String fileName = files.write("data.csv", "One,1\n");
            JexlIOFunctions functions = new JexlIOFunctions();

            assertEquals("One", functions.getCSVData(fileName, 0, false));
            CheckedKillScriptException exception = assertThrows(CheckedKillScriptException.class,
                    () -> functions.getCSVData(fileName, 0, false));
            assertTrue(exception.getMessage().contains("Next line in CSV file is null"));
        }
    }

    @Test
    public void testGetCSVDataFromFileLoops() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            String fileName = files.write("data.csv", "One,1\nTwo,2\n");
            JexlIOFunctions functions = new JexlIOFunctions();

            assertEquals("One", functions.getCSVData(fileName, 0, true));
            assertEquals("Two", functions.getCSVData(fileName, 0, true));
            assertEquals("One", functions.getCSVData(fileName, 0, true));
        }
    }

    @Test
    public void testGetCSVDataFromFileIndexOutOfRangeReturnsEmpty() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            String fileName = files.write("data.csv", "One,1\nTwo,2\n");
            JexlIOFunctions functions = new JexlIOFunctions();

            assertEquals("One", functions.getCSVData(fileName, 0, false));
            // an index past the last column moves on to the next line and returns nothing
            assertEquals("", functions.getCSVData(fileName, 5, false));
            assertEquals("2", functions.getCSVData(fileName, 1, false));
        }
    }

    @Test
    public void testGetCSVDataFromDefaultFile() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            TestDataFiles.useAsDefaultCsv(files.write("csv-data.txt", "One,1,I\nTwo,2,II\n"));
            JexlIOFunctions functions = new JexlIOFunctions();

            assertEquals("One", functions.getCSVData());
            assertEquals("1", functions.getCSVData(1));
            assertEquals("I", functions.getCSVData(2, false));
            // an index past the last column moves on to the next line and returns nothing
            assertEquals("", functions.getCSVData(7, false));
            assertEquals("Two", functions.getCSVData(0, false));
            CheckedKillScriptException exception = assertThrows(CheckedKillScriptException.class,
                    () -> functions.getCSVData(7, false));
            assertTrue(exception.getMessage().contains("Next line in CSV file is null"));
        }
    }

    @Test
    public void testGetCSVDataFromDefaultFileLoops() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            TestDataFiles.useAsDefaultCsv(files.write("csv-data.txt", "One,1\n"));
            JexlIOFunctions functions = new JexlIOFunctions();

            assertEquals("One", functions.getCSVData(0, true));
            assertEquals("One", functions.getCSVData(0, true));
        }
    }

    @Test
    public void testGetFileDataReadsAndCachesFile() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            String fileName = files.write("body.txt", "file contents");
            JexlIOFunctions functions = new JexlIOFunctions();

            assertEquals("file contents", functions.getFileData(fileName));
            // the content is cached, so a deleted file is still served
            Files.delete(files.path("body.txt"));
            assertEquals("file contents", functions.getFileData(fileName));
        }
    }

    @Test
    public void testGetFileDataOfDirectoryIsEmpty() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            JexlIOFunctions functions = new JexlIOFunctions();
            assertEquals("", functions.getFileData(files.mkdir("folder")));
        }
    }

    @Test
    public void testGetFileBytesReadsAndCachesFile() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            String fileName = files.write("body.bin", "bytes");
            JexlIOFunctions functions = new JexlIOFunctions();

            assertArrayEquals("bytes".getBytes(), functions.getFileBytes(fileName));
            Files.delete(files.path("body.bin"));
            assertArrayEquals("bytes".getBytes(), functions.getFileBytes(fileName));
        }
    }

    @Test
    public void testGetFileBytesOfDirectoryIsEmpty() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            JexlIOFunctions functions = new JexlIOFunctions();
            assertEquals(0, functions.getFileBytes(files.mkdir("folder")).length);
        }
    }

    @Test
    public void testJexlIOFunctions_1() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData();;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_2() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData(1);;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_3() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData(1, false);;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_4() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData("testFile");;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_5() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData("testFile", 1);;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_6() {
        JexlIOFunctions functions = new JexlIOFunctions();
        assertEquals("", functions.getFileData("testFile"));
    }

    @Test
    public void testJexlIOFunctions_7() {
        JexlIOFunctions functions = new JexlIOFunctions();
        assertNotNull(functions.getFileBytes("testFile"));
    }
}
