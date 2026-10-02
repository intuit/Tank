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
 * Checks of a step list, as the script editor's "Validate" dialog shows them.
 *
 * @param durationMs           estimated time for one pass of the script
 * @param warnings             best-practice and variable usage issues, as plain text
 * @param orphanedVariables    variables that are used but never set
 * @param superfluousVariables variables that are set but never used
 * @param dataFiles            data file names the steps read
 * @param detailsHtml          the full report as the web UI shows it (HTML)
 */
public record ScriptValidation(long durationMs, List<String> warnings, List<String> orphanedVariables,
                               List<String> superfluousVariables, List<String> dataFiles, String detailsHtml) {
}
