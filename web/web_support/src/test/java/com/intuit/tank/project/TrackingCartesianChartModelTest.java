package com.intuit.tank.project;

/*
 * #%L
 * JSF Support Beans
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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>TrackingCartesianChartModelTest</code> contains tests for the class
 * <code>{@link TrackingCartesianChartModel}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class TrackingCartesianChartModelTest {
    /**
     * Run the TrackingCartesianChartModel() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testTrackingCartesianChartModel_1()
            throws Exception {
        TrackingCartesianChartModel result = new TrackingCartesianChartModel();
        assertNotNull(result);
    }

    /**
     * Run the void addDate(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAddDate()
            throws Exception {
        TrackingCartesianChartModel fixture = new TrackingCartesianChartModel();

        fixture.addDate(new Date());

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.TrackingCartesianChartModel.addDate(TrackingCartesianChartModel.java:43)
    }

    /**
     * Run the long getMax() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetMax_1()
            throws Exception {
        TrackingCartesianChartModel fixture = new TrackingCartesianChartModel();

        long result = fixture.getMax();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.TrackingCartesianChartModel.getMax(TrackingCartesianChartModel.java:26)
        assertEquals(0L, result);
    }

    /**
     * Run the Date getMaxDate() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetMaxDate_1()
            throws Exception {
        TrackingCartesianChartModel fixture = new TrackingCartesianChartModel();
        Date result = fixture.getMaxDate();
    }

    /**
     * Run the long getMin() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetMin_1()
            throws Exception {
        TrackingCartesianChartModel fixture = new TrackingCartesianChartModel();

        long result = fixture.getMin();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.TrackingCartesianChartModel.getMin(TrackingCartesianChartModel.java:22)
        assertEquals(0L, result);
    }

    /**
     * Run the Date getMinDate() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetMinDate_1()
            throws Exception {
        TrackingCartesianChartModel fixture = new TrackingCartesianChartModel();

        Date result = fixture.getMinDate();
    }
}