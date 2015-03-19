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

import com.intuit.tank.runner.TestStepContext;

/**
 * 
 * DefaultFlowController a defulat implementation of a flowcontroller that executes all steps
 * 
 * @author dangleton
 * 
 */
public class DefaultFlowController implements FlowController {

    @Override
    public void startStep(TestStepContext context) {
        // no op
    }

    @Override
    public void endStep(TestStepContext context) {
        // no op
    }

    @Override
    public boolean shouldExecute(TestStepContext context) {
        return true;
    }

    @Override
    public void nextStep(TestStepContext context) {
        // no op
    }

    @Override
    public FlowController cloneController() {
        return this;
    }

    @Override
    public void endTest() {
        System.exit(0);
    }

}
