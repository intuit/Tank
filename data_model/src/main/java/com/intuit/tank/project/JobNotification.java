/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.envers.Audited;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.RecipientType;
import com.intuit.tank.vm.vmManager.Notification;
import com.intuit.tank.vm.vmManager.Recipient;

/**
 * JobNotification
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "job_notification")
public class JobNotification extends BaseEntity implements Notification {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(JobNotification.class);

    @Column(name = "subject")
    private String subject;

    @Column(name = "body", length = 4096)
    private String body;

    @Column(name = "recipient_list", length = 1024)
    private String recipientList;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_notification_to_event", 
    	joinColumns = @JoinColumn(name = "job_notification_id"),
    	uniqueConstraints = @UniqueConstraint(columnNames = { "job_notification_id", "lifecycle_events" }))
    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_events")
    private List<JobLifecycleEvent> lifecycleEvents = new ArrayList<JobLifecycleEvent>();

    @Transient
    private List<JobLifecycleEvent> transientLifecycleEvents;

    public void copyTransient() {
        getTransientLifecycleEvents();
        if (!transientLifecycleEvents.isEmpty()) {
            lifecycleEvents.clear();
            lifecycleEvents.addAll(transientLifecycleEvents);
        }
    }

    public void initTransient() {
        transientLifecycleEvents = new ArrayList<JobLifecycleEvent>();
        try {
            transientLifecycleEvents = new ArrayList<JobLifecycleEvent>(lifecycleEvents);
        } catch (Exception e) {
            LOG.warn("Error initializing lifecycle events: " + e, e);
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getSubject() {
        return subject;
    }

    /**
     * @{inheritDoc
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getBody() {
        return body;
    }

    /**
     * @{inheritDoc
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the recipientList
     */
    public String getRecipientList() {
        return recipientList;
    }

    /**
     * @param recipientList
     *            the recipientList to set
     */
    public void setRecipientList(String recipientList) {
        this.recipientList = recipientList;
    }

    /**
     * @return the lifecycleEvents
     */
    public List<JobLifecycleEvent> getLifecycleEvents() {
        getTransientLifecycleEvents();
        return lifecycleEvents;
    }

    /**
     * @param lifecycleEvents
     *            the lifecycleEvents to set
     */
    public void setLifecycleEvents(List<JobLifecycleEvent> lifecycleEvents) {
        this.lifecycleEvents = lifecycleEvents;
    }

    /**
     * @return the transientLifecycleEvents
     */
    public List<JobLifecycleEvent> getTransientLifecycleEvents() {
        if (transientLifecycleEvents == null || transientLifecycleEvents.isEmpty()) {
            initTransient();
        }
        return transientLifecycleEvents;
    }

    /**
     * @param transientLifecycleEvents
     *            the transientLifecycleEvents to set
     */
    public void setTransientLifecycleEvents(List<JobLifecycleEvent> transientLifecycleEvents) {
        this.transientLifecycleEvents = transientLifecycleEvents;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId())
                .toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobNotification)) {
            return false;
        }
        JobNotification o = (JobNotification) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Set<? extends Recipient> getRecipients() {
        Set<JobRecipient> ret = new HashSet<JobRecipient>();
        if (recipientList != null) {
            String[] addresses = recipientList.split(",");
            if (addresses.length == 1) {
                // try semi colon
                addresses = recipientList.split(",");
            }
            for (String address : addresses) {
                ret.add(new JobRecipient(address.trim(), RecipientType.email));
            }
        }
        return ret;
    }
}
