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
import com.intuit.tank.results.TankResultBuilder;

import static org.junit.Assert.*;

/**
 * The class <code>TankResultBuilderTest</code> contains tests for the class
 * <code>{@link TankResultBuilder}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:31 AM
 */
public class TankResultBuilderTest {
    /**
     * Run the TankResultBuilder() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testTankResultBuilder_1()
            throws Exception {

        TankResultBuilder result = new TankResultBuilder();

        assertNotNull(result);
    }

    /**
     * Run the TankResult build() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testBuild_1()
            throws Exception {
        TankResultBuilder fixture = new TankResultBuilder();

        TankResult result = fixture.build();

        assertNotNull(result);
        assertEquals(false, result.isError());
        assertEquals(null, result.getJobId());
        assertEquals(0, result.getResponseTime());
        assertEquals(0, result.getResponseSize());
        assertEquals(null, result.getRequestName());
        assertEquals(0, result.getStatusCode());
    }

    /**
     * Run the TankResultBuilder TankResult() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testTankResult_1()
            throws Exception {

        TankResultBuilder result = TankResultBuilder.tankResult();

        assertNotNull(result);
    }
}