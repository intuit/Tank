package com.intuit.tank.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * Request model for user data export operations
 */
public class ExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("userIdentifier")
    @NotBlank(message = "User identifier is required")
    private String userIdentifier;

    /**
     * Default constructor
     */
    public ExportRequest() {
    }

    /**
     * Constructor with user identifier
     * @param userIdentifier the user identifier (email or username)
     */
    public ExportRequest(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    /**
     * Gets the user identifier
     * @return the user identifier
     */
    public String getUserIdentifier() {
        return userIdentifier;
    }

    /**
     * Sets the user identifier
     * @param userIdentifier the user identifier
     */
    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    @Override
    public String toString() {
        return "ExportRequest{" +
                "userIdentifier='" + userIdentifier + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportRequest that = (ExportRequest) o;

        return userIdentifier != null ? userIdentifier.equals(that.userIdentifier) : that.userIdentifier == null;
    }

    @Override
    public int hashCode() {
        return userIdentifier != null ? userIdentifier.hashCode() : 0;
    }
} 