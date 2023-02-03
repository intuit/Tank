package com.intuit.tank.runner.method;
import com.intuit.tank.harness.data.MockStep;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

public class MockRunner implements Runner{

    private TestStepContext tsc;
    private MockStep step;

    MockRunner(TestStepContext tsc) {
        this.tsc = tsc;
        step = (MockStep) tsc.getTestStep();
    }
    /* simple execute to test - can be refactored and generalized for future tests */
    public String execute() {
        return TankConstants.HTTP_CASE_FAIL;
    }
}
