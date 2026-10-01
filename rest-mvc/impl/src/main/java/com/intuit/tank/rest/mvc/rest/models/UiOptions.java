/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.List;
import java.util.Map;

/**
 * Reference data for the UI drop-downs, in one payload so the client can load it once per session.
 *
 * @param products           product names for projects and scripts
 * @param locations          test locations
 * @param regions            the regions agents can run in (only the standalone region when standalone)
 * @param loggingProfiles    agent logging profiles
 * @param stopBehaviors      what an agent finishes before it stops
 * @param terminationPolicies when a test ends
 * @param incrementStrategies how users ramp up
 * @param vmInstanceTypes    agent instance types
 * @param httpClients        the HTTP client implementations agents can use; values are class names
 * @param reportingModes     result reporting modes
 * @param stepOptions        options for script step editors, keyed by list name
 * @param filterOptions      options for script filter editors, keyed by list name; the action scopes are
 *                           keyed {@code addActionScopes}, {@code removeActionScopes} and
 *                           {@code replaceActionScopes} by action type
 * @param logicStep          the default script text around a logic step
 */
public record UiOptions(List<Option> products,
                        List<Option> locations,
                        List<Option> regions,
                        List<Option> loggingProfiles,
                        List<Option> stopBehaviors,
                        List<Option> terminationPolicies,
                        List<Option> incrementStrategies,
                        List<InstanceTypeOption> vmInstanceTypes,
                        List<Option> httpClients,
                        List<Option> reportingModes,
                        Map<String, List<Option>> stepOptions,
                        Map<String, List<Option>> filterOptions,
                        LogicStepOptions logicStep) {

    /**
     * @param insertBefore script text placed before a logic step's script
     * @param appendAfter  script text placed after it
     */
    public record LogicStepOptions(String insertBefore, String appendAfter) {
    }
}
