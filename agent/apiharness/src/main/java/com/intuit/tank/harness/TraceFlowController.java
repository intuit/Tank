package com.intuit.tank.harness;

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

import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.runner.TestStepContext;

/**
 * TraceFlowController
 * 
 * @author Kevin McGoldrick
 * 
 */
public class TraceFlowController implements FlowController {

    private int requestNumber = 0;

    public static void debug() {

    }

    @Override
    public void startStep(TestStepContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void endStep(TestStepContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean shouldExecute(TestStepContext context) {
        TestStep step = context.getTestStep();
        return !(step instanceof SleepTimeStep) && !(step instanceof ThinkTimeStep);
    }

    @Override
    public void nextStep(TestStepContext context) {
        requestNumber++;
        System.err.println("...request #" + requestNumber);

    }

    @Override
    public FlowController cloneController() {
        return new TraceFlowController();
    }

    @Override
    public void endTest() {
        System.exit(0);
    }
}
