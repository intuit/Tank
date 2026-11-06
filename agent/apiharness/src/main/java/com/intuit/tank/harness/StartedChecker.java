package com.intuit.tank.harness;

import com.intuit.tank.harness.logging.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class StartedChecker implements Runnable {

	private static Logger LOG = LogManager.getLogger(StartedChecker.class);
    private static final long SLEEP_TIME = 1000 * 60 * 10; // 10 minutes

    private final long sleepTime;
    private final Runnable exitAction;

    public StartedChecker() {
        this(SLEEP_TIME, () -> System.exit(1));
    }

    /** For tests, which cannot wait 10 minutes or let the JVM exit. */
    StartedChecker(long sleepTime, Runnable exitAction) {
        this.sleepTime = sleepTime;
        this.exitAction = exitAction;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // interrupting the checker cancels it; it must not decide the agent never started
            Thread.currentThread().interrupt();
            return;
        }
        if (!APITestHarness.getInstance().isStarted()) {
            LOG.error(LogUtil.getLogMessage("Waited 10 minutes, didn't hear anything from the controller.  Exiting."));
            exitAction.run();
        }

    }

}
