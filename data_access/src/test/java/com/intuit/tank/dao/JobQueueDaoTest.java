/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.JobQueue;
import com.intuit.tank.test.TestGroups;

/**
 * JobQueueDaoTest
 * 
 * @author msreekakula
 * 
 */
public class JobQueueDaoTest {

    private JobQueueDao dao;

    @BeforeEach
    public void setUp() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new JobQueueDao();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFindJobQueues() throws Exception {
        JobQueue queue = dao.findOrCreateForProjectId(1);
        assertEquals(1, queue.getId());
        JobQueue queue1 = dao.findForJobId(2);
        assertEquals(null, queue1);
        List<JobQueue> queue2 = dao.getForProjectIds(Arrays.asList(1,2,3));
        assertEquals(1, queue2.size());
        List<JobQueue> recentQueues = dao.findRecent(new Date());
        assertEquals(0, recentQueues.size());

    }

}
