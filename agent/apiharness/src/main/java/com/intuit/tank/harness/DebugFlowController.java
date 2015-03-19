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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.runner.TestStepContext;

public class DebugFlowController implements FlowController {

    private int skipToRequest = 0;
    private boolean DO_SKIP = false;
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

        if (skipToRequest > 0) {
            if (skipToRequest == requestNumber) {
                DO_SKIP = false;
                skipToRequest = 0;
            }
        }

        if (!DO_SKIP) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String s = "";
                System.err.println("Enter [n]ext to continue, [s]kip to run to end or request number to jump to:");
                while (s != null &&
                        !(s.toLowerCase().startsWith("n") ||
                                s.toLowerCase().startsWith("s") ||
                        skipToRequest != 0)) {
                    s = in.readLine();

                    try {
                        skipToRequest = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        skipToRequest = 0;
                    }
                }
                if (s.toLowerCase().startsWith("s") || (skipToRequest > 0)) {
                    DO_SKIP = true;
                }
            } catch (IOException e) {

            }
        }
    }

    @Override
    public FlowController cloneController() {
        return new DebugFlowController();
    }

    @Override
    public void endTest() {
        System.exit(0);
    }
}
