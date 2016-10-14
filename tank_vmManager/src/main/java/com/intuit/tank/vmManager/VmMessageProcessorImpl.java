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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import com.intuit.tank.vm.vmManager.VMUpdateStateRequest;
import com.intuit.tank.vm.vmManager.VmMessageProcessor;
import com.intuit.tank.vmManager.environment.CreateInstance;
import com.intuit.tank.vmManager.environment.JobRequest;
import com.intuit.tank.vmManager.environment.KillInstance;

/**
 * 
 * VmMessageProcessorImpl
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
public class VmMessageProcessorImpl implements VmMessageProcessor {

    static Logger logger = LogManager.getLogger(VmMessageProcessorImpl.class);

    @Inject
    private VMQueue vmQueue;

    @Inject
    private VMTracker vmTracker;

    /**
     * @param messageObject
     */
    @Override
    public void handleVMRequest(VMRequest messageObject) {
        if (messageObject instanceof VMInstanceRequest) {
            logger.debug("vmManager received VMInstanceRequest");
            CreateInstance instance = new CreateInstance((VMInstanceRequest) messageObject, vmTracker);
            instance.run();
        } else if (messageObject instanceof VMJobRequest) {
            JobRequest instance = new JobRequest((VMJobRequest) messageObject, vmTracker);
            instance.run();
        } else if (messageObject instanceof VMKillRequest) {
            logger.debug("vmManager received VMKillRequest");
            KillInstance instance = new KillInstance((VMKillRequest) messageObject);
            vmQueue.execute(instance);
        } else if (messageObject instanceof VMUpdateStateRequest) {
            logger.debug("vmManager received VMUpdateStateRequest");
            // UpdateInstances instance = new UpdateInstances((VMUpdateStateRequest) messageObject);
            // vmQueue.execute(instance);
        } else {
            logger.error("JMS Object: Message Type Not Expected");
        }
    }

}
