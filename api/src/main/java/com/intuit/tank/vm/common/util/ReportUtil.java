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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ReportUtil
 * 
 * @author dangleton
 * 
 */
public class ReportUtil {

    public static final String JOB_ID_KEY = "JobId";
    public static final String DATE_FORMAT = "yyyy.MM.dd-HH:mm:ss.S z"; // for timestamps
    public static final String LOGGING_KEY = "LoggingKey";

    public static final String PAGE_NAME_SEPERATOR = ":||:";

    public static String getTimestamp(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static Date parseTimestamp(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.parse(dateString);
    }

}
