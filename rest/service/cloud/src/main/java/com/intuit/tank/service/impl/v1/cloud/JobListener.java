/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.cloud;

/*
 * #%L
 * Cloud Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;

/**
 * JobListener
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
public class JobListener implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(JobListener.class);

    @Inject
    Instance<JobController> controllerSource;

    public void observerJobKillRequest(@Observes JobEvent request) {
        LOG.info("Got Job Event: " + request);
        if (request.getEvent() == JobLifecycleEvent.JOB_ABORTED) {
            controllerSource.get().killJob(request.getJobId(), false);
        }
    }
}
