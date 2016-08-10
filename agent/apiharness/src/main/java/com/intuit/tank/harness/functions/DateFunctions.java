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
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.sun.istack.Nullable;

/**
 * 
 * DateFunctions functions that operate on dates. Functions start with #function and use dot notation to pass
 * parameters. The first parameter is the class of function. Valid values are:
 * <ul>
 * <li>currentdate - @see {@link DateFunctions#getCurrentDate(String)}</li>
 * <li>adddays - @see {@link DateFunctions#addDays(String, int)}</li>
 * </ul>
 * 
 * @author dangleton
 * 
 */
class DateFunctions {
    private static final Logger LOG = LogManager.getLogger(DateFunctions.class);

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
            } else if (values[2].equalsIgnoreCase("adddays")) {
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
            else if (values[2].equalsIgnoreCase("adddays"))
                return DateFunctions.addDays(Integer.valueOf(values[3]), values[4]);
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get the current date.
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
     * Get the date x days from now.
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

}
