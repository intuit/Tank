/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import static org.junit.Assert.*;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.UserDetail;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * DataFileDescriptorTest
 * 
 * @author dangleton
 * 
 */
public class CloudVmStatusTest {

    /**
     * Run the CloudVmStatus() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCloudVmStatus_1()
        throws Exception {

        CloudVmStatus result = new CloudVmStatus();

        assertNotNull(result);
        assertEquals(null, result.getJobId());
        assertEquals(null, result.getReportTime());
        assertEquals(null, result.getStartTime());
        assertEquals(null, result.getInstanceId());
        assertEquals(null, result.getEndTime());
        assertEquals(null, result.getVmRegion());
        assertEquals(0, result.getCurrentUsers());
        assertEquals(null, result.getValidationFailures());
        assertEquals(null, result.getSecurityGroup());
        assertEquals(0, result.getTotalTps());
        assertEquals(0, result.getTotalUsers());
    }

    /**
     * Run the CloudVmStatus(CloudVmStatus) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCloudVmStatus_2()
        throws Exception {
        CloudVmStatus copy = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        copy.setTotalTps(1);

        CloudVmStatus result = new CloudVmStatus(copy);

        assertNotNull(result);
        assertEquals("", result.getJobId());
        assertEquals(null, result.getReportTime());
        assertEquals("", result.getInstanceId());
        assertEquals(1, result.getCurrentUsers());
        assertEquals("", result.getSecurityGroup());
        assertEquals(1, result.getTotalTps());
        assertEquals(1, result.getTotalUsers());
    }

    /**
     * Run the CloudVmStatus(String,String,String,JobStatus,VMImageType,VMRegion,VMStatus,ValidationStatus,int,int,Date,Date) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCloudVmStatus_3()
        throws Exception {
        String instanceId = "";
        String jobId = "";
        String securityGroup = "";
        JobStatus jobStatus = JobStatus.Completed;
        VMImageType role = VMImageType.AGENT;
        VMRegion vmRegion = VMRegion.ASIA_1;
        VMStatus vmStatus = VMStatus.pending;
        ValidationStatus validationFailures = new ValidationStatus();
        int totalUsers = 1;
        int currentUsers = 1;
        Date startTime = new Date();
        Date endTime = new Date();

        CloudVmStatus result = new CloudVmStatus(instanceId, jobId, securityGroup, jobStatus, role, vmRegion, vmStatus, validationFailures, totalUsers, currentUsers, startTime, endTime);

        assertNotNull(result);
        assertEquals("", result.getJobId());
        assertEquals(null, result.getReportTime());
        assertEquals("", result.getInstanceId());
        assertEquals(1, result.getCurrentUsers());
        assertEquals("", result.getSecurityGroup());
        assertEquals(0, result.getTotalTps());
        assertEquals(1, result.getTotalUsers());
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        Object obj = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());

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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        Object obj = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the int getCurrentUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetCurrentUsers_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        int result = fixture.getCurrentUsers();

        assertEquals(1, result);
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        Date result = fixture.getEndTime();

        assertNotNull(result);
    }

    /**
     * Run the String getInstanceId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetInstanceId_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        String result = fixture.getInstanceId();

        assertEquals("", result);
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the JobStatus getJobStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetJobStatus_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        JobStatus result = fixture.getJobStatus();

        assertNotNull(result);
        assertEquals("Completed", result.name());
        assertEquals("Completed", result.toString());
        assertEquals(6, result.ordinal());
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        Date result = fixture.getReportTime();

        assertNotNull(result);
    }

    /**
     * Run the VMImageType getRole() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetRole_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        VMImageType result = fixture.getRole();

        assertNotNull(result);
        assertEquals("Agent", result.getConfigName());
        assertEquals("AGENT", result.name());
        assertEquals("AGENT", result.toString());
    }

    /**
     * Run the String getSecurityGroup() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetSecurityGroup_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        String result = fixture.getSecurityGroup();

        assertEquals("", result);
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        Date result = fixture.getStartTime();

        assertNotNull(result);
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        int result = fixture.getTotalTps();

        assertEquals(1, result);
    }

    /**
     * Run the int getTotalUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetTotalUsers_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        int result = fixture.getTotalUsers();

        assertEquals(1, result);
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        List<UserDetail> result = fixture.getUserDetails();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the ValidationStatus getValidationFailures() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetValidationFailures_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        ValidationStatus result = fixture.getValidationFailures();

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getValidationSkips());
        assertEquals(0, result.getValidationRestarts());
        assertEquals(0, result.getValidationGotos());
        assertEquals(0, result.getValidationAborts());
        assertEquals(0, result.getValidationKills());
        assertEquals(0, result.getValidationSkipGroups());
    }

    /**
     * Run the VMRegion getVmRegion() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetVmRegion_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        VMRegion result = fixture.getVmRegion();

        assertNotNull(result);
        assertEquals("Asia Pacific (Singapore)", result.toString());
        assertEquals("ap-southeast-1", result.getRegion());
        assertEquals("ec2.ap-southeast-1.amazonaws.com", result.getEndpoint());
        assertEquals("Asia Pacific (Singapore)", result.getDescription());
        assertEquals("ASIA_1", result.name());
    }

    /**
     * Run the VMStatus getVmStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    @org.junit.Ignore
    public void testGetVmStatus_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        VMStatus result = fixture.getVmStatus();

        assertNotNull(result);
        assertEquals("pending", result.name());
        assertEquals("pending", result.toString());
        assertEquals(1, result.ordinal());
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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        int result = fixture.hashCode();

        assertEquals(58725, result);
    }

    /**
     * Run the void setCurrentUsers(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetCurrentUsers_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        int currentUsers = 1;

        fixture.setCurrentUsers(currentUsers);

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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        Date endTime = new Date();

        fixture.setEndTime(endTime);

    }

    /**
     * Run the void setJobStatus(JobStatus) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetJobStatus_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        JobStatus jobStatus = JobStatus.Completed;

        fixture.setJobStatus(jobStatus);

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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        Date reportTime = new Date();

        fixture.setReportTime(reportTime);

    }

    /**
     * Run the void setTotalTps(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetTotalTps_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        int totalTps = 1;

        fixture.setTotalTps(totalTps);

    }

    /**
     * Run the void setUserDetails(List<UserDetail>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetUserDetails_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        List<UserDetail> userDetails = new LinkedList();

        fixture.setUserDetails(userDetails);

    }

    /**
     * Run the void setVmStatus(VMStatus) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testSetVmStatus_1()
        throws Exception {
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());
        VMStatus vmStatus = VMStatus.pending;

        fixture.setVmStatus(vmStatus);

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
        CloudVmStatus fixture = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        fixture.setReportTime(new Date());
        fixture.setTotalTps(1);
        fixture.setUserDetails(new LinkedList());

        String result = fixture.toString();

    }
}
