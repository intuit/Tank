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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.UserDetail;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

/**
 * The class <code>CloudVmStatusContainerTest</code> contains tests for the class <code>{@link CloudVmStatusContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:57 PM
 */
public class CloudVmStatusContainerTest {
    /**
     * Run the CloudVmStatusContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCloudVmStatusContainer_1()
        throws Exception {

        CloudVmStatusContainer result = new CloudVmStatusContainer();

        assertNotNull(result);
        assertEquals(null, result.getJobId());
        assertEquals(null, result.getEndTime());
    }

    /**
     * Run the CloudVmStatusContainer(Set<CloudVmStatus>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCloudVmStatusContainer_2()
        throws Exception {
        Set<CloudVmStatus> statuses = new HashSet();

        CloudVmStatusContainer result = new CloudVmStatusContainer(statuses);

        assertNotNull(result);
        assertEquals(null, result.getJobId());
        assertEquals(null, result.getEndTime());
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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        fixture.calculateUserDetails();

    }

    /**
     * Run the int compareTo(CloudVmStatusContainer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);
        CloudVmStatusContainer o = new CloudVmStatusContainer();
        o.setStartTime(new Date());

        int result = fixture.compareTo(o);

        assertEquals(0, result);
    }

    /**
     * Run the int compareTo(CloudVmStatusContainer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCompareTo_2()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime((Date) null);
        fixture.setStatus(JobQueueStatus.Aborted);
        CloudVmStatusContainer o = new CloudVmStatusContainer();
        o.setStartTime((Date) null);

        int result = fixture.compareTo(o);

        assertEquals(0, result);
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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        Map<Date, List<UserDetail>> result = fixture.getDetailMap();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Date getEndTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetEndTime_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        Date result = fixture.getEndTime();

        assertNotNull(result);
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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        Date result = fixture.getReportTime();

        assertNotNull(result);
    }

    /**
     * Run the Date getStartTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetStartTime_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        Date result = fixture.getStartTime();

        assertNotNull(result);
    }

    /**
     * Run the JobQueueStatus getStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetStatus_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        JobQueueStatus result = fixture.getStatus();

        assertNotNull(result);
        assertEquals("Aborted", result.name());
        assertEquals("Aborted", result.toString());
        assertEquals(8, result.ordinal());
    }

    /**
     * Run the Set<CloudVmStatus> getStatuses() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetStatuses_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        Set<CloudVmStatus> result = fixture.getStatuses();

        assertNotNull(result);
        assertEquals(0, result.size());
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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);

        List<UserDetail> result = fixture.getUserDetails();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setEndTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetEndTime_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);
        Date endTime = new Date();

        fixture.setEndTime(endTime);

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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);
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
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);
        Date reportTime = new Date();

        fixture.setReportTime(reportTime);

    }

    /**
     * Run the void setStartTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetStartTime_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);
        Date startTime = new Date();

        fixture.setStartTime(startTime);

    }

    /**
     * Run the void setStatus(JobQueueStatus) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetStatus_1()
        throws Exception {
        CloudVmStatusContainer fixture = new CloudVmStatusContainer(new HashSet());
        fixture.setEndTime(new Date());
        fixture.setJobId("");
        fixture.setReportTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setStatus(JobQueueStatus.Aborted);
        JobQueueStatus status = JobQueueStatus.Aborted;

        fixture.setStatus(status);

    }
}