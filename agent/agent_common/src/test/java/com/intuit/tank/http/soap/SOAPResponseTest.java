package com.intuit.tank.http.soap;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.http.soap.SOAPResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>SOAPResponseTest</code> contains tests for the class <code>{@link SOAPResponse}</code>.
 *
 * @generatedBy CodePro at 12/16/14 4:29 PM
 */
public class SOAPResponseTest {
    /**
     * Run the SOAPResponse() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:29 PM
     */
    @Test
    public void testSOAPResponse_1()
        throws Exception {

        SOAPResponse result = new SOAPResponse();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        //       at com.intuit.tank.http.BaseResponse.<clinit>(BaseResponse.java:18)
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
        SOAPResponse fixture = new SOAPResponse();
        String key = "";

        String result = fixture.getValue(key);
    }
}