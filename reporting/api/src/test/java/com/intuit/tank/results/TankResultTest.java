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

import java.text.DateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;

import com.intuit.tank.results.TankResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>TankResultTest</code> contains tests for the class <code>{@link TankResult}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:31 AM
 */
public class TankResultTest {
    /**
     * Run the TankResult() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testTankResult_1()
            throws Exception {
        TankResult result = new TankResult();
        assertNotNull(result);
    }

    /**
     * Run the void add(TankResult) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testAdd_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(0);
        fixture.setRequestName("");
        TankResult result = new TankResult();
        result.setError(true);

        fixture.add(result);

    }

    /**
     * Run the void add(TankResult) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testAdd_2()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        TankResult result = new TankResult();
        result.setError(true);

        fixture.add(result);

    }

    /**
     * Run the void add(TankResult) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testAdd_3()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(false);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(0);
        fixture.setRequestName("");
        TankResult result = new TankResult();
        result.setError(false);

        fixture.add(result);

    }

    /**
     * Run the void add(TankResult) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testAdd_4()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(false);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        TankResult result = new TankResult();
        result.setError(false);

        fixture.add(result);

    }

    /**
     * Run the String getJobId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetJobId_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the String getRequestName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetRequestName_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        String result = fixture.getRequestName();

        assertEquals("", result);
    }

    /**
     * Run the int getResponseSize() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetResponseSize_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        int result = fixture.getResponseSize();

        assertEquals(1, result);
    }

    /**
     * Run the int getResponseTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetResponseTime_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        int result = fixture.getResponseTime();

        assertEquals(1, result);
    }

    /**
     * Run the int getStatusCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetStatusCode_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        int result = fixture.getStatusCode();

        assertEquals(1, result);
    }

    /**
     * Run the Date getTimeStamp() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetTimeStamp_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        Date result = fixture.getTimeStamp();

        assertNotNull(result);
    }

    /**
     * Run the boolean isError() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testIsError_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        boolean result = fixture.isError();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isError() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testIsError_2()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(false);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        boolean result = fixture.isError();

        assertEquals(false, result);
    }

    /**
     * Run the void setError(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetError_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        boolean error = true;

        fixture.setError(error);

    }

    /**
     * Run the void setJobId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetJobId_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        String jobId = "";

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setRequestName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetRequestName_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        String requestName = "";

        fixture.setRequestName(requestName);

    }

    /**
     * Run the void setResponseSize(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetResponseSize_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        int responseSize = 1;

        fixture.setResponseSize(responseSize);

    }

    /**
     * Run the void setResponseTime(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetResponseTime_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        int connectionTime = 1;

        fixture.setResponseTime(connectionTime);

    }

    /**
     * Run the void setStatusCode(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetStatusCode_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        int statusCode = 1;

        fixture.setStatusCode(statusCode);

    }

    /**
     * Run the void setTimeStamp(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetTimeStamp_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");
        Date timeStamp = new Date();

        fixture.setTimeStamp(timeStamp);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        TankResult fixture = new TankResult();
        fixture.setError(true);
        fixture.setResponseSize(1);
        fixture.setResponseTime(1);
        fixture.setJobId("");
        fixture.setTimeStamp(new Date());
        fixture.setStatusCode(1);
        fixture.setRequestName("");

        String result = fixture.toString();

    }
}