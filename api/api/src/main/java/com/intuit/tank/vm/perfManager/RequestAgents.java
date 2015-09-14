package com.intuit.tank.vm.perfManager;

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
import java.util.HashMap;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;

public class RequestAgents implements Serializable {

    private static final long serialVersionUID = -2728316130706928036L;
    private HashMap<String, Object> items = null;

    /**
     * 
     * @param targetEnvironment
     * @param numberOfUsers
     * @param stopBehavior
     */
    public RequestAgents(String jobId, String reportingMode, String loggingProfile, VMRegion region, int numberOfUsers,
            String stopBehavior) {
        this.items = new HashMap<String, Object>();
        this.setJobId(jobId);
        this.setLoggingProfile(loggingProfile);
        this.setNumberOfUsers(numberOfUsers);
        this.setRegion(region);
        this.setReportingMode(reportingMode);
        this.setStopBehavior(stopBehavior);
    }

    public void setLoggingProfile(String data) {
        this.items.put(TankConstants.KEY_LOGGING_PROFILE, data);
    }

    public String getLoggingProfile() {
        return (String) this.items.get(TankConstants.KEY_LOGGING_PROFILE);
    }

    public void setStopBehavior(String data) {
        this.items.put(TankConstants.KEY_STOP_BEHAVIOR, data);
    }

    public String getStopBehavior() {
        return (String) this.items.get(TankConstants.KEY_STOP_BEHAVIOR);
    }

    public void setNumberOfUsers(int data) {
        this.items.put(TankConstants.KEY_NUMBER_OF_USERS, String.valueOf(data));
    }

    public int getNumberOfUsers() {
        return Integer.valueOf((String) this.items.get(TankConstants.KEY_NUMBER_OF_USERS));
    }

    public void setJobId(String jobId) {
        this.items.put(TankConstants.KEY_JOB_ID, jobId);
    }

    public String getJobId() {
        return (String) this.items.get(TankConstants.KEY_JOB_ID);
    }

    public void setRegion(VMRegion region) {
        this.items.put(TankConstants.KEY_REGION, region);
    }

    public VMRegion getRegion() {
        return (VMRegion) this.items.get(TankConstants.KEY_REGION);
    }

    public void setReportingMode(String reportingMode) {
        this.items.put(TankConstants.KEY_REPORTING_MODE, reportingMode);
    }

    public String getReportingMode() {
        return (String) this.items.get(TankConstants.KEY_REPORTING_MODE);
    }

    public void setVmInstanceType(String instanceType) {
        this.items.put(TankConstants.KEY_VM_INSTANCE_TYPE, instanceType);
    }

    public String getVmInstanceType() {
        return (String) this.items.get(TankConstants.KEY_VM_INSTANCE_TYPE);
    }

    public void setNumUsersPerAgent(int num) {
        this.items.put(TankConstants.KEY_NUM_USERS_PER_AGENT, num);
    }

    public int getNumUsersPerAgent() {
        if (this.items.get(TankConstants.KEY_NUM_USERS_PER_AGENT) != null) {
            return (Integer) this.items.get(TankConstants.KEY_NUM_USERS_PER_AGENT);
        }
        return 0;
    }

    public void setUserEips(boolean useEips) {
        this.items.put(TankConstants.KEY_USE_EIPS, useEips);
    }

    public boolean isUseEips() {
        if (this.items.get(TankConstants.KEY_USE_EIPS) != null) {
            return (Boolean) this.items.get(TankConstants.KEY_USE_EIPS);
        }
        return new TankConfig().getVmManagerConfig().isUseElasticIps();
    }
}
