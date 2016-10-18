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

import com.intuit.tank.harness.data.AuthenticationStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

class AuthenticationRunner implements Runner {

    private TestStepContext tsc;
    private AuthenticationStep testStep;
    private Variables variables;

    AuthenticationRunner(TestStepContext tsc) {
        this.tsc = tsc;
        this.testStep = (AuthenticationStep) tsc.getTestStep();
        this.variables = tsc.getVariables();
    }

    @Override
    public String execute() {
        AuthCredentials creds = AuthCredentials.builder()
                .withHost(variables.evaluate(testStep.getHost()))
                .withPassword(variables.evaluate(testStep.getPassword()))
                .withPortString(variables.evaluate(testStep.getPort()))
                .withRealm(variables.evaluate(testStep.getRealm()))
                .withUserName(variables.evaluate(testStep.getUserName()))
                .withScheme(testStep.getScheme())
                .build();
        tsc.getHttpClient().addAuth(creds);
        return TankConstants.HTTP_CASE_PASS;
    }

}
