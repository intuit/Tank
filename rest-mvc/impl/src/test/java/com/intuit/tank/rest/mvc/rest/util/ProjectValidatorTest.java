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
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ProjectValidatorTest {

    private MockedConstruction<JobValidator> validators;
    private Set<String> bestPractice = Set.of();
    private Set<String> orphaned = Set.of();
    private Set<String> superfluous = Set.of();
    private long planDuration = 90_000;

    @BeforeEach
    void setUp() {
        validators = Mockito.mockConstruction(JobValidator.class, (mock, context) -> {
            when(mock.getBestPracticeViolations()).thenAnswer(i -> bestPractice);
            when(mock.getOrphanedVariables()).thenAnswer(i -> orphaned);
            when(mock.getSuperfluousVariables()).thenAnswer(i -> superfluous);
            when(mock.getDataFiles()).thenReturn(Set.of("users.csv"));
            when(mock.getExpectedTime(anyString())).thenAnswer(i -> planDuration);
        });
    }

    @AfterEach
    void tearDown() {
        validators.close();
    }

    private static JobConfiguration config(Project project) {
        return project.getWorkloads().get(0).getJobConfiguration();
    }

    @Test
    void validProject() {
        ProjectValidation result = ProjectValidator.validate(ProjectFixtures.project(1, "Load", "alice"));
        assertTrue(result.valid(), result.errors().toString());
        assertEquals(List.of(), result.errors());
        assertEquals(100, result.totalUsers());
        assertEquals(30 * 60_000, result.simulationTimeMs());
        assertEquals(5 * 60_000, result.rampTimeMs());
        assertEquals(List.of(new ProjectValidation.TestPlanEstimate("Main", 100, 90_000)), result.testPlans());
        assertEquals(List.of("users.csv"), result.dataFiles());
    }

    @Test
    void reportsErrors() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        config(project).setSimulationTimeExpression(null);
        config(project).getJobRegions().clear();
        project.getWorkloads().get(0).getTestPlans().get(0).setUserPercentage(90);
        project.getWorkloads().get(0).getTestPlans().get(0).getScriptGroups().get(0).getScriptGroupSteps().clear();

        ProjectValidation result = ProjectValidator.validate(project);

        assertFalse(result.valid());
        assertEquals(List.of("Simulation time not set.", "No users defined.",
                "User Percentage of Test Plans does not add up to 100%", "No scripts defined."), result.errors());
    }

    @Test
    void scriptPolicyUsesLongestTestPlan() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        config(project).setTerminationPolicy(TerminationPolicy.script);
        config(project).setSimulationTimeExpression(null);
        ProjectValidation result = ProjectValidator.validate(project);
        assertTrue(result.valid(), result.errors().toString());
        assertEquals(90_000, result.simulationTimeMs());
    }

    @Test
    void nonlinearChecksRegionPercentages() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        config(project).setIncrementStrategy(IncrementStrategy.standard);
        ProjectValidation result = ProjectValidator.validate(project);
        assertEquals(List.of("Region Percentage does not add up to 100%"), result.errors());

        config(project).getJobRegions().add(new JobRegion(VMRegion.US_WEST_2, "0", "100"));
        assertTrue(ProjectValidator.validate(project).valid());
    }

    @Test
    void unparseableUsersAreAnError() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        config(project).getJobRegions().iterator().next().setUsers("abc");
        assertTrue(ProjectValidator.validate(project).errors().stream().anyMatch(e -> e.startsWith("Users for region US_EAST")));
    }

    @Test
    void warningsAreSortedPlainText() {
        bestPractice = Set.of("&nbsp;&nbsp;Rule: No Logging Keys.<br/>&nbsp;&nbsp;&nbsp;&nbsp;No Logging keys are defined");
        orphaned = Set.of("token");
        superfluous = Set.of("unused");
        ProjectValidation result = ProjectValidator.validate(ProjectFixtures.project(1, "Load", "alice"));
        assertEquals(List.of("Rule: No Logging Keys. No Logging keys are defined",
                "Variable 'token' is used but never set.", "Variable 'unused' is set but never used."), result.warnings());
        assertTrue(result.valid(), "warnings do not make a project invalid");
        assertEquals(List.of("token"), result.orphanedVariables());
    }

    @Test
    void testPlanPercentagesAcrossPlans() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        TestPlan second = new TestPlan();
        second.setName("Other");
        second.setUserPercentage(20);
        project.getWorkloads().get(0).getTestPlans().get(0).setUserPercentage(80);
        project.getWorkloads().get(0).addTestPlan(second);
        assertTrue(ProjectValidator.validate(project).valid());
    }
}
