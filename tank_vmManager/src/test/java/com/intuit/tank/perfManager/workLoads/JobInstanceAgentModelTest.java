package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intuit.tank.vm.vmManager.JobRequest;

/**
 * The class <code>JobInstanceAgentModelTest</code> contains tests for the class
 * <code>{@link JobInstanceAgentModel}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class JobInstanceAgentModelTest {
    /**
     * Run the JobInstanceAgentModel(JobRequest) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testJobInstanceAgentModel_1()
            throws Exception {
        JobRequest job = null;

        JobInstanceAgentModel result = new JobInstanceAgentModel(job);

        assertNotNull(result);
        assertNull(result.getJob());
        assertEquals(0, result.getStartTime());
    }

    /**
     * Run the JobRequest getJob() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testGetJob_1()
            throws Exception {
        JobInstanceAgentModel fixture = new JobInstanceAgentModel((JobRequest) null);

        JobRequest result = fixture.getJob();

        assertNull(result);
    }

    /**
     * Run the int getStartTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testGetStartTime_1()
            throws Exception {
        JobInstanceAgentModel fixture = new JobInstanceAgentModel((JobRequest) null);

        int result = fixture.getStartTime();

        assertEquals(0, result);
    }

    /**
     * The concurrency, ramp time and script URL are read from the job.
     */
    @Test
    public void testDelegatesToJob() {
        JobRequest job = mock(JobRequest.class);
        when(job.getTotalVirtualUsers()).thenReturn(250);
        when(job.getRampTime()).thenReturn(90L);
        when(job.getScriptsXmlUrl()).thenReturn("http://controller/scripts.xml");

        JobInstanceAgentModel fixture = new JobInstanceAgentModel(job);

        assertSame(job, fixture.getJob());
        assertEquals(250, fixture.getNumberConcurrent());
        assertEquals(90L, fixture.getRampTime());
        assertEquals("http://controller/scripts.xml", fixture.getScriptsXmlUrl());
    }
}