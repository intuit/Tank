/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common.util;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.intuit.tank.vm.settings.TankConfig;

/**
 * ReportUtil
 * 
 * @author dangleton
 * 
 */
public class ReportUtil {

    private static final String[] SUMMARY_HEADERS = {"Page ID", "Page Name", "Page Index", 
        "Sample Size", "Mean",
        "Median", "Min", "Max",
        "Std Dev",
        "Kurtosis", "Skewness", "Varience" };

    public static final String[] BUCKET_HEADERS = {"Job ID", "Page ID", "Page Name", 
        "Page Index", "Sample Size",
        "Average", "Min", "Max",
        "Period", "Start Time" };

    public static final String JOB_ID_KEY = "JobId";
    public static final String DATE_FORMAT = "yyyy.MM.dd-HH:mm:ss.S z"; // for timestamps
    public static final String LOGGING_KEY = "LoggingKey";

    private static final Object[][] PERCENTILES = {
            { "10th Percentile", 10 },
            { "20th Percentile", 20 },
            { "30th Percentile", 30 },
            { "40th Percentile", 40 },
            { "50th Percentile", 50 },
            { "60th Percentile", 60 },
            { "70th Percentile", 70 },
            { "80th Percentile", 80 },
            { "90th Percentile", 90 },
            { "95th Percentile", 95 },
            { "99th Percentile", 99 }

    };

    public static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

    public static final NumberFormat DOUBLE_NF = NumberFormat.getInstance();

    public static final String PAGE_NAME_SEPERATOR = ":||:";
    static {
        DOUBLE_NF.setMinimumFractionDigits(2);
        DOUBLE_NF.setMaximumFractionDigits(2);
        DOUBLE_NF.setGroupingUsed(false);
        INT_NF.setGroupingUsed(false);
    }

    public static final String[] getSummaryHeaders() {
        List<String> l = new ArrayList<String>(SUMMARY_HEADERS.length + PERCENTILES.length);
        l.addAll(Arrays.asList(SUMMARY_HEADERS));
        for (int n = 0; n < PERCENTILES.length; n++) {
            l.add((String) PERCENTILES[n][0]);
        }
        return l.toArray(new String[l.size()]);
    }

    public static final String[] getSummaryData(String key, DescriptiveStatistics stats) {
        String[] ret = new String[ReportUtil.SUMMARY_HEADERS.length + PERCENTILES.length];
        int i = 0;
        ret[i++] = key;// Page ID
        ret[i++] = INT_NF.format(stats.getN());// Sample Size
        ret[i++] = DOUBLE_NF.format(stats.getMean());// Mean
        ret[i++] = INT_NF.format(stats.getPercentile(50));// Meadian
        ret[i++] = INT_NF.format(stats.getMin());// Min
        ret[i++] = INT_NF.format(stats.getMax());// Max
        ret[i++] = DOUBLE_NF.format(stats.getStandardDeviation());// Std Dev
        ret[i++] = DOUBLE_NF.format(stats.getKurtosis());// Kurtosis
        ret[i++] = DOUBLE_NF.format(stats.getSkewness());// Skewness
        ret[i++] = DOUBLE_NF.format(stats.getVariance());// Varience
        for (int n = 0; n < PERCENTILES.length; n++) {
            ret[i++] = INT_NF.format(stats.getPercentile((Integer) PERCENTILES[n][1]));// Percentiles
        }
        return ret;
    }

    /**
     * 
     * @param jobId
     * @return
     */
    public static final String getSummaryTableName(String jobId) {
        return "timing_summary_" + new TankConfig().getInstanceName();
    }

    /**
     * 
     * @param jobId
     * @return
     */
    public static final String getBucketedTableName(String jobId) {
        return "timing_bucket_" + new TankConfig().getInstanceName();
    }

    public static String getTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        return sdf.format(date);
    }

    public static Date parseTimestamp(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
        return sdf.parse(dateString);
    }

}
