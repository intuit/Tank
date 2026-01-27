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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import com.intuit.tank.project.User;
import com.intuit.tank.project.UserProperty;
import com.intuit.tank.project.Group;
import com.intuit.tank.vm.common.PasswordEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDao
 * 
 * @author dangleton
 * 
 */
@Dependent
public class UserDao extends BaseDao<User> {
    private static final Logger LOG = LogManager.getLogger(UserDao.class);

    public UserDao() {
        super();
    }

    /**
     * Authenticates user with given credentials.
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
        return findUserInformationInternal(UserProperty.PROPERTY_NAME, userName);
    }

    /**
     * finds the user by email.
     *
     * @param email
     *            the email to search
     * @return the user or null if no user with the name found.
     */
    public User findByEmail(@Nonnull String email) {
        return findUserInformationInternal(UserProperty.PROPERTY_EMAIL, email);
    }

    /**
     * finds the user by the apiToken.
     * 
     * @param apiToken
     *            the api token
     * @return the user or null if no user with the apiToken.
     */
    public User findByApiToken(@Nonnull String apiToken) {
        return findUserInformationInternal(UserProperty.PROPERTY_TOKEN, apiToken);
    }

    private User findUserInformationInternal(UserProperty userProperty, String userIdentifier) {
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
                    .where(cb.equal(root.<String>get(userProperty.value), userIdentifier));
            user = em.createQuery(query).getSingleResult();
            commit();
        } catch (NoResultException nre) {
            rollback();
            LOG.warn("No entity matching {}: {}", userProperty.value, userIdentifier, nre);
        } catch (Exception e) {
            rollback();
            String printQuery = (query != null) ? query.toString()
                    : "Failed to connect to database: ";
            LOG.warn("no entity matching query: {}", printQuery, e);
        } finally {
            cleanup();
        }
        return user;
    }

    /**
     * Finds user by name or email for access/delete requests
     *
     * @param userIdentifier
     *            the user name or email to search
     * @return the user or null if not found
     */
    @Nullable
    private User findUserByIdentifier(@Nonnull String userIdentifier) {
        User user = findByUserName(userIdentifier);
        if (user == null) {
            user = findByEmail(userIdentifier);
        }
        return user;
    }

    /**
     * Exports user data for access requests
     * Returns only name, email, created, and modified dates.
     *
     * @param userIdentifier
     *            the user name or email to export
     * @return Map containing user data or empty map if user not found
     */
    @Nonnull
    public java.util.Map<String, Object> exportUserData(@Nonnull String userIdentifier) {
        User user = findUserByIdentifier(userIdentifier);
        if (user == null) {
            LOG.warn("No user found for identifier: {}", userIdentifier);
            return new java.util.HashMap<>();
        }

        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("created", user.getCreated());
        userData.put("modified", user.getModified());

        LOG.info("Exported user data for: {}", userIdentifier);
        return userData;
    }

    /**
     * Deletes/anonymizes user data for deletion requests
     * Sets name to "deleted_user", email to "deleted_users@deleted.com",
     * and timestamps to default values.
     *
     * @param userIdentifier
     *            the user name or email to anonymize
     * @return 1 if user was found and anonymized, 0 if not found
     */
    public long deleteUserData(@Nonnull String userIdentifier) {
        User user = findUserByIdentifier(userIdentifier);
        if (user == null) {
            LOG.warn("No user found for identifier: {}", userIdentifier);
            return 0;
        }

        EntityManager em = getEntityManager();
        try {
            begin();
            user = em.find(User.class, user.getId());
            if (user != null) {
                String originalName = user.getName();
                String anonymizedName = "deleted_user_" + user.getId();
                java.util.Date epochDate = new java.util.Date(0L);
                java.time.Instant epochInstant = java.time.Instant.EPOCH;

                user.setName(anonymizedName);
                user.setEmail("deleted_users@deleted.com");
                user.deleteApiToken();
                user.setCreated(epochDate);
                user.setModified(epochDate);
                user.setLastLoginTs(epochInstant);

                em.merge(user);
                em.flush();
                LOG.info("Anonymized user data for: {}", userIdentifier);

                updateCreatorFieldsForOwnableEntities(em, originalName, anonymizedName);

                updateRevisionInfo(em, originalName, anonymizedName);

                updateUserGroups(em, user);
            }
            commit();
            return 1;
        } catch (Exception e) {
            rollback();
            LOG.error("Error anonymizing user data for: {}", userIdentifier, e);
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
    }

    /**
     * Updates creator fields for all OwnableEntity subclasses
     */
    private void updateCreatorFieldsForOwnableEntities(EntityManager em, String originalName, String anonymizedName) {
        updateOwnableEntities(em, originalName, anonymizedName, com.intuit.tank.project.Project.class);
        updateOwnableEntities(em, originalName, anonymizedName, com.intuit.tank.project.Script.class);
        updateOwnableEntities(em, originalName, anonymizedName, com.intuit.tank.project.ScriptFilter.class);
        updateOwnableEntities(em, originalName, anonymizedName, com.intuit.tank.project.ScriptFilterGroup.class);
        updateOwnableEntities(em, originalName, anonymizedName, com.intuit.tank.project.DataFile.class);
        updateOwnableEntities(em, originalName, anonymizedName, com.intuit.tank.project.ExternalScript.class);

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<com.intuit.tank.project.JobInstance> query = cb.createQuery(com.intuit.tank.project.JobInstance.class);
            Root<com.intuit.tank.project.JobInstance> root = query.from(com.intuit.tank.project.JobInstance.class);
            query.select(root).where(cb.equal(root.get("creator"), originalName));

            java.util.List<com.intuit.tank.project.JobInstance> jobInstances = em.createQuery(query).getResultList();
            for (com.intuit.tank.project.JobInstance jobInstance : jobInstances) {
                jobInstance.setCreator(anonymizedName);
                em.merge(jobInstance);
            }

            if (!jobInstances.isEmpty()) {
                LOG.info("Updated {} JobInstance entities for user {}", jobInstances.size(), originalName);
            }
        } catch (Exception e) {
            LOG.warn("Could not update creator for JobInstance entities: {}", e.getMessage());
        }
    }

    /**
     * Helper method to update OwnableEntity instances
     */
    private <T extends com.intuit.tank.project.OwnableEntity> void updateOwnableEntities(
            EntityManager em, String originalName, String anonymizedName, Class<T> entityClass) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> query = cb.createQuery(entityClass);
            Root<T> root = query.from(entityClass);
            query.select(root).where(cb.equal(root.get("creator"), originalName));

            java.util.List<T> entities = em.createQuery(query).getResultList();
            for (T entity : entities) {
                entity.setCreator(anonymizedName);
                em.merge(entity);
            }

            if (!entities.isEmpty()) {
                LOG.info("Updated {} {} entities for user {}", entities.size(), entityClass.getSimpleName(), originalName);
            }
        } catch (Exception e) {
            LOG.warn("Could not update creator for entity type {}: {}", entityClass.getSimpleName(), e.getMessage());
        }
    }

    /**
     * Updates revision_info table entries
     */
    private void updateRevisionInfo(EntityManager em, String originalName, String anonymizedName) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<com.intuit.tank.project.TankRevisionInfo> query = cb.createQuery(com.intuit.tank.project.TankRevisionInfo.class);
            Root<com.intuit.tank.project.TankRevisionInfo> root = query.from(com.intuit.tank.project.TankRevisionInfo.class);
            query.select(root).where(cb.equal(root.get("username"), originalName));

            java.util.List<com.intuit.tank.project.TankRevisionInfo> revisions = em.createQuery(query).getResultList();
            for (com.intuit.tank.project.TankRevisionInfo revision : revisions) {
                revision.setUsername(anonymizedName);
                em.merge(revision);
            }

            if (!revisions.isEmpty()) {
                LOG.info("Updated {} revision_info entries for user {}", revisions.size(), originalName);
            }
        } catch (Exception e) {
            LOG.warn("Could not update revision_info entries: {}", e.getMessage());
        }
    }

    /**
     * Updates user group memberships - removes all groups and adds only guest group
     */
    private void updateUserGroups(EntityManager em, User user) {
        user.getGroups().clear();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Group> query = cb.createQuery(Group.class);
        Root<Group> root = query.from(Group.class);
        query.select(root).where(cb.equal(root.get("name"), "guest"));

        Group guestGroup = null;
        try {
            guestGroup = em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            guestGroup = new Group("guest");
            em.persist(guestGroup);
            LOG.info("Created guest group");
        }

        user.addGroup(guestGroup);
        em.merge(user);
        LOG.info("Set user {} to guest group only", user.getName());
    }

    /**
     * Find users eligible for auto-deletion based on last login timestamp.
     * Returns users who haven't logged in for the specified number of days,
     * including users who have never logged in (using created date for retention).
     *
     * @param retentionDays number of days after which inactive users should be deleted
     * @return list of users eligible for deletion
     */
    public List<User> findUsersEligibleForDeletion(int retentionDays) {
        List<User> eligibleUsers = null;
        EntityManager em = getEntityManager();
        try {
            begin();

            java.time.Instant cutoffDate = java.time.Instant.now().minus(retentionDays, java.time.temporal.ChronoUnit.DAYS);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root)
                .where(cb.lessThan(root.get("lastLoginTs"), cutoffDate))
                .orderBy(cb.asc(root.get("lastLoginTs")), cb.asc(root.get("created")));

            eligibleUsers = em.createQuery(query).getResultList();

            commit();
            LOG.debug("Found {} users eligible for deletion (retention period: {} days)", eligibleUsers.size(), retentionDays);

        } catch (Exception e) {
            rollback();
            LOG.error("Error finding users eligible for deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find users eligible for deletion", e);
        } finally {
            cleanup();
        }
        return eligibleUsers != null ? eligibleUsers : new ArrayList<>();
    }

    /**
     * Count users eligible for auto-deletion without loading the full entities.
     * More efficient for reporting and metrics.
     *
     * @param retentionDays number of days after which inactive users should be deleted
     * @return count of users eligible for deletion
     */
    public long countUsersEligibleForDeletion(int retentionDays) {
        Long count = 0L;
        EntityManager em = getEntityManager();
        try {
            begin();

            java.time.Instant cutoffDate = java.time.Instant.now().minus(retentionDays, java.time.temporal.ChronoUnit.DAYS);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<User> root = query.from(User.class);

            query.select(cb.count(root))
                .where(cb.lessThan(root.get("lastLoginTs"), cutoffDate));

            count = em.createQuery(query).getSingleResult();

            commit();

        } catch (Exception e) {
            rollback();
            LOG.error("Error counting users eligible for deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to count users eligible for deletion", e);
        } finally {
            cleanup();
        }
        return count != null ? count : 0L;
    }

}
