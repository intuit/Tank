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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.apache.commons.lang.time.DateFormatUtils;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.vmManager.VMTrackerImpl;

@Named
@RequestScoped
public class AgentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AgentStatusReporter> agents;
    private List<JobQueue> jobs;
    private JobQueue selectedJob;

    /**
     * @return the selectedJob
     */
    public JobQueue getSelectedJob() {
        if (selectedJob == null) {
            if (!getJobs().isEmpty()) {
                selectedJob = getJobs().get(0);
            }
        }
        return selectedJob;
    }

    /**
     * @param selectedJob
     *            the selectedJob to set
     */
    public void setSelectedJob(JobQueue selectedJob) {
        this.selectedJob = selectedJob;
    }

    /**
     * @return the agents
     */
    public List<AgentStatusReporter> getAgents() {
        if (agents == null) {
            agents = new ArrayList<AgentStatusReporter>();
        }
        return agents;
    }

    /**
     * @param agents
     *            the agents to set
     */
    public void setAgents(List<AgentStatusReporter> agents) {
        this.agents = agents;
    }

    /**
     * @return the jobs
     */
    public List<JobQueue> getJobs() {
        if (jobs == null) {
            jobs = new JobQueueDao().findAll();
        }
        return jobs;
    }

    /**
     * @param jobs
     *            the jobs to set
     */
    public void setJobs(List<JobQueue> jobs) {
        this.jobs = jobs;
    }

    public void getAgentStatus(String jobId) {
        VMTracker tracker = new VMTrackerImpl();
        CloudVmStatusContainer container = tracker.getVmStatusForJob(jobId);
        Set<CloudVmStatus> statuses = container.getStatuses();

        for (CloudVmStatus cloudVmStatus : statuses) {
            AgentStatusReporter asr = new AgentStatusReporter();
            asr.setActiveUsers(String.valueOf(cloudVmStatus.getCurrentUsers()));
            asr.setInstanceId(cloudVmStatus.getInstanceId());
            asr.setJobId(cloudVmStatus.getJobId());
            asr.setStartTime(DateFormatUtils.format(cloudVmStatus.getStartTime(), "mm/dd/yyyy HH:mm:ss"));
            asr.setEndTime(DateFormatUtils.format(cloudVmStatus.getEndTime(), "mm/dd/yyyy HH:mm:ss"));
            asr.setAgentStatus(cloudVmStatus.getVmStatus().toString());
            asr.setRegion(cloudVmStatus.getVmRegion().toString());
            asr.setRole(cloudVmStatus.getRole().toString());
            asr.setJobStatus(cloudVmStatus.getJobStatus().toString());
            asr.setTotalTime("");
            asr.setTotalUsers(String.valueOf(cloudVmStatus.getTotalUsers()));
            asr.setUsersChange("0");
            agents.add(asr);
        }

    }
}
