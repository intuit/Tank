/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JobInstanceDaoTest
 * 
 * @author dangleton
 * 
 */
public class JobInstanceDaoTest {

    private JobInstanceDao dao;

    @BeforeEach
    public void setUp() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new JobInstanceDao();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFindComplete() {
        // Arrange
        insertData();

        // Act & Assert
        List<JobInstance> jobs = dao.findAll();
        assertEquals(3, jobs.size());

        // Act & Assert
        List<JobInstance> findCompleted = dao.findCompleted();
        assertEquals(1, findCompleted.size());
        JobInstance job = findCompleted.get(0);
        assertEquals(JobQueueStatus.Completed, job.getStatus());

    }

    /**
     * 
     */
    private void insertData() {
        JobInstance instance = getBaseInstance();
        dao.saveOrUpdate(instance);

        instance = getBaseInstance();
        instance.setName(JobQueueStatus.Running.name());
        Calendar cal = getCalendar(new Date());
        cal.add(Calendar.HOUR_OF_DAY, -2);
        instance.setStartTime(new Date(cal.getTime().getTime()));
        instance.setStatus(JobQueueStatus.Running);
        dao.saveOrUpdate(instance);

        instance = getBaseInstance();
        instance.setName(JobQueueStatus.Completed.name());
        instance.setStatus(JobQueueStatus.Completed);
        instance.setStartTime(new Date(cal.getTime().getTime()));
        cal.add(Calendar.HOUR_OF_DAY, 2);
        instance.setEndTime(new Date(cal.getTime().getTime()));
        dao.saveOrUpdate(instance);
    }

    private Calendar getCalendar(Date date) {
        return Calendar.getInstance(TimeZone.getDefault());
    }

    /**
     * 
     */
    private JobInstance getBaseInstance() {
        JobInstance instance = new JobInstance();
        instance.setBaselineVirtualUsers(1);
        instance.setName(JobQueueStatus.Created.name());
        instance.setStatus(JobQueueStatus.Created);
        instance.setWorkloadId(1);
        instance.setRampTime(1000 * 60 * 60 * 30);
        instance.setSimulationTime(1000 * 60 * 60 * 60);
        instance.setTotalVirtualUsers(3000);
        return instance;
    }
}
