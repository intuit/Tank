/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.rest.mvc.rest.models.LogicTestRequest;
import com.intuit.tank.rest.mvc.rest.models.LogicTestResult;

/**
 * Runs a logic step script against made-up inputs. The agent's script classes are in the web module, which
 * provides this as a CDI bean.
 */
public interface LogicStepTester {

    /**
     * @throws IllegalStateException when too many tests are already running
     */
    LogicTestResult test(LogicTestRequest request);
}
