/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.spi.StandardLevel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Reads and sets the log level of this controller node. The change lasts until the node restarts or the
 * Log4j configuration is reloaded.
 */
public final class LogLevels {

    private LogLevels() {
    }

    /**
     * @return the standard level names, most to least severe
     */
    public static List<String> names() {
        return Arrays.stream(StandardLevel.values()).map(Enum::name).toList();
    }

    /**
     * @return the level for the given name, ignoring case, or empty when it is not a standard level
     */
    public static Optional<Level> parse(String name) {
        if (name == null) {
            return Optional.empty();
        }
        Level level = Level.getLevel(name.trim().toUpperCase());
        return level != null && names().contains(level.name()) ? Optional.of(level) : Optional.empty();
    }

    /**
     * @return the root logger's level
     */
    public static Level current() {
        return context().getConfiguration().getRootLogger().getLevel();
    }

    /**
     * Sets every configured logger, including the root logger, to the level.
     */
    public static void set(Level level) {
        LoggerContext ctx = context();
        Configuration config = ctx.getConfiguration();
        config.getRootLogger().setLevel(level);
        config.getLoggers().values().forEach(loggerConfig -> loggerConfig.setLevel(level));
        // loggers keep the old level until told the configuration changed
        ctx.updateLoggers();
    }

    private static LoggerContext context() {
        return (LoggerContext) LogManager.getContext(false);
    }
}
