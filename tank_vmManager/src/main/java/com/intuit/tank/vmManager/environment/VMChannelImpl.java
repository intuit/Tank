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

import javax.annotation.Nonnull;

import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMChannel;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

/**
 * CloudChannelImpl
 * 
 * @author dangleton
 * 
 */
public class VMChannelImpl implements VMChannel {


    public VMChannelImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> findInstancesOfType(VMRegion region, VMImageType type) {
        AmazonInstance amazonInstance = new AmazonInstance(region);
        return amazonInstance.findInstancesOfType(region, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminateInstances(@Nonnull List<String> instanceIds) {
        for (VMRegion region : new TankConfig().getVmManagerConfig().getRegions()) {
            new AmazonInstance(region).killInstances(instanceIds);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopInstances(@Nonnull List<String> instanceIds) {
        for (VMRegion region : new TankConfig().getVmManagerConfig().getRegions()) {
            new AmazonInstance(region).stopInstances(instanceIds);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VMInformation> startInstances(@Nonnull VMInstanceRequest request) {
        return new AmazonInstance(request.getRegion()).create(request);
    }

}
