package com.intuit.tank.results;

/*
 * #%L
 * Reporting API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * WatsResult represents a result to log or store to database.
 * 
 * @author dangleton
 * 
 */
public class TankResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private String jobId;
    private int responseTime;
    private int statusCode;
    private int responseSize;
    private String requestName;
    private boolean error;
    private Date timeStamp = new Date();

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int connectionTime) {
        this.responseTime = connectionTime;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestName() {
        return requestName;
    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp
     *            the timeStamp to set
     */
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode
     *            the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void add(TankResult result) {
        responseTime += result.getResponseTime();
        responseSize += result.getResponseSize();
        timeStamp = result.getTimeStamp();
        if (statusCode <= 0) {
            statusCode = result.getStatusCode();
        }
        if (result.error) {
            error = result.error;
        }
    }

}
