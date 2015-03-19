/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.api.enumerated;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

/**
 * JobLifecycleEvent
 * 
 * @author dangleton excessive
 * 
 */
public enum JobLifecycleEvent {

    QUEUE_ADD("Job Added To Queue"),
    JOB_STARTED("Job Started"),
    JOB_STOPPED("Job Stopped"),
    JOB_PAUSED("Job Paused"),
    JOB_RESUMED("Job Resumed"),
    JOB_KILLED("Job Killed"),
    JOB_ABORTED("Job Aborted"),
    JOB_FINISHED("Job Finished"),
    SUMMARY_REPORT_FINISHED("Summary Ready"),

    LOAD_STARTED("Load Started"),
    AGENT_STARTED("Agents Started"),
    AGENT_EXCESSIVE_CPU("Agent Excessive CPU"),
    AGENT_REPORTED("Agents Ready"),
    AGENT_REBOOTED("Agents Rebooted"),
    AGENT_RESTARTED("Agents Restarted"),

    RAMP_PAUSED("Ramp Paused"),
    RAMP_RESUMED("Ramp Resumed")
    // RAMP_COMPLETE("Ramp Completed")
    ;

    private String display;

    /**
     * @param display
     */
    private JobLifecycleEvent(String display) {
        this.display = display;
    }

    /**
     * @return the displayName
     */
    public String getDisplay() {
        return display;
    }

    @Override
    public String toString() {
        return display;
    }

}
