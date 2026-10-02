/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation;
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation.TestPlanEstimate;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.util.TestParameterContainer;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks whether a saved project is ready to run, using the same rules as the job details shown before
 * a job is queued, without creating a job.
 */
public final class ProjectValidator {

    private ProjectValidator() {
    }

    public static ProjectValidation validate(Project project) {
        Workload workload = project.getWorkloads().get(0);
        JobConfiguration config = workload.getJobConfiguration();
        List<TestPlan> plans = workload.getTestPlans();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        JobValidator validator = new JobValidator(plans, config.getVariables());

        long rampTime = 0;
        long simulationTime = 0;
        try {
            TestParameterContainer times = TestParamUtil.evaluateTestTimes(0,
                    StringUtils.defaultString(config.getRampTimeExpression()),
                    StringUtils.defaultString(config.getSimulationTimeExpression()));
            rampTime = times.getRampTime();
            simulationTime = times.getSimulationTime();
        } catch (RuntimeException e) {
            errors.add("Simulation time and ramp time cannot be evaluated: " + e.getMessage());
        }
        if (config.getTerminationPolicy() == TerminationPolicy.script) {
            simulationTime = plans.stream().mapToLong(p -> validator.getExpectedTime(p.getName())).max().orElse(0);
        } else if (simulationTime == 0) {
            errors.add("Simulation time not set.");
        }

        long totalUsers = 0;
        if (config.getIncrementStrategy() == IncrementStrategy.increasing) {
            for (JobRegion region : config.getJobRegions()) {
                try {
                    totalUsers += TestParamUtil.evaluateExpression(region.getUsers(), 0, simulationTime, rampTime);
                } catch (RuntimeException e) {
                    errors.add("Users for region " + region.getRegion().name() + " cannot be evaluated: " + region.getUsers());
                }
            }
            if (totalUsers == 0) {
                errors.add("No users defined.");
            }
        } else {
            int regionPercentage = config.getJobRegions().stream()
                    .mapToInt(r -> NumberUtils.toInt(r.getPercentage())).sum();
            if (regionPercentage != 100) {
                errors.add("Region Percentage does not add up to 100%");
            }
        }

        if (plans.stream().mapToInt(TestPlan::getUserPercentage).sum() != 100) {
            errors.add("User Percentage of Test Plans does not add up to 100%");
        }
        boolean hasScripts = plans.stream().flatMap(p -> p.getScriptGroups().stream())
                .mapToInt(g -> g.getScriptGroupSteps().size()).sum() > 0;
        if (!hasScripts) {
            errors.add("No scripts defined.");
        }

        validator.getBestPracticeViolations().stream().sorted().map(ProjectValidator::plainText).forEach(warnings::add);
        validator.getOrphanedVariables().stream().sorted()
                .forEach(v -> warnings.add("Variable '" + v + "' is used but never set."));
        validator.getSuperfluousVariables().stream().sorted()
                .forEach(v -> warnings.add("Variable '" + v + "' is set but never used."));

        List<TestPlanEstimate> estimates = plans.stream()
                .map(p -> new TestPlanEstimate(p.getName(), p.getUserPercentage(), validator.getExpectedTime(p.getName())))
                .collect(Collectors.toList());

        return new ProjectValidation(errors.isEmpty(), errors, warnings, totalUsers, simulationTime, rampTime, estimates,
                sorted(validator.getOrphanedVariables()), sorted(validator.getSuperfluousVariables()),
                sorted(validator.getDataFiles()));
    }

    /** The validator's messages are written for the HTML job details. */
    static String plainText(String html) {
        return html.replace("<br/>", " ").replace("&nbsp;", "").replaceAll("\\s+", " ").trim();
    }

    private static List<String> sorted(java.util.Collection<String> values) {
        return values.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }
}
