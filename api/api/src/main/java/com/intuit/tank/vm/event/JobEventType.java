/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.event;

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
 * JobEventType
 * 
 * @author dangleton
 * 
 */
public enum JobEventType {

    QUEUE_ADD("Job Added To Queue"),
    JOB_STARTED("Job Started"),
    JOB_STOPPED("Job Stopped"),
    JOB_PAUSED("Job Paused"),
    JOB_KILLED("Job Killed"),
    JOB_FINISHED("Job Finished"),
    LOAD_STARTING("Load Starting"),

    AGENT_STARTED("Agents Started"),
    AGENT_REPORTED("Agents Ready"),
    AGENT_REBOOTED("Agents Rebooted"),
    AGENT_RESTARTED("Agents Restarted"),

    RAMP_PAUSED("Ramp Paused")
    // RAMP_COMPLETE("Ramp Completed")
    ;

    private String display;

    /**
     * @param display
     */
    private JobEventType(String display) {
        this.display = display;
    }

    /**
     * @return the displayName
     */
    public String getDisplay() {
        return display;
    }

}
