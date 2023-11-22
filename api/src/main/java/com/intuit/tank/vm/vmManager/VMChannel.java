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

import java.util.List;

import jakarta.annotation.Nonnull;

import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * VMChannel
 * 
 * @author dangleton
 * 
 */
public interface VMChannel {

    public abstract void terminateInstances(@Nonnull List<String> instanceIds);

    /**
     * 
     * @param instanceIds
     * @return
     */
    public void stopInstances(@Nonnull List<String> instanceIds);

    /**
     * 
     * @param request
     * @return
     */
    public List<VMInformation> startInstances(@Nonnull VMInstanceRequest request);

    /**
     * Returns a list of instances of the specified type in the specified region.
     * 
     * @param region
     *            the region to search
     * @param type
     *            the type of instance
     * @return the list of instances
     */
    @Nonnull
    public List<VMInformation> findInstancesOfType(@Nonnull VMRegion region, @Nonnull VMImageType type);
}