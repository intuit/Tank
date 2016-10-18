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
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.harness.functions.FunctionHandler;
import com.intuit.tank.harness.test.data.Variables;

public class GenericFunctionsNGTest {

    Variables variables;

    @BeforeClass
    public void setUp() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        variables = new Variables();
    }

    @Test(groups = "functional")
    public void testIsValid() {
        // Generic Tests
        Assert.assertFalse(FunctionHandler.validFunction("#function.generic"));
        Assert.assertFalse(FunctionHandler.validFunction("#function.generic.bogusFunction"));

        // Random Positive Whole Number
        Assert.assertFalse(FunctionHandler.validFunction("#function.generic.random"));
        Assert.assertFalse(FunctionHandler.validFunction("#function.generic.random.alphaGrammar"));
        Assert.assertFalse(FunctionHandler.validFunction("#function.generic.getscripttimeremaining.2"));
        Assert.assertTrue(FunctionHandler.validFunction("#function.generic.getcsv.10"));
    }

   

    @Test(groups = "functional")
    public void testCSV() {
        String command = "#function.generic.getcsv.0";
        String str = FunctionHandler.executeFunction(command, variables);
        Assert.assertNotNull(str);

        command = "#function.generic.getcsv.0";
        str = FunctionHandler.executeFunction(command, variables);
        Assert.assertNotNull(str);
        System.err.println(str);
    }

}
