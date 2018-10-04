/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.harness.logging;

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

import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import org.apache.logging.log4j.message.ObjectMessage;

/**
 * LogUtil
 * 
 * @author dangleton
 * 
 */
public final class LogUtil {

    private static final Logger LOG = LogManager.getLogger(LogUtil.class);

    private static ThreadLocalLogEvent logEventProvider = new ThreadLocalLogEvent();

    private LogUtil() {
    }

    public static final LogEvent getLogEvent() {
        return logEventProvider.get();
    }

    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public static final ObjectMessage getLogMessage(String msg) {
        return getLogMessage(msg, null);
    }

    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public static final ObjectMessage getLogMessage(String msg, LogEventType type) {
        LogEvent logEvent = getLogEvent();
        logEvent.setMessage(msg);
        logEvent.setEventType(type != null ? type : LogEventType.System);
        return new ObjectMessage(logEvent.buildMessage());
    }

    /**
     * Returns the message to log. will prepend the jobId to the log message in the form of jobId[id]:
     * 
     * @param msg
     * @return
     */
    public static final ObjectMessage getLogMessage(String msg, LogEventType type, LoggingProfile profile) {
        LogEvent logEvent = getLogEvent();
        LoggingProfile resetProfile = logEvent.getActiveProfile();
        if (null != profile) {
            logEvent.setActiveProfile(profile);
        }
        ObjectMessage map = getLogMessage(msg, type);
        logEvent.setActiveProfile(resetProfile);
        return map;
    }

    /**
     * Returns true if the argument is a valid mimeType else returns false
     * 
     * @param mimeType
     * @return
     */
    public static boolean isTextMimeType(String mimeType) {
        if (mimeType != null) {
            try {
                Collection<String> validMimeTypeRegex = APITestHarness.getInstance().getTankConfig()
                        .getAgentConfig()
                        .getTextMimeTypeRegex();
                for (String regex : validMimeTypeRegex) {
                    if (mimeType.matches(regex)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                LOG.warn(e.toString());
            }
        }
        return false;
    }

}
