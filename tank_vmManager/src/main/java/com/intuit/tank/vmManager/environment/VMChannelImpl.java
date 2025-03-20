/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import jakarta.annotation.Nonnull;

import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMChannel;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CloudChannelImpl
 * 
 * @author dangleton
 * 
 */
public class VMChannelImpl implements VMChannel {

    private static final Logger LOG = LogManager.getLogger(VMChannelImpl.class);

    public VMChannelImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> findInstancesOfType(VMRegion region, VMImageType type) {
        LOG.debug("Finding instances of type: " + type + " in region: " + region);
        AmazonInstance amazonInstance = new AmazonInstance(region);
        List<VMInformation> instances = amazonInstance.findInstancesOfType(region, type);
        LOG.debug("Found " + instances.size() + " instances of type: " + type + " in region: " + region);
        return instances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateInstances(@Nonnull List<String> instanceIds) {
        LOG.info("Terminating instances: " + instanceIds);
        for (VMRegion region : new TankConfig().getVmManagerConfig().getRegions()) {
            LOG.debug("Terminating instances in region: " + region);
            new AmazonInstance(region).killInstances(instanceIds);
        }
        LOG.info("Finished terminating instances: " + instanceIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopInstances(@Nonnull List<String> instanceIds) {
        LOG.info("Stopping instances: " + instanceIds);
        for (VMRegion region : new TankConfig().getVmManagerConfig().getRegions()) {
            LOG.debug("Stopping instances in region: " + region);
            new AmazonInstance(region).stopInstances(instanceIds);
        }
        LOG.info("Finished stopping instances: " + instanceIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> startInstances(@Nonnull VMInstanceRequest request) {
        LOG.info("Starting instances with request: " + request);
        List<VMInformation> instances = new AmazonInstance(request.getRegion()).create(request);
        LOG.debug("Started " + instances.size() + " instances with request: " + request);
        return instances;
    }

}
