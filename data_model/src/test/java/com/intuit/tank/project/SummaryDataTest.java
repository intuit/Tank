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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.SummaryData;

/**
 * The class <code>SummaryDataTest</code> contains tests for the class <code>{@link SummaryData}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class SummaryDataTest {
    /**
     * Run the SummaryData() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSummaryData_1()
        throws Exception {
        SummaryData result = new SummaryData();
        assertNotNull(result);
    }

    /**
     * Run the int compareTo(SummaryData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        SummaryData o = new SummaryData();
        o.setPageId("");

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
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
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
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the int getJobId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobId_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        int result = fixture.getJobId();

        assertEquals(1, result);
    }

    /**
     * Run the double getKurtosis() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetKurtosis_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getKurtosis();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getMax() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetMax_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getMax();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getMean() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetMean_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getMean();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getMin() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetMin_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getMin();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the String getPageId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPageId_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        String result = fixture.getPageId();

        assertEquals("", result);
    }

    /**
     * Run the double getPercentile10() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile10_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile10();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile20() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile20_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile20();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile30() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile30_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile30();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile40() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile40_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile40();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile50() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile50_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile50();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile60() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile60_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile60();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile70() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile70_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile70();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile80() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile80_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile80();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile90() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile90_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile90();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile95() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile95_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile95();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getPercentile99() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPercentile99_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getPercentile99();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the int getSampleSize() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSampleSize_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        int result = fixture.getSampleSize();

        assertEquals(1, result);
    }

    /**
     * Run the double getSkewness() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSkewness_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getSkewness();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getSttDev() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSttDev_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getSttDev();

        assertEquals(1.0, result, 0.1);
    }

    /**
     * Run the double getVarience() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVarience_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        double result = fixture.getVarience();

        assertEquals(1.0, result, 0.1);
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
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        int result = fixture.hashCode();

        assertEquals(-1883288385, result);
    }

    /**
     * Run the void setJobId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJobId_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        int jobId = 1;

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setKurtosis(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetKurtosis_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double kurtosis = 1.0;

        fixture.setKurtosis(kurtosis);

    }

    /**
     * Run the void setMax(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetMax_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double max = 1.0;

        fixture.setMax(max);

    }

    /**
     * Run the void setMean(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetMean_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double mean = 1.0;

        fixture.setMean(mean);

    }

    /**
     * Run the void setMin(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetMin_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double min = 1.0;

        fixture.setMin(min);

    }

    /**
     * Run the void setPageId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPageId_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        String pageId = "";

        fixture.setPageId(pageId);

    }

    /**
     * Run the void setPercentile10(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile10_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile10 = 1.0;

        fixture.setPercentile10(percentile10);

    }

    /**
     * Run the void setPercentile20(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile20_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile20 = 1.0;

        fixture.setPercentile20(percentile20);

    }

    /**
     * Run the void setPercentile30(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile30_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile30 = 1.0;

        fixture.setPercentile30(percentile30);

    }

    /**
     * Run the void setPercentile40(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile40_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile40 = 1.0;

        fixture.setPercentile40(percentile40);

    }

    /**
     * Run the void setPercentile50(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile50_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile50 = 1.0;

        fixture.setPercentile50(percentile50);

    }

    /**
     * Run the void setPercentile60(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile60_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile60 = 1.0;

        fixture.setPercentile60(percentile60);

    }

    /**
     * Run the void setPercentile70(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile70_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile70 = 1.0;

        fixture.setPercentile70(percentile70);

    }

    /**
     * Run the void setPercentile80(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile80_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile80 = 1.0;

        fixture.setPercentile80(percentile80);

    }

    /**
     * Run the void setPercentile90(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile90_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile90 = 1.0;

        fixture.setPercentile90(percentile90);

    }

    /**
     * Run the void setPercentile95(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile95_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile95 = 1.0;

        fixture.setPercentile95(percentile95);

    }

    /**
     * Run the void setPercentile99(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPercentile99_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double percentile99 = 1.0;

        fixture.setPercentile99(percentile99);

    }

    /**
     * Run the void setSampleSize(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSampleSize_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        int sampleSize = 1;

        fixture.setSampleSize(sampleSize);

    }

    /**
     * Run the void setSkewness(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSkewness_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double skewness = 1.0;

        fixture.setSkewness(skewness);

    }

    /**
     * Run the void setSttDev(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSttDev_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double sttDev = 1.0;

        fixture.setSttDev(sttDev);

    }

    /**
     * Run the void setVarience(double) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVarience_1()
        throws Exception {
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);
        double varience = 1.0;

        fixture.setVarience(varience);

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
        SummaryData fixture = new SummaryData();
        fixture.setPercentile10(1.0);
        fixture.setJobId(1);
        fixture.setVarience(1.0);
        fixture.setPercentile60(1.0);
        fixture.setPercentile95(1.0);
        fixture.setSkewness(1.0);
        fixture.setPercentile40(1.0);
        fixture.setPercentile90(1.0);
        fixture.setMax(1.0);
        fixture.setPercentile20(1.0);
        fixture.setSampleSize(1);
        fixture.setPercentile80(1.0);
        fixture.setMean(1.0);
        fixture.setKurtosis(1.0);
        fixture.setPercentile70(1.0);
        fixture.setMin(1.0);
        fixture.setPageId("");
        fixture.setPercentile30(1.0);
        fixture.setSttDev(1.0);
        fixture.setPercentile99(1.0);
        fixture.setPercentile50(1.0);

        String result = fixture.toString();

    }
}