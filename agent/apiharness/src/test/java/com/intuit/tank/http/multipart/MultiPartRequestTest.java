package com.intuit.tank.http.multipart;

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

import java.util.List;

import org.apache.commons.httpclient.HttpClient;

import junit.framework.Assert;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.*;

import static org.junit.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.binary.BinaryResponse;
import com.intuit.tank.http.multipart.MultiPartRequest;
import com.intuit.tank.test.TestGroups;
import com.sun.jersey.core.util.Base64;

public class MultiPartRequestTest {
    @DataProvider(name = "data")
    private Object[][] csvData() {
        return new Object[][] {
                {
                        "-----------------------------10681713301939738037227942752"
                                + "\r\n"
                                + "Content-Disposition: form-data; name=\"createNewScriptForm\""
                                + "\r\n"
                                + "\r\n"
                                + "createNewScriptForm "
                                + "\r\n"
                                + "-----------------------------10681713301939738037227942752"
                                + "\r\n"
                                + "Content-Disposition: form-data; name=\"createNewScriptForm:j_idt56\"; filename=\"test.csv\""
                                + "\r\n"
                                + "Content-Type: text/csv" + "\r\n"
                                + "\r\n"
                                + "1" + "\r\n"
                                + "2" + "\r\n"
                                + "3" + "\r\n"
                                + "-----------------------------10681713301939738037227942752" + "\r\n"
                                + "Content-Disposition: form-data; name=\"createNewScriptForm:saveBtn\"" + "\r\n"
                                + "\r\n"
                                + "\r\n"
                                + "-----------------------------10681713301939738037227942752" + "\r\n"
                                + "Content-Disposition: form-data; name=\"javax.faces.ViewState\"" + "\r\n"
                                + "\r\n"
                                + "-1346138416504364622:5284803266475066700" + "\r\n"
                                + "-----------------------------10681713301939738037227942752--" + "\r\n", 4 },
                { "----AaB03x\r\n"
                        + "Content-Disposition: form-data; name=\"submit-name\"\r\n"
                        + "\r\n"
                        + "Larry\r\n"
                        + "----AaB03x\r\n"
                        + "Content-Disposition: form-data; name=\"files\"; filename=\"file1.txt\"\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "HELLO WORLD!\r\n"
                        + "----AaB03x--\r\n", 2 }
        };
    }

    @Test(groups = TestGroups.FUNCTIONAL, dataProvider = "data")
    public void test(String body, int numParts) {
        MultiPartRequest multiPartRequest = new MultiPartRequest(null);
        multiPartRequest.setBody(new String(Base64.encode(body)));
        List<Part> parts = multiPartRequest.buildParts();
        Assert.assertEquals(numParts, parts.size());

    }

}
