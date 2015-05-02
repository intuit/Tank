package com.intuit.tank.harness;

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
import org.testng.annotations.Test;

import static org.junit.Assert.*;

import com.amazonaws.http.HttpRequest;
import com.intuit.tank.harness.TPSMonitor;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.binary.BinaryRequest;
import com.intuit.tank.reporting.api.TPSInfoContainer;

/**
 * The class <code>TPSMonitorTest</code> contains tests for the class <code>{@link TPSMonitor}</code>.
 * 
 * @generatedBy CodePro at 9/15/14 4:03 PM
 */
public class TPSMonitorTest {
    /**
     * Run the TPSMonitor(int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testTPSMonitor_1()
            throws Exception {
        int period = 15;

        TPSMonitor result = new TPSMonitor(period);

        assertNotNull(result);
    }

    /**
     * Run the void addToMap(String,BaseRequest) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testAddToMap_1()
            throws Exception {
        TPSMonitor fixture = new TPSMonitor(1);
        String loggingKey = "Test";
        BaseRequest req = new MockRequest();
        fixture.addToMap(loggingKey, req);
        Thread.sleep(1000);
        TPSInfoContainer tpsInfo = fixture.getTPSInfo();
        int totalTps = tpsInfo.getTotalTps();
        System.out.println(totalTps);
        Assert.assertEquals(1, totalTps);
    }

    /**
     * Run the boolean isEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testIsEnabled_1()
            throws Exception {
        TPSMonitor fixture = new TPSMonitor(1);

        boolean result = fixture.isEnabled();

        assertTrue(result);
    }

    /**
     * Run the boolean isEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/15/14 4:03 PM
     */
    @Test
    public void testIsEnabled_2()
            throws Exception {
        TPSMonitor fixture = new TPSMonitor(0);

        boolean result = fixture.isEnabled();

        assertFalse(result);
    }
}