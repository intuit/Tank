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
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.functions.FunctionHandler;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ValidationUtil;

class SleepTimeRunner implements Runner {

    static Logger LOG = LogManager.getLogger(SleepTimeRunner.class);

    @SuppressWarnings("unused")
    private TestStepContext tsc;
    private SleepTimeStep testStep;
    private Variables variables;
    private String testPlanName;

    SleepTimeRunner(TestStepContext tsc) {
        this.tsc = tsc;
        this.testStep = (SleepTimeStep) tsc.getTestStep();
        this.variables = tsc.getVariables();
        this.testPlanName = tsc.getTestPlanName();
    }

    @Override
    public String execute() {
        String sleepTime = testStep.getValue();
        long sleepValue = 0;
        try {
            sleepValue = Long.parseLong(eval(sleepTime, variables));
        } catch (NumberFormatException e1) {
            LOG.warn(LogUtil.getLogMessage("Error parsing min Sleep Time from step " + testPlanName)
                    + " with value of " + sleepTime);
        }
        if (sleepValue < 1) {
            sleepValue = 1;
        }
        LOG.debug("Sleeping for " + sleepValue);
        if (APITestHarness.getInstance().hasMetSimulationTime() && LOG.isDebugEnabled()) {
            LOG.debug(LogUtil.getLogMessage("Sleeping for " + sleepValue
                    + "ms. after simulation time has been met with at step " + testPlanName + "."));
        }
        sleep(sleepValue);
        return TankConstants.HTTP_CASE_PASS;
    }

    private static String eval(String valueString, Variables variables) {
        if (ValidationUtil.isFunction(valueString)) {
            return FunctionHandler.executeFunction(valueString, variables);
        } else if (ValidationUtil.isVariable(valueString)) {
            return variables.getVariable(valueString);
        } else {
            valueString = variables.evaluate(valueString);
            return valueString;
        }
    }

    /**
     * @param sleepValue
     */
    private void sleep(long sleepValue) {
        try {
            Thread.sleep(sleepValue);
        } catch (InterruptedException e) {
            // LOG.info(LogUtil.getLogMessage("Sleep time got interrupted."));
        }
    }

}
