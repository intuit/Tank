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

import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.CookieStep;
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.harness.data.TimerStep;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.runner.TestStepContext;

public class RunnerFactory {

    public static Runner getRunner(@Nonnull TestStepContext tsc) {
        TestStep testStep = tsc.getTestStep();
        Runner runner = null;
        if (testStep instanceof ThinkTimeStep) {
            runner = new ThinkTimeRunner(tsc);
        } else if (testStep instanceof SleepTimeStep) {
            runner = new SleepTimeRunner(tsc);
        } else if (testStep instanceof VariableStep) {
            runner = new VariableRunner(tsc);
        } else if (testStep instanceof TimerStep) {
            runner = new TimerRunner(tsc);
        } else if (testStep instanceof ClearCookiesStep) {
            runner = new ClearCookiesRunner(tsc);
        } else if (testStep instanceof LogicStep) {
            runner = new LogicRunner(tsc);
        } else if (testStep instanceof CookieStep) {
            runner = new SetCookieRunner(tsc);
        } else {
            runner = new RequestRunner(tsc);
        }
        return runner;
    }

}
