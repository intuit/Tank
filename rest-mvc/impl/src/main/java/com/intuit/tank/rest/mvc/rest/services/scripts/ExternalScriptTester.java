/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.script.models.ScriptTO;

public interface ExternalScriptTester {

    /**
     * Runs an external script against {@code script}, which it may change.
     *
     * @throws IllegalStateException when too many tests are already running
     */
    Run run(String name, String code, ScriptTO script);

    /**
     * @param timedOut true when the script did not finish within the time limit; {@code output} is then partial
     */
    record Run(String output, boolean timedOut, long durationMs) {
    }
}
