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
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
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
        running.setNumAgents(3);
        running.setTargetRampRate(2.5);
        running.setRampTime(60_000);
        running.setJobDetails("the details");
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
            assertEquals(3, job.numAgents());
            assertEquals(2.5, job.getTargetRampRate());
            assertEquals(60_000, job.getRampTime());

            assertEquals(Set.of("RUNNING", "ENDED"), names(dao.findQueuedJobs(1, null, true, List.of())));
            assertEquals(Set.of("RUNNING", "DELETED"),
                    names(dao.findQueuedJobs(null, hourAgo, false, List.of(ids.get("DELETED")))),
                    "jobs asked for by id are returned whatever their state");
            assertEquals(Set.of(), names(dao.findQueuedJobs(null, new Date(System.currentTimeMillis() + 60_000),
                    true, List.of(ids.get("RUNNING")))), "only queues modified since are read");
            assertEquals(Set.of(), names(dao.findQueuedJobs(2, null, true, List.of())));

            assertEquals(1, dao.findProjectIdForJobId(ids.get("ENDED")));
            assertNull(dao.findProjectIdForJobId(-1));

            assertEquals("the details", jobDao.findJobDetails(ids.get("RUNNING")));
            assertNull(jobDao.findJobDetails(-1));
        } finally {
            // Cleanup
            dao.delete(queue);
        }
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void findAll_loadsJobsWithoutAQueryPerTenQueues() {
        // ten times the old batch size of 10, which needed a query for every 10 queues
        List<JobQueue> created = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            JobQueue queue = dao.findOrCreateForProjectId(5000 + i);
            JobInstance job = new JobInstance();
            job.setName("queued-" + i);
            job.setStatus(JobQueueStatus.Created);
            job.getVariables().put("name", "value");
            queue.addJob(job);
            created.add(dao.saveOrUpdate(queue));
        }
        Set<Integer> ids = created.stream().map(JobQueue::getId).collect(Collectors.toSet());
        dao.getEntityManager().clear(); // load cold, as a new request would
        Statistics statistics = dao.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            List<JobQueue> queues = dao.findAll().stream().filter(q -> ids.contains(q.getId())).toList();

            assertEquals(100, queues.size());
            queues.forEach(q -> {
                assertEquals(1, q.getJobs().size());
                assertEquals(Map.of("name", "value"), q.getJobs().iterator().next().getVariables());
            });
            // the queues, then one subselect for their jobs, which joins in the jobs' own collections
            assertEquals(2, statistics.getPrepareStatementCount());
        } finally {
            statistics.setStatisticsEnabled(false);
            created.forEach(dao::delete);
        }
    }

    private static Set<String> names(List<QueuedJob> jobs) {
        return jobs.stream().map(QueuedJob::name).collect(Collectors.toSet());
    }

}
