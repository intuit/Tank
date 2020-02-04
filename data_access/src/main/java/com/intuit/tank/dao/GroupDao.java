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

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.intuit.tank.project.Group;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * GroupDao
 * 
 * @author dangleton
 * 
 */
public class GroupDao extends BaseDao<Group> {
    private static Logger LOG = LogManager.getLogger(GroupDao.class);

    /**
     * @param entityClass
     */
    public GroupDao() {
        super();
    }

    /**
     * finds the group by the name.
     * 
     * @param name
     *            the name to search
     * @return the group or null if no group with the name found.
     */
    public Group findByName(@Nonnull String name) {
        Group result = null;
        try {
            EntityManager em = getEntityManager();
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Group> query = cb.createQuery(Group.class);
            Root<Group> root = query.from(Group.class);
            query.select(root)
                    .where(cb.equal(root.<String>get(Group.PROPERTY_NAME), name));
            result = em.createQuery(query).getSingleResult();
            commit();
        } catch (Exception e) {
            rollback();
            e.printStackTrace();
            LOG.info("No entities for group " + name);
        } finally {
            cleanup();
        }
        return result;
    }

    /**
     * @param g
     * @return
     */
    public synchronized Group getOrCreateGroup(String g) {
        Group existing = findByName(g);
        if (existing == null) {
            existing = new Group(g);
            existing = saveOrUpdate(existing);
        }
        return existing;
    }

}
