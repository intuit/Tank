package com.intuit.tank.rest.mvc.rest.services.user;

import java.util.List;
import java.util.Map;

/**
 * Service interface for user data operations
 */
public interface UserService {

    /**
     * Processes an export request for user data
     * 
     * @param jobId       The job ID for tracking
     * @param identifiers List of user identifiers to process
     * @param tid         The transaction ID for tracking
     * @return The job ID for tracking
     */
    String processExportRequest(String jobId, List<String> identifiers, String tid);

    /**
     * Processes a delete request for user data
     * 
     * @param jobId       The job ID for tracking
     * @param identifiers List of user identifiers to process
     * @param tid         The transaction ID for tracking
     * @return The job ID for tracking
     */
    String processDeleteRequest(String jobId, List<String> identifiers, String tid);

    /**
     * Validates whether user data can be safely deleted
     * 
     * @param jobId          The job ID for tracking
     * @param userIdentifier The user identifier to validate
     * @return Map containing validation result with "canDelete" boolean and
     *         "reason" string
     */
    Map<String, Object> validateDeletion(String jobId, String userIdentifier);

    /**
     * Gets the current status of a job
     * 
     * @param jobId The job ID to check
     * @return Map containing job status information
     */
    Map<String, Object> getJobStatus(String jobId);
}