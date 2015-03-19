/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashSet;
import java.util.Set;

import com.intuit.tank.harness.FlowController;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.runner.TestStepContext;

/**
 * DebuggerFlowController
 * 
 * @author dangleton
 * 
 */
public class DebuggerFlowController implements FlowController {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DebuggerFlowController.class);
    private AgentDebuggerFrame debuggerFrame;
    private boolean doNext;
    private int skipToIndex = -1;
    private boolean isSkipping;
    private boolean testStarted;
    private Set<Integer> toSkip = new HashSet<Integer>();

    public DebuggerFlowController(AgentDebuggerFrame debuggerFrame) {
        super();
        this.debuggerFrame = debuggerFrame;
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
     * @param doNext
     *            the doNext to set
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
     * @{inheritDoc
     */
    @Override
    public void startStep(TestStepContext context) {
        debuggerFrame.stepStarted(context);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void endStep(TestStepContext context) {
        debuggerFrame.stepFinished(context);

    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean shouldExecute(TestStepContext context) {
        // push setep from debugger into context
        context.setTestStep(debuggerFrame.getStep(context.getTestStep().getStepIndex()));
        TestStep step = context.getTestStep();
        if (!toSkip.contains(context.getTestStep().getStepIndex())) {// move if skiplist does not contain line
            if (debuggerFrame.runTimingSteps()) {
                return true;
            } else if (!(step instanceof SleepTimeStep) && !(step instanceof ThinkTimeStep)) {
                return true;
            }
        }
        debuggerFrame.moveCursor(context);
        debuggerFrame.setNextStep(context);
        return false;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void nextStep(TestStepContext context) {
        if (!testStarted) {
            testStarted = true;
            debuggerFrame.testStarted();
        }
        TestStep step = context.getTestStep();
        debuggerFrame.moveCursor(context);
        // block until doNext is true;
        try {
            if (!isSkipping || debuggerFrame.hasBreakPoint(step.getStepIndex())) {
                isSkipping = false;
                debuggerFrame.pause();
                while (!doNext) {
                    Thread.sleep(200);
                }
            }
            debuggerFrame.setNextStep(context);
        } catch (InterruptedException e) {
            LOG.warn("Sleep Interrupted:" + e);
        }
        doNext = false;

    }

    /**
     * @{inheritDoc
     */
    @Override
    public FlowController cloneController() {
        return this;
    }

    @Override
    public void endTest() {
        skipToIndex = -1;
        debuggerFrame.testFinished();

    }

}
