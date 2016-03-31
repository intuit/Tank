package com.intuit.tank.vmManager.environment;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.amazonaws.services.ec2.model.Address;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vmManager.environment.amazon.KeyValuePair;

/**
 * IEnvironmentInstance Represents an environment to create or act upon in the cloud. TODO: This interface uses mixed
 * metaphors. for some operations (kill, create) it expects to be configured with the infor for create or kill and all
 * others acts like a dao class.
 * 
 * @author dangleton
 * 
 */
public interface IEnvironmentInstance {

    /**
     * Start the VMs that this instance is configured for.
     * 
     * @return list of VmInformation objects
     */
    public List<VMInformation> create();

    /**
     * Terminate the Vms this instance is configured for.
     * 
     * @return list of VmInformation objects
     */
    public List<VMInformation> kill();

    /**
     * Describe The instances specified.
     * 
     * @param instanceIds
     *            the instanceIds to describe
     * @return list of VmInformation objects
     */
    public List<VMInformation> describeInstances(String... instanceIds);

    /**
     * Finds instances of the specified type that are currently running.
     * 
     * @param region
     *            the region to check.
     * @param type
     *            the type of instance
     * @return list of VmInformation objects matching the criteria
     */
    public List<VMInformation> findInstancesOfType(VMRegion region, VMImageType type);

    /**
     * Associate the specified instance with the specified public IP.
     * 
     * @param instanceId
     *            the instance to associate
     * @param ip
     *            the ip to associate
     */
    public void associateAddress(String instanceId, Address address, CountDownLatch latch);

    /**
     * Reboot the specified vms.
     * 
     * @param vmInfos
     *            the vmInfos to reboot
     */
    public void reboot(List<VMInformation> vmInfos);

    /**
     * Tags the instances with the specified tags.
     * 
     * @param instanceIds
     *            the list of instances to tag
     * @param tag
     *            the tags to add (varargs)
     */
    public void tagInstance(List<String> instanceIds, KeyValuePair... tag);

    /**
     * @param instanceIds
     * @return
     */
    public List<VMInformation> kill(List<String> instanceIds);

    /**
     * 
     * @param instanceIds
     *            the instance ids to stop
     */
    public List<VMInformation> stopInstances(List<String> instanceIds);
}
