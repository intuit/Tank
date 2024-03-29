package com.intuit.tank.harness.functions;

import org.apache.logging.log4j.Level;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.test.data.Variables;

import static org.junit.jupiter.api.Assertions.*;

public class DateFunctionsTest {

    private Variables variables;

    @BeforeEach
    public void setUp() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        variables = new Variables();
    }

    @Test
    public void testIsValid() {
        // Generic Tests
        assertFalse(FunctionHandler.validFunction("#function.date"));
        assertFalse(FunctionHandler.validFunction("#function.date.bogusFunction"));

        // Current Date Tests
        assertTrue(FunctionHandler.validFunction("#function.date.currentDate"));
        assertTrue(FunctionHandler.validFunction("#function.date.currentDate.MMddyyyy"));

        // Add Days Tests
        assertFalse(FunctionHandler.validFunction("#function.date.addDays"));
        assertTrue(FunctionHandler.validFunction("#function.date.addDays.MMddyyyy"));
        assertTrue(FunctionHandler.validFunction("#function.date.addDays.14.MMddyyyy"));
    }

    @Test
    public void testGetCurrentDate() {
        String command = "#function.date.currentDate.MM-dd-yyyy";
        String date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);
    }

    @Test
    public void testAddDays() {
        String command = "#function.date.addDays.1.MM-dd-yyyy";
        String date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);

        command = "#function.date.addDays.50.MM-dd-yyyy";
        date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);

        command = "#function.date.addDays.-50.MM-dd-yyyy";
        date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);
    }

    @Test
    public void testExecuteFunctionFail_1() {
        String command = "null";
        String date = FunctionHandler.executeFunction(command, variables);
        assertNull(date);
    }

    @Test
    public void testExecuteFunctionFail_2() {
       assertNull(DateFunctions.executeFunction(null));
    }
}
