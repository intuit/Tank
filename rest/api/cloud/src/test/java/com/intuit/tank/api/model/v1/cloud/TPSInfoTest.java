package com.intuit.tank.api.model.v1.cloud;

/*
 * #%L
 * Cloud Rest API
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

import org.junit.*;

import com.intuit.tank.api.model.v1.cloud.TPSInfo;

import static org.junit.Assert.*;

/**
 * The class <code>TPSInfoTest</code> contains tests for the class <code>{@link TPSInfo}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:57 PM
 */
public class TPSInfoTest {
    /**
     * Run the TPSInfo() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfo_1()
        throws Exception {

        TPSInfo result = new TPSInfo();

        assertNotNull(result);
        assertEquals(null, result.getTimestamp());
        assertEquals(null, result.getKey());
        assertEquals(0, result.getTPS());
        assertEquals(0, result.getPeriodInSeconds());
        assertEquals(0, result.getTransactions());
    }

    /**
     * Run the TPSInfo(Date,String,int,int) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfo_2()
        throws Exception {
        Date timestamp = new Date();
        String key = "";
        int transactions = 1;
        int period = 1;

        TPSInfo result = new TPSInfo(timestamp, key, transactions, period);

        assertNotNull(result);
        assertEquals("", result.getKey());
        assertEquals(1, result.getTPS());
        assertEquals(1, result.getPeriodInSeconds());
        assertEquals(1, result.getTransactions());
    }

    /**
     * Run the TPSInfo add(TPSInfo) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAdd_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);
        TPSInfo toAdd = new TPSInfo(new Date(), "", 1, 1);

        TPSInfo result = fixture.add(toAdd);

        assertNotNull(result);
        assertEquals("", result.getKey());
        assertEquals(2, result.getTPS());
        assertEquals(1, result.getPeriodInSeconds());
        assertEquals(2, result.getTransactions());
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        Date date = new Date();
        TPSInfo fixture = new TPSInfo(date, "", 1, 1);
        Object obj = new TPSInfo(date, "", 1, 1);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        Date date = new Date();
        TPSInfo fixture = new TPSInfo(date, "", 1, 1);
        Object obj = new TPSInfo(date, "", 1, 1);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the int getPeriodInSeconds() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetPeriodInSeconds_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);

        int result = fixture.getPeriodInSeconds();

        assertEquals(1, result);
    }

    /**
     * Run the int getTPS() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTPS_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);

        int result = fixture.getTPS();

        assertEquals(1, result);
    }

    /**
     * Run the int getTPS() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTPS_2()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 0);

        int result = fixture.getTPS();

        assertEquals(0, result);
    }

    /**
     * Run the Date getTimestamp() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTimestamp_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);

        Date result = fixture.getTimestamp();

        assertNotNull(result);
    }

    /**
     * Run the int getTransactions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTransactions_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);

        int result = fixture.getTransactions();

        assertEquals(1, result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);

        int result = fixture.hashCode();

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        TPSInfo fixture = new TPSInfo(new Date(), "", 1, 1);

        String result = fixture.toString();

    }
}