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
import com.intuit.tank.project.SummaryDataBuilder;

import java.util.Date;

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

    @Test
    public void testBuild_2() {
        SummaryDataBuilder builder = new SummaryDataBuilder();

        builder.withJobId(1);
        builder.withPageId("pageId");
        builder.withSampleSize(2);
        builder.withMean(3.0);
        builder.withMin(4.0);
        builder.withMax(5.0);
        builder.withSttDev(6.0);
        builder.withKurtosis(7.0);
        builder.withSkewness(8.0);
        builder.withVarience(9.0);
        builder.withPercentile10(10.0);
        builder.withPercentile20(11.0);
        builder.withPercentile30(12.0);
        builder.withPercentile40(12.0);
        builder.withPercentile50(12.0);
        builder.withPercentile60(12.0);
        builder.withPercentile70(12.0);
        builder.withPercentile80(12.0);
        builder.withPercentile90(12.0);
        builder.withPercentile95(12.0);
        builder.withPercentile99(12.0);
        builder.withId(13);
        Date date = new Date();
        builder.withCreated(date);
        builder.withModified(date);
        builder.withForceCreateDate(date);

        SummaryData result = builder.build();

        assertNotNull(result);
        assertEquals("pageId", result.getPageId());
        assertEquals(5.0, result.getMax());
        assertEquals(3.0, result.getMean());
        assertEquals(1, result.getJobId());
        assertEquals(9.0, result.getVarience());
        assertEquals(8.0, result.getSkewness());
        assertEquals(10.0, result.getPercentile10());
        assertEquals(12.0, result.getPercentile70());
        assertEquals(11.0, result.getPercentile20());
        assertEquals(6.0, result.getSttDev(), 1.0);
        assertEquals(2, result.getSampleSize());
        assertEquals(7.0, result.getKurtosis(), 1.0);
        assertEquals(12.0, result.getPercentile80(), 1.0);
        assertEquals(12.0, result.getPercentile60(), 1.0);
        assertEquals(12.0, result.getPercentile90(), 1.0);
        assertEquals(12.0, result.getPercentile50(), 1.0);
        assertEquals(12.0, result.getPercentile95(), 1.0);
        assertEquals(12.0, result.getPercentile99(), 1.0);
        assertEquals(12.0, result.getPercentile40(), 1.0);
        assertEquals(12.0, result.getPercentile30(), 1.0);
        assertEquals(4.0, result.getMin(), 1.0);
        assertEquals(13, result.getId());
        assertEquals(date, result.getModified());
        assertEquals(date, result.getCreated());
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