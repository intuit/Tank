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
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.intuit.tank.project.ScriptFilterGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
@Named
@Dependent
public class FilterGroupDao extends BaseDao<ScriptFilterGroup> {
    private static Logger LOG = LogManager.getLogger(FilterGroupDao.class);

    /**
     * @param entityClass
     */
    public FilterGroupDao() {
        super();
    }

    /**
     * @param productName
     * @return
     */
    @Nonnull
    public List<ScriptFilterGroup> getFilterGroupsForProduct(@Nonnull String productName) {
        List<ScriptFilterGroup> results = Collections.emptyList();
        try {
            EntityManager em = getEntityManager();
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ScriptFilterGroup> query = cb.createQuery(ScriptFilterGroup.class);
            Root<ScriptFilterGroup> root = query.from(ScriptFilterGroup.class);
            query.select(root)
                    .where(cb.equal(root.<String>get(ScriptFilterGroup.PROPERTY_PRODUCT_NAME), productName));
            results = em.createQuery(query).getResultList();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            LOG.info("No entities for product " + productName);
        } finally {
            cleanup();
        }
        return results;
    }
}
