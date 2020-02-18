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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.intuit.tank.project.OwnableEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * OwnableDao
 * 
 * @author dangleton
 * 
 */
public abstract class OwnableDao<T_ENTITY extends OwnableEntity> extends BaseDao<T_ENTITY> {

    private static Logger LOG = LogManager.getLogger(OwnableDao.class);

    /**
     * @param entityClass
     */
    protected OwnableDao() {
        super();
    }

    /**
     * Returns the list of entities owned by the specified user.
     * 
     * @param owner
     *            the owner.
     * @return the non null list of entities.
     */
    @Nonnull
    public List<T_ENTITY> listForOwner(@Nonnull String owner) {
        List<T_ENTITY> results = Collections.emptyList();
        try {
            EntityManager em = getEntityManager();
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T_ENTITY> query = cb.createQuery(entityClass);
            Root<T_ENTITY> root = query.from(entityClass);
            query.select(root)
                    .where(cb.equal(root.<String>get(OwnableEntity.PROPERTY_CREATOR), owner));
            results = em.createQuery(query).getResultList();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            LOG.info("No entities for owner " + owner);
        } finally {
            cleanup();
        }
        return results;
    }

}
