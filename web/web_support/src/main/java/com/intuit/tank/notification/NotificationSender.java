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
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.mail.MailService;
import com.intuit.tank.vm.event.JobEvent;

/**
 * NotificationSender
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
public class NotificationSender implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(NotificationSender.class);

    @Inject
    private Instance<MailService> mailServiceFactory;

    @Inject
    private VMTracker vmTracker;

    public void observerJobEvents(@Observes final JobEvent jobEvent) {
        LOG.info("Got Job Event: " + jobEvent);
        Thread t = new Thread(new NotificationRunner(jobEvent, mailServiceFactory.get(), vmTracker));
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }
}
