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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
        String timestamp = ReportUtil.getTimestamp(new Date());
        System.out.println(timestamp);

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
        assertEquals("NaN", result[2]);
        assertEquals("NaN", result[3]);
        assertEquals("NaN", result[4]);
        assertEquals("NaN", result[5]);
        assertEquals("NaN", result[6]);
        assertEquals("NaN", result[7]);
        assertEquals("NaN", result[8]);
        assertEquals("NaN", result[9]);
        assertEquals("NaN", result[10]);
        assertEquals("NaN", result[11]);
        assertEquals("NaN", result[12]);
        assertEquals("NaN", result[13]);
        assertEquals("NaN", result[14]);
        assertEquals("NaN", result[15]);
        assertEquals("NaN", result[16]);
        assertEquals("NaN", result[17]);
        assertEquals("NaN", result[18]);
        assertEquals("NaN", result[19]);
        assertEquals("NaN", result[20]);
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
        assertEquals("NaN", result[2]);
        assertEquals("NaN", result[3]);
        assertEquals("NaN", result[4]);
        assertEquals("NaN", result[5]);
        assertEquals("NaN", result[6]);
        assertEquals("NaN", result[7]);
        assertEquals("NaN", result[8]);
        assertEquals("NaN", result[9]);
        assertEquals("NaN", result[10]);
        assertEquals("NaN", result[11]);
        assertEquals("NaN", result[12]);
        assertEquals("NaN", result[13]);
        assertEquals("NaN", result[14]);
        assertEquals("NaN", result[15]);
        assertEquals("NaN", result[16]);
        assertEquals("NaN", result[17]);
        assertEquals("NaN", result[18]);
        assertEquals("NaN", result[19]);
        assertEquals("NaN", result[20]);
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