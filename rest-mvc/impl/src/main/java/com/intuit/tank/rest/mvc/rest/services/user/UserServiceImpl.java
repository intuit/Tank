package com.intuit.tank.rest.mvc.rest.services.user;

import com.intuit.tank.dao.UserDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of UserService for user data operations
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class);

    // Status constants
    private static final String STATUS_ACKNOWLEDGED = "acknowledged";
    private static final String STATUS_PROCESSING = "processing";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_FAILED = "failed";
    private static final String STATUS_NO_DATA = "no_data";

    // Operation constants
    private static final String OPERATION_DELETE = "DELETE";

    // LoginId validation regex
    private static final String LOGIN_ID_REGEX = "^[a-zA-Z0-9._-]{3,20}$";

    private UserDao userDao;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private final ConcurrentHashMap<String, Map<String, Object>> jobResults = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        userDao = new UserDao();
        LOG.info("UserServiceImpl initialized");
    }

    @Override
    public String processExportRequest(String jobId, List<String> userIdentifiers, String tid) {
        if (userIdentifiers == null || userIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("User identifiers cannot be null or empty");
        }

        // Use the first identifier for now (as per current implementation)
        String userIdentifier = userIdentifiers.get(0);

        Map<String, Object> jobStatus = new ConcurrentHashMap<>();
        jobStatus.put("status", STATUS_ACKNOWLEDGED);
        jobStatus.put("message", "Job accepted for processing");
        jobStatus.put("userIdentifier", userIdentifier);
        jobStatus.put("tid", tid);

        jobResults.put(jobId, jobStatus);

        // Process asynchronously
        executorService.submit(() -> processExportAsync(jobId, userIdentifier));

        return jobId;
    }

    @Override
    public String processDeleteRequest(String jobId, List<String> identifiers, String tid) {
        String userIdentifier = getLoginIdentifier(identifiers);

        Map<String, Object> jobStatus = new ConcurrentHashMap<>();
        jobStatus.put("status", STATUS_ACKNOWLEDGED);
        jobStatus.put("message", "Job accepted for processing");
        jobStatus.put("userIdentifier", userIdentifier);
        jobStatus.put("tid", tid);

        jobResults.put(jobId, jobStatus);

        // Process asynchronously
        executorService.submit(() -> processDeleteAsync(jobId, userIdentifier));

        return jobId;
    }

    @Override
    public Map<String, Object> getJobStatus(String jobId) {
        return jobResults.get(jobId);
    }

    @Override
    public Map<String, Object> validateDeletion(String jobId, String userIdentifier) {
        Map<String, Object> validationResult = new HashMap<>();

        try {
            LOG.info("Validating deletion safety for user: {} with jobId: {}", userIdentifier, jobId);

            // Simple check: does the user exist?
            if (userDao.findByUserName(userIdentifier) == null &&
                    userDao.findByEmail(userIdentifier) == null) {
                validationResult.put("canDelete", false);
                validationResult.put("reason", "User not found in system");
                LOG.warn("Deletion validation failed: User not found - {}", userIdentifier);
                return validationResult;
            }

            // User exists - safe to delete
            validationResult.put("canDelete", true);
            validationResult.put("reason", "User exists and can be deleted");
            LOG.info("Deletion validation successful: User exists - {}", userIdentifier);

        } catch (Exception e) {
            LOG.error("Error during deletion validation for user: {} jobId: {}", userIdentifier, jobId, e);
            validationResult.put("canDelete", false);
            validationResult.put("reason", "Error during validation: " + e.getMessage());
        }

        return validationResult;
    }

    /**
     * Gets the loginId identifier from the list, prioritizing valid loginId format
     */
    private String getLoginIdentifier(List<String> identifiers) {
        if (identifiers == null || identifiers.isEmpty()) {
            throw new IllegalArgumentException("No identifiers provided");
        }

        // First, try to find a loginId identifier
        for (String identifier : identifiers) {
            if (isValidLoginId(identifier)) {
                LOG.info("Processing user data operation with loginId: {}", identifier);
                return identifier;
            }
        }

        // If no valid loginId found, use the first identifier
        String firstIdentifier = identifiers.get(0);
        LOG.info("No valid loginId found, using first identifier: {}", firstIdentifier);
        return firstIdentifier;
    }

    /**
     * Validates if the identifier is a valid loginId format
     * LoginId should match the username format stored in our user database
     */
    private boolean isValidLoginId(String identifier) {
        return identifier != null &&
                !identifier.trim().isEmpty() &&
                identifier.matches(LOGIN_ID_REGEX);
    }

    private void processExportAsync(String jobId, String userIdentifier) {
        Map<String, Object> jobStatus = jobResults.get(jobId);

        try {
            jobStatus.put("status", STATUS_PROCESSING);
            LOG.info("Starting export for user: {} (jobId: {})", userIdentifier, jobId);

            Map<String, Object> userData = userDao.exportUserData(userIdentifier);

            if (userData.isEmpty()) {
                jobStatus.put("status", STATUS_NO_DATA);
                jobStatus.put("message", "No data found for user");
            } else {
                jobStatus.put("status", STATUS_COMPLETED);
                jobStatus.put("data", userData);
                jobStatus.put("recordCount", userData.size());
                jobStatus.put("message", "User data exported successfully");
                LOG.info("Export completed for user: {} (jobId: {}) - {} records", userIdentifier, jobId,
                        userData.size());
            }

        } catch (Exception e) {
            jobStatus.put("status", STATUS_FAILED);
            jobStatus.put("message", "Export failed: " + e.getMessage());
            LOG.error("Export failed for user: {} (jobId: {}) - Error: {}", userIdentifier, jobId, e.getMessage(), e);
        }

        // Clean up job after 1 hour
        executorService.schedule(() -> jobResults.remove(jobId), 1, TimeUnit.HOURS);
    }

    private void processDeleteAsync(String jobId, String userIdentifier) {
        Map<String, Object> jobStatus = jobResults.get(jobId);

        try {
            jobStatus.put("status", STATUS_PROCESSING);
            LOG.info("Starting deletion for user: {} (jobId: {})", userIdentifier, jobId);

            // Perform deletion directly
            long deletedCount = userDao.deleteUserData(userIdentifier);

            if (deletedCount == 0) {
                jobStatus.put("status", STATUS_NO_DATA);
                jobStatus.put("message", "No data found for user");
            } else {
                jobStatus.put("status", STATUS_COMPLETED);
                jobStatus.put("recordsAffected", deletedCount);
                jobStatus.put("message", "User data successfully anonymized");
            }

            LOG.info("Deletion completed for user: {} (jobId: {})", userIdentifier, jobId);

        } catch (Exception e) {
            LOG.error("Deletion failed for user: {} (jobId: {})", userIdentifier, jobId, e);
            jobStatus.put("status", STATUS_FAILED);
            jobStatus.put("message", "Deletion failed: " + e.getMessage());
        }

        // Schedule cleanup after 1 hour
        executorService.schedule(() -> jobResults.remove(jobId), 1, TimeUnit.HOURS);
    }
}