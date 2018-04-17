/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vmManager;

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

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.vm.vmManager.VMChannel;
import com.intuit.tank.vm.vmManager.VMTerminator;

/**
 * VMTerminator
 * 
 * @author dangleton
 * 
 */
public class VMTerminatorImpl implements VMTerminator {

    private static final Logger LOG = LogManager.getLogger(VMTerminatorImpl.class);
    private static final long WAIT_TIME = 30000;// 30 seconds

    @Inject
    private VMQueue queue;

    @Inject
    private VMChannel channel;

    @Inject
    private VMTracker vmTracker;

    /**
     * @inheritDoc
     */
    @Override
    public void terminate(@Nonnull String instanceId) {
        if (!vmTracker.isDevMode()) {
            final List<String> finalList = new ArrayList<String>();
            finalList.add(instanceId);
            queue.execute( () -> {
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    LOG.error("Thread interrupted. trying to send mesage to kill vm.");
                }
                channel.terminateInstances(finalList);
            });
        }
    }

}
