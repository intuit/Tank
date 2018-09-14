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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.notification.NotificationSender;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;

/**
 * The class <code>NotificationSenderTest</code> contains tests for the class <code>{@link NotificationSender}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class NotificationSenderTest {
    /**
     * Run the NotificationSender() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testNotificationSender_1()
        throws Exception {
        NotificationSender result = new NotificationSender();
        assertNotNull(result);
    }


}