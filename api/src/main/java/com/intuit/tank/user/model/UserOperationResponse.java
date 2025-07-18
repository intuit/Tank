package com.intuit.tank.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;

/**
 * Response model for user operations (export/delete)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserOperationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("jobId")
    private String jobId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Map<String, Object> data;

    @JsonProperty("recordsAffected")
    private Long recordsAffected;

    /**
     * Default constructor
     */
    public UserOperationResponse() {
    }

    /**
     * Constructor with job ID and status
     * @param jobId the job ID
     * @param status the operation status
     */
    public UserOperationResponse(String jobId, String status) {
        this.jobId = jobId;
        this.status = status;
    }

    /**
     * Constructor with all fields
     * @param jobId the job ID
     * @param status the operation status
     * @param message the status message
     * @param data the operation data (for export)
     * @param recordsAffected the number of records affected (for delete)
     */
    public UserOperationResponse(String jobId, String status, String message, 
                                Map<String, Object> data, Long recordsAffected) {
        this.jobId = jobId;
        this.status = status;
        this.message = message;
        this.data = data;
        this.recordsAffected = recordsAffected;
    }

    /**
     * Gets the job ID
     * @return the job ID
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the job ID
     * @param jobId the job ID
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets the status
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the message
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the data
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Sets the data
     * @param data the data
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Gets the records affected count
     * @return the records affected count
     */
    public Long getRecordsAffected() {
        return recordsAffected;
    }

    /**
     * Sets the records affected count
     * @param recordsAffected the records affected count
     */
    public void setRecordsAffected(Long recordsAffected) {
        this.recordsAffected = recordsAffected;
    }

    @Override
    public String toString() {
        return "UserOperationResponse{" +
                "jobId='" + jobId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", recordsAffected=" + recordsAffected +
                '}';
    }
} 