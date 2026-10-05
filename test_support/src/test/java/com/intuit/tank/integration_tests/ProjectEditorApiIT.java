package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Project editor endpoints: paged list, aggregate GET/PUT, validate, copy and bulk delete (React migration phase 2).
 */
public class ProjectEditorApiIT extends BaseIT {

    private static final int NO_SUCH_PROJECT = 999_999_999;

    private final List<Integer> createdProjectIds = new ArrayList<>();

    @AfterEach
    public void cleanup() throws Exception {
        for (Integer id : createdProjectIds) {
            try {
                send("DELETE", "/v2/projects/" + id, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up project " + id + ": " + e.getMessage());
            }
        }
        createdProjectIds.clear();
    }

    @Test
    @Tag("integration")
    public void testPagedList_findsProjectByName() throws Exception {
        String name = uniqueName("Paged");
        int id = createProject(name);

        JsonNode page = expect(200, get("/v2/projects?page=0&size=5&sort=name,asc&q=" + encode(name)));

        assertEquals(1, page.get("total").asInt(), "Exactly one project should match the unique name");
        assertEquals(0, page.get("page").asInt());
        assertEquals(5, page.get("size").asInt());
        JsonNode item = page.get("items").get(0);
        assertEquals(id, item.get("id").asInt());
        assertEquals(name, item.get("name").asText());
        assertTrue(item.hasNonNull("owner"), "Summaries should include the owner");
    }

    @Test
    @Tag("integration")
    public void testPagedList_rejectsBadPaging() throws Exception {
        expect(400, get("/v2/projects?page=-1"));
        expect(400, get("/v2/projects?page=0&size=1000"));
        expect(400, get("/v2/projects?page=0&sort=password"));
    }

    @Test
    @Tag("integration")
    public void testGetFull_returnsTheWholeProject() throws Exception {
        String name = uniqueName("Full");
        int id = createProject(name);

        JsonNode project = expect(200, get("/v2/projects/" + id + "/full"));

        assertEquals(name, project.get("name").asText());
        assertEquals(currentUserName(), project.get("owner").asText(), "The caller should own a project they created");
        assertTrue(project.hasNonNull("modified"), "Needed to save changes");
        assertTrue(project.get("settings").hasNonNull("incrementStrategy"));
        assertEquals("US_WEST_2", project.get("regions").get(0).get("region").asText());
        JsonNode script = project.get("testPlans").get(0).get("scriptGroups").get(0).get("scripts").get(0);
        assertEquals(QA_SCRIPT_ID, script.get("scriptId").asInt());
        assertTrue(project.get("permissions").get("edit").asBoolean(), "The owner can edit");
        assertEquals("value1", project.get("variables").get("testVar1").asText());
    }

    @Test
    @Tag("integration")
    public void testGetFull_unknownProjectIs404() throws Exception {
        expect(404, get("/v2/projects/" + NO_SUCH_PROJECT + "/full"));
    }

    @Test
    @Tag("integration")
    public void testPutFull_savesChangesAndRejectsStaleCopies() throws Exception {
        int id = createProject(uniqueName("Edit"));
        ObjectNode project = (ObjectNode) expect(200, get("/v2/projects/" + id + "/full"));
        JsonNode loadedModified = project.get("modified");

        // saves are compared to the second, so make sure this one gets a later modified time than the load
        Thread.sleep(1_100);
        project.put("comments", "edited by integration test");
        ((ObjectNode) project.get("variables")).put("addedVar", "added");
        JsonNode saved = expect(200, send("PUT", "/v2/projects/" + id + "/full", project.toString()));

        assertEquals("edited by integration test", saved.get("comments").asText());
        assertEquals("added", saved.get("variables").get("addedVar").asText());
        assertEquals("edited by integration test", expect(200, get("/v2/projects/" + id + "/full")).get("comments").asText(),
                "The change should be stored");

        // the copy loaded before the save is now stale
        project.set("modified", loadedModified);
        project.put("comments", "a second, conflicting edit");
        expect(409, send("PUT", "/v2/projects/" + id + "/full", project.toString()));
    }

    @Test
    @Tag("integration")
    public void testPutFull_validates() throws Exception {
        int id = createProject(uniqueName("Invalid"));
        ObjectNode project = (ObjectNode) expect(200, get("/v2/projects/" + id + "/full"));

        ObjectNode noModified = project.deepCopy();
        noModified.remove("modified");
        expect(400, send("PUT", "/v2/projects/" + id + "/full", noModified.toString()));

        ObjectNode blankName = project.deepCopy();
        blankName.put("name", " ");
        expect(400, send("PUT", "/v2/projects/" + id + "/full", blankName.toString()));

        ObjectNode badTime = project.deepCopy();
        ((ObjectNode) badTime.get("settings")).put("simulationTime", "not a time");
        expect(400, send("PUT", "/v2/projects/" + id + "/full", badTime.toString()));
    }

    @Test
    @Tag("integration")
    public void testValidate_reportsTotalsAndProblems() throws Exception {
        int id = createProject(uniqueName("Validate"));

        JsonNode validation = expect(200, get("/v2/projects/" + id + "/validate"));

        assertTrue(validation.has("valid"));
        assertTrue(validation.get("errors").isArray());
        assertTrue(validation.get("warnings").isArray());
        assertEquals(10, validation.get("totalUsers").asLong(), "One region with 10 users");
        assertTrue(validation.get("simulationTimeMs").asLong() > 0, "The simulation time should be computed");
        assertTrue(validation.get("rampTimeMs").asLong() > 0, "The ramp time should be computed");
        assertEquals("Main Test Plan", validation.get("testPlans").get(0).get("name").asText());
    }

    @Test
    @Tag("integration")
    public void testCopy_createsAProjectOwnedByTheCaller() throws Exception {
        int sourceId = createProject(uniqueName("Source"));
        String copyName = uniqueName("Copy");

        JsonNode copy = expect(201, send("POST", "/v2/projects/" + sourceId + "/copy", "{\"name\":\"" + copyName + "\"}"));
        int copyId = copy.get("id").asInt();
        createdProjectIds.add(copyId);

        assertNotEquals(sourceId, copyId);
        assertEquals(copyName, copy.get("name").asText());
        assertEquals(currentUserName(), copy.get("owner").asText());
        assertEquals(QA_SCRIPT_ID, copy.get("testPlans").get(0).get("scriptGroups").get(0).get("scripts").get(0)
                .get("scriptId").asInt(), "The copy should hold the same scripts");

        expect(409, send("POST", "/v2/projects/" + sourceId + "/copy", "{\"name\":\"" + copyName + "\"}"));
        expect(400, send("POST", "/v2/projects/" + sourceId + "/copy", "{\"name\":\" \"}"));
        expect(404, send("POST", "/v2/projects/" + NO_SUCH_PROJECT + "/copy", "{\"name\":\"" + uniqueName("Missing") + "\"}"));
    }

    @Test
    @Tag("integration")
    public void testBulkDelete_reportsDeletedAndMissing() throws Exception {
        int first = createProject(uniqueName("BulkA"));
        int second = createProject(uniqueName("BulkB"));

        JsonNode result = expect(200, send("DELETE", "/v2/projects?ids=" + first + "," + second + "," + NO_SUCH_PROJECT, null));

        assertEquals(java.util.Set.of(first, second), new java.util.HashSet<>(ids(result.get("deleted"))));
        assertEquals(List.of(NO_SUCH_PROJECT), ids(result.get("notFound")));
        expect(404, get("/v2/projects/" + first + "/full"));
        createdProjectIds.removeAll(List.of(first, second));
    }

    private int createProject(String name) throws Exception {
        int id = createRunnableProject(name);
        createdProjectIds.add(id);
        return id;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static List<Integer> ids(JsonNode array) {
        List<Integer> ids = new ArrayList<>();
        array.forEach(n -> ids.add(n.asInt()));
        return ids;
    }
}
