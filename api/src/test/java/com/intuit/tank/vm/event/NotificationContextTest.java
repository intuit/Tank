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

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * NotificationContextTest
 * 
 * @author dangleton
 * 
 */
public class NotificationContextTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testContext() {
        NotificationContext context = new NotificationContext();
        context.addContextEntry("testKey", "testValue");
        context.addContextEntry("nullKey", null);
        context.addContextEntry("nullKey1", " ");

        String val = context.replaceValues("testKey = {testKey}");
        assertEquals("testKey = testValue", val);
        val = context.replaceValues("nullKey = {nullKey}");
        assertEquals("nullKey = N/A", val);
        val = context.replaceValues("nullKey1 = {nullKey1}");
        assertEquals("nullKey1 = N/A", val);
    }
}
