package com.intuit.tank.http;

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

import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.HttpResponseFactory;

import static org.junit.Assert.*;

/**
 * The class <code>HttpResponseFactoryTest</code> contains tests for the class <code>{@link HttpResponseFactory}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class HttpResponseFactoryTest {
    /**
     * Run the BaseResponse getHttpResponse(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpResponse_1()
        throws Exception {
        String responseType = null;

        BaseResponse result = HttpResponseFactory.getHttpResponse(responseType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.http.HttpResponseFactory.getHttpResponse(HttpResponseFactory.java:25)
        assertNotNull(result);
    }

    /**
     * Run the BaseResponse getHttpResponse(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpResponse_2()
        throws Exception {
        String responseType = null;

        BaseResponse result = HttpResponseFactory.getHttpResponse(responseType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.http.HttpResponseFactory.getHttpResponse(HttpResponseFactory.java:25)
        assertNotNull(result);
    }

    /**
     * Run the BaseResponse getHttpResponse(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetHttpResponse_3()
        throws Exception {
        String responseType = "";

        BaseResponse result = HttpResponseFactory.getHttpResponse(responseType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.http.HttpResponseFactory.getHttpResponse(HttpResponseFactory.java:25)
        assertNotNull(result);
    }
}