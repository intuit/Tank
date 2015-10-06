package com.intuit.tank.http.xml;

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

import org.junit.*;

import com.intuit.tank.http.xml.XMLResponse;

import static org.junit.Assert.*;

/**
 * The class <code>XMLResponseTest</code> contains tests for the class <code>{@link XMLResponse}</code>.
 *
 * @generatedBy CodePro at 12/16/14 4:29 PM
 */
public class XMLResponseTest {
    /**
     * Run the XMLResponse() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testXMLResponse_1()
        throws Exception {

        XMLResponse result = new XMLResponse();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.Logger.getLogger(Logger.java:117)
        //       at com.intuit.tank.http.BaseResponse.<clinit>(BaseResponse.java:18)
        assertNotNull(result);
    }

    /**
     * Run the XMLResponse(String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testXMLResponse_2()
        throws Exception {
        String body = "";

        XMLResponse result = new XMLResponse(body);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.xml.XMLResponse
        assertNotNull(result);
    }

    /**
     * Run the String getResponseBody() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetResponseBody_1()
        throws Exception {
        XMLResponse fixture = new XMLResponse("");

        String result = fixture.getResponseBody();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.xml.XMLResponse
        assertNotNull(result);
    }

    /**
     * Run the String getResponseBody() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetResponseBody_2()
        throws Exception {
        XMLResponse fixture = new XMLResponse("");

        String result = fixture.getResponseBody();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.xml.XMLResponse
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        XMLResponse fixture = new XMLResponse("htllo=there");
        String key = "";

        String result = fixture.getValue(key);
        assertNotNull(result);
    }

    /**
     * Run the String getValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testGetValue_2()
        throws Exception {
        XMLResponse fixture = new XMLResponse("");
        String key = "";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.http.xml.XMLResponse
        assertNotNull(result);
    }
}