package com.intuit.tank.harness.test;

import com.intuit.tank.harness.FlowController;
import com.intuit.tank.runner.TestStepContext;

public class MockFlowController implements FlowController {

    @Override
    public void startStep(TestStepContext context) {


    }

    @Override
    public void endStep(TestStepContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean shouldExecute(TestStepContext context) {
        return true;
    }

    @Override
    public void nextStep(TestStepContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public FlowController cloneController() {
        return new MockFlowController();
    }

    @Override
    public void endTest() {
        // TODO Auto-generated method stub
    }
}

