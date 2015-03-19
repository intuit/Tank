package com.intuit.tank.perfManager;

/*
 * #%L
 * Common
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

import com.intuit.tank.vm.perfManager.Environment;

import static org.junit.Assert.*;

/**
 * The class <code>EnvironmentTest</code> contains tests for the class <code>{@link Environment}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:36 AM
 */
public class EnvironmentTest {
    /**
     * Run the Environment() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testEnvironment_1()
            throws Exception {

        Environment result = new Environment();

        assertNotNull(result);
        assertEquals(null, result.getFrontEndUrl());
        assertEquals(null, result.getBackEndUrl());
    }

    /**
     * Run the Environment(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testEnvironment_2()
            throws Exception {
        String frontEndUrl = "";
        String backEndUrl = "";

        Environment result = new Environment(frontEndUrl, backEndUrl);

        assertNotNull(result);
        assertEquals("", result.getFrontEndUrl());
        assertEquals("", result.getBackEndUrl());
    }

    /**
     * Run the String getBackEndUrl() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetBackEndUrl_1()
            throws Exception {
        Environment fixture = new Environment();

        String result = fixture.getBackEndUrl();

        assertEquals(null, result);
    }

    /**
     * Run the String getFrontEndUrl() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetFrontEndUrl_1()
            throws Exception {
        Environment fixture = new Environment();

        String result = fixture.getFrontEndUrl();

        assertEquals(null, result);
    }

    /**
     * Run the void setBackEndUrl(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testSetBackEndUrl_1()
            throws Exception {
        Environment fixture = new Environment();
        String data = "";

        fixture.setBackEndUrl(data);

    }

    /**
     * Run the void setFrontEndUrl(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testSetFrontEndUrl_1()
            throws Exception {
        Environment fixture = new Environment();
        String data = "";

        fixture.setFrontEndUrl(data);

    }
}