/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.Date;
import java.util.List;

/**
 * Recent and running jobs grouped by project, with live agent status, for the job queue pages.
 *
 * <p>Live figures come from the agents' reports to the controller that serves the request; with several
 * controllers behind a load balancer, each one only knows the agents that report to it.</p>
 *
 * @param projects  projects with at least one job to show, newest jobs first
 * @param otherJobs jobs that agents report but no project queue lists
 */
public record JobTree(List<ProjectJobs> projects, List<JobNode> otherJobs, Date generatedAt) {

    public record ProjectJobs(int projectId, String name, int activeUsers, int totalUsers, int tps,
                              Failures failures, List<JobNode> jobs) {
    }

    /**
     * @param status JobQueueStatus name
     */
    public record JobNode(String jobId, String name, String status, String incrementStrategy, int activeUsers,
                          int totalUsers, int tps, Failures failures, Date startTime, Date endTime, boolean useTwoStep,
                          Actions actions, List<AgentNode> agents) {
    }

    /**
     * @param status      VMStatus name
     * @param jobStatus   the agent's JobStatus name (paused agents keep running VMs)
     * @param wsState     the agent's WebSocket connection state, if it uses one
     * @param lastSeenMs  when the agent was last heard from over its WebSocket (epoch milliseconds)
     */
    public record AgentNode(String instanceId, String region, String status, String jobStatus, int activeUsers, int totalUsers, int tps,
                            Failures failures, Date startTime, Date endTime, String wsState, String transferProgress,
                            Long lastSeenMs, Actions actions) {
    }

    /**
     * Which actions are available now. {@code control} is whether the caller may control the job at all;
     * the others are false when it may not.
     */
    public record Actions(boolean control, boolean start, boolean startLoad, boolean pause, boolean resume,
                          boolean pauseRamp, boolean resumeRamp, boolean stop, boolean kill, boolean delete) {
    }
}
