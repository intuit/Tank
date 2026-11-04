/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.jobs;

import java.util.Arrays;
import java.util.Optional;

/**
 * Actions on a job or one of its agents, named as they appear in the URL.
 */
public enum JobAction {
    /** Launch the agents of a created job; a one-step job also starts its load. */
    START("start", false),
    /** Start the load of a two-step job whose agents are ready. */
    START_LOAD("start-load", false),
    PAUSE("pause", true),
    RESUME("resume", true),
    PAUSE_RAMP("pause-ramp", true),
    RESUME_RAMP("resume-ramp", true),
    STOP("stop", true),
    KILL("kill", true);

    private final String path;
    private final boolean forAgents;

    JobAction(String path, boolean forAgents) {
        this.path = path;
        this.forAgents = forAgents;
    }

    public String getPath() {
        return path;
    }

    /** Whether the action applies to a single agent as well as a whole job. */
    public boolean isForAgents() {
        return forAgents;
    }

    public static Optional<JobAction> fromPath(String path) {
        return Arrays.stream(values()).filter(a -> a.path.equals(path)).findFirst();
    }
}
