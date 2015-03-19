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

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
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
                { "Hello #{name}, I want you to meet #{other}. ${string.concat('She ', 'is ', adjective, '.')}",
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
}
