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

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.harness.functions.FunctionHandler;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ValidationUtil;

class ThinkTimeRunner implements Runner {

    static Logger LOG = LogManager.getLogger(ThinkTimeRunner.class);

    private TestStepContext tsc;
    private ThinkTimeStep testStep;
    private Variables variables;

    ThinkTimeRunner(TestStepContext tsc) {
        this.tsc = tsc;
        this.testStep = (ThinkTimeStep) tsc.getTestStep();
        this.variables = tsc.getVariables();
    }

    @Override
    public String execute() {
        String testPlanName = tsc.getTestPlanName();

        int min = 1;
        int max = 1;

        String minTime = testStep.getMinTime();
        String maxTime = testStep.getMaxTime();

        try {
            min = Integer.parseInt(eval(minTime, variables));
        } catch (NumberFormatException e1) {
            LOG.warn(LogUtil.getLogMessage("Error parsing min Think Time from step " + testPlanName)
                    + " with value of " + minTime);
        }
        try {
            max = Integer.parseInt(eval(maxTime, variables));
        } catch (NumberFormatException e1) {
            LOG.warn(LogUtil.getLogMessage("Error parsing max Think Time from step " + testPlanName)
                    + " with value of " + maxTime);
            max = min;
        }
        if (min < 0) {
            min = 0;
        }
        if (min > max) {
            max = min;
        }
        // get the range
        int range = max - min;
        if (range > 0) {
            Random random = new Random();
            range = random.nextInt(range);
        }
        long randomNumber = min + range;
        LOG.debug("Sleeping for " + randomNumber);
        if (APITestHarness.getInstance().hasMetSimulationTime() && LOG.isDebugEnabled()) {
            LOG.debug(LogUtil.getLogMessage("Sleeping from thinkTime for " + randomNumber
                    + "ms. after simulation time has been met with at step " + testPlanName + "."));
        }
        sleep(randomNumber);
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
