/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.annotation.Nullable;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import com.intuit.tank.project.JobNotification;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;

/**
 * JobInstance
 * 
 * @author dangleton
 * 
 */
public class JobNotificationDao extends BaseDao<JobNotification> {
    private static final Logger LOG = LogManager.getLogger(JobNotificationDao.class);

    /**
     * @param entityClass
     */
    public JobNotificationDao() {
        super();
    }

    /**
     * gets the entity at the specified revision
     * 
     * @param id
     *            the id of the entity to fetch
     * @param revisionNumber
     *            the revision number
     * @return the entity or null if no entity can be found
     */
    @Nullable
    @Override
    public JobNotification findRevision(int id, int revisionNumber) {
        JobNotification result = null;
        try {
            begin();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            result = reader.find(JobNotification.class, id, revisionNumber);
            if(result != null) {
                Hibernate.initialize(result.getLifecycleEvents());
                result.getLifecycleEvents().contains(JobLifecycleEvent.QUEUE_ADD);
            }
            commit();
        } catch (NoResultException e) {
            rollback();
            LOG.warn("No result for revision " + revisionNumber + " with id of " + id);
        } finally {
            cleanup();
        }
        return result;
    }

}
