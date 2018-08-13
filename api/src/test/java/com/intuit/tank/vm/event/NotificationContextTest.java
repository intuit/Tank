/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.event;

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

import org.testng.Assert;

import org.testng.annotations.Test;

import com.intuit.tank.vm.event.NotificationContext;
import com.intuit.tank.test.TestGroups;

/**
 * NotificationContextTest
 * 
 * @author dangleton
 * 
 */
public class NotificationContextTest {

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testContext() {
        NotificationContext context = new NotificationContext();
        context.addContextEntry("testKey", "testValue");
        context.addContextEntry("nullKey", null);
        context.addContextEntry("nullKey1", " ");

        String val = context.replaceValues("testKey = {testKey}");
        Assert.assertEquals("testKey = testValue", val);
        val = context.replaceValues("nullKey = {nullKey}");
        Assert.assertEquals("nullKey = N/A", val);
        val = context.replaceValues("nullKey1 = {nullKey1}");
        Assert.assertEquals("nullKey1 = N/A", val);
    }
}
