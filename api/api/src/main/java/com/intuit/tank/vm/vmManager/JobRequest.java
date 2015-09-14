/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.vmManager;

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
import java.util.Set;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;

/**
 * JobRequest
 * 
 * @author dangleton
 * 
 */
public interface JobRequest extends Serializable {

    /**
     * 
     * @return the stop behavior
     */
    public String getStopBehavior();

    /**
     * @return the id
     */
    public abstract String getId();

    /**
     * @return the incrementStrategy
     */
    public abstract IncrementStrategy getIncrementStrategy();

    /**
     * @return the location
     */
    public abstract String getLocation();

    /**
     * @return the terminationPolicy
     */
    public abstract TerminationPolicy getTerminationPolicy();

    /**
     * @return the rampTime in milis
     */
    public abstract long getRampTime();

    /**
     * @return the baselineVirtualUsers
     */
    public abstract int getBaselineVirtualUsers();

    /**
     * @return the simulationTime in milis
     */
    public abstract long getSimulationTime();

    /**
     * 
     * @return
     */
    public abstract boolean isUseEips();

    /**
     * @return the userIntervalIncrement
     */
    public abstract int getUserIntervalIncrement();

    /**
     * @return the reportingMode
     */
    public abstract String getReportingMode();

    /**
     * 
     * @return the logging profile
     */
    public abstract String getLoggingProfile();

    /**
     * @return the totalVirtualUsers
     */
    public abstract int getTotalVirtualUsers();

    /**
     * @return the status
     */
    public abstract JobQueueStatus getStatus();

    /**
     * @return the regions
     */
    public abstract Set<? extends RegionRequest> getRegions();

    /**
     * @return the notifications
     */
    public abstract Set<? extends Notification> getNotifications();

    /**
     * @return the dataFileIds
     */
    public abstract Set<Integer> getDataFileIds();

    /**
     * 
     * @return the script xml url
     */
    public String getScriptsXmlUrl();

    /**
     * 
     * @return
     */
    public String getVmInstanceType();

    /**
     * @return the numUsersPerAgent
     */
    public int getNumUsersPerAgent();
}