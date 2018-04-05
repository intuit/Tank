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
     * @param node
     */
    public void run(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            ActJobNodeBean jobNode = (ActJobNodeBean) node;

            if (jobNode.getStatus().equals(JobQueueStatus.Created.toString())) {
                controller.startJob(jobNode.getId());
            } else {
                if (jobNode.getStatus().equals(JobStatus.Paused.toString())) {
                    controller.restartJob(jobNode.getId());
                } else if ( jobNode.getStatus().equals(JobStatus.RampPaused.toString())) {
                    controller.resumeRampJob(jobNode.getJobId());
                } else {
                    controller.startJob(jobNode.getId());
                }
            }
        } else if (node instanceof VMNodeBean) {
             controller.restartAgent(node.getId());
        }
    }

    /**
     * Pauses the job for the given jobId
     */
    public void pause(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            controller.pauseJob(node.getId());
        } else if (node instanceof VMNodeBean) {
            controller.pauseAgent(node.getId());
        }
    }

    /**
     * Pauses the job for the given jobId
     */
    public void pauseRamp(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            controller.pauseRampJob(node.getId());
        } else if (node instanceof VMNodeBean) {
            controller.pauseRampInstance(node.getId());
        }
    }

    /**
     * Kills the job for the given jobId
     * 
     * @param node
     */
    public void kill(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            controller.killJob(node.getId());
        } else if (node instanceof VMNodeBean) {
            controller.killInstance(node.getId());
        }
    }

    /**
     * Stops the job with the given jobId
     */
    public void stop(JobNodeBean node) {
        if (node instanceof ActJobNodeBean) {
            controller.stopJob(node.getId());
        } else if (node instanceof VMNodeBean) {
            controller.stopAgent(node.getId());
        }
    }

}
