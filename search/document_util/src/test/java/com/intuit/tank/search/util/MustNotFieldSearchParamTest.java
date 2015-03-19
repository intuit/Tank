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

import com.intuit.tank.search.util.MustNotFieldSearchParam;

import static org.junit.Assert.*;

/**
 * The class <code>MustNotFieldSearchParamTest</code> contains tests for the class
 * <code>{@link MustNotFieldSearchParam}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:27 AM
 */
public class MustNotFieldSearchParamTest {
    /**
     * Run the MustNotFieldSearchParam(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testMustNotFieldSearchParam_1()
            throws Exception {
        String fieldName = "";
        String value = "";

        MustNotFieldSearchParam result = new MustNotFieldSearchParam(fieldName, value);

        assertNotNull(result);
        assertEquals("-:", result.toString());
        assertEquals("-:", result.getQuery());
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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");
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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");
        Object obj = new MustNotFieldSearchParam("", "");

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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");
        Object obj = new MustNotFieldSearchParam("", "");

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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");
        Object obj = new MustNotFieldSearchParam("", "");

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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");

        Query result = fixture.getLuceneQuery();

        assertNotNull(result);
        assertEquals("-", result.toString());
        assertEquals(1.0f, result.getBoost(), 1.0f);
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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");

        String result = fixture.getQuery();

        assertEquals("-:", result);
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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");

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
        MustNotFieldSearchParam fixture = new MustNotFieldSearchParam("", "");

        String result = fixture.toString();

        assertEquals("-:", result);
    }
}