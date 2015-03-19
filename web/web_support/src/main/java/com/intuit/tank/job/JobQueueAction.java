package com.intuit.tank.job;

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

import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.service.impl.v1.cloud.JobController;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;

@Named
public class JobQueueAction {

    @Inject
    private JobController controller;

    /**
     * Runs the job for the given jobId
     * 
     * @param jobId
     */
    public void run(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            ActJobNodeBean jobNode = (ActJobNodeBean) node;

            if (jobNode.getStatus().equals(JobQueueStatus.Created.name())) {
                controller.startJob(jobNode.getId());
            } else {
                if (jobNode.getStatus().equals(JobStatus.Paused.name())
                        || jobNode.getStatus().equals(JobStatus.RampPaused.name())) {
                    controller.restartJob(jobNode.getId());
                } else {
                    controller.startJob(jobNode.getId());
                }
            }
        } else if (node instanceof VMNodeBean) {
            VMNodeBean agentNode = (VMNodeBean) node;
            if (agentNode.getStatus().equals(JobStatus.Paused.name())
                    || agentNode.getStatus().equals(JobStatus.RampPaused.name())) {
                controller.restartAgent(agentNode.getId());
            }
        }
    }

    /**
     * Pauses the job for the given jobId
     */
    public void pause(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            ActJobNodeBean jobNode = (ActJobNodeBean) node;
            controller.pauseJob(jobNode.getId());
        } else if (node instanceof VMNodeBean) {
            VMNodeBean agentNode = (VMNodeBean) node;
            controller.pauseAgent(agentNode.getId());
        }
    }

    /**
     * Pauses the job for the given jobId
     */
    public void pauseRamp(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            ActJobNodeBean jobNode = (ActJobNodeBean) node;
            controller.pauseRampJob(jobNode.getId());
        } else if (node instanceof VMNodeBean) {
            VMNodeBean agentNode = (VMNodeBean) node;
            controller.pauseRampInstance(agentNode.getId());
        }
    }

    /**
     * Kills the job for the given jobId
     * 
     * @param jobId
     */
    public void kill(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            ActJobNodeBean jobNode = (ActJobNodeBean) node;
            controller.killJob(jobNode.getId());
        } else if (node instanceof VMNodeBean) {
            VMNodeBean agentNode = (VMNodeBean) node;
            controller.killInstance(agentNode.getId());
        }
    }

    /**
     * Stops the job with the given jobId
     */
    public void stop(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            ActJobNodeBean jobNode = (ActJobNodeBean) node;
            controller.stopJob(jobNode.getId());
        } else if (node instanceof VMNodeBean) {
            VMNodeBean agentNode = (VMNodeBean) node;
            controller.stopAgent(agentNode.getId());
        }
    }

}
