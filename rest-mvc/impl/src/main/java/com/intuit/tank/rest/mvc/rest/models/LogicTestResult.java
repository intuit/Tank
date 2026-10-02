/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * What a logic step test printed.
 *
 * @param output   the script's log, outputs and variables before and after, as the web UI shows them
 * @param timedOut true when the script did not finish within the time limit; {@code output} is then partial
 */
public record LogicTestResult(String output, boolean timedOut, long durationMs) {
}
