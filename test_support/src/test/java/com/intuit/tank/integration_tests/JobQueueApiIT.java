package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Job preview, queue, live tree, details, time series and delete (React migration phase 3).
 * <p>
 * Jobs are only queued, never started, so no agents are launched on QA. Each queued job is deleted while it is
 * still in the Created state.
 */
public class JobQueueApiIT extends BaseIT {

    private static final int NO_SUCH_ID = 999_999_999;

    private final List<Integer> createdProjectIds = new ArrayList<>();
    private final List<Integer> queuedJobIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        for (Integer jobId : queuedJobIds) {
            try {
                send("DELETE", "/v2/jobs/" + jobId, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up job " + jobId + ": " + e.getMessage());
            }
        }
        queuedJobIds.clear();
        for (Integer projectId : createdProjectIds) {
            try {
                send("DELETE", "/v2/projects/" + projectId, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up project " + projectId + ": " + e.getMessage());
            }
        }
        createdProjectIds.clear();
    }

    @Test
    @Tag("integration")
    public void testPreview_computesTheJobWithoutQueueingIt() throws Exception {
        int projectId = createProject();
        String jobName = uniqueName("Preview job");

        JsonNode preview = expect(200, send("POST", "/v2/projects/" + projectId + "/jobs/preview",
                "{\"name\":\"" + jobName + "\"}"));

        assertTrue(preview.get("valid").asBoolean(), "The project should be ready to run: " + preview.get("errors"));
        assertEquals(jobName, preview.get("name").asText());
        assertEquals(10, preview.get("totalUsers").asInt(), "One region with 10 users");
        assertTrue(preview.get("simulationTimeMs").asLong() > 0);
        assertTrue(preview.hasNonNull("detailsHtml"), "Should include the job details shown before queueing");
        JsonNode tree = expect(200, get("/v2/jobs/tree?includeFinished=true&projectId=" + projectId));
        assertTrue(allJobs(tree).isEmpty(), "A preview must not queue a job");
    }

    @Test
    @Tag("integration")
    public void testPreview_defaultsTheJobName() throws Exception {
        int projectId = createProject();
        JsonNode preview = expect(200, send("POST", "/v2/projects/" + projectId + "/jobs/preview", null));
        assertFalse(preview.get("name").asText().isBlank(), "Without a name the server picks one");
    }

    @Test
    @Tag("integration")
    public void testQueueJob_appearsInTreeAndDetailsThenDeletes() throws Exception {
        int projectId = createProject();
        String jobName = uniqueName("Queued job");

        JsonNode queued = expect(201, send("POST", "/v2/projects/" + projectId + "/jobs", "{\"name\":\"" + jobName + "\"}"));
        int jobId = queued.get("jobId").asInt();
        queuedJobIds.add(jobId);
        assertEquals("Created", queued.get("status").asText());
        assertEquals(jobName, queued.get("name").asText());

        // live tree
        JsonNode tree = expect(200, get("/v2/jobs/tree?includeFinished=true&projectId=" + projectId));
        assertTrue(tree.hasNonNull("generatedAt"));
        JsonNode node = allJobs(tree).stream().filter(j -> j.get("jobId").asText().equals(Integer.toString(jobId)))
                .findFirst().orElseThrow(() -> new AssertionError("Job " + jobId + " should be in the tree: " + tree));
        assertEquals("Created", node.get("status").asText());
        assertTrue(node.get("actions").get("control").asBoolean(), "The owner may control the job");
        assertTrue(node.get("actions").get("delete").asBoolean(), "A job that has not started can be deleted");

        // details and series
        JsonNode details = expect(200, get("/v2/jobs/" + jobId + "/details"));
        assertEquals(jobId, details.get("jobId").asInt());
        assertEquals(currentUserName(), details.get("creator").asText(), "The caller should be recorded as creator");
        assertEquals(10, details.get("totalUsers").asInt());
        JsonNode users = expect(200, get("/v2/jobs/" + jobId + "/users-timeseries"));
        assertTrue(users.get("times").isArray() && users.get("series").isArray());
        JsonNode tps = expect(200, get("/v2/jobs/" + jobId + "/tps-timeseries"));
        assertTrue(tps.get("times").isArray() && tps.get("series").isArray());

        // delete while Created; a second delete finds it already deleted
        expect(204, send("DELETE", "/v2/jobs/" + jobId, null));
        queuedJobIds.remove(Integer.valueOf(jobId));
        expect(409, send("DELETE", "/v2/jobs/" + jobId, null));
    }

    @Test
    @Tag("integration")
    public void testJobActions_rejectUnknownActions() throws Exception {
        int projectId = createProject();
        JsonNode queued = expect(201, send("POST", "/v2/projects/" + projectId + "/jobs", null));
        int jobId = queued.get("jobId").asInt();
        queuedJobIds.add(jobId);

        expect(400, send("POST", "/v2/jobs/" + jobId + "/explode", null));
    }

    @Test
    @Tag("integration")
    public void testUnknownProjectsAndJobs_are404() throws Exception {
        expect(404, send("POST", "/v2/projects/" + NO_SUCH_ID + "/jobs/preview", null));
        expect(404, send("POST", "/v2/projects/" + NO_SUCH_ID + "/jobs", null));
        expect(404, get("/v2/jobs/" + NO_SUCH_ID + "/details"));
        expect(404, send("DELETE", "/v2/jobs/" + NO_SUCH_ID, null));
    }

    @Test
    @Tag("integration")
    public void testJobTreeWithoutCredentials_returns401() throws Exception {
        assertEquals(401, sendAnonymous("GET", "/v2/jobs/tree", null).statusCode());
    }

    private int createProject() throws Exception {
        int id = createRunnableProject(uniqueName("Job project"));
        createdProjectIds.add(id);
        return id;
    }

    /**
     * Every job node in the tree, whether grouped under a project or not.
     */
    private static List<JsonNode> allJobs(JsonNode tree) {
        List<JsonNode> jobs = new ArrayList<>();
        tree.get("projects").forEach(p -> p.get("jobs").forEach(jobs::add));
        tree.get("otherJobs").forEach(jobs::add);
        return jobs;
    }
}
