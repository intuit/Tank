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
 * Script filters to apply, in order, to an unsaved step list, for {@code POST /v2/scripts/steps/apply-filters}.
 */
public record ApplyFiltersRequest(List<ScriptStepTO> steps, List<Integer> filterIds) {
}
