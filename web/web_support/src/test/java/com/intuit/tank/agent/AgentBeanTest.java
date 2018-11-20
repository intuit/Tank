package com.intuit.tank.agent;

/*
 * #%L
 * JSF Support Beans
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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.agent.AgentBean;
import com.intuit.tank.agent.AgentStatusReporter;
import com.intuit.tank.project.JobQueue;

/**
 * The class <code>AgentBeanTest</code> contains tests for the class <code>{@link AgentBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class AgentBeanTest {
    /**
     * Run the AgentBean() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAgentBean_1()
            throws Exception {
        AgentBean result = new AgentBean();
        assertNotNull(result);
    }

    /**
     * Run the List<AgentStatusReporter> getAgents() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAgents_1()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(null);
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob(new JobQueue());

        List<AgentStatusReporter> result = fixture.getAgents();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
        assertNotNull(result);
    }

    /**
     * Run the List<AgentStatusReporter> getAgents() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAgents_2()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob(new JobQueue());

        List<AgentStatusReporter> result = fixture.getAgents();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
        assertNotNull(result);
    }

    /**
     * Run the List<JobQueue> getJobs() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetJobs_1()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(null);
        fixture.setSelectedJob(new JobQueue());

        List<JobQueue> result = fixture.getJobs();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
        assertNotNull(result);
    }

    /**
     * Run the List<JobQueue> getJobs() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetJobs_2()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob(new JobQueue());

        List<JobQueue> result = fixture.getJobs();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
        assertNotNull(result);
    }

    /**
     * Run the JobQueue getSelectedJob() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectedJob_1()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob((JobQueue) null);

        JobQueue result = fixture.getSelectedJob();
    }

    /**
     * Run the JobQueue getSelectedJob() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectedJob_2()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob((JobQueue) null);

        JobQueue result = fixture.getSelectedJob();
    }

    /**
     * Run the JobQueue getSelectedJob() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectedJob_3()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob(new JobQueue());

        JobQueue result = fixture.getSelectedJob();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
        assertNotNull(result);
    }

    /**
     * Run the void setAgents(List<AgentStatusReporter>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetAgents_1()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob(new JobQueue());
        List<AgentStatusReporter> agents = new LinkedList();

        fixture.setAgents(agents);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
    }

    /**
     * Run the void setJobs(List<JobQueue>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetJobs_1()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob(new JobQueue());
        List<JobQueue> jobs = new LinkedList();

        fixture.setJobs(jobs);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
    }

    /**
     * Run the void setSelectedJob(JobQueue) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelectedJob_1()
            throws Exception {
        AgentBean fixture = new AgentBean();
        fixture.setAgents(new LinkedList());
        fixture.setJobs(new LinkedList());
        fixture.setSelectedJob(new JobQueue());
        JobQueue selectedJob = new JobQueue();

        fixture.setSelectedJob(selectedJob);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.agent.AgentBean.setAgents(AgentBean.java:66)
    }
}