package com.intuit.tank.harness.functions;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JexlDateFunctionsTest {

    Variables variables;

    @BeforeEach
    public void setUp() {
        variables = new Variables();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetCurrentDate() {
        String date = variables.evaluate("#{dateFunctions.currentDate('yyyy-MM-dd HH:mm:ssZ')}");
        assertNotNull(date);
        date = variables.evaluate("#{dateFunctions.currentDate('yyyy-MM-dd HH:mm:ssZ', 'PST')}");
        assertNotNull(date);
        date = variables.evaluate("#{dateFunctions.currentDate('yyyy-MM-dd HH:mm:ssZ', 'America/Los_Angeles')}");
        assertNotNull(date);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCurrentTimeMilis() {
        long currentTime = System.currentTimeMillis();
        String time = variables.evaluate("#{dateFunctions.currentTimeMilis()}");
        assertTrue(currentTime <= Long.parseLong(time));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testAddDays() {

        String date = variables.evaluate("#{dateFunctions.addDays(1, '07-05-2012')}");
        assertNotNull(date);

        date = variables.evaluate("#{dateFunctions.addDays(5, '07-05-2012')}");
        assertNotNull(date);

        date = variables.evaluate("#{dateFunctions.addDays(-50, '07-05-2012')}");
        assertNotNull(date);
    }
}
