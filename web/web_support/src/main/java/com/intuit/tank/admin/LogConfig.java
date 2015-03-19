/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.admin;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Enumeration;

import javax.inject.Named;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * LogConfig
 * 
 * @author dangleton
 * 
 */
@Named
public class LogConfig {

    private static final Logger LOG = Logger.getLogger(LogConfig.class);

    public void setLogLevel(String level) {
        Level l = Level.toLevel(level);
        @SuppressWarnings("unchecked") Enumeration<Logger> currentLoggers = LogManager.getCurrentLoggers();
        LogManager.getRootLogger().setLevel(l);
        while (currentLoggers.hasMoreElements()) {
            Logger nextElement = currentLoggers.nextElement();
            // Level originalLevel = nextElement.getLevel();
            nextElement.setLevel(l);
            // LOG.debug("setting level on logger " + nextElement.getName() + " from " + originalLevel + " to " + l);
        }
        LOG.debug("Log level changed to " + l);
        LOG.info("Log level changed to " + l);
    }
}
