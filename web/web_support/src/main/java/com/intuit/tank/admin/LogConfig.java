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

import java.util.Collection;

import javax.inject.Named;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * LogConfig
 * 
 * @author dangleton
 * 
 */
@Named
public class LogConfig {

    private static final Logger LOG = LogManager.getLogger(LogConfig.class);

    public void setLogLevel(String level) {
        Level l = Level.toLevel(level);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        Collection<LoggerConfig> loggerConfigs = config.getLoggers().values();
        for ( LoggerConfig loggerConfig : loggerConfigs ) {
        	loggerConfig.setLevel(l);
        }
        LOG.debug("Log level changed to " + l);
        LOG.info("Log level changed to " + l);
    }
}
