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
import com.intuit.tank.project.Script;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.RegionUsers;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.ScriptGroupDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.ScriptRef;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.Settings;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail.TestPlanDetail;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProjectDetailMapperTest {

    private static final ProjectDetail.Permissions ALL = new ProjectDetail.Permissions(true, true, true);

    @Test
    void toDetail_mapsTheWholeProject() {
        ProjectDetail detail = ProjectDetailMapper.toDetail(ProjectFixtures.project(1, "Load", "alice"), ALL,
                List.of(VMRegion.US_EAST, VMRegion.US_WEST_2), false);
        assertEquals(1, detail.id());
        assertEquals("Load", detail.name());
        assertEquals("alice", detail.owner());
        assertEquals(ProjectFixtures.MODIFIED, detail.modified());
        assertEquals("30m", detail.settings().simulationTime());
        assertEquals(IncrementStrategy.increasing, detail.settings().incrementStrategy());
        assertTrue(detail.settings().allowOverride());
        assertEquals(List.of(new RegionUsers(VMRegion.US_EAST, "100", "0"), new RegionUsers(VMRegion.US_WEST_2, "0", "0")),
                detail.regions(), "configured regions without users are listed with 0");
        assertEquals(List.of(new TestPlanDetail("Main", 100, List.of(
                new ScriptGroupDetail("Login", 2, List.of(new ScriptRef(42, "login script", 3)))))), detail.testPlans());
        assertEquals(Map.of("host", "example.com"), detail.variables());
        assertEquals(List.of(7), detail.dataFileIds());
        assertSame(ALL, detail.permissions());
    }

    @Test
    void regions_standaloneUsesTheStandaloneOrEastRegion() {
        Set<JobRegion> stored = Set.of(new JobRegion(VMRegion.US_EAST, "50", "10"), new JobRegion(VMRegion.EUROPE, "5", "0"));
        assertEquals(List.of(new RegionUsers(VMRegion.STANDALONE, "50", "10")),
                ProjectDetailMapper.regions(stored, List.of(), true));
    }

    @Test
    void regions_standaloneCombinesOtherRegions() {
        Set<JobRegion> stored = new HashSet<>(List.of(new JobRegion(VMRegion.EUROPE, "5", "40"),
                new JobRegion(VMRegion.US_WEST_2, "0", "60")));
        assertEquals(List.of(new RegionUsers(VMRegion.STANDALONE, "5", "100")),
                ProjectDetailMapper.regions(stored, List.of(), true));
        assertEquals(List.of(new RegionUsers(VMRegion.STANDALONE, "0", "0")),
                ProjectDetailMapper.regions(Set.of(), List.of(), true));
    }

    @Test
    void validate_acceptsValidDetail() {
        assertEquals(List.of(), ProjectDetailMapper.validate(ProjectFixtures.detail("Load", "alice")));
    }

    @Test
    void validate_reportsEveryProblem() {
        ProjectDetail bad = new ProjectDetail(1, " ", null, "x".repeat(1025), null, null, null,
                new Settings(null, TerminationPolicy.time, "soon", "5x", -1, -1, -1, null, "SOMETIMES", "LOUD", null, 0, 0,
                        false, null, null, false),
                List.of(new RegionUsers(VMRegion.US_EAST, "abc", "101"), new RegionUsers(VMRegion.US_EAST, "1", "0")),
                List.of(new TestPlanDetail("Main", 120, List.of(new ScriptGroupDetail("", 1, List.of()),
                        new ScriptGroupDetail("G", 0, List.of(new ScriptRef(1, null, 0)))))),
                Map.of(" ", "v"), null, null);
        List<String> errors = ProjectDetailMapper.validate(bad);
        String all = String.join("\n", errors);
        for (String expected : List.of("name is required", "comments must be at most", "owner is required",
                "incrementStrategy is required", "simulationTime cannot be parsed", "rampTime cannot be parsed",
                "baselineVirtualUsers", "userIntervalIncrement", "targetRampRate", "numUsersPerAgent", "targetRatePerAgent",
                "stopBehavior is not a known value", "loggingProfile is not a known value", "users for region US_EAST",
                "percentage for region US_EAST", "listed more than once", "userPercentage of test plan Main",
                "every script group in test plan Main needs a name", "loop of script group G", "loop of script 1",
                "variable names must not be blank")) {
            assertTrue(all.contains(expected), "missing error: " + expected + " in " + all);
        }
    }

    @Test
    void validate_requiresTestPlansAndRegions() {
        ProjectDetail d = ProjectFixtures.detail("Load", "alice");
        ProjectDetail noPlans = new ProjectDetail(d.id(), d.name(), d.productName(), d.comments(), d.owner(), null,
                d.modified(), d.settings(), null, List.of(), d.variables(), d.dataFileIds(), null);
        List<String> errors = ProjectDetailMapper.validate(noPlans);
        assertTrue(errors.contains("regions are required"));
        assertTrue(errors.contains("at least one test plan is required"));
    }

    @Test
    void apply_replacesContentsAndKeepsRegionRows() {
        Project project = ProjectFixtures.project(1, "Load", "alice");
        JobConfiguration config = project.getWorkloads().get(0).getJobConfiguration();
        ProjectDetail d = ProjectFixtures.detail("Load", "alice");
        ProjectDetail changed = new ProjectDetail(d.id(), " Renamed ", "Tax", "new comments", "bob", null, d.modified(),
                new Settings(IncrementStrategy.standard, TerminationPolicy.script, " ", "10m", 0, 1, 25.5, "lab",
                        "END_OF_TEST", "VERBOSE", "c5.xlarge", 500, 2.5, true, "com.example.Client", "none", false),
                List.of(new RegionUsers(VMRegion.US_EAST, " 60 ", "60"), new RegionUsers(VMRegion.US_WEST_2, "40", "40")),
                List.of(new TestPlanDetail("A", 70, List.of(new ScriptGroupDetail("G1", 1,
                                List.of(new ScriptRef(42, null, 1), new ScriptRef(43, null, 5))))),
                        new TestPlanDetail("B", 30, List.of())),
                Map.of("env", "qa"), List.of(8, 9), null);
        Script s42 = ProjectFixtures.script(42, "a");
        Script s43 = ProjectFixtures.script(43, "b");

        ProjectDetailMapper.apply(changed, project, Map.of(42, s42, 43, s43));

        assertEquals("Renamed", project.getName());
        assertEquals("Renamed", project.getWorkloads().get(0).getName());
        assertEquals("bob", project.getCreator());
        assertEquals("Tax", project.getProductName());
        assertEquals(IncrementStrategy.standard, config.getIncrementStrategy());
        assertEquals(TerminationPolicy.script, config.getTerminationPolicy());
        assertNull(config.getSimulationTimeExpression(), "blank expression clears it");
        assertEquals("10m", config.getRampTimeExpression());
        assertEquals(25.5, config.getTargetRampRate());
        assertEquals("lab", config.getLocation());
        assertEquals("VERBOSE", config.getLoggingProfile());
        assertEquals(500, config.getNumUsersPerAgent());
        assertTrue(config.isUseTwoStep());
        assertFalse(config.isAllowOverride());
        assertEquals(Map.of("env", "qa"), config.getVariables());
        assertEquals(Set.of(8, 9), config.getDataFileIds());

        assertEquals(2, config.getJobRegions().size());
        JobRegion east = config.getJobRegions().stream().filter(r -> r.getRegion() == VMRegion.US_EAST).findFirst().orElseThrow();
        assertEquals(501, east.getId(), "an existing region row is updated in place");
        assertEquals("60", east.getUsers());
        assertEquals("60", east.getPercentage());

        List<TestPlan> plans = project.getWorkloads().get(0).getTestPlans();
        assertEquals(List.of("A", "B"), plans.stream().map(TestPlan::getName).toList());
        assertSame(s43, plans.get(0).getScriptGroups().get(0).getScriptGroupSteps().get(1).getScript());
        assertEquals(5, plans.get(0).getScriptGroups().get(0).getScriptGroupSteps().get(1).getLoop());
        assertEquals(1, config.getNotifications().size(), "notifications are not part of the editor and are kept");
    }

    @Test
    void scriptIds() {
        assertEquals(Set.of(42), ProjectDetailMapper.scriptIds(ProjectFixtures.detail("Load", "alice")));
    }
}
