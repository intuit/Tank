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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>GenericFunctionsTest</code> contains tests for the class <code>{@link GenericFunctions}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:21 PM
 */
public class GenericFunctionsTest {

    @Test
    public void testGenericFunctions_1() {
        GenericFunctions result = new GenericFunctions();
        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_1() {
        String[] values = new String[] { null, null, null};
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);

        assertNotNull(result);
    }

    @Test
    public void testExecuteFunction_2() {
        String[] values = new String[] { null, null, "getfiledata", "testfile", "1"};
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);
        assertEquals("", result);
    }

    @Test
    public void testExecuteFunction_3() {
        String[] values = new String[] { null, null, "getcsv", "1"};
        Variables variables = new Variables();

        String result = GenericFunctions.executeFunction(values, variables);
        assertEquals("", result);
    }

    @AfterEach
    public void cleanup() {
        CSVReader.reset();
    }

    @Test
    public void testIsValid() {
        assertTrue(GenericFunctions.isValid(new String[] { "#function", "generic", "getcsv", "0" }));
        assertTrue(GenericFunctions.isValid(new String[] { "#function", "generic", "getfiledata", "file", "0" }));
        assertTrue(GenericFunctions.isValid(new String[] { "#function", "generic", "getssn" }));
        assertFalse(GenericFunctions.isValid(new String[] { "#function", "generic", "bogus" }));
        // missing arguments are invalid rather than an error
        assertFalse(GenericFunctions.isValid(new String[] { "#function", "generic", "getcsv" }));
        assertFalse(GenericFunctions.isValid(new String[] { "#function", "generic" }));
    }

    @Test
    public void testExecuteFunctionUnknownReturnsEmpty() {
        assertEquals("", GenericFunctions.executeFunction(new String[] { null, null, "bogus" }, new Variables()));
    }

    @Test
    public void testGetFileDataReadsColumnsThenNextLine() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            files.write("data.csv", "One,1,I\nTwo,2,II\nThree,3,III\n");
            // the file name is split on dots, the last value is the column index
            String file = files.relativeName("data");
            Variables variables = new Variables();

            assertEquals("1", GenericFunctions.executeFunction(new String[] { null, null, "getfiledata", file, "csv", "1" },
                    variables));
            assertEquals("One", GenericFunctions.executeFunction(new String[] { null, null, "getfiledata", file, "csv", "0" },
                    variables));
            // column 1 of the current line was consumed, so the next line is read
            assertEquals("2", GenericFunctions.executeFunction(new String[] { null, null, "getfiledata", file, "csv", "1" },
                    variables));
            // an index past the last column moves on to the next line and returns nothing
            assertEquals("", GenericFunctions.executeFunction(new String[] { null, null, "getfiledata", file, "csv", "9" },
                    variables));
            assertEquals("III", GenericFunctions.executeFunction(new String[] { null, null, "getfiledata", file, "csv", "2" },
                    variables));
        }
    }

    @Test
    public void testGetCsvReadsDefaultFile() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            TestDataFiles.useAsDefaultCsv(files.write("csv-data.txt", "One,1\nTwo,2\n"));
            Variables variables = new Variables();

            assertEquals("One", GenericFunctions.executeFunction(new String[] { null, null, "getcsv", "0" }, variables));
            assertEquals("1", GenericFunctions.executeFunction(new String[] { null, null, "getcsv", "1" }, variables));
            // both columns of the first line were consumed, so the next line is read
            assertEquals("Two", GenericFunctions.getCSVData(0));
            // an index past the last column moves on to the next line, which does not exist
            assertEquals("", GenericFunctions.executeFunction(new String[] { null, null, "getcsv", "5" }, variables));
        }
    }

    @Test
    public void testGetSsn() throws Exception {
        // the start of the range is only read on the first call, so reset it to make the test independent of order
        FieldUtils.writeStaticField(GenericFunctions.class, "lastSSN", -1, true);
        try {
            String[] values = new String[] { null, null, "getssn", "899999000" };
            String first = GenericFunctions.executeFunction(values, new Variables());
            String second = GenericFunctions.executeFunction(values, new Variables());

            assertTrue(first.matches("899-99-9\\d{3}"), first);
            assertTrue(second.matches("899-99-9\\d{3}"), second);
            assertNotEquals(first, second);
        } finally {
            FieldUtils.writeStaticField(GenericFunctions.class, "lastSSN", -1, true);
        }
    }
}
