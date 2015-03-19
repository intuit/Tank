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

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.runner.TestStepContext;

public class TestStepRunner {

    static Logger LOG = Logger.getLogger(TestStepRunner.class);

    private TestStepContext tsc;
    long lastSslHandshake = 0;
    long sslTimeout = 30000;

    public TestStepRunner(@Nonnull TestStepContext tsc) {
        this.tsc = tsc;
    }

    public String execute() {
        if (APITestHarness.getInstance().hasMetSimulationTime() && LOG.isDebugEnabled()) {
            LOG.debug(LogUtil.getLogMessage("Simulation time has been met while executing "
                    + tsc.getTestStep().getInfo(), LogEventType.System));
        }
        return RunnerFactory.getRunner(tsc).execute();
    }

}
