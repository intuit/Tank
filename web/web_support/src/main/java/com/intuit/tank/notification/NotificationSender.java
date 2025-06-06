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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.event.JobEvent;

/**
 * NotificationSender
 * 
 * @author dangleton
 * 
 */
@Named
@ApplicationScoped
public class NotificationSender implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(NotificationSender.class);

    public void observerJobEvents(@Observes final JobEvent jobEvent) {
        LOG.info("Got Job Event: " + jobEvent + ". Ignoring for now.");
    }
}
