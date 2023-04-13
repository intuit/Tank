/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;

@Service
@ApplicationScope
public class JobEventListener implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(JobEventListener.class);

    @Autowired
    private ObjectProvider<JobEventSender> controllerSource;

    @EventListener
    public void observerJobKillRequest(JobEvent request) {
        LOG.info("Got Job Event: " + request);
        if (request.getEvent() == JobLifecycleEvent.JOB_ABORTED) {
            controllerSource.getIfAvailable().killJob(request.getJobId(), false);
        }
    }
}
