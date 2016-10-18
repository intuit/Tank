/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.settings;

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

import org.junit.Assert;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.vm.settings.TimeUtil;
import com.intuit.tank.test.TestGroups;

/**
 * TimeUtilTest
 * 
 * @author dangleton
 * 
 */
public class TimeUtilTest {

    @SuppressWarnings("unused")
    @DataProvider(name = "times")
    private Object[][] data() {
        return new Object[][] {
                { "1234", 1234L },
                { "1s", 1000L },
                { "30s", 1000L * 30 },
                { "1m", 1000L * 60 },
                { "1ms", 1L },
                { "1h", 1000L * 60 * 60 },
                { "1m30s", 1000L * 60 + 1000L * 30 },
                { "1d 1h 30m 10s 50ms", 1000L * 60 * 60 * 24 + 1000L * 60 * 60 + 1000L * 60 * 30 + 1000L * 10 + 50 },
                { "1d", 1000L * 60 * 60 * 24 }

        };
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "times")
    public void testJSONParse(String time, long expected) throws Exception {
        long result = TimeUtil.parseTimeString(time);
        System.out.println(result);
        Assert.assertEquals(expected, result);
    }
}
