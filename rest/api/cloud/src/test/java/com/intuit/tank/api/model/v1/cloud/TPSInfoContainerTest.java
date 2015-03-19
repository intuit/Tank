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
import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import com.intuit.tank.api.model.v1.cloud.TPSInfo;
import com.intuit.tank.api.model.v1.cloud.TPSInfoContainer;

import static org.junit.Assert.*;

/**
 * The class <code>TPSInfoContainerTest</code> contains tests for the class <code>{@link TPSInfoContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:57 PM
 */
public class TPSInfoContainerTest {
    /**
     * Run the TPSInfoContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_1()
        throws Exception {

        TPSInfoContainer result = new TPSInfoContainer();

        assertNotNull(result);
        assertEquals(0, result.getPeriod());
        assertEquals(null, result.getMaxTime());
        assertEquals(null, result.getMinTime());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the TPSInfoContainer(Date,Date,int,List<TPSInfo>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_2()
        throws Exception {
        Date minTime = new Date();
        Date maxTime = new Date();
        int period = 1;
        List<TPSInfo> tpsInfos = new LinkedList();

        TPSInfoContainer result = new TPSInfoContainer(minTime, maxTime, period, tpsInfos);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the TPSInfoContainer(Date,Date,int,List<TPSInfo>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_3()
        throws Exception {
        Date minTime = new Date();
        Date maxTime = new Date();
        int period = 1;
        List<TPSInfo> tpsInfos = new LinkedList();

        TPSInfoContainer result = new TPSInfoContainer(minTime, maxTime, period, tpsInfos);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the TPSInfoContainer(Date,Date,int,List<TPSInfo>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_4()
        throws Exception {
        Date minTime = new Date();
        Date maxTime = new Date();
        int period = 1;
        List<TPSInfo> tpsInfos = new LinkedList();

        TPSInfoContainer result = new TPSInfoContainer(minTime, maxTime, period, tpsInfos);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the TPSInfoContainer(Date,Date,int,List<TPSInfo>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_5()
        throws Exception {
        Date minTime = new Date();
        Date maxTime = new Date();
        int period = 1;
        List<TPSInfo> tpsInfos = new LinkedList();

        TPSInfoContainer result = new TPSInfoContainer(minTime, maxTime, period, tpsInfos);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the TPSInfoContainer(Date,Date,int,List<TPSInfo>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_6()
        throws Exception {
        Date minTime = new Date();
        Date maxTime = new Date();
        int period = 1;
        List<TPSInfo> tpsInfos = new LinkedList();

        TPSInfoContainer result = new TPSInfoContainer(minTime, maxTime, period, tpsInfos);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the TPSInfoContainer(Date,Date,int,List<TPSInfo>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_7()
        throws Exception {
        Date minTime = new Date();
        Date maxTime = new Date();
        int period = 1;
        List<TPSInfo> tpsInfos = new LinkedList();

        TPSInfoContainer result = new TPSInfoContainer(minTime, maxTime, period, tpsInfos);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the TPSInfoContainer(Date,Date,int,List<TPSInfo>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testTPSInfoContainer_8()
        throws Exception {
        Date minTime = new Date();
        Date maxTime = new Date();
        int period = 1;
        List<TPSInfo> tpsInfos = null;

        TPSInfoContainer result = new TPSInfoContainer(minTime, maxTime, period, tpsInfos);

        assertNotNull(result);
        assertEquals(1, result.getPeriod());
        assertEquals(null, result.getTpsInfos());
        assertEquals(0, result.getTotalTps());
    }

    /**
     * Run the Date getMaxTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetMaxTime_1()
        throws Exception {
        TPSInfoContainer fixture = new TPSInfoContainer(new Date(), new Date(), 1, new LinkedList());

        Date result = fixture.getMaxTime();

        assertNotNull(result);
    }

    /**
     * Run the Date getMinTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetMinTime_1()
        throws Exception {
        TPSInfoContainer fixture = new TPSInfoContainer(new Date(), new Date(), 1, new LinkedList());

        Date result = fixture.getMinTime();

        assertNotNull(result);
    }

    /**
     * Run the int getPeriod() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetPeriod_1()
        throws Exception {
        TPSInfoContainer fixture = new TPSInfoContainer(new Date(), new Date(), 1, new LinkedList());

        int result = fixture.getPeriod();

        assertEquals(1, result);
    }

    /**
     * Run the int getTotalTps() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTotalTps_1()
        throws Exception {
        TPSInfoContainer fixture = new TPSInfoContainer(new Date(), new Date(), 1, new LinkedList());

        int result = fixture.getTotalTps();

        assertEquals(0, result);
    }

    /**
     * Run the List<TPSInfo> getTpsInfos() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTpsInfos_1()
        throws Exception {
        TPSInfoContainer fixture = new TPSInfoContainer(new Date(), new Date(), 1, new LinkedList());

        List<TPSInfo> result = fixture.getTpsInfos();

        assertNotNull(result);
        assertEquals(0, result.size());
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
        TPSInfoContainer fixture = new TPSInfoContainer(new Date(), new Date(), 1, new LinkedList());

        String result = fixture.toString();

    }
}