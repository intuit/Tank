package com.intuit.tank.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simplified request model for service API contract.
 * What external services send to Tank - focused on what Tank needs to know.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    
    /**
     * Operation to perform: ACCESS, DELETE, DELETE_CHECK
     */
    private String operation;
    
    /**
     * User identifiers (usernames, emails, user IDs)
     */
    private List<String> identifiers;
    
    /**
     * Job ID for correlation
     */
    private String jobId;
    
    /**
     * User authentication ID
     */
    private String authId;
    
    /**
     * Transaction ID for tracking purposes
     */
    private String tid;
} 