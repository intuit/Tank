package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.harness.data.HDAssignment;
import com.intuit.tank.harness.data.HDResponse;
import com.intuit.tank.harness.data.HDValidation;

/**
 * The class <code>HDResponseTest</code> contains tests for the class <code>{@link HDResponse}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDResponseTest {
    /**
     * Run the HDResponse() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDResponse_1()
            throws Exception {

        HDResponse result = new HDResponse();

        assertNotNull(result);
        assertEquals(null, result.getRespFormat());
    }

    /**
     * Run the HDResponse(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDResponse_2()
            throws Exception {
        String responseFormat = "";

        HDResponse result = new HDResponse(responseFormat);

        assertNotNull(result);
        assertEquals("", result.getRespFormat());
    }

    /**
     * Run the HDAssignment getAssignment() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetAssignment_1()
            throws Exception {
        HDResponse fixture = new HDResponse("");

        HDAssignment result = fixture.getAssignment();

        assertNotNull(result);
    }

    /**
     * Run the String getRespFormat() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetRespFormat_1()
            throws Exception {
        HDResponse fixture = new HDResponse("");

        String result = fixture.getRespFormat();

        assertEquals("", result);
    }

    /**
     * Run the HDValidation getValidation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetValidation_1()
            throws Exception {
        HDResponse fixture = new HDResponse("");

        HDValidation result = fixture.getValidation();

        assertNotNull(result);
    }
}