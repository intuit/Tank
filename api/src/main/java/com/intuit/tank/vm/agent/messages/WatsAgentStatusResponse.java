/**
 * Copyright 2013 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.agent.messages;

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

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

//import com.intuit.tank.api.model.v1.cloud.ValidationStatus;

public class WatsAgentStatusResponse implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 230150628670367273L;

    // Unable to use ValidationStatus class because the dependency
    // in maven is in the other direction -> this class cannot find the right
    // package.
    private int kills;
    private int aborts;
    private int gotos;
    private int skips;
    private int skipGroups;
    private int restarts;
    private int numVirtualUsers;
    private int maxVirtualUsers;
    private int rampTimeLeft;
    private long runTime;

    public WatsAgentStatusResponse(long time, int kills, int aborts, int gotos, int skips, int skipGroups,
            int restarts,
            int numVirtualUsers, int max, int ramp) {
        this.runTime = time;
        this.numVirtualUsers = numVirtualUsers;
        this.kills = kills;
        this.aborts = aborts;
        this.gotos = gotos;
        this.skips = skips;
        this.skipGroups = skipGroups;
        this.restarts = restarts;
        this.maxVirtualUsers = max;
        this.rampTimeLeft = ramp;
    }

    /**
     * @return kills
     */
    public int getKills() {
        return kills;
    }

    /**
     * @return aborts
     */
    public int getAborts() {
        return aborts;
    }

    /**
     * @return gotos
     */
    public int getGotos() {
        return gotos;
    }

    /**
     * @return Skips
     */
    public int getSkips() {
        return skips;
    }

    /**
     * @return SkipGroups
     */
    public int getSkipGroups() {
        return skipGroups;
    }

    /**
     * @return Restarts
     */
    public int getRestarts() {
        return restarts;
    }

    public int getRampTimeLeft() {
        return rampTimeLeft;
    }

    public long getRunTime() {
        return runTime;
    }

    public int getCurrentNumberUsers() {
        return numVirtualUsers;
    }

    public int getMaxVirtualUsers() {
        return maxVirtualUsers;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
