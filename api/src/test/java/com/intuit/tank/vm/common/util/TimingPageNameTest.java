/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common.util;

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

import junit.framework.Assert;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.vm.common.util.TimingPageName;
import com.intuit.tank.test.TestGroups;

/**
 * TimingPageNameTest
 * 
 * @author dangleton
 * 
 */
public class TimingPageNameTest {

    @SuppressWarnings("unused")
    @DataProvider(name = "data")
    private Object[][] data() {
        return new Object[][] {
                { "Page ID:||:Page Name:||:14", "Page ID", "Page Name", 14 },
                { "Page ID", "Page ID", "Page ID", null },
                { ":||:Page Name", "Page Name", "Page Name", null },
                { ":||:Page Name:||:15", "Page Name", "Page Name", 15 },
                { "Page ID:||:", "Page ID", "Page ID", null },
                { "Page ID:||::||:16", "Page ID", "Page ID", 16 },
                { "Old Page", "Old Page", "Old Page", null },
        };
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "data")
    public void testParse(String pageIdString, String id, String name, Integer index) {

        TimingPageName timingPageName = new TimingPageName(pageIdString);
        Assert.assertEquals(timingPageName.getId(), id);
        Assert.assertEquals(timingPageName.getName(), name);
        Assert.assertEquals(timingPageName.getIndex(), index);
    }
}
