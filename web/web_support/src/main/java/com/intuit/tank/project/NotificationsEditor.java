package com.intuit.tank.project;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.intuit.tank.ProjectBean;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;

@Named
@ConversationScoped
public class NotificationsEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProjectBean projectBean;

    private List<JobNotification> notifications;
    private JobNotification currentNotification;

    /**
     * @return the currentNotification
     */
    public JobNotification getCurrentNotification() {
        return currentNotification;
    }

    /**
     * @param currentNotification
     *            the currentNotification to set
     */
    public void setCurrentNotification(JobNotification currentNotification) {
        this.currentNotification = currentNotification;
    }

    /**
     * @return the notifications
     */
    public List<JobNotification> getNotifications() {
        return notifications;
    }

    /**
     * 
     */
    public void addNotification() {
        this.notifications.add(new JobNotification());
    }

    /**
     * 
     * @return
     */
    public JobLifecycleEvent[] getNotificationEventList() {
        return JobLifecycleEvent.values();
    }

    /**
     * Initializes the class variables with appropriate references
     * 
     * @param project
     * @param workload
     */
    public void init() {
        Set<JobNotification> existing = projectBean.getJobConfiguration().getNotifications();
        this.notifications = new ArrayList<JobNotification>();
        if (existing != null) {
            for (JobNotification n : existing) {
                n.initTransient();
                notifications.add(n);
            }
        }
    }

    /**
     * Deletes the script group from the workload. This does not persist the changes to the database.
     * 
     * @param group
     */
    public void delete(JobNotification currentNotification) {
        for (int i = notifications.size(); --i >= 0;) {
            if (notifications.get(i) == currentNotification) {
                notifications.remove(i);
                break;
            }
        }
    }

    public String getInplaceLabel(String recipients) {
        if (StringUtils.isEmpty(recipients)) {
            return "<Click to Add Recipients>";
        }
        return recipients;
    }

    /**
     * persists the workload in the database
     */
    public void save() {
        JobNotificationDao dao = new JobNotificationDao();
        projectBean.getWorkload().getJobConfiguration().getNotifications().clear();
        for (JobNotification notification : notifications) {
            notification.copyTransient();
            notification = dao.saveOrUpdate(notification);
            projectBean.getWorkload().getJobConfiguration().getNotifications().add(notification);
        }
    }

    /**
     * @param workload
     */
    public void copyTo(Workload workload) {
        Set<JobNotification> jobNotifications = new HashSet<JobNotification>();
        for (JobNotification n : notifications) {
            JobNotification not = new JobNotification();
            not.setBody(n.getBody());
            not.setSubject(n.getSubject());
            not.setLifecycleEvents(new ArrayList<JobLifecycleEvent>(n.getLifecycleEvents()));
            not.setRecipientList(n.getRecipientList());
            jobNotifications.add(not);
        }

        workload.getJobConfiguration().setNotifications(jobNotifications);

    }

}
