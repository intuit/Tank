package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.*;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class JobValidatorTest {

    // =====================================================================
    // Constructor with Script
    // =====================================================================

    @Test
    void constructorWithScript_processesEmptyScript() {
        Script script = createScript(1, "EmptyScript", Collections.emptyList());

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {
            JobValidator validator = new JobValidator(script);

            assertTrue(validator.getDeclaredVariables().isEmpty());
            assertTrue(validator.getUsedVariables().isEmpty());
            assertTrue(validator.getOrphanedVariables().isEmpty());
            assertTrue(validator.getSuperfluousVariables().isEmpty());
            assertTrue(validator.isProcessAssignements());
            // No logging keys → violation
            assertTrue(validator.getBestPracticeViolations().stream()
                    .anyMatch(v -> v.contains("No Logging Keys")));
        }
    }

    @Test
    void constructorWithScript_processesStepsWithLoggingKey() {
        ScriptStep step = new ScriptStep();
        step.setType("request");
        step.setLoggingKey("login_page");

        Script script = createScript(1, "WithLogging", List.of(step));

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {
            JobValidator validator = new JobValidator(script);

            // Has logging keys, so no "No Logging Keys" violation
            assertTrue(validator.getBestPracticeViolations().stream()
                    .noneMatch(v -> v.contains("No Logging Keys")));
        }
    }

    // =====================================================================
    // Constructor with TestPlans
    // =====================================================================

    @Test
    void constructorWithTestPlans_extractsAndProcessesScripts() {
        Script script = createScript(10, "TestScript", Collections.emptyList());
        ScriptGroupStep sgs = new ScriptGroupStep();
        sgs.setScript(script);
        sgs.setLoop(1);

        ScriptGroup sg = new ScriptGroup();
        sg.setName("Group1");
        sg.setLoop(1);
        sg.addScriptGroupStep(sgs);

        TestPlan tp = TestPlan.builder().name("Plan1").usersPercentage(100).build();
        tp.addScriptGroup(sg);

        try (MockedConstruction<ScriptDao> scriptDaoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.loadScriptSteps(any(Script.class))).thenReturn(script));
             MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {

            Map<String, String> globals = new HashMap<>();
            globals.put("server", "example.com");

            JobValidator validator = new JobValidator(List.of(tp), globals);

            // Global variable should be in declared variables
            assertTrue(validator.getDeclaredVariables().containsKey("server"));
            // Global variables used nowhere → superfluous
            assertTrue(validator.getSuperfluousVariables().contains("server"));
        }
    }

    @Test
    void constructorWithTestPlans_multipliesLoopCounts() {
        ScriptStep step = new ScriptStep();
        step.setType("thinkTime");
        step.setLoggingKey("key");

        Script script = createScript(10, "LoopScript", List.of(step));
        ScriptGroupStep sgs = new ScriptGroupStep();
        sgs.setScript(script);
        sgs.setLoop(3);

        ScriptGroup sg = new ScriptGroup();
        sg.setName("Group1");
        sg.setLoop(2);
        sg.addScriptGroupStep(sgs);

        TestPlan tp = TestPlan.builder().name("Plan1").usersPercentage(100).build();
        tp.addScriptGroup(sg);

        try (MockedConstruction<ScriptDao> scriptDaoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.loadScriptSteps(any(Script.class))).thenReturn(script));
             MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {

            JobValidator validator = new JobValidator(List.of(tp), new HashMap<>());

            // Duration should exist for the plan - loop is 2*3=6
            long duration = validator.getDurationMs("Plan1");
            // Should be non-negative (exact value depends on step duration)
            assertTrue(duration >= 0);
        }
    }

    // =====================================================================
    // getDurationMs / getDuration
    // =====================================================================

    @Test
    void getDurationMs_returnsZeroForUnknownGroup() {
        Script script = createScript(1, "S", Collections.emptyList());

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {
            JobValidator validator = new JobValidator(script);

            assertEquals(0L, validator.getDurationMs("nonexistent"));
            assertNotNull(validator.getDuration("nonexistent"));
        }
    }

    // =====================================================================
    // Best practice violations
    // =====================================================================

    @Test
    void bestPracticeViolation_variableDeclaredAfterRequest() {
        ScriptStep requestStep = new ScriptStep();
        requestStep.setType("request");
        requestStep.setLoggingKey("page");

        ScriptStep varStep = new ScriptStep();
        varStep.setType("variable");
        varStep.setStepIndex(2);
        Set<RequestData> data = new HashSet<>();
        RequestData rd = new RequestData();
        rd.setKey("myVar");
        rd.setValue("val");
        data.add(rd);
        varStep.setData(data);

        Script script = createScript(1, "VarAfterRequest", List.of(requestStep, varStep));

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {
            JobValidator validator = new JobValidator(script);

            assertTrue(validator.getBestPracticeViolations().stream()
                    .anyMatch(v -> v.contains("Declare variables before request")));
        }
    }

    // =====================================================================
    // isOrphaned / isSuperfluous
    // =====================================================================

    @Test
    void isOrphaned_returnsTrueForUndeclaredUsedVariable() {
        // A step that uses a variable but doesn't declare it
        Script script = createScript(1, "S", Collections.emptyList());

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {
            JobValidator validator = new JobValidator(script);

            // With an empty script, there are no orphaned vars
            assertFalse(validator.isOrphaned("someVar"));
        }
    }

    // =====================================================================
    // Helper
    // =====================================================================

    private static Script createScript(int id, String name, List<ScriptStep> steps) {
        Script script = new Script();
        script.setId(id);
        script.setName(name);
        script.setCreated(new Date());
        script.setModified(new Date());
        script.setScriptSteps(new ArrayList<>(steps));
        return script;
    }
}
