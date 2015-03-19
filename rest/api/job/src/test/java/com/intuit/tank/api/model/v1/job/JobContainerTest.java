package com.intuit.tank.api.model.v1.job;

/*
 * #%L
 * Job Rest Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import com.intuit.tank.api.model.v1.job.JobContainer;
import com.intuit.tank.api.model.v1.job.JobTO;

import static org.junit.Assert.*;

/**
 * The class <code>JobContainerTest</code> contains tests for the class <code>{@link JobContainer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:07 PM
 */
public class JobContainerTest {
    /**
     * Run the JobContainer() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testJobContainer_1()
        throws Exception {

        JobContainer result = new JobContainer();

        assertNotNull(result);
    }

    /**
     * Run the JobContainer(List<JobTO>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testJobContainer_2()
        throws Exception {
        List<JobTO> jobs = new LinkedList();

        JobContainer result = new JobContainer(jobs);

        assertNotNull(result);
    }

    /**
     * Run the List<JobTO> getJobs() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetJobs_1()
        throws Exception {
        JobContainer fixture = new JobContainer(new LinkedList());

        List<JobTO> result = fixture.getJobs();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}