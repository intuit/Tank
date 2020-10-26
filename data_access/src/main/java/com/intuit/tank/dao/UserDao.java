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
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.intuit.tank.project.User;
import com.intuit.tank.vm.common.PasswordEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * UserDao
 * 
 * @author dangleton
 * 
 */
@Dependent
public class UserDao extends BaseDao<User> {
    private static final Logger LOG = LogManager.getLogger(UserDao.class);
    /**
     * @param entityClass
     */
    public UserDao() {
        super();
    }

    /**
     * Authenticats the user with the given credentials.
     * 
     * @param userName
     *            the userName
     * @param password
     *            the raw password
     * @return the user if authentication succeeds, null if not.
     */
    @Nullable
    public User authenticate(@Nonnull String userName, @Nonnull String password) {
        User user = findByUserName(userName);
        if (user != null) {
            if (PasswordEncoder.validatePassword(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * finds the user by the userName.
     * 
     * @param userName
     *            the name to search
     * @return the user or null if no user with the name found.
     */
    public User findByUserName(@Nonnull String userName) {
        User user = null;
        CriteriaQuery<User> query = null;
        EntityManager em = getEntityManager();
        try {
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            root.fetch(User.PROPERTY_GROUPS, JoinType.INNER);
            query.select(root)
                    .where(cb.equal(root.<String>get(User.PROPERTY_NAME), userName));
            user = em.createQuery(query).getSingleResult();
            commit();
        } catch (NoResultException nre) {
            rollback();
            LOG.info("No entity matching username: " + userName);
        } catch (Exception e) {
            rollback();
            String printQuery = (query != null) ? query.toString()
                    : "Failed to connect to database: ";
            LOG.info("No entity matching query: " + printQuery, e);
        } finally {
            cleanup();
        }
        return user;
    }
    /**
     * finds the user by the apiToken.
     * 
     * @param apiToken
     *            the api token
     * @return the user or null if no user with the apiToken.
     */
    public User findByApiToken(@Nonnull String apiToken) {
        User user = null;
        CriteriaQuery<User> query = null;
        EntityManager em = getEntityManager();
        try {
            begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            root.fetch(User.PROPERTY_GROUPS, JoinType.INNER);
            query.select(root)
                    .where(cb.equal(root.<String>get(User.PROPERTY_TOKEN), apiToken));
            user = em.createQuery(query).getSingleResult();
            commit();
        } catch (NoResultException nre) {
            rollback();
            LOG.info("no entity matching apiToken: " + apiToken, nre);
        } catch (Exception e) {
            rollback();
            String printQuery = (query != null) ? query.toString()
                    : "Failed to connect to database: ";
            LOG.info("no entity matching query: " + printQuery, e);
        } finally {
            cleanup();
        }
        return user;
    }

}
