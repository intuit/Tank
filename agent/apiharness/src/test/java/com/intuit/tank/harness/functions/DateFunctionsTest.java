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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.intuit.tank.harness.functions.FunctionHandler;
import com.intuit.tank.harness.test.data.Variables;

public class DateFunctionsTest {

    Variables variables;

    @Before
    public void setUp() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        variables = new Variables();
    }

    @Test
    public void testIsValid() {
        // Generic Tests
        Assert.assertFalse(FunctionHandler.validFunction("#function.date"));
        Assert.assertFalse(FunctionHandler.validFunction("#function.date.bogusFunction"));

        // Current Date Tests
        Assert.assertTrue(FunctionHandler.validFunction("#function.date.currentDate"));
        Assert.assertTrue(FunctionHandler.validFunction("#function.date.currentDate.MMddyyyy"));

        // Add Days Tests
        Assert.assertFalse(FunctionHandler.validFunction("#function.date.addDays"));
        Assert.assertTrue(FunctionHandler.validFunction("#function.date.addDays.MMddyyyy"));
        Assert.assertTrue(FunctionHandler.validFunction("#function.date.addDays.14.MMddyyyy"));
    }

    @Test
    public void testGetCurrentDate() {
        String command = "#function.date.currentDate.MM-dd-yyyy";
        String date = FunctionHandler.executeFunction(command, variables);
        Assert.assertNotNull(date);
    }

    @Test
    public void testAddDays() {
        String command = "#function.date.addDays.1.MM-dd-yyyy";
        String date = FunctionHandler.executeFunction(command, variables);
        Assert.assertNotNull(date);

        command = "#function.date.addDays.50.MM-dd-yyyy";
        date = FunctionHandler.executeFunction(command, variables);
        Assert.assertNotNull(date);

        command = "#function.date.addDays.-50.MM-dd-yyyy";
        date = FunctionHandler.executeFunction(command, variables);
        Assert.assertNotNull(date);
    }
}
