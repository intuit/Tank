package com.intuit.tank.util;

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

import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.util.FilterUtil;
import com.intuit.tank.test.TestGroups;

public class FilterUtilTest {

    @DataProvider(name = "data")
    private Object[][] data() {
        return new Object[][] {
                { "One", "o", true },
                { "One", "O", true },
                { "One", "N", true },
                { "One", "n", true },
                { "One", "one", true },
                { "One", "OnE", true },
                { "One", "OnEx", false },
                { "One", "f", false },
                { null, "", true },
                { null, "d", false },
                { "Three", null, true }
        };
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "data")
    public void filterByName(Object value, String filterText, boolean expected) {
        boolean filtered = new FilterUtil().contains(value, filterText, Locale.US);
        Assert.assertEquals(filtered, expected);
    }
}
