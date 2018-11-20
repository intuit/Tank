package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobVMInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

/**
 * The class <code>JobInstanceTest</code> contains tests for the class <code>{@link JobInstance}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class JobInstanceTest {
    /**
     * Run the JobInstance() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobInstance_1()
        throws Exception {

        JobInstance result = new JobInstance();
        assertNotNull(result);
    }

    /**
     * Run the JobInstance(Workload,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobInstance_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        String name = "";

        JobInstance result = new JobInstance(workload, name);
        assertNotNull(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Object obj = new Object();

        boolean result = fixture.equals(obj);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Object obj = new JobInstance();

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Object obj = new JobInstance();

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        String result = fixture.getCreator();
        assertNotNull(result);
    }

    /**
     * Run the Set<EntityVersion> getDataFileVersions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetDataFileVersions_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Set<EntityVersion> result = fixture.getDataFileVersions();
        assertNotNull(result);
    }

    /**
     * Run the Date getEndTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetEndTime_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Date result = fixture.getEndTime();
        assertNotNull(result);
    }

    /**
     * Run the String getJobDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobDetails_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        String result = fixture.getJobDetails();
        assertNotNull(result);
    }

    /**
     * Run the Set<EntityVersion> getJobRegionVersions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobRegionVersions_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Set<EntityVersion> result = fixture.getJobRegionVersions();
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        String result = fixture.getName();
        assertNotNull(result);
    }

    /**
     * Run the Set<EntityVersion> getNotificationVersions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetNotificationVersions_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Set<EntityVersion> result = fixture.getNotificationVersions();
        assertNotNull(result);
    }

    /**
     * Run the Date getScheduledTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScheduledTime_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Date result = fixture.getScheduledTime();
        assertNotNull(result);
    }

    /**
     * Run the Date getStartTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetStartTime_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Date result = fixture.getStartTime();
        assertNotNull(result);
    }

    /**
     * Run the JobQueueStatus getStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetStatus_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        JobQueueStatus result = fixture.getStatus();
        assertNotNull(result);
    }

    /**
     * Run the int getTotalVirtualUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTotalVirtualUsers_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        int result = fixture.getTotalVirtualUsers();
    }

    /**
     * Run the Map<String, String> getVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVariables_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(null);
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Map<String, String> result = fixture.getVariables();
        assertNotNull(result);
    }

    /**
     * Run the Map<String, String> getVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVariables_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Map<String, String> result = fixture.getVariables();
        assertNotNull(result);
    }

    /**
     * Run the Set<JobVMInstance> getVmInstances() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVmInstances_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        Set<JobVMInstance> result = fixture.getVmInstances();
        assertNotNull(result);
    }

    /**
     * Run the int getWorkloadId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetWorkloadId_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        int result = fixture.getWorkloadId();
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        int result = fixture.hashCode();
    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        String creator = "";

        fixture.setCreator(creator);
    }

    /**
     * Run the void setDataFileVersions(Set<EntityVersion>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetDataFileVersions_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Set<EntityVersion> dataFileVersions = new HashSet();

        fixture.setDataFileVersions(dataFileVersions);
    }

    /**
     * Run the void setEndTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetEndTime_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Date endTime = new Date();

        fixture.setEndTime(endTime);
    }

    /**
     * Run the void setJobDetails(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJobDetails_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        String jobDetails = "";

        fixture.setJobDetails(jobDetails);
    }

    /**
     * Run the void setJobRegionVersions(Set<EntityVersion>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJobRegionVersions_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Set<EntityVersion> jobRegionVersions = new HashSet();

        fixture.setJobRegionVersions(jobRegionVersions);
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        String name = "";

        fixture.setName(name);
    }

    /**
     * Run the void setNotificationVersions(Set<EntityVersion>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetNotificationVersions_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Set<EntityVersion> notificationVersions = new HashSet();

        fixture.setNotificationVersions(notificationVersions);
    }

    /**
     * Run the void setScheduledTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScheduledTime_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Date scheduledTime = new Date();

        fixture.setScheduledTime(scheduledTime);
    }

    /**
     * Run the void setStartTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetStartTime_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Date startTime = new Date();

        fixture.setStartTime(startTime);
    }

    /**
     * Run the void setStatus(JobQueueStatus) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetStatus_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        JobQueueStatus status = JobQueueStatus.Aborted;

        fixture.setStatus(status);
    }

    /**
     * Run the void setTotalVirtualUsers(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetTotalVirtualUsers_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        int totalVirtualUsers = 1;

        fixture.setTotalVirtualUsers(totalVirtualUsers);
    }

    /**
     * Run the void setVariables(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVariables_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Map<String, String> variables = new HashMap();

        fixture.setVariables(variables);
    }

    /**
     * Run the void setVmInstances(Set<JobVMInstance>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVmInstances_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        Set<JobVMInstance> vmInstances = new HashSet();

        fixture.setVmInstances(vmInstances);
    }

    /**
     * Run the void setWorkloadId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetWorkloadId_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());
        int workloadId = 1;

        fixture.setWorkloadId(workloadId);
    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance fixture = new JobInstance(workload, "");
        fixture.setVariables(new HashMap());
        fixture.setStatus(JobQueueStatus.Aborted);
        fixture.setDataFileVersions(new HashSet());
        fixture.setVmInstances(new HashSet());
        fixture.setScheduledTime(new Date());
        fixture.setWorkloadId(1);
        fixture.setStartTime(new Date());
        fixture.setEndTime(new Date());
        fixture.setTotalVirtualUsers(1);
        fixture.setNotificationVersions(new HashSet());
        fixture.setCreator("");
        fixture.setJobDetails("");
        fixture.setJobRegionVersions(new HashSet());

        String result = fixture.toString();
        assertNotNull(result);
    }
}