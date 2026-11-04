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
 * Checks of a saved project before it is run.
 *
 * @param valid               true when there are no errors; warnings do not make a project invalid
 * @param errors              problems that would stop a job from running correctly
 * @param warnings            best-practice and variable usage issues
 * @param totalUsers          users across all regions, for the {@code increasing} strategy
 * @param simulationTimeMs    the simulation time, or the longest test plan for the {@code script} policy
 * @param rampTimeMs          the ramp time
 * @param testPlans           each test plan's share of users and estimated duration of one pass
 * @param orphanedVariables   variables that are used but never set
 * @param superfluousVariables variables that are set but never used
 * @param dataFiles           data file names the scripts read
 */
public record ProjectValidation(boolean valid,
                                List<String> errors,
                                List<String> warnings,
                                long totalUsers,
                                long simulationTimeMs,
                                long rampTimeMs,
                                List<TestPlanEstimate> testPlans,
                                List<String> orphanedVariables,
                                List<String> superfluousVariables,
                                List<String> dataFiles) {

    public record TestPlanEstimate(String name, int userPercentage, long expectedDurationMs) {
    }
}
