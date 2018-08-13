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
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.ProjectStatusContainer;
import com.intuit.tank.api.model.v1.cloud.UserDetail;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ProjectStatusContainerTest</code> contains tests for the class <code>{@link ProjectStatusContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:57 PM
 */
public class ProjectStatusContainerTest {
    /**
     * Run the ProjectStatusContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testProjectStatusContainer_1()
        throws Exception {

        ProjectStatusContainer result = new ProjectStatusContainer();

        assertNotNull(result);
        assertEquals(null, result.getJobId());
        assertEquals(null, result.getUserDetails());
    }

    /**
     * Run the void addStatusContainer(CloudVmStatusContainer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testAddStatusContainer_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        CloudVmStatusContainer container = new CloudVmStatusContainer();
        container.setJobId("");

        fixture.addStatusContainer(container);

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_2()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_3()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_4()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_5()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_6()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_7()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_8()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_9()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_10()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_11()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_12()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_13()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_14()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_15()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the void calculateUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCalculateUserDetails_16()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        fixture.calculateUserDetails();

    }

    /**
     * Run the Map<Date, List<UserDetail>> getDetailMap() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetDetailMap_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        Map<Date, List<UserDetail>> result = fixture.getDetailMap();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getJobId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetJobId_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the Date getReportTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetReportTime_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        Date result = fixture.getReportTime();

        assertNotNull(result);
    }

    /**
     * Run the List<UserDetail> getUserDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetUserDetails_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());

        List<UserDetail> result = fixture.getUserDetails();

        assertEquals(null, result);
    }

    /**
     * Run the void setJobId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetJobId_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        String jobId = "";

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setReportTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetReportTime_1()
        throws Exception {
        ProjectStatusContainer fixture = new ProjectStatusContainer();
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        Date reportTime = new Date();

        fixture.setReportTime(reportTime);

    }
}