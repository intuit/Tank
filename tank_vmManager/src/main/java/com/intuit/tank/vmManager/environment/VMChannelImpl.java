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
        LOG.debug("Finding instances of type: {} in region: {}", type, region);
        try {
            AmazonInstance amazonInstance = new AmazonInstance(region);
            List<VMInformation> instances = amazonInstance.findInstancesOfType(region, type);
            LOG.debug("Found {} instances of type: {} in region: {}", instances.size(), type, region);
            return instances;
        } catch (Exception e) {
            LOG.error("Error finding instances of type: {} in region: {}", type, region, e);
            return List.of();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateInstances(@Nonnull List<String> instanceIds) {
        LOG.debug("Terminating instances: {}", instanceIds);
        try {
            for (VMRegion region : new TankConfig().getVmManagerConfig().getRegions()) {
                LOG.debug("Terminating instances in region: {}", region);
                new AmazonInstance(region).killInstances(instanceIds);
            }
            LOG.info("Finished terminating instances: {}", instanceIds);
        } catch (Exception e) {
            LOG.error("Error terminating instances: {}", instanceIds, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopInstances(@Nonnull List<String> instanceIds) {
        LOG.debug("Stopping instances: {}", instanceIds);
        try {
            for (VMRegion region : new TankConfig().getVmManagerConfig().getRegions()) {
                LOG.debug("Stopping instances in region: {}", region);
                new AmazonInstance(region).stopInstances(instanceIds);
            }
            LOG.debug("Finished stopping instances: {}", instanceIds);
        } catch (Exception e) {
            LOG.error("Error stopping instances: {}", instanceIds, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> startInstances(@Nonnull VMInstanceRequest request) {
        LOG.debug("Starting instances with request: {}", request);
        try {
            List<VMInformation> instances = new AmazonInstance(request.getRegion()).create(request);
            LOG.debug("Started {} instances with request: {}", instances.size(), request);
            return instances;
        } catch (Exception e) {
            LOG.error("Error starting instances with request: {}", request, e);
            return List.of(); // Return an empty list or handle the error appropriately
        }
    }

}
