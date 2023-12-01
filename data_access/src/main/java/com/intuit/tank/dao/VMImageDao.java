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

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.intuit.tank.project.VMInstance;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class VMImageDao extends BaseDao<VMInstance> {

    public VMImageDao() {
        super();

    }

    /**
     * Gets the VMImage associated with the instanceID.
     * 
     * @param instanceId
     *            the instance id to find for
     * @return the VMImage or null if not found
     */
    @Nullable
    public VMInstance getImageByInstanceId(@Nonnull String instanceId) {
        VMInstance results;
        EntityManager em = getEntityManager();
        try {
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<VMInstance> query = cb.createQuery(entityClass);
            Root<VMInstance> root = query.from(entityClass);
            query.where(cb.equal(root.get(VMInstance.PROPERTY_INSTANCE_ID), instanceId));
            results = em.createQuery(query).getSingleResult();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
        return results;
    }

    /**
     * 
     * @param info
     * @param region
     * @return
     */
    @Nonnull
    public VMInstance addImageFromInfo(String jobId, @Nonnull VMInformation info, @Nonnull VMRegion region) {
        // persist the VMImages to database:
        VMInstance image = VMInstance.builder()
                .amiId(info.getImageId())
                .instanceId(info.getInstanceId())
                .startTime(info.getLaunchTime() != null ? info.getLaunchTime().getTime() : new Date())
                .status(info.getState())
                .size(info.getSize())
                .region(region)
                .jobId(jobId)
                .build();
        return saveOrUpdate(image);
    }

}
