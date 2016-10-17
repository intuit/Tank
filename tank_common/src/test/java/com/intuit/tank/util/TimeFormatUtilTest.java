/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.Assert;

import static org.junit.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.util.TimeFormatUtil;

/**
 * TimeUtilTest
 * 
 * @author dangleton
 * 
 */
public class TimeFormatUtilTest {

    @DataProvider(name = "data")
    private Object[][] testData() {
        return new Object[][] {
                { 60, "00:01:00" },
                { 5, "00:00:05" },
                { 65, "00:01:05" },
                { 3665, "01:01:05" },
                { 3600, "01:00:00" }
        };

    }

    @Test(groups = { "functional" }, dataProvider = "data")
    public void testFormatTime(int seconds, String expected) throws Exception {
        String result = TimeFormatUtil.formatTime(seconds);
        Assert.assertEquals(expected, result);
    }

    /**
     * Run the String formatTime(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testFormatTime_1()
            throws Exception {
        int numSeconds = 1;

        String result = TimeFormatUtil.formatTime(numSeconds);

        assertEquals("00:00:01", result);
    }

    /**
     * Run the String getFormattedDuration(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetFormattedDuration_1()
            throws Exception {
        int simTimeSeconds = 1;

        String result = TimeFormatUtil.getFormattedDuration(simTimeSeconds);

        assertEquals("00:00:01", result);
    }

}
