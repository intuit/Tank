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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "timing_summary",
        indexes = { @Index(name = "IDX_TS_JOB_ID", columnList = "job_id"),
                    @Index(name = "IDX_TS_PAGE_ID", columnList = "page_id")})
public class SummaryData extends BaseEntity implements Comparable<SummaryData> {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_JOB_ID = "jobId";
    public static final String PROPERTY_PAGE_ID = "pageId";

    @Column(name = "job_id", nullable = false, updatable = false)
    private int jobId;

    @Column(name = "page_id", nullable = false, updatable = false)
    private String pageId;

    @Column(name = "sample_size", nullable = false, updatable = false)
    private int sampleSize;

    @Column(name = "mean", nullable = false, updatable = false)
    private double mean;

    @Column(name = "min", nullable = false, updatable = false)
    private double min;

    @Column(name = "max", nullable = false, updatable = false)
    private double max;

    @Column(name = "std_dev", nullable = false, updatable = false)
    private double sttDev;

    @Column(name = "kurtosis", nullable = false, updatable = false)
    private double kurtosis;

    @Column(name = "skewness", nullable = false, updatable = false)
    private double skewness;

    @Column(name = "varience", nullable = false, updatable = false)
    private double varience;

    @Column(name = "percentile_10", nullable = false, updatable = false)
    private double percentile10;

    @Column(name = "percentile_20", nullable = false, updatable = false)
    private double percentile20;

    @Column(name = "percentile_30", nullable = false, updatable = false)
    private double percentile30;

    @Column(name = "percentile_40", nullable = false, updatable = false)
    private double percentile40;

    @Column(name = "percentile_50", nullable = false, updatable = false)
    private double percentile50;

    @Column(name = "percentile_60", nullable = false, updatable = false)
    private double percentile60;

    @Column(name = "percentile_70", nullable = false, updatable = false)
    private double percentile70;

    @Column(name = "percentile_80", nullable = false, updatable = false)
    private double percentile80;

    @Column(name = "percentile_90", nullable = false, updatable = false)
    private double percentile90;

    @Column(name = "percentile_95", nullable = false, updatable = false)
    private double percentile95;

    @Column(name = "percentile_99", nullable = false, updatable = false)
    private double percentile99;

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
     * @return the sttDev
     */
    public double getSttDev() {
        return sttDev;
    }

    /**
     * @param sttDev
     *            the sttDev to set
     */
    public void setSttDev(double sttDev) {
        this.sttDev = sttDev;
    }

    /**
     * @return the kurtosis
     */
    public double getKurtosis() {
        return kurtosis;
    }

    /**
     * @param kurtosis
     *            the kurtosis to set
     */
    public void setKurtosis(double kurtosis) {
        this.kurtosis = kurtosis;
    }

    /**
     * @return the skewness
     */
    public double getSkewness() {
        return skewness;
    }

    /**
     * @param skewness
     *            the skewness to set
     */
    public void setSkewness(double skewness) {
        this.skewness = skewness;
    }

    /**
     * @return the varience
     */
    public double getVarience() {
        return varience;
    }

    /**
     * @param varience
     *            the varience to set
     */
    public void setVarience(double varience) {
        this.varience = varience;
    }

    /**
     * @return the percentile10
     */
    public double getPercentile10() {
        return percentile10;
    }

    /**
     * @param percentile10
     *            the percentile10 to set
     */
    public void setPercentile10(double percentile10) {
        this.percentile10 = percentile10;
    }

    /**
     * @return the percentile20
     */
    public double getPercentile20() {
        return percentile20;
    }

    /**
     * @param percentile20
     *            the percentile20 to set
     */
    public void setPercentile20(double percentile20) {
        this.percentile20 = percentile20;
    }

    /**
     * @return the percentile30
     */
    public double getPercentile30() {
        return percentile30;
    }

    /**
     * @param percentile30
     *            the percentile30 to set
     */
    public void setPercentile30(double percentile30) {
        this.percentile30 = percentile30;
    }

    /**
     * @return the percentile40
     */
    public double getPercentile40() {
        return percentile40;
    }

    /**
     * @param percentile40
     *            the percentile40 to set
     */
    public void setPercentile40(double percentile40) {
        this.percentile40 = percentile40;
    }

    /**
     * @return the percentile50
     */
    public double getPercentile50() {
        return percentile50;
    }

    /**
     * @param percentile50
     *            the percentile50 to set
     */
    public void setPercentile50(double percentile50) {
        this.percentile50 = percentile50;
    }

    /**
     * @return the percentile60
     */
    public double getPercentile60() {
        return percentile60;
    }

    /**
     * @param percentile60
     *            the percentile60 to set
     */
    public void setPercentile60(double percentile60) {
        this.percentile60 = percentile60;
    }

    /**
     * @return the percentile70
     */
    public double getPercentile70() {
        return percentile70;
    }

    /**
     * @param percentile70
     *            the percentile70 to set
     */
    public void setPercentile70(double percentile70) {
        this.percentile70 = percentile70;
    }

    /**
     * @return the percentile80
     */
    public double getPercentile80() {
        return percentile80;
    }

    /**
     * @param percentile80
     *            the percentile80 to set
     */
    public void setPercentile80(double percentile80) {
        this.percentile80 = percentile80;
    }

    /**
     * @return the percentile90
     */
    public double getPercentile90() {
        return percentile90;
    }

    /**
     * @param percentile90
     *            the percentile90 to set
     */
    public void setPercentile90(double percentile90) {
        this.percentile90 = percentile90;
    }

    /**
     * @return the percentile95
     */
    public double getPercentile95() {
        return percentile95;
    }

    /**
     * @param percentile95
     *            the percentile95 to set
     */
    public void setPercentile95(double percentile95) {
        this.percentile95 = percentile95;
    }

    /**
     * @return the percentile99
     */
    public double getPercentile99() {
        return percentile99;
    }

    /**
     * @param percentile99
     *            the percentile99 to set
     */
    public void setPercentile99(double percentile99) {
        this.percentile99 = percentile99;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(SummaryData o) {
        return new CompareToBuilder().append(pageId, o.pageId).toComparison();
    }

}
