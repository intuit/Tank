package com.intuit.tank.search.util;

/*
 * #%L
 * DocumentUtil
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.lucene.search.Query;
import org.junit.*;

import com.intuit.tank.search.util.FieldSearchParam;

import static org.junit.Assert.*;

/**
 * The class <code>FieldSearchParamTest</code> contains tests for the class <code>{@link FieldSearchParam}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:27 AM
 */
public class FieldSearchParamTest {
    /**
     * Run the FieldSearchParam(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testFieldSearchParam_1()
            throws Exception {
        String fieldName = "";
        String value = "";

        FieldSearchParam result = new FieldSearchParam(fieldName, value);

        assertNotNull(result);
        assertEquals(":", result.toString());
        assertEquals(":", result.getQuery());
        assertEquals(null, result.getLuceneQuery());
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");
        Object obj = new FieldSearchParam("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");
        Object obj = new FieldSearchParam("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testEquals_4()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");
        Object obj = new FieldSearchParam("", "");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the Query getLuceneQuery() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testGetLuceneQuery_1()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "*");

        Query result = fixture.getLuceneQuery();

        assertNotNull(result);
        assertEquals("*", result.toString());
        assertEquals(1.0f, result.getBoost(), 1.0f);
    }

    /**
     * Run the Query getLuceneQuery() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testGetLuceneQuery_2()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");

        Query result = fixture.getLuceneQuery();

        assertEquals(null, result);
    }

    /**
     * Run the String getQuery() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testGetQuery_1()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");

        String result = fixture.getQuery();

        assertEquals(":", result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");

        int result = fixture.hashCode();

        assertEquals(5445, result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        FieldSearchParam fixture = new FieldSearchParam("", "");

        String result = fixture.toString();

        assertEquals(":", result);
    }
}