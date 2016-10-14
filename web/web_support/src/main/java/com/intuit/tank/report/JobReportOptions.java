/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.report;

/*
 * #%L
 * JSF Support Beans
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JobReport
 * 
 * @author dangleton
 * 
 */
@Named
public class JobReportOptions implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(JobReportOptions.class);

    private Date startDate;
    private Date endDate;
    private String startTimeString;
    private String endTimeString;

    private String projectNameMatch;
    private String durationStart;
    private String durationEnd;
    private String minUsers;
    private String maxUsers;
    private String jobIdStart;
    private String jobIdEnd;

    private DateFormat tf = new SimpleDateFormat("hh:mm");

    public Date getStartTime() {
        Date ret = null;
        if (startDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            if (StringUtils.isNotBlank(startTimeString)) {
                try {
                    Calendar parsed = Calendar.getInstance();
                    parsed.setTime(tf.parse(startTimeString));
                    cal.set(Calendar.HOUR_OF_DAY, parsed.get(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, parsed.get(Calendar.MINUTE));
                } catch (ParseException e) {
                    LOG.warn("Could not parse time string " + startTimeString);
                }
            }
            ret = cal.getTime();
        }
        return ret;
    }

    public Date getEndTime() {
        Date ret = null;
        if (endDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            if (StringUtils.isNotBlank(endTimeString)) {
                try {
                    Calendar parsed = Calendar.getInstance();
                    parsed.setTime(tf.parse(endTimeString));
                    cal.set(Calendar.HOUR_OF_DAY, parsed.get(Calendar.HOUR_OF_DAY));
                    cal.set(Calendar.MINUTE, parsed.get(Calendar.MINUTE));
                } catch (ParseException e) {
                    LOG.warn("Could not parse time string " + startTimeString);
                }
            }
            ret = cal.getTime();
        }
        return ret;
    }

    /**
     * @return the startTimeString
     */
    public String getStartTimeString() {
        return startTimeString;
    }

    /**
     * @param startTimeString
     *            the startTimeString to set
     */
    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public String getJobIdStart() {
        return jobIdStart;
    }

    public void setJobIdStart(String jobIdStart) {
        this.jobIdStart = StringUtils.trim(jobIdStart);
    }

    public String getJobIdEnd() {
        return jobIdEnd;
    }

    public void setJobIdEnd(String jobIdEnd) {
        this.jobIdEnd = StringUtils.trim(jobIdEnd);
    }

    /**
     * @return the endTimeString
     */
    public String getEndTimeString() {
        return endTimeString;
    }

    /**
     * @param endTimeString
     *            the endTimeString to set
     */
    public void setEndTimeString(String endTimeString) {
        this.endTimeString = endTimeString;
    }

    /**
     * @return the minUsers
     */
    public String getMinUsers() {
        return minUsers;
    }

    /**
     * @param minUsers
     *            the minUsers to set
     */
    public void setMinUsers(String minUsers) {
        this.minUsers = StringUtils.trim(minUsers);
    }

    /**
     * @return the maxUsers
     */
    public String getMaxUsers() {
        return maxUsers;
    }

    /**
     * @param maxUsers
     *            the maxUsers to set
     */
    public void setMaxUsers(String maxUsers) {
        this.maxUsers = StringUtils.trim(maxUsers);
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the projectNameMatch
     */
    public String getProjectNameMatch() {
        return projectNameMatch;
    }

    /**
     * @param projectNameMatch
     *            the projectNameMatch to set
     */
    public void setProjectNameMatch(String projectNameMatch) {
        this.projectNameMatch = projectNameMatch;
    }

    /**
     * @return the durationStart
     */
    public String getDurationStart() {
        return durationStart;
    }

    /**
     * @param durationStart
     *            the durationStart to set
     */
    public void setDurationStart(String durationStart) {
        this.durationStart = durationStart;
    }

    /**
     * @return the durationEnd
     */
    public String getDurationEnd() {
        return durationEnd;
    }

    /**
     * @param durationEnd
     *            the durationEnd to set
     */
    public void setDurationEnd(String durationEnd) {
        this.durationEnd = durationEnd;
    }

}
