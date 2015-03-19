/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.automation;

/*
 * #%L
 * Automation Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.annotation.Nonnull;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.intuit.tank.vm.settings.ModifiedEntityMessage;

/**
 * MessageSender
 * 
 * @author dangleton
 * 
 */
public class MessageSender {

    @Inject
    private Event<ModifiedEntityMessage> eventSender;

    /**
     * Fires the specified event
     * 
     * @param msg
     */
    public void sendEvent(@Nonnull ModifiedEntityMessage msg) {
        eventSender.fire(msg);
    }
}
