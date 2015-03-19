package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.project.DataFile;

/**
 * The class <code>DataFileTest</code> contains tests for the class <code>{@link DataFile}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class DataFileTest {
    /**
     * Run the DataFile() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testDataFile_1()
        throws Exception {
        DataFile result = new DataFile();
        assertNotNull(result);
    }

    /**
     * Run the int compareTo(DataFile) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");
        DataFile o = new DataFile();
        o.setPath("");

        int result = fixture.compareTo(o);

        assertEquals(0, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");
        Object obj = new DataFile();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");
        Object obj = new DataFile();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");

        String result = fixture.getComments();

        assertEquals("", result);
    }

    /**
     * Run the String getFileName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetFileName_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");

        String result = fixture.getFileName();

        assertEquals("", result);
    }

    /**
     * Run the String getPath() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPath_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");

        String result = fixture.getPath();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");
        String comments = "";

        fixture.setComments(comments);

    }

    /**
     * Run the void setFileName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetFileName_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");
        String fileName = "";

        fixture.setFileName(fileName);

    }

    /**
     * Run the void setPath(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPath_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");
        String path = "";

        fixture.setPath(path);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        DataFile fixture = new DataFile();
        fixture.setPath("");
        fixture.setFileName("");
        fixture.setComments("");

        String result = fixture.toString();

        assertEquals("", result);
    }
}