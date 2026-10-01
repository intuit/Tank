/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Everything the project editor shows and saves, for {@code GET} and {@code PUT /v2/projects/{id}/full}.
 *
 * <p>A {@code PUT} replaces the whole project with the body. {@code id}, {@code created},
 * {@code permissions} and script names are ignored on input. {@code modified} must be the value from the
 * {@code GET}; if the project was saved since, the {@code PUT} is rejected with 409.</p>
 *
 * @param owner       the project owner (its creator); only the owner or an admin may change it
 * @param regions     users per region; on {@code GET} every configured region is listed, with "0" users
 *                    where none are set
 * @param variables   project variables, in name order
 * @param permissions what the caller may do with this project
 */
public record ProjectDetail(Integer id,
                            String name,
                            String productName,
                            String comments,
                            String owner,
                            Date created,
                            Date modified,
                            Settings settings,
                            List<RegionUsers> regions,
                            List<TestPlanDetail> testPlans,
                            Map<String, String> variables,
                            List<Integer> dataFileIds,
                            Permissions permissions) {

    /**
     * Load and timing settings, and the job defaults used when a job is queued from this project.
     *
     * @param simulationTime an expression such as {@code 30m}; required when {@code terminationPolicy} is
     *                       {@code time}
     * @param rampTime       an expression such as {@code 5m}
     * @param targetRampRate users per second to reach, for the {@code standard} (nonlinear) strategy
     * @param location       a location value from {@code /v2/config/options}
     * @param stopBehavior   a {@code StopBehavior} name
     * @param loggingProfile a {@code LoggingProfile} name
     * @param tankClientClass an HTTP client class name from {@code /v2/config/options}
     */
    public record Settings(IncrementStrategy incrementStrategy,
                           TerminationPolicy terminationPolicy,
                           String simulationTime,
                           String rampTime,
                           int baselineVirtualUsers,
                           int userIntervalIncrement,
                           double targetRampRate,
                           String location,
                           String stopBehavior,
                           String loggingProfile,
                           String vmInstanceType,
                           int numUsersPerAgent,
                           double targetRatePerAgent,
                           boolean useTwoStep,
                           String tankClientClass,
                           String reportingMode,
                           boolean allowOverride) {
    }

    /**
     * @param users      users for the {@code increasing} (linear) strategy; a number or an expression
     * @param percentage share of the load for the {@code standard} strategy
     */
    public record RegionUsers(VMRegion region, String users, String percentage) {
    }

    public record TestPlanDetail(String name, int userPercentage, List<ScriptGroupDetail> scriptGroups) {
    }

    public record ScriptGroupDetail(String name, int loop, List<ScriptRef> scripts) {
    }

    /**
     * @param scriptName ignored on input
     */
    public record ScriptRef(int scriptId, String scriptName, int loop) {
    }

    public record Permissions(boolean edit, boolean delete, boolean changeOwner) {
    }
}
