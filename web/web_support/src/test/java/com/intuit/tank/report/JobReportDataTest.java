package com.intuit.tank.report;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Date;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JobReportDataTest {

    private JobInstance jobInstance;
    private static final String PROJECT_NAME = "TestProject";

    @BeforeEach
    void setUp() {
        jobInstance = new JobInstance();
        jobInstance.setName("TestJob");
        jobInstance.setStatus(JobQueueStatus.Completed);
        Date startTime = new Date(1000000L);
        Date endTime = new Date(1060000L); // 60 seconds later
        jobInstance.setStartTime(startTime);
        jobInstance.setEndTime(endTime);
    }

    @Test
    public void testGetProjectName() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertEquals(PROJECT_NAME, data.getProjectName());
    }

    @Test
    public void testGetJobInstance() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertEquals(jobInstance, data.getJobInstance());
    }

    @Test
    public void testGetDuration() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        // duration in minutes calculation
        assertTrue(data.getDuration() >= 0);
    }

    @Test
    public void testGetStatus() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertEquals(JobQueueStatus.Completed.name(), data.getStatus());
    }

    @Test
    public void testGetStartTime() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertEquals(jobInstance.getStartTime(), data.getStartTime());
    }

    @Test
    public void testEquals_SameObject() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertEquals(data, data);
    }

    @Test
    public void testEquals_EqualObjects() {
        JobReportData data1 = new JobReportData(PROJECT_NAME, jobInstance);
        JobReportData data2 = new JobReportData("Other", jobInstance);
        assertEquals(data1, data2);
    }

    @Test
    public void testEquals_NullObject_ReturnsFalse() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertNotEquals(data, null);
    }

    @Test
    public void testEquals_NotJobReportData() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertNotEquals(data, "not a JobReportData");
    }

    @Test
    public void testHashCode() {
        JobReportData data = new JobReportData(PROJECT_NAME, jobInstance);
        assertEquals(jobInstance.hashCode(), data.hashCode());
    }
}
