/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import com.intuit.tank.script.models.ScriptStepTO;

import java.util.List;

/**
 * A search over an unsaved step list, for {@code POST /v2/scripts/steps/search}.
 *
 * @param query    the text to find; {@code *} and {@code ?} are wildcards, and the whole value must match
 * @param sections the parts of each step to search, by name, such as {@code host}, {@code requestHeaderValue},
 *                 {@code variableKey}, {@code minTime} or {@code search} (everything)
 */
public record StepSearchRequest(List<ScriptStepTO> steps, String query, List<String> sections) {
}
