/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.settings;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.vm.common.util.RegexUtil;
import com.intuit.tank.test.TestGroups;

/**
 * AbstractReplacementTest
 * 
 * @author dangleton
 * 
 */
public class RegexUtilTest {

    @SuppressWarnings("unused")
    @DataProvider(name = "validations")
    private Object[][] violationData() {
        return new Object[][] {
                { "query*", "queryOtherStuff", true },
                { "query*", "query*", true },
                { "query*", "query", true },
                { "y\\*", "y*", true },
                { "y\\*", "y*Stuff", false },
                { "*$100*", "I have $100 Dollars", true },
                { "$100*", "I have $100 Dollars", false },
                { "y\\\\*", "y\\*", true },
                { "y\\\\*", "y\\anything else", true },
                { "*", "hello There", true },
                { "*", "", true },

        };
    }

    @Test(groups = { TestGroups.FUNCTIONAL }, dataProvider = "validations")
    public void testPatternConcvert(String query, String matchValue, boolean isMatch) throws Exception {
        String pattern = RegexUtil.wildcardToRegexp(query);
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(matchValue);
        Assert.assertEquals(m.matches(), isMatch);
    }

}
