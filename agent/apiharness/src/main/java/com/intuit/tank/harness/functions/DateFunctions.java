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

import jakarta.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * DateFunctions functions that operate on dates. Functions start with #function and use dot notation to pass
 * parameters. The first parameter is the class of function. Valid values are:
 * <ul>
 * <li>currentdate - @see {@link DateFunctions#getCurrentDate(String)}</li>
 * <li>currentdatetime - @see {@link DateFunctions#getCurrentDateTime(String)}</li>
 * <li>currentutc - @see {@link DateFunctions#getCurrentUTC(String)}</li>
 * <li>adddays - @see {@link DateFunctions#addDays(String, int)}</li>
 * <li>adddaysutc - @see {@link DateFunctions#addDaysUTC(String, int)}</li>
 * </ul>
 * 
 * @author dangleton
 * 
 */
class DateFunctions {
    private static final Logger LOG = LogManager.getLogger(DateFunctions.class);

    // UTC format constants
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
     * Is this a valid Date function request
     * 
     * @param values
     *            The command
     * @return TRUE if valid format; FALSE otherwise
     */
    static public boolean isValid(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("currentdate")) {
                return true;
            } else if (values[2].equalsIgnoreCase("currentdatetime")) {
                return true;
            } else if (values[2].equalsIgnoreCase("currentutc")) {
                return true;
            } else if (values[2].equalsIgnoreCase("adddays")) {
                if (!StringUtils.isEmpty(values[3])) {
                    return true;
                }
            } else if (values[2].equalsIgnoreCase("adddaysutc")) {
                if (!StringUtils.isEmpty(values[3])) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Process the date request
     * 
     * @param values
     *            The command line
     * @return The requested value; NULL if there was an error
     * 
     */
    static public String executeFunction(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("currentdate"))
                return DateFunctions.getCurrentDate(values[3]);
            else if (values[2].equalsIgnoreCase("currentdatetime"))
                return DateFunctions.getCurrentDateTime(values[3]);
            else if (values[2].equalsIgnoreCase("currentutc"))
                return DateFunctions.getCurrentUTC(values[3]);
            else if (values[2].equalsIgnoreCase("adddays"))
                return DateFunctions.addDays(Integer.valueOf(values[3]), values[4]);
            else if (values[2].equalsIgnoreCase("adddaysutc"))
                return DateFunctions.addDaysUTC(Integer.valueOf(values[3]), values[4]);
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get the current date (legacy method - local timezone).
     *
     * #function.date.currentDate.06-01-2011
     *
     * @param format
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @return The current date
     */
    private static String getCurrentDate(@Nullable String format) {
        DateFormat formatter = getFormatter(format);
        return formatter.format(new Date());
    }

    /**
     * Get the current date and time (local timezone).
     *
     * #function.date.currentDateTime.yyyy-MM-dd HH:mm:ss
     *
     * @param format
     *            The format of the response (yyyy-MM-dd HH:mm:ss, etc)
     * @return The current date and time
     */
    private static String getCurrentDateTime(@Nullable String format) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat formatter = getFormatter(format);
        return formatter.format(new Date());
    }

    /**
     * Get the current UTC date and time.
     *
     * #function.date.currentUTC.ISO_UTC
     * #function.date.currentUTC.yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     *
     * @param format
     *            The format of the response (ISO_UTC, ISO_UTC_NO_MS, or custom format)
     * @return The current UTC date and time
     */
    private static String getCurrentUTC(@Nullable String format) {
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
            LOG.warn("Error parsing UTC date format string: " + e.toString() + ". Using default ISO format.");
            return Instant.now().toString();
        }
    }

    /**
     * @param format
     * @return
     */
    private static DateFormat getFormatter(String format) {
        DateFormat formatter = null;
        try {
            formatter = StringUtils.isEmpty(format) ? DateFormat.getDateInstance() : new SimpleDateFormat(format);
        } catch (Exception e) {
            // bad format
            LOG.warn("Error parsing date format string: " + e.toString() + ". Using default format.");
            formatter = DateFormat.getDateInstance();
        }
        return formatter;
    }

    /**
     * Get the date x days from now (local timezone).
     *
     * e.g. #function.date.addDays.1.MM-dd-yyyy
     *
     * @param days
     *            The number of days to add (negative number to remove)
     * @param format
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @return The new date
     */
    private static String addDays(int days, @Nullable String format) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, days);
        DateFormat formatter = getFormatter(format);
        return formatter.format(now.getTime());
    }

    /**
     * Get the date x days from now (UTC timezone).
     *
     * e.g. #function.date.addDaysUTC.1.ISO_UTC
     *
     * @param days
     *            The number of days to add (negative number to remove)
     * @param format
     *            The format of the response (ISO_UTC, ISO_UTC_NO_MS, or custom format)
     * @return The new UTC date
     */
    private static String addDaysUTC(int days, @Nullable String format) {
        try {
            // Handle predefined format constants
            if (UTC_FORMATS.containsKey(format)) {
                format = UTC_FORMATS.get(format);
            }

            // Default to ISO 8601 UTC format if no format specified
            if (StringUtils.isEmpty(format)) {
                return Instant.now().plusSeconds(days * 24 * 60 * 60).toString();
            }

            // Use modern java.time API for UTC calculations
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return ZonedDateTime.now(ZoneOffset.UTC)
                    .plusDays(days)
                    .format(formatter);

        } catch (Exception e) {
            LOG.warn("Error parsing UTC date format string: " + e.toString() + ". Using default ISO format.");
            return Instant.now().plusSeconds(days * 24 * 60 * 60).toString();
        }
    }

}
