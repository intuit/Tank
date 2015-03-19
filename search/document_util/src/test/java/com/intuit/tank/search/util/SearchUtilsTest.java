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

import java.util.Date;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.junit.*;

import com.intuit.tank.search.util.SearchUtils;

import static org.junit.Assert.*;

/**
 * The class <code>SearchUtilsTest</code> contains tests for the class <code>{@link SearchUtils}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:27 AM
 */
public class SearchUtilsTest {

    /**
     * Run the String formatDate(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testFormatDate_1()
            throws Exception {
        Date d = new Date();

        String result = SearchUtils.formatDate(d);
        Assert.assertNotNull(result);

    }

    /**
     * Run the String makeCompoundField(String,String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testMakeCompoundField_1()
            throws Exception {
        String delimiter = "";

        String result = SearchUtils.makeCompoundField(delimiter);

        assertEquals("", result);
    }

    /**
     * Run the String makeCompoundField(String,String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testMakeCompoundField_2()
            throws Exception {
        String delimiter = "";

        String result = SearchUtils.makeCompoundField(delimiter);

        assertEquals("", result);
    }

    /**
     * Run the String makeCompoundField(String,String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testMakeCompoundField_3()
            throws Exception {
        String delimiter = "";

        String result = SearchUtils.makeCompoundField(delimiter);

        assertEquals("", result);
    }

    /**
     * Run the String makeCompoundField(String,String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testMakeCompoundField_4()
            throws Exception {
        String delimiter = "";

        String result = SearchUtils.makeCompoundField(delimiter);

        assertEquals("", result);
    }

    /**
     * Run the String makeCompoundField(String,String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testMakeCompoundField_5()
            throws Exception {
        String delimiter = "";

        String result = SearchUtils.makeCompoundField(delimiter);

        assertEquals("", result);
    }

    /**
     * Run the String padInt(Number,Padding) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testPadInt_1()
            throws Exception {
        Number num = new Byte((byte) 1);
        SearchUtils.Padding padding = SearchUtils.Padding.EIGHT;

        String result = SearchUtils.padInt(num, padding);

        assertEquals("00000001", result);
    }
}