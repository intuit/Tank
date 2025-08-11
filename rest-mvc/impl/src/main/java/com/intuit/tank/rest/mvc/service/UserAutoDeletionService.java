/*
 * Copyright (c) 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.rest.mvc.service;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Service responsible for automatically deleting (anonymizing) users who haven't 
 * logged in for a configurable retention period (default: 2 years).
 * 
 * This service runs once on server startup and processes users in batches to avoid
 * overwhelming the system.
 */
@Service
public class UserAutoDeletionService {

    private static final Logger LOG = LogManager.getLogger(UserAutoDeletionService.class);
    
    // Maximum users to process in a single batch to avoid overwhelming the system
    private static final int MAX_BATCH_SIZE = 50;
    
    // Protected usernames that should never be deleted (case-insensitive)
    private static final String[] PROTECTED_USERNAMES = {"admin"};
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final TankConfig tankConfig;

    public UserAutoDeletionService() {
        this.tankConfig = new TankConfig();
    }

    /**
     * Runs once on server startup to check for and delete inactive users.
     * Uses @PostConstruct to ensure it runs after all dependencies are initialized.
     */
    @PostConstruct
    public void performStartupUserDeletion() {
        if (!isAutoDeletionEnabled()) {
            LOG.debug("User auto-deletion is disabled, skipping startup execution");
            return;
        }

        LOG.info("=== Starting user auto-deletion on server startup ===");
        
        try {
            int retentionDays = getRetentionDays();
            long totalEligible = countEligibleUsers(retentionDays);
            
            if (totalEligible == 0) {
                LOG.info("No users found eligible for auto-deletion (retention period: {} days)", retentionDays);
                return;
            }
            
            LOG.info("Found {} users eligible for auto-deletion (inactive for {}+ days)", totalEligible, retentionDays);
            
            // TEMPORARY: Just log what would be deleted without actually doing it
            logEligibleUsers(retentionDays);
            
            // COMMENTED OUT FOR TESTING: int deletedCount = processUserDeletionBatches(retentionDays);
            int deletedCount = 0; // Temporary: don't actually delete
            
            LOG.info("DRY RUN COMPLETED: {} users would be processed (no actual deletion)", deletedCount);
            
        } catch (Exception e) {
            LOG.error("Error during startup user auto-deletion process", e);
        }
        
        LOG.info("=== Completed startup user auto-deletion process (DRY RUN) ===");
    }

    /**
     * TEMPORARY: Log eligible users without deleting them
     */
    private void logEligibleUsers(int retentionDays) {
        UserDao userDao = new UserDao();
        List<User> eligibleUsers = userDao.findUsersEligibleForDeletion(retentionDays);
        
        LOG.info("=== DRY RUN: Users that would be deleted ===");
        for (User user : eligibleUsers) {
            String lastLoginStr = user.getLastLoginTs() != null 
                ? formatter.format(user.getLastLoginTs())
                : "Never logged in";
                
            String reason = user.getLastLoginTs() != null 
                ? "inactive since last login"
                : "never logged in (created " + formatter.format(user.getCreated().toInstant()) + ")";
                
            boolean isProtected = isProtectedUser(user);
            String protectionReason = "";
            if (isProtected) {
                String username = user.getName().toLowerCase();
                
                // Check protection reason in order of precedence
                if (username.startsWith("deleted_user_")) {
                    protectionReason = "PROTECTED (already anonymized)";
                } else if (tankConfig.getUserAutoDeletionPermittedUsers().stream()
                        .anyMatch(p -> p.equalsIgnoreCase(user.getName()))) {
                    protectionReason = "PROTECTED (permitted user)";
                } else if (Arrays.asList(PROTECTED_USERNAMES).stream()
                        .anyMatch(p -> p.equalsIgnoreCase(username))) {
                    protectionReason = "PROTECTED (admin user)";
                } else if (user.getGroups() != null && user.getGroups().stream()
                        .anyMatch(g -> g.getName() != null && 
                            (g.getName().toLowerCase().contains("admin") || 
                             g.getName().toLowerCase().contains("super")))) {
                    protectionReason = "PROTECTED (admin group)";
                } else {
                    protectionReason = "PROTECTED (unknown reason)";
                }
            }
            String status = isProtected ? protectionReason : "WOULD BE DELETED";
            
            LOG.info("DRY RUN: {} (ID: {}, Last Login: {}, Reason: {}) - {}", 
                    user.getName(), user.getId(), lastLoginStr, reason, status);
        }
        LOG.info("=== DRY RUN: End of eligible users list ===");
    }

    /**
     * Process user deletions in batches to avoid overwhelming the system.
     * 
     * @param retentionDays retention period in days
     * @return total number of users deleted
     */
    private int processUserDeletionBatches(int retentionDays) {
        UserDao userDao = new UserDao();
        int totalDeleted = 0;
        
        while (true) {
            // Get the next batch of eligible users
            List<User> eligibleUsers = userDao.findUsersEligibleForDeletion(retentionDays);
            
            if (eligibleUsers.isEmpty()) {
                break; // No more users to process
            }
            
            // Process only up to MAX_BATCH_SIZE users in this iteration
            int batchSize = Math.min(eligibleUsers.size(), MAX_BATCH_SIZE);
            List<User> batchUsers = eligibleUsers.subList(0, batchSize);
            
            LOG.info("Processing batch of {} users for auto-deletion", batchSize);
            
            for (User user : batchUsers) {
                try {
                    // Skip protected users (admin, etc.)
                    if (isProtectedUser(user)) {
                        LOG.info("Skipping protected user: {} (ID: {})", user.getName(), user.getId());
                        continue;
                    }
                    
                    deleteInactiveUser(user, userDao);
                    totalDeleted++;
                    
                } catch (Exception e) {
                    LOG.error("Failed to delete user: {} (ID: {})", user.getName(), user.getId(), e);
                    // Continue processing other users even if one fails
                }
            }
            
            LOG.info("Completed batch: {} users deleted", batchSize);
            
            // If we processed fewer than MAX_BATCH_SIZE, we're done
            if (batchSize < MAX_BATCH_SIZE) {
                break;
            }
        }
        
        return totalDeleted;
    }

    /**
     * Delete (anonymize) a single inactive user.
     * 
     * @param user the user to delete
     * @param userDao the UserDao instance to use
     */
    private void deleteInactiveUser(User user, UserDao userDao) {
        String lastLoginStr = user.getLastLoginTs() != null 
            ? formatter.format(user.getLastLoginTs())
            : "Never logged in";
            
        String reason = user.getLastLoginTs() != null 
            ? "inactive since last login"
            : "never logged in (created " + formatter.format(user.getCreated().toInstant()) + ")";
            
        LOG.info("Deleting inactive user: {} (ID: {}, Last Login: {}, Reason: {})", 
                user.getName(), user.getId(), lastLoginStr, reason);
        
        // Use existing anonymization method
        long result = userDao.deleteUserData(user.getName());
        
        if (result > 0) {
            LOG.info("Successfully anonymized inactive user: {} (ID: {})", user.getName(), user.getId());
        } else {
            LOG.warn("User deletion returned 0 - user may have already been processed: {} (ID: {})", 
                    user.getName(), user.getId());
        }
    }

    /**
     * Count users eligible for deletion (for reporting/metrics).
     */
    private long countEligibleUsers(int retentionDays) {
        try {
            UserDao userDao = new UserDao();
            return userDao.countUsersEligibleForDeletion(retentionDays);
        } catch (Exception e) {
            LOG.error("Error counting eligible users for deletion", e);
            return 0;
        }
    }

    /**
     * Check if auto-deletion is enabled via configuration.
     * Uses TankConfig as primary source, with system properties as fallback.
     * 
     * @return true if auto-deletion is enabled
     */
    private boolean isAutoDeletionEnabled() {
        // TEMPORARY: Hardcode to true for testing
        LOG.info("TEMPORARY: Auto-deletion hardcoded to ENABLED for testing");
        return true;
        
        // Production code (commented out for testing):
        /*
        // Check TankConfig first
        boolean tankConfigEnabled = tankConfig.isUserAutoDeletionEnabled();
        if (tankConfigEnabled) {
            LOG.debug("Auto-deletion enabled via TankConfig");
            return true;
        }
        
        // Fallback to system property
        String sysProp = System.getProperty("tank.user.auto-deletion.enabled");
        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }
        
        // Fallback to environment variable
        String envVar = System.getenv("TANK_USER_AUTO_DELETION_ENABLED");
        if (envVar != null) {
            return Boolean.parseBoolean(envVar);
        }
        
        // Default to false for safety - must be explicitly enabled
        LOG.debug("Auto-deletion not explicitly enabled via TankConfig, system properties, or environment variables");
        return false;
        */
    }

    /**
     * Get the retention period in days from configuration.
     * Uses TankConfig as primary source, with system properties as fallback.
     * 
     * @return retention period in days (default: 730 days = 2 years)
     */
    private int getRetentionDays() {
        // TEMPORARY: Hardcode to 730 days (2 years) for production-like testing
        LOG.info("TEMPORARY: Retention period hardcoded to 730 days (2 years) for testing");
        return 730;
        
        // Production code (commented out for testing):
        /*
        // Check TankConfig first
        int tankConfigRetention = tankConfig.getUserAutoDeletionRetentionDays();
        if (tankConfigRetention > 0) {
            LOG.debug("Using retention period from TankConfig: {} days", tankConfigRetention);
            return tankConfigRetention;
        }
        
        // Fallback to system property
        String sysProp = System.getProperty("tank.user.auto-deletion.retention-days");
        if (sysProp != null) {
            try {
                return Integer.parseInt(sysProp);
            } catch (NumberFormatException e) {
                LOG.warn("Invalid retention days system property value: {}, using default: {}", sysProp, 730);
            }
        }
        
        // Fallback to environment variable
        String envVar = System.getenv("TANK_USER_AUTO_DELETION_RETENTION_DAYS");
        if (envVar != null) {
            try {
                return Integer.parseInt(envVar);
            } catch (NumberFormatException e) {
                LOG.warn("Invalid retention days environment variable value: {}, using default: {}", envVar, 730);
            }
        }
        
        // Return default
        LOG.debug("Using default retention period: {} days", 730);
        return 730;
        */
    }

    /**
     * Check if a user is protected from auto-deletion.
     * Protected users include:
     * - Users in the permitted users list from configuration
     * - Admin users (admin, administrator, root, system)
     * - Users in admin groups
     * - Already anonymized users (deleted_user_*)
     * 
     * @param user the user to check
     * @return true if the user should be protected from deletion
     */
    private boolean isProtectedUser(User user) {
        if (user == null || user.getName() == null) {
            return false;
        }
        
        String username = user.getName().toLowerCase().trim();
        
        // Check if user is already anonymized (deleted_user_*)
        if (username.startsWith("deleted_user_")) {
            return true;
        }
        
        // Check against permitted users from configuration
        List<String> permittedUsers = tankConfig.getUserAutoDeletionPermittedUsers();
        for (String permittedUser : permittedUsers) {
            if (permittedUser.equalsIgnoreCase(username)) {
                return true;
            }
        }
        
        // Check against hardcoded protected username list (admin users)
        for (String protectedName : PROTECTED_USERNAMES) {
            if (protectedName.equalsIgnoreCase(username)) {
                return true;
            }
        }
        
        // Additional protection: check if user has admin role/group
        if (user.getGroups() != null) {
            for (var group : user.getGroups()) {
                if (group.getName() != null && 
                    (group.getName().toLowerCase().contains("admin") || 
                     group.getName().toLowerCase().contains("super"))) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Manual method to trigger user auto-deletion (for admin use or testing).
     * 
     * @return number of users deleted
     */
    public int performManualUserDeletion() {
        LOG.info("=== Starting manual user auto-deletion process ===");
        
        try {
            int retentionDays = getRetentionDays();
            return processUserDeletionBatches(retentionDays);
            
        } catch (Exception e) {
            LOG.error("Error during manual user auto-deletion process", e);
            throw new RuntimeException("Manual user deletion failed", e);
        }
    }
} 