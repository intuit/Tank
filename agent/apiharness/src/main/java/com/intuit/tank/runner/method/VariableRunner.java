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

import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

class VariableRunner implements Runner {
    private TestStepContext tsc;
    private Variables variables;
    private VariableStep testStep;

    VariableRunner(TestStepContext tsc) {
        this.tsc = tsc;
        this.variables = tsc.getVariables();
        this.testStep = (VariableStep) tsc.getTestStep();
    }

    public String execute() {
        variables.addVariable(testStep.getKey(), testStep.getValue());
        return TankConstants.HTTP_CASE_PASS;
    }
}
