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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.common.util.ExpressionContextVisitor;

/**
 * JexlDateFunctions functions that operate on dates.
 * 
 * @author rchalmela
 */
public class JexlDateFunctions implements ExpressionContextVisitor {
    private static final Logger LOG = Logger.getLogger(DateFunctions.class);

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
     * @param days
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
