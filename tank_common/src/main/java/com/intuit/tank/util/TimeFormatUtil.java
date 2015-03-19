/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

/*
 * #%L
 * Common
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
import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * TimeUtil
 * 
 * @author dangleton
 * 
 */
public final class TimeFormatUtil {

    private static final int TIME_60 = 60;

    private static final int TIME_3600 = 3600;

    private static final FastDateFormat DF = FastDateFormat.getInstance("HH:mm:ss", TimeZone.getTimeZone("GMT"));

    private static NumberFormat nf;
    static {
        nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        nf.setMaximumIntegerDigits(2);
    }

    /**
     * private no arg constructor to implement util lpattern
     */
    private TimeFormatUtil() {
        // private
    }

    public static String formatTime(int numSeconds) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTimeInMillis(0);
        c.add(Calendar.SECOND, numSeconds);
        return DF.format(c);
    }

    /**
     * @return the simulation time in the following format hh:mm:ss
     */
    public static String getFormattedDuration(int simTimeSeconds) {
        int hours = simTimeSeconds / TIME_3600;
        simTimeSeconds = simTimeSeconds - (hours * TIME_3600);
        int minutes = simTimeSeconds / TIME_60;
        simTimeSeconds = simTimeSeconds - (minutes * TIME_60);
        int seconds = simTimeSeconds;
        return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
    }

    /**
     * Sets the simulation time for the job
     * 
     * @param formattedTime
     *            the simulation time is in the format hh:mm:ss
     */
    public static int parseFormattedDuration(@Nonnull String formattedTime) throws NumberFormatException {
        String[] times = formattedTime.split(":");
        int time = 0;
        if (times.length > 0) {
            time += Integer.parseInt(times[0]) * TIME_3600;
        }
        if (times.length > 1) {
            time += Integer.parseInt(times[1]) * TIME_60;
        }
        if (times.length > 2) {
            time += Integer.parseInt(times[2]);
        }
        return time;
    }

}
