package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "timing_periodic")
public class PeriodicData extends BaseEntity implements Comparable<PeriodicData> {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_JOB_ID = "jobId";
    public static final String PROPERTY_PAGE_ID = "pageId";
    public static final String PROPERTY_TIMESTAMP = "timestamp";

    @Column(name = "job_id", nullable = false, updatable = false)
    @Index(name = "IDX_TS_JOB_ID")
    private int jobId;

    @Column(name = "timestamp", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Index(name = "IDX_TS_TIME")
    private Date timestamp;

    @Column(name = "page_id", nullable = false, updatable = false)
    @Index(name = "IDX_TS_PAGE_ID")
    private String pageId;

    @Column(name = "sample_size", nullable = false, updatable = false)
    private int sampleSize;

    @Column(name = "period", nullable = false, updatable = false)
    private int period;

    @Column(name = "mean", nullable = false, updatable = false)
    private double mean;

    @Column(name = "min", nullable = false, updatable = false)
    private double min;

    @Column(name = "max", nullable = false, updatable = false)
    private double max;

    /**
     * @return the jobId
     */
    public int getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the period
     */
    public int getPeriod() {
        return period;
    }

    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * @return the pageId
     */
    public String getPageId() {
        return pageId;
    }

    /**
     * @param pageId
     *            the pageId to set
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * @return the sampleSize
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * @param sampleSize
     *            the sampleSize to set
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @param mean
     *            the mean to set
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @param min
     *            the min to set
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public double getMax() {
        return max;
    }

    /**
     * @param max
     *            the max to set
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int compareTo(PeriodicData o) {
        return new CompareToBuilder().append(timestamp, o.timestamp).toComparison();
    }

}
