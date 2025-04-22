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

import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.test.TestGroups;

/**
 * JobQueueDaoTest
 * 
 * @author msreekakula
 * 
 */
public class JobNotificationDaoTest {

    private JobNotificationDao dao;

    @BeforeEach
    public void setUp() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new JobNotificationDao();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFindRevisions() throws Exception {
        JobNotification jobnotification = new JobNotification();
        jobnotification.setBody("Test");
        JobNotification result = dao.saveOrUpdate(jobnotification);
        assertEquals(jobnotification.getBody(), result.getBody());
        JobNotification revisionJob = dao.findRevision(0,1);
        assertEquals(null, revisionJob);

    }

}
