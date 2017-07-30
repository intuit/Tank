package com.intuit.tank.vm.common.util;

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

import com.intuit.tank.vm.common.util.JSONBuilder;
import com.intuit.tank.test.TestGroups;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Summary
 * 
 * @author wlee5
 */
public class JSONBuilderTest {

    JSONBuilder builder;

    @BeforeSuite
    public void setUp() {
        builder = new JSONBuilder();
        builder.add("/1/2/[0]/TransactionId", "myTransactionId");
        builder.add("/1/2/[0]/Name", "myName");
        builder.add("/1/2/[0]/A", "1");
        builder.add("/1/2/[0]/B", "1.2");
        builder.add("/1/2/[0]/C", "true");
        builder.add("/1/2/[0]/D", "false");
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testMain() throws Exception {
        // just run it to make sure no exceptions
        JSONBuilder.main(null);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testGetValue() throws Exception {
        Assert.assertEquals(builder.getValue("/1/2/[0]/Name"), "myName");
    }

//    @Test(groups = TestGroups.FUNCTIONAL)
//    public void testToJsonString() throws Exception {
//        String noIndent = "\"D\":false,\"Name\":\"myName\",\"TransactionId\"";
//        Assert.assertTrue(builder.toJsonString().contains(noIndent));
//
//        String twoIntent = "\"1\": {\"2\": [{\n  \"A\": 1,\n  \"B\": 1.2";
//        Assert.assertTrue(builder.toJsonString(2).contains(twoIntent));
//    }
}
