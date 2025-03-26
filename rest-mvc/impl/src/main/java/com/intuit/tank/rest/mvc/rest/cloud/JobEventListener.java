/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.cloud;

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.google.common.collect.ImmutableMap;
import com.intuit.tank.logging.ControllerLoggingConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.ThreadContext;

@Named
@ApplicationScoped
public class JobEventListener implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(JobEventListener.class);

    @Inject
    private Instance<JobEventSender> controllerSource;

    public void observerJobKillRequest(@Observes JobEvent request) {
        ControllerLoggingConfig.setupThreadContext();
        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Got Job Event: " + request)));
        if (request.getEvent() == JobLifecycleEvent.JOB_ABORTED) {
            controllerSource.get().killJob(request.getJobId(), false);
        } else if (request.getEvent() == JobLifecycleEvent.JOB_FINISHED ||
                request.getEvent() == JobLifecycleEvent.JOB_KILLED) {
            ThreadContext.clearAll();
        }
    }
}
