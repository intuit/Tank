package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.*;

import com.intuit.tank.persistence.databases.BucketDataItem;

import static org.junit.Assert.*;

/**
 * The class <code>BucketDataItemTest</code> contains tests for the class <code>{@link BucketDataItem}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:32 AM
 */
public class BucketDataItemTest {
    /**
     * Run the BucketDataItem(int,Date,DescriptiveStatistics) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testBucketDataItem_1()
            throws Exception {
        int period = 1;
        Date startTime = new Date();
        DescriptiveStatistics stats = new DescriptiveStatistics();

        BucketDataItem result = new BucketDataItem(period, startTime, stats);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
    }

    /**
     * Run the int getPeriod() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testGetPeriod_1()
            throws Exception {
        BucketDataItem fixture = new BucketDataItem(1, new Date(), new DescriptiveStatistics());

        int result = fixture.getPeriod();

        assertEquals(1, result);
    }

    /**
     * Run the Date getStartTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testGetStartTime_1()
            throws Exception {
        BucketDataItem fixture = new BucketDataItem(1, new Date(), new DescriptiveStatistics());

        Date result = fixture.getStartTime();

        assertNotNull(result);
    }

    /**
     * Run the DescriptiveStatistics getStats() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testGetStats_1()
            throws Exception {
        BucketDataItem fixture = new BucketDataItem(1, new Date(), new DescriptiveStatistics());

        DescriptiveStatistics result = fixture.getStats();

        assertNotNull(result);
        assertEquals(
                "DescriptiveStatistics:\nn: 0\nmin: NaN\nmax: NaN\nmean: NaN\nstd dev: NaN\nmedian: NaN\nskewness: NaN\nkurtosis: NaN\n",
                result.toString());
        assertEquals(Double.NaN, result.getMax(), 1.0);
        assertEquals(Double.NaN, result.getVariance(), 1.0);
        assertEquals(Double.NaN, result.getMean(), 1.0);
        assertEquals(-1, result.getWindowSize());
        assertEquals(0.0, result.getSumsq(), 1.0);
        assertEquals(Double.NaN, result.getKurtosis(), 1.0);
        assertEquals(0.0, result.getSum(), 1.0);
        assertEquals(Double.NaN, result.getSkewness(), 1.0);
        assertEquals(Double.NaN, result.getPopulationVariance(), 1.0);
        assertEquals(Double.NaN, result.getStandardDeviation(), 1.0);
        assertEquals(Double.NaN, result.getGeometricMean(), 1.0);
        assertEquals(0L, result.getN());
        assertEquals(Double.NaN, result.getMin(), 1.0);
    }
}