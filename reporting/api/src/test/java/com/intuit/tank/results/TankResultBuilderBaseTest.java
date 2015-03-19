package com.intuit.tank.results;

/*
 * #%L
 * Reporting API
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

import com.intuit.tank.results.TankResult;
import com.intuit.tank.results.TankResultBuilderBase;

import static org.junit.Assert.*;

/**
 * The class <code>TankResultBuilderBaseTest</code> contains tests for the class
 * <code>{@link TankResultBuilderBase}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:31 AM
 */
public class TankResultBuilderBaseTest {
    /**
     * Run the TankResultBuilderBase(TankResult) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testTankResultBuilderBase_1()
            throws Exception {
        TankResult aInstance = new TankResult();

        TankResultBuilderBase result = new TankResultBuilderBase(aInstance);

        assertNotNull(result);
    }

    /**
     * Run the TankResult getInstance() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetInstance_1()
            throws Exception {
        TankResultBuilderBase fixture = new TankResultBuilderBase(new TankResult());

        TankResult result = fixture.getInstance();

        assertNotNull(result);
        assertEquals(false, result.isError());
        assertEquals(null, result.getJobId());
        assertEquals(0, result.getResponseTime());
        assertEquals(0, result.getResponseSize());
        assertEquals(null, result.getRequestName());
        assertEquals(0, result.getStatusCode());
    }

    /**
     * Run the TankResultBuilderBase withError(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testWithError_1()
            throws Exception {
        TankResultBuilderBase fixture = new TankResultBuilderBase(new TankResult());
        boolean aValue = true;

        TankResultBuilderBase result = fixture.withError(aValue);

        assertNotNull(result);
    }

    /**
     * Run the TankResultBuilderBase withJobId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testWithJobId_1()
            throws Exception {
        TankResultBuilderBase fixture = new TankResultBuilderBase(new TankResult());
        String aValue = "";

        TankResultBuilderBase result = fixture.withJobId(aValue);

        assertNotNull(result);
    }

    /**
     * Run the TankResultBuilderBase withRequestName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testWithRequestName_1()
            throws Exception {
        TankResultBuilderBase fixture = new TankResultBuilderBase(new TankResult());
        String aValue = "";

        TankResultBuilderBase result = fixture.withRequestName(aValue);

        assertNotNull(result);
    }

    /**
     * Run the TankResultBuilderBase withResponseSize(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testWithResponseSize_1()
            throws Exception {
        TankResultBuilderBase fixture = new TankResultBuilderBase(new TankResult());
        int aValue = 1;

        TankResultBuilderBase result = fixture.withResponseSize(aValue);

        assertNotNull(result);
    }

    /**
     * Run the TankResultBuilderBase withResponseTime(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testWithResponseTime_1()
            throws Exception {
        TankResultBuilderBase fixture = new TankResultBuilderBase(new TankResult());
        int aValue = 1;

        TankResultBuilderBase result = fixture.withResponseTime(aValue);

        assertNotNull(result);
    }

    /**
     * Run the TankResultBuilderBase withStatusCode(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testWithStatusCode_1()
            throws Exception {
        TankResultBuilderBase fixture = new TankResultBuilderBase(new TankResult());
        int aValue = 1;

        TankResultBuilderBase result = fixture.withStatusCode(aValue);

        assertNotNull(result);
    }
}