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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.vm.vmManager.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vmManager.environment.CreateInstance;
import com.intuit.tank.vmManager.environment.JobRequest;
import com.intuit.tank.vmManager.environment.KillInstance;

import java.util.Objects;

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
    private VMTracker vmTracker;

    /**
     * @param messageObject
     */
    @Override
    public void handleVMRequest(VMRequest messageObject) {
        if (messageObject instanceof VMInstanceRequest) {
            logger.debug("vmManager received VMInstanceRequest");
            CreateInstance instance = new CreateInstance((VMInstanceRequest) messageObject, vmTracker);
            Objects.requireNonNull(AWSXRay.getGlobalRecorder().getTraceEntity()).run(instance);
        } else if (messageObject instanceof VMJobRequest) {
            JobRequest instance = new JobRequest((VMJobRequest) messageObject, vmTracker);
            Objects.requireNonNull(AWSXRay.getGlobalRecorder().getTraceEntity()).run(instance);
        } else if (messageObject instanceof VMKillRequest) {
            logger.debug("vmManager received VMKillRequest");
            KillInstance instance = new KillInstance((VMKillRequest) messageObject);
            Objects.requireNonNull(AWSXRay.getGlobalRecorder().getTraceEntity()).run(instance);
        } else if (messageObject instanceof VMUpdateStateRequest) {
            logger.debug("vmManager received VMUpdateStateRequest");
            // UpdateInstances instance = new UpdateInstances((VMUpdateStateRequest) messageObject);
            // vmQueue.execute(instance);
        } else {
            logger.error("JMS Object: Message Type Not Expected");
        }
    }

}
