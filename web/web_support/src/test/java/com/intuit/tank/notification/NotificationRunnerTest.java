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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.mail.MailService;
import com.intuit.tank.notification.NotificationRunner;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vmManager.VMTrackerImpl;

/**
 * The class <code>NotificationRunnerTest</code> contains tests for the class <code>{@link NotificationRunner}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class NotificationRunnerTest {
    /**
     * Run the NotificationRunner(JobEvent,MailService,VMTracker) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testNotificationRunner_1()
        throws Exception {
        JobEvent jobEvent = new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU);
        MailService mailService = null;
        VMTracker tracker = new VMTrackerImpl();

        NotificationRunner result = new NotificationRunner(jobEvent, mailService, tracker);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.enumerated.JobLifecycleEvent.<init>(JobLifecycleEvent.java:43)
        //       at com.intuit.tank.api.enumerated.JobLifecycleEvent.<clinit>(JobLifecycleEvent.java:14)
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertNotNull(result);
    }

    /**
     * Run the void run() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRun_1()
        throws Exception {
        NotificationRunner fixture = new NotificationRunner(new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU), (MailService) null, new VMTrackerImpl());

        fixture.run();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }

    /**
     * Run the void run() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRun_2()
        throws Exception {
        NotificationRunner fixture = new NotificationRunner(new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU), (MailService) null, new VMTrackerImpl());

        fixture.run();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }

    /**
     * Run the void run() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRun_3()
        throws Exception {
        NotificationRunner fixture = new NotificationRunner(new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU), (MailService) null, new VMTrackerImpl());

        fixture.run();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }

    /**
     * Run the void run() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRun_4()
        throws Exception {
        NotificationRunner fixture = new NotificationRunner(new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU), (MailService) null, new VMTrackerImpl());

        fixture.run();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }

    /**
     * Run the void run() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRun_5()
        throws Exception {
        NotificationRunner fixture = new NotificationRunner(new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU), (MailService) null, new VMTrackerImpl());

        fixture.run();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }

    /**
     * Run the void run() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRun_6()
        throws Exception {
        NotificationRunner fixture = new NotificationRunner(new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU), (MailService) null, new VMTrackerImpl());

        fixture.run();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }

    /**
     * Run the void run() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRun_7()
        throws Exception {
        NotificationRunner fixture = new NotificationRunner(new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU), (MailService) null, new VMTrackerImpl());

        fixture.run();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }
}