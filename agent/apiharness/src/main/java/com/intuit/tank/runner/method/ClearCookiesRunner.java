package com.intuit.tank.runner.method;

import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

class ClearCookiesRunner implements Runner {
    private TestStepContext tsc;

    // private Variables variables;
    ClearCookiesRunner(TestStepContext tsc) {
        this.tsc = tsc;
    }

    public String execute() {
        tsc.getHttpClient().clearSession();
        return TankConstants.HTTP_CASE_PASS;
    }
}
