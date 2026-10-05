package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Script editor endpoints: paged list, step document GET/PUT, copy, recorded responses, the stateless draft
 * operations (search, replace, apply filters, validate), the logic step tester and recording uploads with
 * filters (React migration phase 5).
 * <p>
 * The logic tests only send scripts that finish at once: a script that never ends keeps a thread busy on the QA
 * controller after the 5 second limit.
 */
public class ScriptEditorApiIT extends BaseIT {

    private static final int NO_SUCH_ID = 999_999_999;
    /** The host every request step in Sample_TS.xml calls. */
    private static final String SAMPLE_HOST = "api.example.com";

    private final List<Integer> createdScriptIds = new ArrayList<>();
    private final List<Integer> createdFilterIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        for (Integer id : createdScriptIds) {
            try {
                send("DELETE", "/v2/scripts/" + id, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up script " + id + ": " + e.getMessage());
            }
        }
        createdScriptIds.clear();
        for (Integer id : createdFilterIds) {
            try {
                send("DELETE", "/v2/filters/" + id, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up filter " + id + ": " + e.getMessage());
            }
        }
        createdFilterIds.clear();
    }

    // ---------------------------------------------------------------- the step document

    @Test
    @Tag("integration")
    public void testGetSteps_returnsTheWholeScript() throws Exception {
        int id = uploadSampleScript();

        JsonNode script = expect(200, get("/v2/scripts/" + id + "/steps"));

        assertEquals(id, script.get("id").asInt());
        assertEquals(currentUserName(), script.get("owner").asText(), "The uploader owns the script");
        assertTrue(script.hasNonNull("modified"), "Needed to save changes");
        assertTrue(script.get("permissions").get("edit").asBoolean());
        JsonNode steps = script.get("steps");
        assertTrue(steps.size() >= 2, "Sample_TS.xml has several steps");
        assertEquals("request", steps.get(0).get("type").asText());
        assertEquals(SAMPLE_HOST, steps.get(0).get("hostname").asText());
        expect(404, get("/v2/scripts/" + NO_SUCH_ID + "/steps"));
    }

    @Test
    @Tag("integration")
    public void testPutSteps_savesRenumbersAndRejectsStaleCopies() throws Exception {
        int id = uploadSampleScript();
        ObjectNode script = (ObjectNode) expect(200, get("/v2/scripts/" + id + "/steps"));
        JsonNode loadedModified = script.get("modified");
        String newName = uniqueName("Script edit");

        // saves are compared to the second, so make sure this one gets a later modified time than the upload
        Thread.sleep(1_100);
        script.put("name", newName);
        script.put("comments", "edited by integration test");
        ArrayNode steps = (ArrayNode) script.get("steps");
        steps.add(steps.get(0).deepCopy());
        ((ObjectNode) steps.get(steps.size() - 1)).remove("uuid");
        JsonNode saved = expect(200, send("PUT", "/v2/scripts/" + id + "/steps", script.toString()));

        assertEquals(newName, saved.get("name").asText());
        assertEquals("edited by integration test", saved.get("comments").asText());
        assertEquals(steps.size(), saved.get("steps").size(), "The added step should be saved");
        for (int i = 0; i < saved.get("steps").size(); i++) {
            assertEquals(i + 1, saved.get("steps").get(i).get("stepIndex").asInt(), "Steps are renumbered from 1 in order");
        }
        assertTrue(saved.get("steps").get(steps.size() - 1).hasNonNull("uuid"), "A new step gets a uuid");

        script.set("modified", loadedModified);
        expect(409, send("PUT", "/v2/scripts/" + id + "/steps", script.toString()));
        script.remove("modified");
        expect(400, send("PUT", "/v2/scripts/" + id + "/steps", script.toString()));
    }

    @Test
    @Tag("integration")
    public void testPagedList_findsRenamedScript() throws Exception {
        int id = uploadSampleScript();
        String name = rename(id, uniqueName("Script paged"));

        JsonNode page = expect(200, get("/v2/scripts?page=0&size=5&q=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));

        assertEquals(1, page.get("total").asInt());
        assertEquals(id, page.get("items").get(0).get("id").asInt());
        assertEquals(currentUserName(), page.get("items").get(0).get("owner").asText());
        expect(400, get("/v2/scripts?page=0&sort=password"));
    }

    @Test
    @Tag("integration")
    public void testCopy_createsAScriptOwnedByTheCaller() throws Exception {
        int id = uploadSampleScript();
        String copyName = uniqueName("Script copy");

        JsonNode copy = expect(201, send("POST", "/v2/scripts/" + id + "/copy", "{\"name\":\"" + copyName + "\"}"));
        int copyId = copy.get("id").asInt();
        createdScriptIds.add(copyId);

        assertNotEquals(id, copyId);
        assertEquals(copyName, copy.get("name").asText());
        assertEquals(currentUserName(), copy.get("owner").asText());
        assertEquals(expect(200, get("/v2/scripts/" + id + "/steps")).get("steps").size(),
                expect(200, get("/v2/scripts/" + copyId + "/steps")).get("steps").size(), "The copy has the same steps");
        expect(400, send("POST", "/v2/scripts/" + id + "/copy", "{\"name\":\" \"}"));
        expect(404, send("POST", "/v2/scripts/" + NO_SUCH_ID + "/copy", "{\"name\":\"" + uniqueName("Missing") + "\"}"));
    }

    @Test
    @Tag("integration")
    public void testStepResponse_missingIs404() throws Exception {
        int id = uploadSampleScript();
        expect(404, get("/v2/scripts/" + id + "/steps/no-such-step/response"));
    }

    // ---------------------------------------------------------------- draft operations

    @Test
    @Tag("integration")
    public void testSearch_findsMatchesInADraft() throws Exception {
        JsonNode steps = sampleSteps();

        JsonNode matches = expect(200, send("POST", "/v2/scripts/steps/search",
                draft(steps).put("query", SAMPLE_HOST).set("sections", list("host")).toString()));

        assertFalse(matches.isEmpty(), "Every request step uses " + SAMPLE_HOST);
        for (JsonNode match : matches) {
            assertEquals("host", match.get("section").asText());
            assertTrue(match.get("value").asText().contains(SAMPLE_HOST));
            assertTrue(match.hasNonNull("uuid"));
        }
        expect(400, send("POST", "/v2/scripts/steps/search",
                draft(steps).put("query", SAMPLE_HOST).set("sections", list("no-such-section")).toString()));
        expect(400, send("POST", "/v2/scripts/steps/search",
                draft(steps).put("query", "").set("sections", list("host")).toString()));
    }

    @Test
    @Tag("integration")
    public void testReplace_changesTheDraftOnly() throws Exception {
        int id = uploadSampleScript();
        JsonNode steps = expect(200, get("/v2/scripts/" + id + "/steps")).get("steps");

        ObjectNode request = draft(steps).put("query", SAMPLE_HOST).put("replacement", "replaced.example.com")
                .put("mode", "VALUE");
        request.set("sections", list("host"));
        JsonNode result = expect(200, send("POST", "/v2/scripts/steps/replace", request.toString()));

        assertTrue(result.get("changed").asInt() > 0);
        assertEquals("replaced.example.com", result.get("steps").get(0).get("hostname").asText());
        assertEquals(SAMPLE_HOST, expect(200, get("/v2/scripts/" + id + "/steps")).get("steps").get(0).get("hostname").asText(),
                "Replacing in a draft must not change the saved script");

        request.put("mode", "SIDEWAYS");
        expect(400, send("POST", "/v2/scripts/steps/replace", request.toString()));
    }

    @Test
    @Tag("integration")
    public void testApplyFilters_runsSavedFiltersOverADraft() throws Exception {
        int filterId = createHostFilter("filtered.example.com");
        JsonNode steps = sampleSteps();

        ObjectNode request = draft(steps);
        request.set("filterIds", objectMapper.createArrayNode().add(filterId));
        JsonNode result = expect(200, send("POST", "/v2/scripts/steps/apply-filters", request.toString()));

        assertTrue(result.get("changed").asInt() > 0, "The filter matches the /health step");
        assertEquals("filtered.example.com", result.get("steps").get(0).get("hostname").asText());

        request.set("filterIds", objectMapper.createArrayNode().add(NO_SUCH_ID));
        expect(400, send("POST", "/v2/scripts/steps/apply-filters", request.toString()));
        request.set("filterIds", objectMapper.createArrayNode());
        expect(400, send("POST", "/v2/scripts/steps/apply-filters", request.toString()));
    }

    @Test
    @Tag("integration")
    public void testValidate_estimatesTheDraft() throws Exception {
        ObjectNode request = draft(sampleSteps()).put("name", "draft");

        JsonNode validation = expect(200, send("POST", "/v2/scripts/steps/validate", request.toString()));

        assertTrue(validation.get("durationMs").asLong() >= 0);
        assertTrue(validation.get("warnings").isArray());
        assertTrue(validation.hasNonNull("detailsHtml"));
    }

    // ---------------------------------------------------------------- logic steps

    @Test
    @Tag("integration")
    public void testLogicStep_runsScriptWithVariables() throws Exception {
        String body = """
                {"script": "variables.addVariable('itResult', 'ok-' + variables.getVariable('itInput'));",
                 "variables": {"itInput": "hello"}}
                """;

        JsonNode result = expect(200, send("POST", "/v2/scripts/logic/test", body));

        assertFalse(result.get("timedOut").asBoolean());
        assertTrue(result.get("output").asText().contains("itResult = ok-hello"), result.get("output").asText());
    }

    @Test
    @Tag("integration")
    public void testLogicStep_hasNoJavaAccess() throws Exception {
        String body = "{\"script\": \"java.lang.System.getProperty('user.home');\"}";

        JsonNode result = expect(200, send("POST", "/v2/scripts/logic/test", body));

        assertTrue(result.get("output").asText().contains("Exception thrown"),
                "Scripts must not reach Java classes: " + result.get("output").asText());
    }

    @Test
    @Tag("integration")
    public void testLogicStep_rejectsEmptyAndOversizedScripts() throws Exception {
        expect(400, send("POST", "/v2/scripts/logic/test", "{\"script\": \"\"}"));
        expect(400, send("POST", "/v2/scripts/logic/test",
                objectMapper.createObjectNode().put("script", "x".repeat(65 * 1024)).toString()));
    }

    // ---------------------------------------------------------------- recording uploads

    @Test
    @Tag("integration")
    public void testRecordingUpload_setsProductAndAppliesFilters() throws Exception {
        int filterId = createHostFilter("recorded.example.com");
        String name = uniqueName("Recording");
        byte[] recording = loadResourceFileAsBytes("testfiles/Sample_Proxy_Recording.xml");

        HttpResponse<String> response = postFiles("/v2/scripts?recording&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
                + "&productName=IT-Recording&filterIds=" + filterId, "file", Map.of("Sample_Proxy_Recording.xml", recording));
        int id = Integer.parseInt(expect(201, response).get("scriptId").asText());
        createdScriptIds.add(id);

        JsonNode script = expect(200, get("/v2/scripts/" + id + "/steps"));
        assertEquals("IT-Recording", script.get("productName").asText());

        HttpResponse<String> unknownFilter = postFiles("/v2/scripts?recording&name=" + URLEncoder.encode(uniqueName("Recording"),
                StandardCharsets.UTF_8) + "&filterIds=" + NO_SUCH_ID, "file", Map.of("Sample_Proxy_Recording.xml", recording));
        assertEquals(400, unknownFilter.statusCode(), "Unknown filter IDs are rejected: " + unknownFilter.body());
    }

    // ---------------------------------------------------------------- helpers

    /**
     * Uploads testfiles/Sample_TS.xml as a new script owned by the caller.
     */
    private int uploadSampleScript() throws Exception {
        HttpResponse<String> response = postFiles("/v2/scripts", "file",
                Map.of("Sample_TS.xml", loadResourceFileAsBytes("testfiles/Sample_TS.xml")));
        JsonNode body = expect(201, response);
        int id;
        if (body.hasNonNull("scriptId")) {
            id = Integer.parseInt(body.get("scriptId").asText());
        } else {
            Matcher m = Pattern.compile("script ID (\\d+)").matcher(body.get("message").asText());
            assertTrue(m.find(), "The upload message should name the script ID: " + body);
            id = Integer.parseInt(m.group(1));
        }
        createdScriptIds.add(id);
        return id;
    }

    private String rename(int id, String name) throws Exception {
        Thread.sleep(1_100);
        ObjectNode script = (ObjectNode) expect(200, get("/v2/scripts/" + id + "/steps"));
        script.put("name", name);
        expect(200, send("PUT", "/v2/scripts/" + id + "/steps", script.toString()));
        return name;
    }

    private JsonNode sampleSteps() throws Exception {
        return expect(200, get("/v2/scripts/" + uploadSampleScript() + "/steps")).get("steps");
    }

    /**
     * An internal filter that sends every step whose path contains /health to the given host.
     */
    private int createHostFilter(String host) throws Exception {
        String body = """
                {
                  "name": "%s",
                  "productName": "IntegrationTest",
                  "allConditionsMustPass": true,
                  "conditions": [{ "scope": "path", "condition": "contains", "value": "/health" }],
                  "actions": [{ "action": "replace", "scope": "host", "value": "%s" }]
                }
                """.formatted(uniqueName("Host filter"), host);
        int id = expect(201, send("POST", "/v2/filters", body)).get("id").asInt();
        createdFilterIds.add(id);
        return id;
    }

    private ObjectNode draft(JsonNode steps) {
        ObjectNode request = objectMapper.createObjectNode();
        request.set("steps", steps);
        return request;
    }

    private ArrayNode list(String... values) {
        ArrayNode array = objectMapper.createArrayNode();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }
}
