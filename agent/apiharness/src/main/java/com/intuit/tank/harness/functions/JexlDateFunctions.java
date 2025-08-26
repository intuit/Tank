/*
 * Copyright 2012 Intuit Inc. All Rights Reserved
 */

package com.intuit.tank.harness.functions;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.common.util.ExpressionContextVisitor;

/**
 * JexlDateFunctions functions that operate on dates.
 * 
 * @author rchalmela
 */
public class JexlDateFunctions implements ExpressionContextVisitor {
    private static final Logger LOG = LogManager.getLogger(JexlDateFunctions.class);

    // Predefined UTC format constants for common use cases
    private static final Map<String, String> UTC_FORMATS = new HashMap<>();
    static {
        UTC_FORMATS.put("ISO_UTC", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        UTC_FORMATS.put("ISO_UTC_NO_MS", "yyyy-MM-dd'T'HH:mm:ss'Z'");
        UTC_FORMATS.put("ISO_UTC_OFFSET", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        UTC_FORMATS.put("ISO_UTC_DATE", "yyyy-MM-dd");
        UTC_FORMATS.put("ISO_UTC_TIME", "HH:mm:ss.SSS'Z'");
        UTC_FORMATS.put("RFC_1123", "EEE, dd MMM yyyy HH:mm:ss zzz");
    }

    /**
     * Get the current date.
     * 
     * @param format
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @param timezone
     *            the timeZone that the date should be formatted in.
     * 
     * @return The current date
     */
    public String currentDate(String format, String timezone) {
        DateFormat formatter = getFormatter(format);
        if (StringUtils.isNotBlank(timezone)) {
            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        return formatter.format(new Date());
    }

    /**
     * Get the current date.
     * 
     * @param format
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @return The current date
     */
    public String currentDate(String format) {
        DateFormat formatter = getFormatter(format);
        return formatter.format(new Date());
    }

    /**
     * Get the current date and time (local timezone).
     *
     * @param format
     *            The format of the response (yyyy-MM-dd HH:mm:ss, etc)
     * @return The current date and time
     */
    public String currentDateTime(String format) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat formatter = getFormatter(format);
        return formatter.format(new Date());
    }

    /**
     * Get the current date and time in a specific timezone.
     * 
     * @param format
     *            The format of the response (yyyy-MM-dd HH:mm:ss, etc)
     * @param timezone
     *            the timeZone that the date should be formatted in.
     * @return The current date and time
     */
    public String currentDateTime(String format, String timezone) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat formatter = getFormatter(format);
        if (StringUtils.isNotBlank(timezone)) {
            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        return formatter.format(new Date());
    }

    /**
     * Get the current UTC date and time.
     *
     * @param format
     *            The format of the response (ISO_UTC, ISO_UTC_NO_MS, or custom format)
     * @return The current UTC date and time
     */
    public String currentUTC(String format) {
        try {
            // Handle predefined format constants
            if (UTC_FORMATS.containsKey(format)) {
                format = UTC_FORMATS.get(format);
            }

            // Default to ISO 8601 UTC format if no format specified
            if (StringUtils.isEmpty(format)) {
                return Instant.now().toString(); // Returns: 2025-08-18T22:11:28.600Z
            }

            // Use modern java.time API for UTC formatting
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return ZonedDateTime.now(ZoneOffset.UTC).format(formatter);
            
        } catch (Exception e) {
            LOG.warn(LogUtil.getLogMessage("Error parsing UTC date format string: " + e.toString()
                    + ". Using default ISO format.", LogEventType.System));
            return Instant.now().toString();
        }
    }

    /**
     * Get the current UTC date and time (no parameters).
     *
     * @return The current UTC date and time in ISO format
     */
    public String currentUTC() {
        return Instant.now().toString();
    }

    /**
     * Gets the current Time in miliseconds since January 1, 1970. (Unix epoch time)
     * 
     * @return the time.
     */
    public String currentTimeMilis() {
        return Long.toString(System.currentTimeMillis());
    }

    /**
     * Gets DateFormat based on the string input
     *
     * @param format
     * @return the correct DateFormat
     */
    private DateFormat getFormatter(String format) {
        DateFormat formatter = null;
        try {
            formatter = StringUtils.isEmpty(format) ? DateFormat.getDateInstance() : new SimpleDateFormat(format);
        } catch (Exception e) {
            // bad format
            LOG.warn(LogUtil.getLogMessage("Error parsing date format string: " + e.toString()
                    + ". Using default format.", LogEventType.System));
            formatter = DateFormat.getDateInstance();
        }
        return formatter;
    }

    /**
     * Get the date x days from now.
     * 
     * @param odays
     *            The number of days to add (negative number to remove)
     * @param format
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @return The new date
     */
    public String addDays(Object odays, String format) {
        int days = FunctionHandler.getInt(odays);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, days);
        DateFormat formatter = getFormatter(format);
        return formatter.format(now.getTime());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intuit.tank.common.util.ExpressionContextVisitor#visit(org.apache.commons.jexl2.JexlContext)
     */
    @Override
    public void visit(JexlContext context) {
        context.set("dateFunctions", this);
    }

}
