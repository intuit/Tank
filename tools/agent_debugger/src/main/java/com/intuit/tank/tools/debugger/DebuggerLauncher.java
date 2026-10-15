package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.lang3.ArrayUtils;

import com.intuit.tank.tools.headless.HeadlessDebuggerCli;

/**
 * Main class of Tank-Debugger-all.jar: opens the visual debugger, or with --headless replays a project from the
 * command line. It has no logger, because log4j reads its configuration when the first logger is created.
 */
public final class DebuggerLauncher {

    static final String HEADLESS_FLAG = "--headless";
    static final String HEADLESS_LOG_CONFIG = "log4j2-headless.xml";

    private DebuggerLauncher() {
    }

    public static void main(String[] args) {
        String[] headlessArgs = headlessArgs(args);
        if (headlessArgs == null) {
            AgentDebuggerFrame.main(args);
            return;
        }
        System.setProperty("log4j2.configurationFile", HEADLESS_LOG_CONFIG);
        System.setProperty("java.awt.headless", "true");
        System.exit(HeadlessDebuggerCli.run(headlessArgs));
    }

    /** @return the arguments without --headless, or null when --headless isn't one of them */
    static String[] headlessArgs(String[] args) {
        return ArrayUtils.contains(args, HEADLESS_FLAG) ? ArrayUtils.removeAllOccurrences(args, HEADLESS_FLAG) : null;
    }
}
