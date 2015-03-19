/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.rest;

/*
 * #%L
 * Rest Client Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.rest.RestUrlBuilder;
import com.intuit.tank.test.TestGroups;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * RestUrlBuilderTest
 * 
 * @author dangleton
 * 
 */
public class RestUrlBuilderTest {

    @SuppressWarnings("unused")
    @DataProvider(name = "patterns")
    private Object[][] violationData() {
        return new Object[][] {
                { "http://base.url.com/rest/v1", "method", "parameter", "http://base.url.com/rest/v1/method/parameter" },
                { "http://base.url.com/rest/v1/", "method", "parameter", "http://base.url.com/rest/v1/method/parameter" },
                { "http://base.url.com/rest/v1/", "/method", "parameter",
                        "http://base.url.com/rest/v1/method/parameter" },
                { "http://base.url.com/rest/v1/", "/method/", "parameter",
                        "http://base.url.com/rest/v1/method/parameter" },
                { "http://base.url.com/rest/v1/", "/method/", "/parameter",
                        "http://base.url.com/rest/v1/method/parameter" },
                { "http://base.url.com/rest/v1/", "/method/", "/parameter/",
                        "http://base.url.com/rest/v1/method/parameter/" },
                { "http://base.url.com/rest/v1", "method", null, "http://base.url.com/rest/v1/method" },
                { "http://base.url.com/rest/v1", "/method", null, "http://base.url.com/rest/v1/method" },
                { "http://base.url.com/rest/v1", "/method/", null, "http://base.url.com/rest/v1/method" },
                { "http://base.url.com/rest/v1", null, null, "http://base.url.com/rest/v1" }

        };
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "patterns")
    public void testUrlPatterns(String baseUrl, String methodName, String parameter, String expectedResult) {
        RestUrlBuilder restUrlBuilder = new RestUrlBuilder(baseUrl);
        String result = restUrlBuilder.buildUrl(methodName, parameter);
        Assert.assertEquals(result, expectedResult);
    }
}
