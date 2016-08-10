/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.intuit.tank.persistence.databases.BucketDataItem;
import com.intuit.tank.persistence.databases.MetricsCalculator;

/**
 * MetricsCalculatorTest
 * 
 * @author dangleton
 * 
 */
public class MetricsCalculatorTest {
    private static final Logger LOG = LogManager.getLogger(MetricsCalculatorTest.class);

    /**
     * Run the MetricsCalculator() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testMetricsCalculator_1()
            throws Exception {
        MetricsCalculator result = new MetricsCalculator();
        assertNotNull(result);
    }

    /**
     * Run the Map<String, Map<Date, BucketDataItem>> getBucketItems() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testGetBucketItems_1()
            throws Exception {
        MetricsCalculator fixture = new MetricsCalculator();

        Map<String, Map<Date, BucketDataItem>> result = fixture.getBucketItems();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Map<String, DescriptiveStatistics> getSummaryResults() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testGetSummaryResults_1()
            throws Exception {
        MetricsCalculator fixture = new MetricsCalculator();

        Map<String, DescriptiveStatistics> result = fixture.getSummaryResults();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
