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

import org.apache.log4j.Logger;

import com.intuit.tank.vm.settings.TimeUtil;

/**
 * MethodTimer
 * 
 * @author dangleton
 * 
 */
public class MethodTimer {

    public static final long MS_CONV_FACTOR = 0xf4240L;
    private Logger log;
    private String methodName;
    private long start;
    private long end;
    private long mark;
    private long prevMark;
    private int numMarks;

    /**
     * 
     * @param log
     * @param clazz
     * @param methodName
     */
    @SuppressWarnings("rawtypes")
    public MethodTimer(Logger log, Class clazz, String methodName) {
        this.log = log;
        this.methodName = methodName == null ? "" : methodName;
        start = System.nanoTime();
    }

    public MethodTimer start() {
        start = System.nanoTime();
        return this;
    }

    public MethodTimer end() {
        end = System.nanoTime();
        return this;
    }

    public MethodTimer mark() {
        prevMark = mark == 0L ? start : mark;
        numMarks++;
        mark = System.nanoTime();
        return this;
    }

    private long elapsedToMs(long elapsed) {
        return elapsed / 0xf4240L;
    }

    public String getMarkTimeMessage(String message) {
        return (new StringBuilder(String.valueOf(methodName))).append(" :: ")
                .append(message == null ? "" : message).append(" took ")
                .append(elapsedToMs(mark - prevMark)).append(" ms.").toString();
    }

    public String getTimeMessage() {
        if (end == 0L)
            end();
        return (new StringBuilder(String.valueOf(methodName))).append(" took ")
                .append(elapsedToMs(end - start)).append(" ms.").toString();
    }

    public String getNaturalTimeMessage() {
        if (end == 0L)
            end();
        return (new StringBuilder(String.valueOf(methodName))).append(" took ")
                .append(TimeUtil.toTimeString(elapsedToMs(end - start))).toString();
    }

    public MethodTimer logMark(String message) {
        if (log != null) {
            log.info(getMarkTimeMessage(message));
        } else {
            System.out.println(getMarkTimeMessage(message));
        }
        return this;
    }

    public MethodTimer markAndLog(String message) {
        mark();
        logMark(message);
        return this;
    }

    public MethodTimer markAndLog() {
        mark();
        logMark((new StringBuilder("Mark ("))
                .append(Integer.toString(numMarks)).append(")").toString());
        return this;
    }

    public MethodTimer logTime() {
        if (log != null) {
            log.info(getTimeMessage());
        } else {
            System.out.println(getTimeMessage());
        }
        return this;
    }

    public MethodTimer endAndLog() {
        end();
        logTime();
        return this;
    }
}
