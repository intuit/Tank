/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vmManager.environment.amazon;

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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;

/**
 * AmazonDataConverter
 * 
 * @author dangleton
 * 
 */
public class AmazonDataConverter {
    private static final Logger LOG = LogManager.getLogger(AmazonDataConverter.class);

    public List<VMInformation> processStateChange(List<InstanceStateChange> changes) {
        List<VMInformation> output = new ArrayList<VMInformation>();
        try {
            for (InstanceStateChange instance : changes) {
                VMInformation info = new VMInformation();
                info.setInstanceId(instance.instanceId());
                info.setState(instance.currentState().nameAsString());
                output.add(info);
            }

            return output;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param requesterId
     * @param instance
     * @param region
     * @return
     */
    public VMInformation instanceToVmInformation(String requesterId, Instance instance, VMRegion region) {
        VMInformation info = new VMInformation();
        info.setProvider(VMProvider.Amazon);
        info.setRequestId(requesterId);
        info.setImageId(instance.imageId());
        info.setInstanceId(instance.instanceId());
        info.setKeyName(instance.keyName());
        // info.setLaunchTime();
        info.setRegion(region);
        info.setPlatform(instance.platformAsString());
        info.setPrivateDNS(instance.privateDnsName());
        info.setPublicDNS(instance.publicDnsName());
        info.setState(instance.state().nameAsString());
        info.setSize(instance.instanceTypeAsString());
        return info;
    }

    /**
     * @param requesterId
     * @param instances
     * @param region
     * @return
     */
    public List<VMInformation> processReservation(String requesterId, List<Instance> instances, VMRegion region) {
        try {
            return instances.stream().map(instance -> instanceToVmInformation(requesterId, instance, region)).collect(Collectors.toList());
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
