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

import java.util.HashMap;

import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;

public class VMJobRequest extends VMRequest {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6022543863819975533L;

    public VMJobRequest(String jobId, String reportingMode, String loggingProfile, int numberOfUsers,
            VMRegion region, String stopBehavior, String vmInstanceType, int numUsersPerAgent) {
        this.items = new HashMap<String, Object>();
        this.setJobId(jobId);
        this.setReportingMode(reportingMode);
        this.setNumberOfUsers(numberOfUsers);
        this.setLoggingProfile(loggingProfile);
        this.setRegion(region);
        this.setStopBehavior(stopBehavior);
        this.setVmInstanceType(vmInstanceType);
        this.setNumUsersPerAgent(numUsersPerAgent);
    }

    /**
     * Set the job id
     * 
     * @param data
     *            The job id
     */
    public void setJobId(String data) {
        this.items.put(TankConstants.KEY_JOB_ID, data);
    }

    /**
     * Get the job id
     * 
     * @return The job id
     */
    public String getJobId() {
        return (String) this.items.get(TankConstants.KEY_JOB_ID);
    }

    /**
     * 
     * @param useEips
     */
    public void setUserEips(boolean useEips) {
        this.items.put(TankConstants.KEY_USE_EIPS, useEips);
    }

    /**
     * 
     * @return
     */
    public boolean isUseEips() {
        if (this.items.get(TankConstants.KEY_USE_EIPS) != null) {
            return (Boolean) this.items.get(TankConstants.KEY_USE_EIPS);
        }
        return new TankConfig().getVmManagerConfig().isUseElasticIps();
    }

    /**
     * Set the job id
     * 
     * @param data
     *            The job id
     */
    public void setStopBehavior(String data) {
        this.items.put(TankConstants.KEY_STOP_BEHAVIOR, data);
    }

    /**
     * Get the job id
     * 
     * @return The job id
     */
    public String getStopBehavior() {
        return (String) this.items.get(TankConstants.KEY_STOP_BEHAVIOR);
    }

    /**
     * Set the image type
     * 
     * @param data
     *            The image type
     */
    public void setImage(VMImageType data) {
        this.items.put(TankConstants.KEY_IMAGE, data);
    }

    /**
     * Get the image type
     * 
     * @return The image type
     */
    public VMImageType getImage() {
        return (VMImageType) this.items.get(TankConstants.KEY_IMAGE);
    }

    /**
     * Set the number of instances requested
     * 
     * @param data
     *            The number of instances requested
     */
    public void setNumberOfUsers(int data) {
        this.items.put(TankConstants.KEY_NUMBER_OF_USERS, Integer.toString(data));
    }

    /**
     * Get the number of instances requested
     * 
     * @return The number of instances requested
     */
    public int getNumberOfUsers() {
        if (!this.items.containsKey(TankConstants.KEY_NUMBER_OF_USERS)) {
            return -1;
        }
        return Integer.valueOf((String) this.items.get(TankConstants.KEY_NUMBER_OF_USERS));
    }

    /**
     * Set the virtual machine region
     * 
     * @param data
     *            The virtual machine region
     */
    public void setRegion(VMRegion data) {
        this.items.put(TankConstants.KEY_REGION, data);
    }

    /**
     * Get the virtual machine region
     * 
     * @return The virtual machine region
     */
    public VMRegion getRegion() {
        return (VMRegion) this.items.get(TankConstants.KEY_REGION);
    }

    /**
     * 
     * @param reportingMode
     */
    public void setReportingMode(String reportingMode) {
        this.items.put(TankConstants.KEY_REPORTING_MODE, reportingMode);
    }

    /**
     * 
     * @return
     */
    public String getReportingMode() {
        return (String) this.items.get(TankConstants.KEY_REPORTING_MODE);
    }

    /**
     * 
     * @return
     */
    public String getLoggingProfile() {
        return (String) this.items.get(TankConstants.KEY_LOGGING_PROFILE);
    }

    /**
     * 
     * @param reportingMode
     */
    public void setLoggingProfile(String data) {
        this.items.put(TankConstants.KEY_LOGGING_PROFILE, data);
    }

    /**
     * 
     * @return
     */
    public String getVmInstanceType() {
        return (String) this.items.get(TankConstants.KEY_VM_INSTANCE_TYPE);
    }

    /**
     * 
     * @param reportingMode
     */
    public void setVmInstanceType(String data) {
        this.items.put(TankConstants.KEY_VM_INSTANCE_TYPE, data);
    }

    /**
     * 
     * @return
     */
    public int getNumUsersPerAgent() {
        if (!this.items.containsKey(TankConstants.KEY_NUM_USERS_PER_AGENT)) {
            return 0;
        }
        return (Integer) this.items.get(TankConstants.KEY_NUM_USERS_PER_AGENT);
    }

    /**
     * 
     * @param reportingMode
     */
    public void setNumUsersPerAgent(int data) {
        this.items.put(TankConstants.KEY_NUM_USERS_PER_AGENT, data);
    }

}
