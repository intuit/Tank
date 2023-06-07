/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;

@Named
@ApplicationScoped
public class JobEventListener implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(JobEventListener.class);

    @Inject
    private Instance<JobEventSender> controllerSource;

    public void observerJobKillRequest(@Observes JobEvent request) {
        LOG.info("Got Job Event: " + request);
        if (request.getEvent() == JobLifecycleEvent.JOB_ABORTED) {
            controllerSource.get().killJob(request.getJobId(), false);
        }
    }
}
