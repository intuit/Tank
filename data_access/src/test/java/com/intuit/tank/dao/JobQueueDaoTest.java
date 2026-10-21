/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.*;
import java.util.stream.Collectors;

import com.intuit.tank.project.*;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
        assertEquals(1, queue.getProjectId());

        assertNull(dao.findForJobId(2));

        List<JobQueue> queue2 = dao.getForProjectIds(Arrays.asList(1,2,3));
        assertEquals(1, queue2.size());

        List<JobQueue> recentQueues = dao.findRecent(new Date());
        assertEquals(0, recentQueues.size());

        dao.delete(queue);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void test_findOrCreateForProjectId() {
        // Arrange
        int projectId = 1;

        // Act
        JobQueue queue = dao.findOrCreateForProjectId(projectId);

        // Assert
        assertEquals(1, queue.getProjectId());

        // Cleanup
        dao.delete(queue);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void test_lookupJobs() {
        // Arrange
        JobQueue queue = dao.findOrCreateForProjectId(1);

        // Act
        assertEquals(0, queue.getJobs().size());
        Set<JobRegion> jobRegion = Collections.singleton(JobRegion.builder().region(VMRegion.US_EAST_2).build());
        JobConfiguration jobConfiguration = JobConfiguration.builder().jobRegion(jobRegion).build();
        Workload workload = Workload.builder().name("TEST_WORKLOAD").jobConfiguration(jobConfiguration).build();
        queue.addJob(new JobInstance(workload, "TEST"));
        dao.saveOrUpdate(queue);

        JobQueue jobQueue = dao.findOrCreateForProjectId(1);

        // Assert
        assertEquals(1, jobQueue.getProjectId());
        assertEquals(1, jobQueue.getJobs().size());

        //Cleanup
        dao.delete(jobQueue);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void test_findQueuedJobs() {
        // Arrange
        JobQueue queue = dao.findOrCreateForProjectId(1);
        Set<JobRegion> jobRegion = Collections.singleton(JobRegion.builder().region(VMRegion.US_EAST_2).build());
        Workload workload = Workload.builder().name("TEST_WORKLOAD")
                .jobConfiguration(JobConfiguration.builder().jobRegion(jobRegion).build()).build();
        JobInstance running = new JobInstance(workload, "RUNNING");
        running.setStatus(JobQueueStatus.Running);
        JobInstance ended = new JobInstance(workload, "ENDED");
        ended.setStatus(JobQueueStatus.Completed);
        ended.setEndTime(new Date());
        JobInstance deleted = new JobInstance(workload, "DELETED");
        deleted.setStatus(JobQueueStatus.Deleted);
        // saved first, as the jobs of an unsaved queue would all have id 0 and collapse in its set
        JobInstanceDao jobDao = new JobInstanceDao();
        queue.addJob(jobDao.saveOrUpdate(running));
        queue.addJob(jobDao.saveOrUpdate(ended));
        queue.addJob(jobDao.saveOrUpdate(deleted));
        queue = dao.saveOrUpdate(queue);
        Map<String, Integer> ids = new HashMap<>();
        queue.getJobs().forEach(job -> ids.put(job.getName(), job.getId()));
        Date hourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);

        try {
            // Act & Assert
            List<QueuedJob> unfinished = dao.findQueuedJobs(1, null, false, List.of());
            assertEquals(List.of("RUNNING"), unfinished.stream().map(QueuedJob::name).toList());
            QueuedJob job = unfinished.get(0);
            assertEquals(1, job.projectId());
            assertEquals(ids.get("RUNNING"), job.jobId());
            assertEquals(JobQueueStatus.Running, job.status());

            assertEquals(Set.of("RUNNING", "ENDED"), names(dao.findQueuedJobs(1, null, true, List.of())));
            assertEquals(Set.of("RUNNING", "DELETED"),
                    names(dao.findQueuedJobs(null, hourAgo, false, List.of(ids.get("DELETED")))),
                    "jobs asked for by id are returned whatever their state");
            assertEquals(Set.of(), names(dao.findQueuedJobs(null, new Date(System.currentTimeMillis() + 60_000),
                    true, List.of(ids.get("RUNNING")))), "only queues modified since are read");
            assertEquals(Set.of(), names(dao.findQueuedJobs(2, null, true, List.of())));

            assertEquals(1, dao.findProjectIdForJobId(ids.get("ENDED")));
            assertNull(dao.findProjectIdForJobId(-1));
        } finally {
            // Cleanup
            dao.delete(queue);
        }
    }

    private static Set<String> names(List<QueuedJob> jobs) {
        return jobs.stream().map(QueuedJob::name).collect(Collectors.toSet());
    }

}
