package com.intuit.tank.vm.settings;

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

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class with utility methods for time related operations.
 * 
 * @author etirelli
 */
public class TimeUtil {

    // Simple syntax
    private static final Pattern SIMPLE = Pattern
            .compile("([+-])?((\\d+)[Dd])?\\s*((\\d+)[Hh])?\\s*((\\d+)[Mm])?\\s*((\\d+)[Ss])?\\s*((\\d+)([Mm][Ss])?)?");
    private static final int SIM_SGN = 1;
    private static final int SIM_DAY = 3;
    private static final int SIM_HOU = 5;
    private static final int SIM_MIN = 7;
    private static final int SIM_SEC = 9;
    private static final int SIM_MS = 11;

    // ISO 8601 compliant
    // private static final Pattern ISO8601 = Pattern.compile(
    // "(P((\\d+)[Yy])?((\\d+)[Mm])?((\\d+)[Dd])?)?(T((\\d+)[Hh])?((\\d+)[Mm])?((\\d+)[Ss])?((\\d+)([Mm][Ss])?)?)?" );

    private static final long SEC_MS = 1000;
    private static final long MIN_MS = 60 * SEC_MS;
    private static final long HOU_MS = 60 * MIN_MS;
    private static final long DAY_MS = 24 * HOU_MS;

    // private static final long MON_MS = 30 * DAY_MS;
    // private static final long YEA_MS = 365 * DAY_MS;

    /**
     * Parses the given time String and returns the corresponding time in milliseconds
     * 
     * @param time
     * @return
     * 
     * @throws NullPointerException
     *             if time is null
     */
    public static long parseTimeString(String time) {
        String trimmed = time.trim();
        long result = 0;
        if (trimmed.length() > 0) {
            Matcher mat = SIMPLE.matcher(trimmed);
            if (mat.matches()) {
                int days = (mat.group(SIM_DAY) != null) ? Integer.parseInt(mat.group(SIM_DAY)) : 0;
                int hours = (mat.group(SIM_HOU) != null) ? Integer.parseInt(mat.group(SIM_HOU)) : 0;
                int min = (mat.group(SIM_MIN) != null) ? Integer.parseInt(mat.group(SIM_MIN)) : 0;
                int sec = (mat.group(SIM_SEC) != null) ? Integer.parseInt(mat.group(SIM_SEC)) : 0;
                int ms = (mat.group(SIM_MS) != null) ? Integer.parseInt(mat.group(SIM_MS)) : 0;
                long r = days * DAY_MS + hours * HOU_MS + min * MIN_MS + sec * SEC_MS + ms;
                if (mat.group(SIM_SGN) != null && mat.group(SIM_SGN).equals("-")) {
                    r = -r;
                }
                result = r;
            } else if ("*".equals(trimmed) || "+*".equals(trimmed)) {
                // positive infinity
                result = Long.MAX_VALUE;
            } else if ("-*".equals(trimmed)) {
                // negative infinity
                result = Long.MIN_VALUE;
            } else {
                throw new RuntimeException("Error parsing time string: [ " + time + " ]");
            }
        }
        return result;
    }

    public static String toTimeString(long millis) {
        int ms = (int) (millis % 1000);
        int sec = (int) (Math.floor(millis / 1000)) % 60;
        int min = (int) (Math.floor(millis / (1000 * 60))) % 60;
        int hours = (int) (Math.floor(millis / (1000 * 60 * 60))) % 24;
        int days = (int) (Math.floor(millis / (1000 * 60 * 60 * 24)));
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d");
        }
        if (hours > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(hours).append("h");
        }
        if (min > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(min).append("m");
        }
        if (sec > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(sec).append("s");
        }
        if (ms > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(ms).append("ms");
        }
        if (sb.length() == 0) {
            sb.append('0');
        }
        return sb.toString();
    }

    public static final Date normalizeToPeriod(int period, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MILLISECOND, 0);
        int seconds = c.get(Calendar.SECOND);
        seconds -= seconds % period;
        c.set(Calendar.SECOND, seconds);
        return c.getTime();
    }

}
