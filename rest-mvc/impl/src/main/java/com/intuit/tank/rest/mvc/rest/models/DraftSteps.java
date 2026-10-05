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
 * An unsaved step list returned by a draft operation, ready for {@code PUT /v2/scripts/{id}/steps}.
 *
 * @param changed how many steps the operation changed
 */
public record DraftSteps(List<ScriptStepTO> steps, int changed) {
}
