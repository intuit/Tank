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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.VMSize;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.InstanceDescription;
import com.intuit.tank.vm.settings.TankConfig;

public class VMInstanceRequest extends VMRequest implements Serializable {

    private static final long serialVersionUID = 1556616649246707829L;

    /**
     * Constructor
     */
    public VMInstanceRequest() {
        super();
    }

    /**
     * Constructor
     * 
     * @param provider
     *            The virtual machine environment
     * @param region
     *            The region for the virtual machine
     * @param size
     *            The size of the virtual machine
     * @param image
     *            The image to instantiate
     * @param reuseStoppedInstances
     *            set to true to start a stopped instance instead of creating a new instance
     * @param elasticIps
     *            Comma-delimited elastic ips to associate with the newly created instances (will associate as many as
     *            possible)
     */
    public VMInstanceRequest(VMProvider provider, VMRegion region, String instanceType, VMImageType image,
            int numberOfInstances, boolean reuseStoppedInstances, String zone, InstanceDescription description) {
        super(provider);
        this.setRegion(region);
        this.setSize(instanceType);
        this.setImage(image);
        this.setNumberOfInstances(numberOfInstances);
        this.setReportingMode(TankConstants.RESULTS_NONE);
        this.setReuseStoppedInstance(reuseStoppedInstances);
        this.setInstanceDescription(description);
        this.setZone(zone);
        // this.setJobId("unknown");
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
     * @param ami
     */
    public void setInstanceDescription(InstanceDescription description) {
        this.items.put(TankConstants.KEY_DESCRIPTION, description);
    }

    public InstanceDescription getInstanceDescription() {
        return (InstanceDescription) this.items.get(TankConstants.KEY_DESCRIPTION);
    }

    /**
     * @param ami
     */
    public void setSubnetId(String subnetId) {
        this.items.put(TankConstants.KEY_SUBNET_ID, subnetId);
    }

    public String getSubnetId() {
        return (String) this.items.get(TankConstants.KEY_SUBNET_ID);
    }

    /**
     * @param reuseStoppedInstances
     */
    public void setReuseStoppedInstance(boolean reuseStoppedInstances) {
        this.items.put(TankConstants.KEY_REUSE_STOPPED_INSTANCE, reuseStoppedInstances);
    }

    /**
     * 
     * @return
     */
    public boolean getReuseStoppedInstance() {
        Boolean result = (Boolean) this.items.get(TankConstants.KEY_REUSE_STOPPED_INSTANCE);
        return result != null ? result : false;
    }

    /**
     * @param reuseStoppedInstances
     */
    public void setZone(String zone) {
        this.items.put(TankConstants.KEY_ZONE, zone);
    }

    /**
     * 
     * @return
     */
    public String getZone() {
        return (String) this.items.get(TankConstants.KEY_ZONE);
    }

    /**
     * 
     * @return
     */
    public Set<String> getAssociatedIps() {
        @SuppressWarnings("unchecked") Set<String> ips = (Set<String>) this.items.get(TankConstants.KEY_IPS);
        if (ips == null) {
            ips = new HashSet<String>();
            this.items.put(TankConstants.KEY_IPS, ips);
        }
        return ips;
    }

    /**
     * 
     * @param ipAddress
     */
    public void addAssociatedIp(String ipAddress) {
        getAssociatedIps().add(ipAddress);
    }

    /**
     * @param hashMap
     */
    public Map<String, String> getUserData() {
        @SuppressWarnings("unchecked") HashMap<String, String> userDataMap = (HashMap<String, String>) this.items
                .get("userData");
        if (userDataMap == null) {
            userDataMap = new HashMap<String, String>();
            this.items.put("userData", userDataMap);
        }
        return userDataMap;
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void addUserData(@Nonnull String key, @Nonnull String value) {
        getUserData().put(key, value);
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
     * Set the job id
     * 
     * @param data
     *            The job id
     */
    public void setLoggingProfile(String data) {
        this.items.put(TankConstants.KEY_LOGGING_PROFILE, data);
    }

    /**
     * Get the job id
     * 
     * @return The job id
     */
    public String getLoggingProfile() {
        return (String) this.items.get(TankConstants.KEY_LOGGING_PROFILE);
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
     * Set the virtual machine size
     * 
     * @param data
     *            The virtual machine size
     */
    public void setSize(String data) {
        this.items.put(TankConstants.KEY_SIZE, data);
    }

    /**
     * Get the virtual machine size
     * 
     * @return The virtual machine size
     */
    public String getSize() {
        return (String) this.items.get(TankConstants.KEY_SIZE);
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
     * @param data
     */
    public void setNumUsersPerAgent(int data) {
        this.items.put(TankConstants.KEY_NUM_USERS_PER_AGENT, data);
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
    public void setNumberOfInstances(int data) {
        this.items.put(TankConstants.KEY_NUMBER_OF_INSTANCES, Integer.toString(data));
    }

    /**
     * Get the number of instances requested
     * 
     * @return The number of instances requested
     */
    public int getNumberOfInstances() {
        if (!this.items.containsKey(TankConstants.KEY_NUMBER_OF_INSTANCES)) {
            return -1;
        }
        return Integer.valueOf((String) this.items.get(TankConstants.KEY_NUMBER_OF_INSTANCES));
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
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
