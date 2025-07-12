package com.intuit.tank.rest.mvc.rest.services.user;

import java.util.Map;

/**
 * Service interface for user data operations
 */
public interface UserService {
    
    /**
     * Processes user data export request
     * @param userIdentifier user email or username
     * @return job ID for tracking
     */
    String processExportRequest(String userIdentifier);
    
    /**
     * Processes user data deletion request
     * @param userIdentifier user email or username
     * @return job ID for tracking
     */
    String processDeleteRequest(String userIdentifier);
    
    /**
     * Gets the status and result of a job
     * @param jobId the job ID
     * @return job status and result
     */
    Map<String, Object> getJobStatus(String jobId);
} 