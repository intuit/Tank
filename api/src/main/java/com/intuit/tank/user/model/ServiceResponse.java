package com.intuit.tank.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response model for user data operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse {
    
    /**
     * Status of the operation: acknowledged, completed, processing, failed, no_data
     */
    private String status;
    
    /**
     * Human-readable message
     */
    private String message;
    
    /**
     * Service-specific data (optional)
     */
    private Map<String, String> data;
    
    /**
     * Job ID for correlation and tracking
     */
    private String jobId;
    
    /**
     * Transaction ID for tracking purposes
     */
    private String tid;
}
