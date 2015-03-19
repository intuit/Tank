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

import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

import com.intuit.tank.project.PeriodicData;

/**
 * The class <code>PeriodicDataTest</code> contains tests for the class <code>{@link PeriodicData}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class PeriodicDataTest {
    /**
     * Run the PeriodicData() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testPeriodicData_1()
        throws Exception {
        PeriodicData result = new PeriodicData();
        assertNotNull(result);
    }

    /**
     * Run the int compareTo(PeriodicData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
        PeriodicData o = new PeriodicData();
        o.setTimestamp(new Date());

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

        int result = fixture.getJobId();

        assertEquals(1, result);
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

        String result = fixture.getPageId();

        assertEquals("", result);
    }

    /**
     * Run the int getPeriod() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPeriod_1()
        throws Exception {
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

        int result = fixture.getPeriod();

        assertEquals(1, result);
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

        int result = fixture.getSampleSize();

        assertEquals(1, result);
    }

    /**
     * Run the Date getTimestamp() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTimestamp_1()
        throws Exception {
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

        Date result = fixture.getTimestamp();

        assertNotNull(result);
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

        int result = fixture.hashCode();

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
        int jobId = 1;

        fixture.setJobId(jobId);

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
        String pageId = "";

        fixture.setPageId(pageId);

    }

    /**
     * Run the void setPeriod(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPeriod_1()
        throws Exception {
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
        int period = 1;

        fixture.setPeriod(period);

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
        int sampleSize = 1;

        fixture.setSampleSize(sampleSize);

    }

    /**
     * Run the void setTimestamp(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetTimestamp_1()
        throws Exception {
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());
        Date timestamp = new Date();

        fixture.setTimestamp(timestamp);

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
        PeriodicData fixture = new PeriodicData();
        fixture.setMin(1.0);
        fixture.setSampleSize(1);
        fixture.setPeriod(1);
        fixture.setPageId("");
        fixture.setMax(1.0);
        fixture.setMean(1.0);
        fixture.setJobId(1);
        fixture.setTimestamp(new Date());

        String result = fixture.toString();

    }
}