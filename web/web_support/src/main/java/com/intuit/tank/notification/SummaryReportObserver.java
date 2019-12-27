/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.notification;

/*
 * #%L
 * JSF Support Beans
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
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.service.impl.v1.report.SummaryReportRunner;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * SummaryReportObserver
 * 
 * @author dangleton
 * 
 */
@Named
@ApplicationScoped
public class SummaryReportObserver implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Event<JobEvent> jobEventProducer;

    @Inject
    private TankConfig tankConfig;

    public void observerJobEvents(@Observes final JobEvent jobEvent) {
        if (jobEvent.getEvent() == JobLifecycleEvent.JOB_FINISHED) {
            Thread t = new Thread(new SummaryReportRunner(tankConfig.getControllerBase(), jobEventProducer,
                    jobEvent));
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
        }
    }
}
