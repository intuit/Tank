/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.rest.mvc.rest.models.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.models.DraftSteps;
import com.intuit.tank.rest.mvc.rest.models.LogicTestRequest;
import com.intuit.tank.rest.mvc.rest.models.LogicTestResult;
import com.intuit.tank.rest.mvc.rest.models.ScriptValidation;
import com.intuit.tank.rest.mvc.rest.models.StepMatch;
import com.intuit.tank.rest.mvc.rest.models.StepReplaceRequest;
import com.intuit.tank.rest.mvc.rest.models.StepSearchRequest;
import com.intuit.tank.rest.mvc.rest.models.ValidateStepsRequest;

import java.util.List;

/**
 * Operations on an unsaved step list for the script editor. Nothing is saved: the client keeps the draft
 * and saves it with {@code PUT /v2/scripts/{id}/steps}. Every method requires a user caller.
 */
public interface ScriptDraftServiceV2 {

    List<StepMatch> search(StepSearchRequest request);

    DraftSteps replace(StepReplaceRequest request);

    DraftSteps applyFilters(ApplyFiltersRequest request);

    ScriptValidation validate(ValidateStepsRequest request);

    /**
     * Runs a logic step script in a sandbox: no Java access, a time limit, and a limit on tests running at
     * once. Needs {@code EDIT_SCRIPT} or ownership of {@code request.scriptId()}.
     */
    LogicTestResult testLogic(LogicTestRequest request);
}
