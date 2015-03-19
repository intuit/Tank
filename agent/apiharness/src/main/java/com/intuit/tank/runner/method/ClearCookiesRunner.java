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

import org.apache.commons.httpclient.HttpState;

import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

class ClearCookiesRunner implements Runner {
    private TestStepContext tsc;

    // private Variables variables;
    ClearCookiesRunner(TestStepContext tsc) {
        this.tsc = tsc;
    }

    public String execute() {
        tsc.getHttpClient().setState(new HttpState());
        tsc.getHttpClient().getHttpConnectionManager().closeIdleConnections(0);
        return TankConstants.HTTP_CASE_PASS;
    }
}
