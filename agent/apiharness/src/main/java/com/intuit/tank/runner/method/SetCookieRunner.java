package com.intuit.tank.runner.method;

import com.intuit.tank.harness.data.CookieStep;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

class SetCookieRunner implements Runner {
    private TestStepContext tsc;

    public SetCookieRunner(TestStepContext tsc) {
        this.tsc = tsc;
    }

    /**
     * 
     */
    public String execute() {
        CookieStep testStep = (CookieStep) tsc.getTestStep();
        String domain = tsc.getVariables().evaluate(testStep.getCookieDomain());
        String name = tsc.getVariables().evaluate(testStep.getCookieName());
        String value = tsc.getVariables().evaluate(testStep.getCookieValue());
        String path = tsc.getVariables().evaluate(testStep.getCookiePath());
        TankCookie cookie = TankCookie.builder().withDomain(domain).withName(name).withPath(path).withValue(value).build();
        tsc.getHttpClient().setCookie(cookie);
        return TankConstants.HTTP_CASE_PASS;
    }

}
