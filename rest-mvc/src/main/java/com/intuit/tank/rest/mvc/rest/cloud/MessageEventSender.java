/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.cloud;

import com.intuit.tank.vm.settings.ModifiedEntityMessage;

import jakarta.annotation.Nonnull;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

public class MessageEventSender {

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
