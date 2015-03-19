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

import org.apache.commons.httpclient.Cookie;

import com.intuit.tank.harness.data.CookieStep;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

class SetCookieRunner implements Runner {
    private TestStepContext tsc;

    // private Variables variables;
    SetCookieRunner(TestStepContext tsc) {
        this.tsc = tsc;
    }

    public String execute() {
        CookieStep testStep = (CookieStep) tsc.getTestStep();
        String domain = tsc.getVariables().evaluate(testStep.getCookieDomain());
        String name = tsc.getVariables().evaluate(testStep.getCookieName());
        String value = tsc.getVariables().evaluate(testStep.getCookieValue());
        String path = tsc.getVariables().evaluate(testStep.getCookiePath());
        Cookie cookie = new Cookie(domain, name, value);
        cookie.setPath(path);
        tsc.getHttpClient().getState().addCookie(cookie);
        return TankConstants.HTTP_CASE_PASS;
    }
}
