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

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.Reservation;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;

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
                info.setInstanceId(instance.getInstanceId());
                info.setState(instance.getCurrentState().getName());
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
     * @param data
     * @param instance
     * @param region
     * @return
     */
    public VMInformation instanceToVmInformation(Reservation data, Instance instance, VMRegion region) {
        VMInformation info = new VMInformation();
        info.setProvider(VMProvider.Amazon);
        info.setRequestId(data.getRequesterId());
        info.setImageId(instance.getImageId());
        info.setInstanceId(instance.getInstanceId());
        info.setKeyName(instance.getKeyName());
        // info.setLaunchTime();
        info.setRegion(region);
        info.setPlatform(instance.getPlatform());
        info.setPrivateDNS(instance.getPrivateDnsName());
        info.setPublicDNS(instance.getPublicDnsName());
        info.setState(instance.getState().getName());
        info.setSize(instance.getInstanceType());
        return info;
    }

    /**
     * @param reservation
     * @param region
     * @return
     */
    public List<VMInformation> processReservation(Reservation reservation, VMRegion region) {
        try {
            return reservation.getInstances().stream().map(instance -> instanceToVmInformation(reservation, instance, region)).collect(Collectors.toList());
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
