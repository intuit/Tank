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

import com.intuit.tank.project.SummaryData;
import com.intuit.tank.project.SummaryDataBuilder;

/**
 * The class <code>SummaryDataBuilderTest</code> contains tests for the class <code>{@link SummaryDataBuilder}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class SummaryDataBuilderTest {
    /**
     * Run the SummaryDataBuilder() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSummaryDataBuilder_1()
        throws Exception {

        SummaryDataBuilder result = new SummaryDataBuilder();

        assertNotNull(result);
    }

    /**
     * Run the SummaryData build() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuild_1()
        throws Exception {
        SummaryDataBuilder fixture = new SummaryDataBuilder();

        SummaryData result = fixture.build();

        assertNotNull(result);
        assertEquals(null, result.getPageId());
        assertEquals(0.0, result.getMax(), 1.0);
        assertEquals(0.0, result.getMean(), 1.0);
        assertEquals(0, result.getJobId());
        assertEquals(0.0, result.getVarience(), 1.0);
        assertEquals(0.0, result.getSkewness(), 1.0);
        assertEquals(0.0, result.getPercentile10(), 1.0);
        assertEquals(0.0, result.getPercentile70(), 1.0);
        assertEquals(0.0, result.getPercentile20(), 1.0);
        assertEquals(0.0, result.getSttDev(), 1.0);
        assertEquals(0, result.getSampleSize());
        assertEquals(0.0, result.getKurtosis(), 1.0);
        assertEquals(0.0, result.getPercentile80(), 1.0);
        assertEquals(0.0, result.getPercentile60(), 1.0);
        assertEquals(0.0, result.getPercentile90(), 1.0);
        assertEquals(0.0, result.getPercentile50(), 1.0);
        assertEquals(0.0, result.getPercentile95(), 1.0);
        assertEquals(0.0, result.getPercentile99(), 1.0);
        assertEquals(0.0, result.getPercentile40(), 1.0);
        assertEquals(0.0, result.getPercentile30(), 1.0);
        assertEquals(0.0, result.getMin(), 1.0);
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the SummaryDataBuilder summaryData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSummaryData_1()
        throws Exception {

        SummaryDataBuilder result = SummaryDataBuilder.summaryData();

        assertNotNull(result);
    }
}