package com.intuit.tank.vm.common.util;

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

import java.util.Date;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.*;

import com.intuit.tank.vm.common.util.ReportUtil;

import static org.junit.Assert.*;

/**
 * The class <code>ReportUtilCpTest</code> contains tests for the class <code>{@link ReportUtil}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class ReportUtilCpTest {
    /**
     * Run the ReportUtil() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testReportUtil_1()
            throws Exception {
        ReportUtil result = new ReportUtil();
        assertNotNull(result);
    }

    /**
     * Run the String getBucketedTableName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetBucketedTableName_1()
            throws Exception {
        String jobId = "";

        String result = ReportUtil.getBucketedTableName(jobId);


        assertNotNull(result);
    }

    /**
     * Run the String[] getSummaryData(String,DescriptiveStatistics) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSummaryData_1()
            throws Exception {
        String key = "";
        DescriptiveStatistics stats = new DescriptiveStatistics();

        String[] result = ReportUtil.getSummaryData(key, stats);

        assertNotNull(result);
        assertEquals(23, result.length);
        assertEquals("", result[0]);
        assertEquals("0", result[1]);
        assertEquals("�", result[2]);
        assertEquals("�", result[3]);
        assertEquals("�", result[4]);
        assertEquals("�", result[5]);
        assertEquals("�", result[6]);
        assertEquals("�", result[7]);
        assertEquals("�", result[8]);
        assertEquals("�", result[9]);
        assertEquals("�", result[10]);
        assertEquals("�", result[11]);
        assertEquals("�", result[12]);
        assertEquals("�", result[13]);
        assertEquals("�", result[14]);
        assertEquals("�", result[15]);
        assertEquals("�", result[16]);
        assertEquals("�", result[17]);
        assertEquals("�", result[18]);
        assertEquals("�", result[19]);
        assertEquals("�", result[20]);
        assertEquals(null, result[21]);
        assertEquals(null, result[22]);
    }

    /**
     * Run the String[] getSummaryData(String,DescriptiveStatistics) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSummaryData_2()
            throws Exception {
        String key = "";
        DescriptiveStatistics stats = new DescriptiveStatistics();

        String[] result = ReportUtil.getSummaryData(key, stats);

        assertNotNull(result);
        assertEquals(23, result.length);
        assertEquals("", result[0]);
        assertEquals("0", result[1]);
        assertEquals("�", result[2]);
        assertEquals("�", result[3]);
        assertEquals("�", result[4]);
        assertEquals("�", result[5]);
        assertEquals("�", result[6]);
        assertEquals("�", result[7]);
        assertEquals("�", result[8]);
        assertEquals("�", result[9]);
        assertEquals("�", result[10]);
        assertEquals("�", result[11]);
        assertEquals("�", result[12]);
        assertEquals("�", result[13]);
        assertEquals("�", result[14]);
        assertEquals("�", result[15]);
        assertEquals("�", result[16]);
        assertEquals("�", result[17]);
        assertEquals("�", result[18]);
        assertEquals("�", result[19]);
        assertEquals("�", result[20]);
        assertEquals(null, result[21]);
        assertEquals(null, result[22]);
    }

    /**
     * Run the String[] getSummaryHeaders() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSummaryHeaders_1()
            throws Exception {

        String[] result = ReportUtil.getSummaryHeaders();

        assertNotNull(result);
        assertEquals(23, result.length);
        assertEquals("Page ID", result[0]);
        assertEquals("Page Name", result[1]);
        assertEquals("Page Index", result[2]);
        assertEquals("Sample Size", result[3]);
        assertEquals("Mean", result[4]);
        assertEquals("Median", result[5]);
        assertEquals("Min", result[6]);
        assertEquals("Max", result[7]);
        assertEquals("Std Dev", result[8]);
        assertEquals("Kurtosis", result[9]);
        assertEquals("Skewness", result[10]);
        assertEquals("Varience", result[11]);
        assertEquals("10th Percentile", result[12]);
        assertEquals("20th Percentile", result[13]);
        assertEquals("30th Percentile", result[14]);
        assertEquals("40th Percentile", result[15]);
        assertEquals("50th Percentile", result[16]);
        assertEquals("60th Percentile", result[17]);
        assertEquals("70th Percentile", result[18]);
        assertEquals("80th Percentile", result[19]);
        assertEquals("90th Percentile", result[20]);
        assertEquals("95th Percentile", result[21]);
        assertEquals("99th Percentile", result[22]);
    }

    /**
     * Run the String[] getSummaryHeaders() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSummaryHeaders_2()
            throws Exception {

        String[] result = ReportUtil.getSummaryHeaders();

        assertNotNull(result);
        assertEquals(23, result.length);
        assertEquals("Page ID", result[0]);
        assertEquals("Page Name", result[1]);
        assertEquals("Page Index", result[2]);
        assertEquals("Sample Size", result[3]);
        assertEquals("Mean", result[4]);
        assertEquals("Median", result[5]);
        assertEquals("Min", result[6]);
        assertEquals("Max", result[7]);
        assertEquals("Std Dev", result[8]);
        assertEquals("Kurtosis", result[9]);
        assertEquals("Skewness", result[10]);
        assertEquals("Varience", result[11]);
        assertEquals("10th Percentile", result[12]);
        assertEquals("20th Percentile", result[13]);
        assertEquals("30th Percentile", result[14]);
        assertEquals("40th Percentile", result[15]);
        assertEquals("50th Percentile", result[16]);
        assertEquals("60th Percentile", result[17]);
        assertEquals("70th Percentile", result[18]);
        assertEquals("80th Percentile", result[19]);
        assertEquals("90th Percentile", result[20]);
        assertEquals("95th Percentile", result[21]);
        assertEquals("99th Percentile", result[22]);
    }

    /**
     * Run the String getSummaryTableName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSummaryTableName_1()
            throws Exception {
        String jobId = "";

        String result = ReportUtil.getSummaryTableName(jobId);

        assertNotNull(result);
    }

    /**
     * Run the String getTimestamp(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetTimestamp_1()
            throws Exception {
        Date date = new Date();

        String result = ReportUtil.getTimestamp(date);

    }
}