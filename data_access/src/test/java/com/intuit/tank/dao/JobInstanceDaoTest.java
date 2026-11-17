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
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import jakarta.persistence.EntityManager;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        // Act & Assert
        assertEquals(List.of(JobQueueStatus.Created, JobQueueStatus.Running),
                dao.findNotCompleted().stream().map(JobInstance::getStatus).sorted().toList());
        assertEquals(List.of(JobQueueStatus.Running),
                dao.getForStatus(List.of(JobQueueStatus.Running)).stream().map(JobInstance::getStatus).toList());
        assertEquals(List.of(), dao.getForStatus(List.of()));
        assertNull(dao.findJobDetails(job.getId()), "no details were recorded");

    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void findStatuses_readsEveryStatusInOneQuery() {
        JobInstance running = getBaseInstance();
        running.setStatus(JobQueueStatus.Running);
        running = dao.saveOrUpdate(running);
        JobInstance completed = getBaseInstance();
        completed.setStatus(JobQueueStatus.Completed);
        completed = dao.saveOrUpdate(jobWithCollections(completed));
        Statistics statistics = statistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            Map<Integer, JobQueueStatus> statuses = dao.findStatuses();

            assertEquals(JobQueueStatus.Running, statuses.get(running.getId()));
            assertEquals(JobQueueStatus.Completed, statuses.get(completed.getId()));
            assertEquals(statuses.keySet().stream().sorted().toList(), List.copyOf(statuses.keySet()), "in id order");
            assertEquals(1, statistics.getPrepareStatementCount());
        } finally {
            statistics.setStatisticsEnabled(false);
            dao.delete(running);
            dao.delete(completed);
        }
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void findSummaries_readsNewestFirstWithoutDeletedInOneQuery() {
        JobInstance older = getBaseInstance();
        older.setName("older");
        older.setStatus(JobQueueStatus.Running);
        older.setLocation("us-west-2");
        older = dao.saveOrUpdate(jobWithCollections(older));
        JobInstance deleted = getBaseInstance();
        deleted.setStatus(JobQueueStatus.Deleted);
        deleted = dao.saveOrUpdate(deleted);
        JobInstance newer = getBaseInstance();
        newer.setName("newer");
        newer.setStatus(JobQueueStatus.Completed);
        newer = dao.saveOrUpdate(jobWithCollections(newer));
        Statistics statistics = statistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            List<JobInstanceDao.JobSummary> summaries = dao.findSummaries();

            assertEquals(1, statistics.getPrepareStatementCount());
            assertEquals(List.of(newer.getId(), older.getId()), summaries.stream().map(JobInstanceDao.JobSummary::id).toList(),
                    "newest first, without the deleted job");
            JobInstanceDao.JobSummary summary = summaries.get(1);
            assertEquals("older", summary.name());
            assertEquals(JobQueueStatus.Running, summary.status());
            assertEquals("us-west-2", summary.location());
            assertEquals(3000, summary.totalVirtualUsers());
            assertEquals(1000L * 60 * 60 * 30, summary.rampTime());
            assertEquals(1000L * 60 * 60 * 60, summary.simulationTime());
        } finally {
            statistics.setStatisticsEnabled(false);
            dao.delete(older);
            dao.delete(deleted);
            dao.delete(newer);
        }
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void findSummaries_fallsBackToLegacySecondsColumns() {
        JobInstance job = dao.saveOrUpdate(getBaseInstance());
        EntityManager em = dao.getEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("update job_instance set ramp_time_ms = null, ramp_time_seconds = 7,"
                + " simulation_time_ms = null, simulation_time = 9 where id = ?1")
                .setParameter(1, job.getId()).executeUpdate();
        em.getTransaction().commit();
        try {
            JobInstanceDao.JobSummary summary = dao.findSummaries().get(0);

            assertEquals(job.getId(), summary.id());
            assertEquals(7000L, summary.rampTime());
            assertEquals(9000L, summary.simulationTime());
        } finally {
            dao.delete(job);
        }
    }

    private Statistics statistics() {
        return dao.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
    }

    private static JobInstance jobWithCollections(JobInstance job) {
        job.getVariables().put("name", "value");
        job.getDataFileVersions().add(new EntityVersion(1, 1, DataFile.class));
        job.getNotificationVersions().add(new EntityVersion(1, 1, JobNotification.class));
        job.getJobRegionVersions().add(new EntityVersion(1, 1, JobRegion.class));
        return job;
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
