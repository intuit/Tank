/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.runner.method;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.tools.script.OutputLogger;

/**
 * LoggingOutputLogger
 * 
 * @author dangleton
 * 
 */
public class AgentLoggingOutputLogger implements OutputLogger {
    private static final Logger LOG = LogManager.getLogger(AgentLoggingOutputLogger.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScrollContent(boolean autoScroll) {
        // nothing to do

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(String text) {
        LOG.info(LogUtil.getLogMessage(text, LogEventType.Informational, APITestHarness.getInstance().getAgentRunData()
                .getActiveProfile()));

    }

    @Override
    public void debug(String text) {
        LOG.debug(text);
    }

    @Override
    public void error(String text) {
        LOG.error(LogUtil.getLogMessage(text, LogEventType.Validation, LoggingProfile.VERBOSE));

    }

}
