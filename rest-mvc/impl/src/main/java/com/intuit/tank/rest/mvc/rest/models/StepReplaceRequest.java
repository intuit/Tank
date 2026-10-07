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
 * A replacement in an unsaved step list, for {@code POST /v2/scripts/steps/replace}. Steps are matched as
 * {@link StepSearchRequest} does, and in each matching part the whole key or value becomes
 * {@code replacement}.
 *
 * @param mode  {@code VALUE} (default) or {@code KEY}, for key/value sections
 * @param uuids only replace in these steps; null for every matching step
 */
public record StepReplaceRequest(List<ScriptStepTO> steps, String query, List<String> sections, String replacement,
                                 String mode, List<String> uuids) {
}
