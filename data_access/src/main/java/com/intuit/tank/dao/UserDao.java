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
import com.intuit.tank.vm.common.PasswordEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
                String anonymizedName = "deleted_user_" + user.getId();

                // Use native SQL to bypass JPA lifecycle callbacks and anonymize timestamps and token
                // Note: created and modified cannot be NULL due to database constraints, so we set them to epoch time
                // Also set last_login_ts to epoch time to prevent re-deletion of anonymized users
                String sql = "UPDATE user SET name = ?, email = ?, created = '1970-01-01 00:00:00', modified = '1970-01-01 00:00:00', last_login_ts = '1970-01-01 00:00:00', token = NULL WHERE id = ?";
                em.createNativeQuery(sql)
                    .setParameter(1, anonymizedName)
                    .setParameter(2, "deleted_users@deleted.com")
                    .setParameter(3, user.getId())
                    .executeUpdate();
                LOG.info("Anonymized user data for: {}", userIdentifier);

                // Dynamically find all tables with 'creator' column and update them
                List<String> tables = em.createNativeQuery(
                    "SELECT TABLE_NAME FROM information_schema.COLUMNS " +
                    "WHERE COLUMN_NAME = 'creator' AND TABLE_SCHEMA = DATABASE()")
                    .getResultList();

                for (String table : tables) {
                    String updateSql = "UPDATE " + table + " SET creator = ? WHERE creator = ?";
                    int rowsUpdated = em.createNativeQuery(updateSql)
                        .setParameter(1, anonymizedName)
                        .setParameter(2, user.getName())
                        .executeUpdate();
                    LOG.info("Updated {} rows in table {} for user {}", rowsUpdated, table, userIdentifier);
                }

                // Handle revision_info table separately as it uses user_name instead of creator
                String revisionSql = "UPDATE revision_info SET user_name = ? WHERE user_name = ?";
                int revisionRows = em.createNativeQuery(revisionSql)
                    .setParameter(1, anonymizedName)
                    .setParameter(2, user.getName())
                    .executeUpdate();
                LOG.info("Updated {} rows in revision_info table for user {}", revisionRows, userIdentifier);

                // Set user to guest group only
                // First, clear all existing group memberships
                String clearGroupsSql = "DELETE FROM user_user_group WHERE group_id = ?";
                int clearedGroups = em.createNativeQuery(clearGroupsSql)
                    .setParameter(1, user.getId())
                    .executeUpdate();
                LOG.info("Cleared {} group memberships for user {}", clearedGroups, userIdentifier);

                // Then add user to guest group (find guest group ID)
                String guestGroupIdSql = "SELECT id FROM user_group WHERE name = 'guest'";
                Integer guestGroupId = (Integer) em.createNativeQuery(guestGroupIdSql).getSingleResult();

                String addGuestGroupSql = "INSERT INTO user_user_group (group_id, user_id) VALUES (?, ?)";
                em.createNativeQuery(addGuestGroupSql)
                    .setParameter(1, user.getId())
                    .setParameter(2, guestGroupId)
                    .executeUpdate();
                LOG.info("Added user {} to guest group", userIdentifier);
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
     * Find users eligible for auto-deletion based on last login timestamp.
     * Returns users who haven't logged in for the specified number of days,
     * including users who have never logged in (using created date for retention).
     *
     * @param retentionDays number of days after which inactive users should be deleted
     * @return list of users eligible for deletion
     */
    @SuppressWarnings("unchecked")
    public List<User> findUsersEligibleForDeletion(int retentionDays) {
        EntityManager em = getEntityManager();
        try {
            begin();

            // Calculate the cutoff date
            java.time.Instant cutoffDate = java.time.Instant.now().minus(retentionDays, java.time.temporal.ChronoUnit.DAYS);

            // Find users who either:
            // 1. Have never logged in (lastLoginTs IS NULL) AND were created before cutoff date, OR
            // 2. Haven't logged in since the cutoff date (lastLoginTs < cutoffDate)
            String jpql = "FROM User u WHERE " +
                "(u.lastLoginTs IS NULL AND u.created < :cutoffDate) OR " +
                "(u.lastLoginTs IS NOT NULL AND u.lastLoginTs < :cutoffDate) " +
                "ORDER BY u.lastLoginTs ASC, u.created ASC";

            List<User> eligibleUsers = em.createQuery(jpql, User.class)
                .setParameter("cutoffDate", cutoffDate)
                .getResultList();

            commit();
            LOG.debug("Found {} users eligible for deletion (retention period: {} days)", eligibleUsers.size(), retentionDays);
            return eligibleUsers;

        } catch (Exception e) {
            rollback();
            LOG.error("Error finding users eligible for deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find users eligible for deletion", e);
        } finally {
            cleanup();
        }
    }

    /**
     * Count users eligible for auto-deletion without loading the full entities.
     * More efficient for reporting and metrics.
     *
     * @param retentionDays number of days after which inactive users should be deleted
     * @return count of users eligible for deletion
     */
    public long countUsersEligibleForDeletion(int retentionDays) {
        EntityManager em = getEntityManager();
        try {
            begin();

            java.time.Instant cutoffDate = java.time.Instant.now().minus(retentionDays, java.time.temporal.ChronoUnit.DAYS);

            String jpql = "SELECT COUNT(u) FROM User u WHERE " +
                "(u.lastLoginTs IS NULL AND u.created < :cutoffDate) OR " +
                "(u.lastLoginTs IS NOT NULL AND u.lastLoginTs < :cutoffDate)";

            Long count = em.createQuery(jpql, Long.class)
                .setParameter("cutoffDate", cutoffDate)
                .getSingleResult();

            commit();
            return count != null ? count : 0L;

        } catch (Exception e) {
            rollback();
            LOG.error("Error counting users eligible for deletion: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to count users eligible for deletion", e);
        } finally {
            cleanup();
        }
    }

}
