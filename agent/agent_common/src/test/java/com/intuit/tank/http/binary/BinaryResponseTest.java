package com.intuit.tank.http.binary;

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

import com.intuit.tank.http.binary.BinaryResponse;

import static org.junit.Assert.*;

/**
 * The class <code>BinaryResponseTest</code> contains tests for the class <code>{@link BinaryResponse}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class BinaryResponseTest {
    /**
     * Run the String getValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        BinaryResponse fixture = new BinaryResponse();
        String key = "";

        String result = fixture.getValue(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        //       at com.intuit.tank.http.BaseResponse.<clinit>(BaseResponse.java:18)
        assertNotNull(result);
    }
}