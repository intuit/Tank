/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.tools.headless;

import com.intuit.tank.harness.FlowController;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.runner.TestStepContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class AutomationFlowController implements FlowController {

    private static Logger LOG = LogManager.getLogger(AutomationFlowController.class);
    private HeadlessDebuggerSetup debuggerSetup;
    private boolean doNext;
    private int skipToIndex = -1;
    private boolean isSkipping;
    private boolean testStarted;
    private Set<Integer> toSkip = new HashSet<Integer>();

    public AutomationFlowController(HeadlessDebuggerSetup debuggerSetup) {
        super();
        this.debuggerSetup = debuggerSetup;
    }

    public void skip(int index) {
        toSkip.add(index);
    }

    public void removeSkip(int index) {
        toSkip.remove(index);
    }

    /**
     * @return the toSkip
     */
    public Set<Integer> getSkipList() {
        return toSkip;
    }

    /**
     * @param isSkipping
     *            the isSkipping to set
     */
    public void setSkipping(boolean isSkipping) {
        this.isSkipping = isSkipping;
    }

    /**
     *
     */
    public void doNext() {
        this.doNext = true;
    }

    /**
     * @param skipToIndex
     *            the skipToIndex to set
     */
    public void skipTo(int skipToIndex) {
        this.skipToIndex = skipToIndex;
    }

    public boolean isSkipping() {
        return skipToIndex != -1 || isSkipping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startStep(TestStepContext context) {
        debuggerSetup.stepStarted(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endStep(TestStepContext context) {
        debuggerSetup.stepFinished(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldExecute(TestStepContext context) {
        // push setep from debugger into context
        context.setTestStep(debuggerSetup.getStep(context.getTestStep().getStepIndex()));
        TestStep step = context.getTestStep();
        if (!toSkip.contains(context.getTestStep().getStepIndex())) {// move if skiplist does not contain line
            if (!(step instanceof SleepTimeStep) && !(step instanceof ThinkTimeStep)) { // automation skips sleep and think time steps
                return true;
            }
        }
        debuggerSetup.setNextStep(context);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextStep(TestStepContext context) {
        if (!testStarted) {
            testStarted = true;
        }
        TestStep step = context.getTestStep();
        debuggerSetup.setNextStep(context);
        doNext = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlowController cloneController() {
        return this;
    }

    @Override
    public void endTest() {
        skipToIndex = -1;
        debuggerSetup.testFinished();
    }

}
