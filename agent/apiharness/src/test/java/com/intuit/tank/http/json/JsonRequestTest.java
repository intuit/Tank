package com.intuit.tank.http.json;

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

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.junit.*;

import static org.junit.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.http.json.JsonRequest;
import com.intuit.tank.test.TestGroups;

public class JsonRequestTest {

    /**
     * 
     */
    @Test(groups = TestGroups.FUNCTIONAL)
    public void testCreate() {

        JsonRequest req = new JsonRequest(null);
        req.setKey("/address/home", "123 street");
        String body = req.getBody();
        Assert.assertEquals("{\"address\":{\"home\":\"123 street\"}}", body);
        req.setKey("/address/employeeId", "123");
        body = req.getBody();
        Assert.assertEquals("{\"address\":{\"employeeId\":123,\"home\":\"123 street\"}}", body);
    }

    @SuppressWarnings("unused")
    @DataProvider(name = "jsonRaw")
    private Object[][] jsonData() {
        return new Object[][] { {
                "{\"ProcessBeaconRequest\":[{\"TransactionID\":\"17599c19%2D7ec8%2D4219%2D81ce%2Df3b5e9357868\",\"BeaconRequestData\":{"
                        +
                        "\"Screen\":\"DialogWelcome\",\"CustomerType\":2,\"TaxSessionLocation\":\"pprftta11001:65533\",\"ClientProfileData\":{\"renderTime\":188,"
                        +
                        "\"uniqueDialogID\":\"DialogWelcome\",\"requestTime\":5703,\"customerUsageTime\":null},\"Tab\":\"Home\",\"FederalRefundAmount\":\"$0\","
                        +
                        "\"EventType\":\"PageEntry\",\"SubTab\":\"Program%20Options\",\"ProductId\":16,\"DialogTitle\":null}}]}", // expected
                                                                                                                                  // result
                new String[] { "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/customerUsageTime",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/renderTime",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/requestTime",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/uniqueDialogID",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/CustomerType",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/DialogTitle",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/EventType",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/FederalRefundAmount",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ProductId",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/Screen",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/SubTab",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/Tab",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/TaxSessionLocation",
                        "/ProcessBeaconRequest/[0]/TransactionID" }, // validKeys
                new String[] { "null",
                        "188",
                        "5703",
                        "DialogWelcome",
                        "2",
                        "",
                        "PageEntry",
                        "$0",
                        "16",
                        "DialogWelcome",
                        "Program%20Options",
                        "Home",
                        "pprftta11001:65533",
                        "17599c19%2D7ec8%2D4219%2D81ce%2Df3b5e9357868" } } // validValues

        };
    }

    @SuppressWarnings("unused")
    @DataProvider(name = "jsonRaw")
    private Object[][] jsonNumbers() {
        return new Object[][] { {
                "{\"}", // expected
                        // result
                new String[] { "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/customerUsageTime",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/renderTime",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/requestTime",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ClientProfileData/uniqueDialogID",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/CustomerType",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/DialogTitle",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/EventType",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/FederalRefundAmount",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/ProductId",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/Screen",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/SubTab",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/Tab",
                        "/ProcessBeaconRequest/[0]/BeaconRequestData/TaxSessionLocation",
                        "/ProcessBeaconRequest/[0]/TransactionID" }, // validKeys
                new String[] { "null",
                        "188",
                        "5703",
                        "DialogWelcome",
                        "2",
                        "",
                        "PageEntry",
                        "$0",
                        "16",
                        "DialogWelcome",
                        "Program%20Options",
                        "Home",
                        "pprftta11001:65533",
                        "17599c19%2D7ec8%2D4219%2D81ce%2Df3b5e9357868" } } // validValues

        };
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "jsonRaw")
    public void testJSONParse(String result, String[] keys, String[] values) throws Exception {
        JsonRequest req = new JsonRequest(null);
        for (int i = 0; i < keys.length; i++) {
            req.setKey(keys[i], values[i]);
        }
        String body = req.getBody();
        // System.out.println(body);
        Assert.assertNotNull(body);
        Assert.assertEquals(result, body);

    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testNumbers() throws Exception {
        JsonRequest req = new JsonRequest(null);
        req.setKey("number", "123232323232323232323");
        String body = req.getBody();
        System.out.println(body);
        Assert.assertNotNull(body);
        // Assert.assertEquals(", body);

    }

}
