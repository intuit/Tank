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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.mail.MailService;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.settings.MailMessage;
import com.intuit.tank.vm.settings.MailMessageConfig;
import com.intuit.tank.vm.vmManager.Notification;
import com.intuit.tank.vm.vmManager.Recipient;

/**
 * NotificationRunner
 * 
 * @author dangleton
 * 
 */
public class NotificationRunner implements Runnable {
    private static final Logger LOG = LogManager.getLogger(NotificationRunner.class);

    private JobEvent jobEvent;
    private MailService mailService;
    private VMTracker tracker;

    /**
     * 
     * @param jobEvent
     */
    public NotificationRunner(JobEvent jobEvent, MailService mailService, VMTracker tracker) {
        this.jobEvent = jobEvent;
        this.mailService = mailService;
        this.tracker = tracker;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void run() {
        if (NumberUtils.isDigits(jobEvent.getJobId())) {
            int id = Integer.parseInt(jobEvent.getJobId());
            JobInstance job = new JobInstanceDao().findById(id);
            if (job != null) {
                Set<? extends Notification> notifications = getNotifications(job);
                for (Notification n : notifications) {
                    if (!n.getRecipients().isEmpty()) {
                        if (n.getLifecycleEvents().contains(jobEvent.getEvent())) {
                            // we have an event to send mail for
                            sendMail(n);
                        }
                    }
                }

            }
        } else { // do automation stuff

        }
    }

    /**
     * @param n
     * 
     */
    private void sendMail(Notification n) {
        // what to do if we don't have a template for that event?
        MailMessage mailMessage = getMailMessage(jobEvent.getEvent());
        List<String> addresses = new ArrayList<String>();
        for (Recipient r : n.getRecipients()) {
            addresses.add(r.getAddress());
        }
        if (mailMessage != null) {
            mailService.sendMail(mailMessage, addresses.toArray(new String[addresses.size()]));
        } else {
            LOG.warn("Mail template does not exist for job event: " + jobEvent.getEvent().getDisplay());
        }
    }

    /**
     * @param event2
     * @return
     */
    private String getSubject(JobLifecycleEvent lifecycleEvent) {
        return "Event " + lifecycleEvent.getDisplay() + " for job " + jobEvent.getJobId();
    }

    /**
     * @param event2
     * @return
     */
    private String getBody(JobLifecycleEvent lifecycleEvent) {
        return "Event " + lifecycleEvent.getDisplay() + " for job " + jobEvent.getJobId() + " at "
                + new Date().toString() + "\n\n" + jobEvent.getMessage();
    }

    private MailMessage getMailMessage(JobLifecycleEvent lifecycleEvent) {
        MailMessage ret = null;
        MailMessageConfig config = new MailMessageConfig();
        MailMessage template = config.getMailMessage(lifecycleEvent.name());
        if (template != null) {
            NotificationContextBuilder contextBuilder = new NotificationContextBuilder(jobEvent, tracker);
            String newSubject = contextBuilder.getContext().replaceValues(template.getSubject());
            String newBody = contextBuilder.getContext().replaceValues(template.getBody() + "\n" + config.getFooter());
            ret = new MailMessage(newBody, newSubject, config.getStyle());
        } else {
            ret = new MailMessage(getBody(lifecycleEvent), getSubject(lifecycleEvent), config.getStyle());
        }
        return ret;
    }

    /**
     * @param job
     * @return
     */
    private Set<? extends Notification> getNotifications(JobInstance job) {
        HashSet<JobNotification> ret = new HashSet<JobNotification>();
        JobNotificationDao dao = new JobNotificationDao();
        for (EntityVersion version : job.getNotificationVersions()) {
            JobNotification notification = dao.findById(version.getObjectId());
            if (notification != null) {
                ret.add(notification);
            } else {
                LOG.warn("Attempt to add Notification that does not exist.");
            }
        }
        return ret;
    }

}
