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

import jakarta.inject.Named;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.rest.mvc.rest.util.LogLevels;

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
        LogLevels.set(l);
        LOG.debug("Log level changed to {}", l);
        LOG.info("Log level changed to {}", l);
    }
}
