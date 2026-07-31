package com.intuit.tank.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates two job-creation configurations against the packaged WAR.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobCreationIT {

    static {
        try {
            TankIntegrationEnvironment.installSettingsRoot();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String UI_USER = "admin";
    private static final String UI_PASSWORD = "admin";
    private static final String BUILD_USER = "system-test";
    private static final List<String> SCRIPT_ROLES = List.of(
            "setup", "new-user", "transition", "return-user", "cleanup");

    private TankIntegrationEnvironment env;
    private TankApiClient api;
    private TankUiBrowser uiBrowser;
    private final List<Integer> scriptIdsToDelete = new ArrayList<>();
    private final List<Integer> projectIdsToDelete = new ArrayList<>();
    private Path artifactDir;

    @BeforeAll
    void startEnvironment() {
        env = TankIntegrationEnvironment.start();
        api = new TankApiClient(env);
        uiBrowser = new TankUiBrowser(env.baseUrl(), UI_USER, UI_PASSWORD);
        artifactDir = Path.of("target", "playwright-artifacts");
        try {
            Files.createDirectories(artifactDir);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create Playwright artifact directory", e);
        }
    }

    @AfterEach
    void cleanupScenario() {
        if (api != null) {
            for (int i = projectIdsToDelete.size() - 1; i >= 0; i--) {
                api.deleteProject(projectIdsToDelete.get(i));
            }
            projectIdsToDelete.clear();
            for (int i = scriptIdsToDelete.size() - 1; i >= 0; i--) {
                api.deleteScript(scriptIdsToDelete.get(i));
            }
            scriptIdsToDelete.clear();
        }
    }

    @AfterAll
    void stopEnvironment() {
        if (uiBrowser != null) {
            uiBrowser.close();
        }
        if (env != null) {
            env.close();
        }
    }

    @Test
    @Tag("system-integration")
    void createIncreasingJob_persistsCreatedState_andShowsInUi() throws Exception {
        try {
            Scenario scenario = createScenario("increasing");
            String jobName = "system_test_job_" + BUILD_USER + "_"
                    + Instant.now().toEpochMilli();
            TankApiClient.CreatedJob created = api.createJobFromContract(
                    JobCreationContract.Profile.INCREASING, scenario.projectId(), jobName);
            JsonNode job = assertCreatedJob(created, jobName);
            assertEquals(JobCreationContract.INCREASING_CONFIGURED_USERS, job.get("numUsers").asInt(),
                    "Configured users must match the increasing profile");

            TankApiClient.PersistedJobConfiguration config =
                    api.queryJobConfiguration(created.jobId());
            assertEquals("increasing", config.workloadType(),
                    "Persisted workload_type must remain increasing");
            assertEquals(0, api.getVmStatusCount(created.jobId()),
                    "Create-only flow must not launch agent VMs");

            confirmJobInUi(created.jobId(), scenario.projectName(), jobName, "Created",
                    String.valueOf(JobCreationContract.INCREASING_CONFIGURED_USERS));
        } catch (Throwable failure) {
            env.retainLogsOnFailure(failure);
            throw failure;
        }
    }

    @Test
    @Tag("system-integration")
    void createStandardJob_persistsRates_andShowsInUi() throws Exception {
        try {
            Scenario scenario = createScenario("standard");
            String jobName = "system_test_job_" + BUILD_USER + "_"
                    + Instant.now().toEpochMilli();
            TankApiClient.CreatedJob created = api.createJobFromContract(
                    JobCreationContract.Profile.STANDARD, scenario.projectId(), jobName);
            assertCreatedJob(created, jobName);

            TankApiClient.PersistedJobConfiguration config =
                    api.queryJobConfiguration(created.jobId());
            assertEquals("standard", config.workloadType(),
                    "Persisted workload_type must remain standard");
            assertEquals(Double.parseDouble(JobCreationContract.STANDARD_TARGET_RAMP_RATE),
                    config.targetRampRate(), 0.001,
                    "Target ramp rate must match the standard profile");
            assertEquals(Double.parseDouble(JobCreationContract.STANDARD_TARGET_RATE_PER_AGENT),
                    config.targetRatePerAgent(), 0.001,
                    "Target rate per agent must match the standard profile");
            assertEquals(0, api.getVmStatusCount(created.jobId()),
                    "Create-only flow must not launch agent VMs");

            String uiJobName = jobName + "_ui";
            uiBrowser.createJobFromProject(artifactDir, scenario.projectName(), uiJobName,
                    "Workload Type: Nonlinear",
                    "Max Users/Sec Per Agent: 0.191",
                    "Target Users Per Second: 0.38");

            int uiJobId = api.findJobIdByName(uiJobName);
            JsonNode uiJob = api.getJob(uiJobId);
            assertEquals(uiJobName, uiJob.get("name").asText());
            assertEquals("Created", uiJob.get("status").asText());

            TankApiClient.PersistedJobConfiguration uiConfig =
                    api.queryJobConfiguration(uiJobId);
            assertEquals("standard", uiConfig.workloadType(),
                    "UI-created job must preserve standard workload_type");
            assertEquals(Double.parseDouble(JobCreationContract.STANDARD_TARGET_RAMP_RATE),
                    uiConfig.targetRampRate(), 0.001,
                    "UI-created job must preserve target ramp rate");
            assertEquals(Double.parseDouble(JobCreationContract.STANDARD_TARGET_RATE_PER_AGENT),
                    uiConfig.targetRatePerAgent(), 0.001,
                    "UI-created job must preserve target rate per agent");
            assertEquals(0, api.getVmStatusCount(uiJobId),
                    "UI create-only flow must not launch agent VMs");

            confirmJobInUi(uiJobId, scenario.projectName(), uiJobName, "Created", null);
        } catch (Throwable failure) {
            env.retainLogsOnFailure(failure);
            throw failure;
        }
    }

    private Scenario createScenario(String profileLabel) throws Exception {
        String suffix = Instant.now().toEpochMilli() + "-"
                + UUID.randomUUID().toString().substring(0, 8);
        List<Integer> scenarioScriptIds = new ArrayList<>();
        for (String role : SCRIPT_ROLES) {
            int scriptId = api.uploadScript(
                    "it-" + profileLabel + "-" + role + "-" + suffix);
            scenarioScriptIds.add(scriptId);
            scriptIdsToDelete.add(scriptId);
        }

        String projectName = "it-" + profileLabel + "-project-" + suffix;
        int projectId = api.createProject(projectName, scenarioScriptIds);
        projectIdsToDelete.add(projectId);
        assertEquals(scenarioScriptIds, api.getProjectScriptIds(projectId),
                "Project must preserve all five automation-style scripts in order");
        return new Scenario(suffix, projectName, projectId);
    }

    private JsonNode assertCreatedJob(TankApiClient.CreatedJob created, String jobName)
            throws Exception {
        assertEquals("created", created.createStatus().toLowerCase());
        assertTrue(created.jobId() > 0, "JobId should be positive");

        JsonNode job = api.getJob(created.jobId());
        assertEquals(created.jobId(), job.get("id").asInt());
        assertEquals(jobName, job.get("name").asText());
        assertEquals("Created", job.get("status").asText());
        return job;
    }

    private void confirmJobInUi(int jobId, String projectName, String expectedName,
                                String expectedStatus, String expectedUsers) {
        uiBrowser.confirmJobInQueue(artifactDir, jobId, projectName, expectedName,
                expectedStatus, expectedUsers);
    }

    private record Scenario(String suffix, String projectName, int projectId) {}
}
