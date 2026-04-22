package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.*;
import com.intuit.tank.projects.models.ProjectTO;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceUtilTest {

    @Test
    void projectToTransferObject_convertsBasicFields() {
        Project project = createProject(1, "TestProject", "System");

        ProjectTO to = ProjectServiceUtil.projectToTransferObject(project);

        assertEquals(1, to.getId());
        assertEquals("TestProject", to.getName());
        assertEquals("System", to.getCreator());
    }

    @Test
    void projectToTransferObject_convertsJobConfiguration() {
        Project project = createProject(2, "Configured", "admin");
        JobConfiguration config = project.getWorkloads().get(0).getJobConfiguration();
        config.setRampTimeExpression("60s");
        config.setSimulationTimeExpression("300s");
        config.setStopBehavior("END_OF_SCRIPT_GROUP");
        config.setLocation("us-west-2");
        config.setUserIntervalIncrement(5);

        ProjectTO to = ProjectServiceUtil.projectToTransferObject(project);

        assertEquals("60s", to.getRampTime());
        assertEquals("END_OF_SCRIPT_GROUP", to.getStopBehavior());
        assertEquals("us-west-2", to.getLocation());
        assertEquals(5, to.getUserIntervalIncrement());
    }

    @Test
    void projectToTransferObject_convertsTestPlans() {
        Project project = createProject(3, "WithPlans", "user");
        Workload workload = project.getWorkloads().get(0);

        // Add a script group to the test plan
        Script script = new Script();
        script.setId(10);
        script.setName("LoginScript");

        ScriptGroupStep sgs = new ScriptGroupStep();
        sgs.setScript(script);
        sgs.setLoop(2);

        ScriptGroup sg = new ScriptGroup();
        sg.setName("LoginGroup");
        sg.setLoop(1);
        sg.addScriptGroupStep(sgs);

        workload.getTestPlans().get(0).addScriptGroup(sg);

        ProjectTO to = ProjectServiceUtil.projectToTransferObject(project);

        assertNotNull(to.getTestPlans());
        assertFalse(to.getTestPlans().isEmpty());
        assertEquals("Main", to.getTestPlans().get(0).getName());
        assertEquals(1, to.getTestPlans().get(0).getScriptGroups().size());
        assertEquals("LoginGroup", to.getTestPlans().get(0).getScriptGroups().get(0).getName());
        assertEquals(10, to.getTestPlans().get(0).getScriptGroups().get(0).getScripts().get(0).getScriptId());
    }

    @Test
    void projectToTransferObject_convertsVariables() {
        Project project = createProject(4, "WithVars", "admin");
        JobConfiguration config = project.getWorkloads().get(0).getJobConfiguration();
        config.getVariables().put("env", "staging");
        config.getVariables().put("timeout", "30");

        ProjectTO to = ProjectServiceUtil.projectToTransferObject(project);

        assertNotNull(to.getVariables());
        assertEquals(2, to.getVariables().size());
    }

    @Test
    void projectToTransferObject_convertsDataFileIds() {
        Project project = createProject(5, "WithDataFiles", "admin");
        JobConfiguration config = project.getWorkloads().get(0).getJobConfiguration();
        config.setDataFileIds(Set.of(1, 2, 3));

        ProjectTO to = ProjectServiceUtil.projectToTransferObject(project);

        assertNotNull(to.getDataFileIds());
        assertEquals(3, to.getDataFileIds().size());
    }

    @Test
    void projectToTransferObject_handlesEmptyTestPlans() {
        Project project = createProject(6, "NoPlans", "admin");
        project.getWorkloads().get(0).getTestPlans().clear();

        ProjectTO to = ProjectServiceUtil.projectToTransferObject(project);
        assertTrue(to.getTestPlans() == null || to.getTestPlans().isEmpty());
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private static Project createProject(int id, String name, String creator) {
        Project p = new Project();
        p.setId(id);
        p.setName(name);
        p.setCreator(creator);
        p.setCreated(new Date());
        p.setModified(new Date());

        Workload workload = new Workload();
        workload.setName(name);
        JobConfiguration jc = new JobConfiguration();
        workload.setJobConfiguration(jc);
        jc.setParent(workload);

        TestPlan tp = TestPlan.builder().name("Main").usersPercentage(100).build();
        workload.addTestPlan(tp);

        List<Workload> workloads = new ArrayList<>();
        workloads.add(workload);
        p.setWorkloads(workloads);
        workload.setParent(p);

        return p;
    }
}
