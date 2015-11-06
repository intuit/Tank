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

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.test.TestGroups;

/**
 * JobInstanceDaoTest
 * 
 * @author dangleton
 * 
 */
public class JobInstanceDaoTest {

    private JobInstanceDao dao;

    @BeforeClass
    public void setUp() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        dao = new JobInstanceDao();
        insertData();
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testFindComplete() throws Exception {
        List<JobInstance> all = dao.findAll();
        Assert.assertEquals(3, all.size());
        List<JobInstance> findCompleted = dao.findCompleted();
        Assert.assertEquals(1, findCompleted.size());
        JobInstance job = findCompleted.get(0);
        Assert.assertEquals(JobQueueStatus.Completed, job.getStatus());

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
