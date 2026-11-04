/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.List;

/**
 * What an external script test printed, and what it did to the script's steps.
 *
 * @param output      what the script printed, and any error
 * @param timedOut    true when the script did not finish within the time limit; the step counts are then zero
 * @param stepsBefore the number of steps before the script ran
 * @param stepsAfter  the number of steps after it ran
 * @param changes     the steps added, removed or changed, at most {@code MAX_STEP_CHANGES} of them
 */
public record ExternalScriptTestResult(String output, boolean timedOut, long durationMs, int stepsBefore,
                                       int stepsAfter, int added, int removed, int changed,
                                       List<StepChange> changes) {

    /**
     * @param kind     {@code added}, {@code removed} or {@code changed}
     * @param position the step's position, from 0: after the script ran, or before it for a removed step
     */
    public record StepChange(String kind, int position, String type, String label) {
    }
}
