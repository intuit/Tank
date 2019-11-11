package com.intuit.tank.harness.functions;

import com.intuit.tank.test.TestGroups;
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

import com.intuit.tank.harness.test.data.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenericFunctionsNGTest {

    Variables variables;

    @BeforeEach
    public void setUp() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        variables = new Variables();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testIsValid() {
        // Generic Tests
        assertFalse(FunctionHandler.validFunction("#function.generic"));
        assertFalse(FunctionHandler.validFunction("#function.generic.bogusFunction"));

        // Random Positive Whole Number
        assertFalse(FunctionHandler.validFunction("#function.generic.random"));
        assertFalse(FunctionHandler.validFunction("#function.generic.random.alphaGrammar"));
        assertFalse(FunctionHandler.validFunction("#function.generic.getscripttimeremaining.2"));
        assertTrue(FunctionHandler.validFunction("#function.generic.getcsv.10"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCSV() {
        String command = "#function.generic.getcsv.0";
        String str = FunctionHandler.executeFunction(command, variables);
        assertNotNull(str);

        command = "#function.generic.getcsv.0";
        str = FunctionHandler.executeFunction(command, variables);
        assertNotNull(str);
        System.err.println(str);
    }

}
