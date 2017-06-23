package com.intuit.tank.harness;

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

    @Override
    public void run() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!APITestHarness.getInstance().isStarted()) {
            LOG.error("Waited 10 minutes, didn't hear anything from the controller.  Exiting.");
            System.exit(1);
        }

    }

}
