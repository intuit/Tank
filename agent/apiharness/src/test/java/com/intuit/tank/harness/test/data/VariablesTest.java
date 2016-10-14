package com.intuit.tank.harness.test.data;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.vm.common.util.ValidationUtil;
import com.intuit.tank.test.TestGroups;

public class VariablesTest {

    @SuppressWarnings("unused")
    @DataProvider(name = "validations")
    private Object[][] matching() {
        return new Object[][] {
                { "Hello #{name}, I want you to meet #{other}. #{string.concat('She ', 'is ', adjective, '.')}", //${string.
                        "Hello Denis Angleton, I want you to meet Sue King. She is cool." },
                { "No Replacements", "No Replacements" },
                { "#{bogus_name}", "" },
                { "#{something  }", "" },
                { "#{string.concat}", "" },
                { "#{bogusFunction}", "" },
                { "#{string.concat()}", "" },

        };
    }

    @BeforeClass
    public void configure() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testVariable_NoPrefix() {
        Variables variables = new Variables();
        variables.addVariable("VariableName_1", "Variable Value_1");
        TestCase.assertFalse(ValidationUtil.isVariable("VariableName_1"));
        TestCase.assertFalse(ValidationUtil.isVariable("NOTVARIABLE"));
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testVariable_Prefix() {
        // Variables variables = Variables.getInstance();
        Variables variables = new Variables();
        variables.addVariable("VariableName_2", "Variable Value_2");
        TestCase.assertTrue(ValidationUtil.isVariable("@VariableName_2"));
        TestCase.assertFalse(ValidationUtil.isVariable("NOTVARIABLE"));
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "validations")
    public void testEvaluate(String expression, String expected) {
        Variables variables = new Variables();
        variables.getContext().set("string", VariablesTest.class);
        variables.addVariable("name", "Denis Angleton");
        variables.addVariable("other", "Sue King");
        variables.addVariable("adjective", "cool");
        String evaluated = variables.evaluate(expression);
        Assert.assertEquals(evaluated, expected);
    }

    public static String concat(String... s) {
        return StringUtils.join(s);
    }
    
    
    @Test(groups = TestGroups.FUNCTIONAL)
    public void testSetJson() throws IOException{
    	
    	String json = readFile("src/test/resources/json-response.json");
    	
    	Variables variables = new Variables();
    	variables.addVariable("RESPONSE_BODY", json);
    }
    
    
    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
