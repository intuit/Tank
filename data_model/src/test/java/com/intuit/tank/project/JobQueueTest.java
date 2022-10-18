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

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>JobQueueTest</code> contains tests for the class <code>{@link JobQueue}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class JobQueueTest {
    /**
     * Run the JobQueue() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void test_JobQueue_empty()
        throws Exception {

        JobQueue result = new JobQueue();

        assertNotNull(result);
        assertEquals(0, result.getProjectId());
        assertEquals(0, result.getId());
        assertNull(result.getModified());
        assertNull(result.getCreated());
    }

    /**
     * Run the JobQueue(int) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void test_JobQueue_projectId()
        throws Exception {
        int projectId = 1;

        JobQueue result = new JobQueue(projectId);

        assertNotNull(result);
        assertEquals(1, result.getProjectId());
        assertEquals(0, result.getId());
        assertNull(result.getModified());
        assertNull(result.getCreated());
    }

    /**
     * Run the void addJob(JobInstance) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void test_setJobs_empty()
        throws Exception {
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());

        assertEquals(0, fixture.getJobs().size());
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void test_NOT_Equals()
        throws Exception {
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());
        Object obj = new Object();

        assertFalse(fixture.equals(obj));
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void test_Equals()
        throws Exception {
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());
        Object obj = new JobQueue();

        assertTrue(fixture.equals(obj));
    }

    /**
     * Run the Set<JobInstance> getJobs() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobs_1()
        throws Exception {
        JobQueue fixture = new JobQueue(1);
        fixture.addJob(new JobInstance(Workload.builder().build(), "TEST"));

        Set<JobInstance> result = fixture.getJobs();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetJobs_2()
            throws Exception {
        JobQueue fixture = new JobQueue(1);

        fixture.addJob(new JobInstance(Workload.builder().build(), "TEST"));

        assertEquals(1, fixture.getJobs().size());
    }

    /**
     * Run the int getProjectId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProjectId_1()
        throws Exception {
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());

        int result = fixture.getProjectId();

        assertEquals(1, result);
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
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());

        assertEquals(1305, fixture.hashCode());
    }

    /**
     * Run the void setJobs(Set<JobInstance>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJobs_1()
        throws Exception {
        JobQueue fixture = new JobQueue(1);
        Set<JobInstance> jobs = new HashSet();
        jobs.add(new JobInstance(Workload.builder().name("TEST").build(), "TEST"));

        fixture.setJobs(jobs);

        assertEquals(1, fixture.getJobs().size());

    }

    /**
     * Run the void setProjectId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetProjectId_1()
        throws Exception {
        int projectId = 1;
        JobQueue fixture = new JobQueue(projectId);
        fixture.setJobs(new HashSet());


        fixture.setProjectId(projectId);

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
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());

        String result = fixture.toString();

    }
}