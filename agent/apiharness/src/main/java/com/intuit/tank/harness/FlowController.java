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

public interface FlowController {

    /**
     * indicates that the step has started executing.
     * 
     * @param step
     *            the step that has started.
     */
    public void startStep(TestStepContext context);

    /**
     * indicates that the step has finished executing.
     * 
     * @param step
     *            the step that hasfinished
     */
    public void endStep(TestStepContext context);

    /**
     * should the next step be executed?
     * 
     * @param step
     *            the step to be executed.
     * @return true if the step should be executed
     */
    public boolean shouldExecute(TestStepContext context);

    /**
     * indicates taht the next step is ready to run. controller can block until user indicates that the step should be
     * executed.
     * 
     * @param step
     *            the next step to be executed.
     */
    public void nextStep(TestStepContext context);

    /**
     * 
     */
    public void endTest();

    /**
     * make a suitable clone of this controller. Each Thread that is runing needs it's own controller.
     * 
     * @return
     */
    public FlowController cloneController();
}
