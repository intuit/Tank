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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;

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
    public void testJobQueue_1()
        throws Exception {

        JobQueue result = new JobQueue();

        assertNotNull(result);
        assertEquals(0, result.getProjectId());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the JobQueue(int) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobQueue_2()
        throws Exception {
        int projectId = 1;

        JobQueue result = new JobQueue(projectId);

        assertNotNull(result);
        assertEquals(1, result.getProjectId());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the void addJob(JobInstance) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddJob_1()
        throws Exception {
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());
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
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
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
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());
        Object obj = new JobQueue();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());
        Object obj = new JobQueue();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        fixture.setJobs(new HashSet());

        Set<JobInstance> result = fixture.getJobs();

        assertNotNull(result);
        assertEquals(0, result.size());
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

        int result = fixture.hashCode();

        assertEquals(1305, result);
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
        fixture.setJobs(new HashSet());
        Set<JobInstance> jobs = new HashSet();

        fixture.setJobs(jobs);

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
        JobQueue fixture = new JobQueue(1);
        fixture.setJobs(new HashSet());
        int projectId = 1;

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