package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Admin endpoints: users, API tokens, preferences, groups, log files and the log level (React migration phase 7).
 * <p>
 * Needs the integration test user to be an admin; otherwise every test is skipped. Users are created with unique
 * "it-admin-" names and deleted afterwards. The log level is only ever set to the level it already has.
 */
public class AdminApiIT extends BaseIT {

    private static final int NO_SUCH_ID = 999_999_999;
    private static final String PASSWORD = "it-password-123";

    private final List<Integer> createdUserIds = new ArrayList<>();
    private final List<Integer> createdProjectIds = new ArrayList<>();

    @BeforeEach
    public void requireAdmin() throws Exception {
        assumeTrue(expect(200, get("/v2/me")).get("admin").asBoolean(),
                "The integration test user is not an admin on this environment");
    }

    @AfterEach
    public void cleanup() {
        for (Integer id : createdProjectIds) {
            try {
                send("DELETE", "/v2/projects/" + id, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up project " + id + ": " + e.getMessage());
            }
        }
        createdProjectIds.clear();
        for (Integer id : createdUserIds) {
            try {
                send("DELETE", "/v2/admin/users/" + id, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up user " + id + ": " + e.getMessage());
            }
        }
        createdUserIds.clear();
    }

    // ---------------------------------------------------------------- users

    @Test
    @Tag("integration")
    public void testCreateGetListUpdateAndDeleteUser() throws Exception {
        String name = userName();
        JsonNode created = expect(201, send("POST", "/v2/admin/users", userBody(name).toString()));
        int id = created.get("id").asInt();
        createdUserIds.add(id);
        assertEquals(name, created.get("name").asText());
        assertFalse(created.get("groups").isEmpty(), "Without groups the user gets the default groups");
        assertFalse(created.get("hasApiToken").asBoolean());
        assertFalse(created.has("password"), "Never return the password hash");

        assertEquals(name, expect(200, get("/v2/admin/users/" + id)).get("name").asText());
        JsonNode page = expect(200, get("/v2/admin/users?page=0&size=5&q=" + URLEncoder.encode(name, StandardCharsets.UTF_8)));
        assertEquals(1, page.get("total").asInt());
        assertEquals(id, page.get("items").get(0).get("id").asInt());

        String group = firstGroup(false);
        ObjectNode update = objectMapper.createObjectNode().put("email", name + "@changed.example.com");
        update.set("groups", objectMapper.createArrayNode().add(group));
        JsonNode updated = expect(200, send("PUT", "/v2/admin/users/" + id, update.toString()));
        assertEquals(name + "@changed.example.com", updated.get("email").asText());
        assertEquals(List.of(group), texts(updated.get("groups")));

        expect(204, send("DELETE", "/v2/admin/users/" + id, null));
        createdUserIds.remove(Integer.valueOf(id));
        expect(404, get("/v2/admin/users/" + id));
    }

    @Test
    @Tag("integration")
    public void testCreateUser_validates() throws Exception {
        String name = userName();
        int id = createUser(name);

        expect(409, send("POST", "/v2/admin/users", userBody(name).toString()));
        expect(400, send("POST", "/v2/admin/users", userBody(userName()).put("password", "short").toString()));
        expect(400, send("POST", "/v2/admin/users", userBody(userName()).put("email", "not-an-email").toString()));
        ObjectNode unknownGroup = userBody(userName());
        unknownGroup.set("groups", objectMapper.createArrayNode().add("no-such-group"));
        expect(400, send("POST", "/v2/admin/users", unknownGroup.toString()));
        expect(400, send("PUT", "/v2/admin/users/" + id, "{\"name\":\"renamed\"}"));
        expect(404, send("PUT", "/v2/admin/users/" + NO_SUCH_ID, "{\"email\":\"x@example.com\"}"));
    }

    @Test
    @Tag("integration")
    public void testDeleteUser_refusedWhileOwningProjects() throws Exception {
        String name = userName();
        int userId = createUser(name);
        int projectId = createRunnableProject(uniqueName("Admin owned"));
        createdProjectIds.add(projectId);
        ObjectNode project = (ObjectNode) expect(200, get("/v2/projects/" + projectId + "/full"));
        project.put("owner", name);
        expect(200, send("PUT", "/v2/projects/" + projectId + "/full", project.toString()));

        JsonNode conflict = expect(409, send("DELETE", "/v2/admin/users/" + userId, null));
        assertTrue(conflict.toString().contains(project.get("name").asText()), "The error should list the project: " + conflict);

        expect(204, send("DELETE", "/v2/projects/" + projectId, null));
        createdProjectIds.remove(Integer.valueOf(projectId));
        expect(204, send("DELETE", "/v2/admin/users/" + userId, null));
        createdUserIds.remove(Integer.valueOf(userId));
    }

    @Test
    @Tag("integration")
    public void testAdminCannotDeleteThemselves() throws Exception {
        String me = currentUserName();
        JsonNode page = expect(200, get("/v2/admin/users?page=0&size=50&q=" + URLEncoder.encode(me, StandardCharsets.UTF_8)));
        int myId = -1;
        for (JsonNode user : page.get("items")) {
            if (me.equals(user.get("name").asText())) {
                myId = user.get("id").asInt();
            }
        }
        assertNotEquals(-1, myId, "The current user should be listed");
        expect(400, send("DELETE", "/v2/admin/users/" + myId, null));
    }

    // ---------------------------------------------------------------- tokens and preferences

    @Test
    @Tag("integration")
    public void testApiToken_worksOnceIssuedAndNonAdminsAreRefused() throws Exception {
        int id = createUser(userName());

        String token = expect(200, send("POST", "/v2/admin/users/" + id + "/api-token", null)).get("apiToken").asText();
        JsonNode user = expect(200, get("/v2/admin/users/" + id));
        assertTrue(user.get("hasApiToken").asBoolean());
        assertEquals(token.substring(token.length() - 4), user.get("apiTokenHint").asText(), "Only the hint is shown");
        assertFalse(user.toString().contains(token), "The token itself is never listed");

        // the new user is not an admin
        assertEquals(200, sendAs(token, "/v2/me").statusCode(), "The issued token should sign the user in");
        assertEquals(403, sendAs(token, "/v2/admin/users?page=0").statusCode(), "Admin pages are for admins only");

        expect(204, send("DELETE", "/v2/admin/users/" + id + "/api-token", null));
        assertFalse(expect(200, get("/v2/admin/users/" + id)).get("hasApiToken").asBoolean());
        assertEquals(401, sendAs(token, "/v2/me").statusCode(), "A deleted token no longer works");
    }

    @Test
    @Tag("integration")
    public void testResetPreferences() throws Exception {
        int id = createUser(userName());
        expect(204, send("DELETE", "/v2/admin/users/" + id + "/preferences", null));
        expect(404, send("DELETE", "/v2/admin/users/" + NO_SUCH_ID + "/preferences", null));
    }

    // ---------------------------------------------------------------- groups and logs

    @Test
    @Tag("integration")
    public void testGroups_includeAdminAndDefaults() throws Exception {
        JsonNode groups = expect(200, get("/v2/admin/groups"));

        assertTrue(texts(groups, "name").contains("admin"));
        boolean anyDefault = false;
        for (JsonNode group : groups) {
            // Jackson may name an is-prefixed boolean record component either way
            anyDefault |= group.path("isDefault").asBoolean() || group.path("default").asBoolean();
        }
        assertTrue(anyDefault, "At least one group is given to new users");
    }

    @Test
    @Tag("integration")
    public void testLogFiles_listedAndReadable() throws Exception {
        JsonNode files = expect(200, get("/v2/admin/logs"));

        assertFalse(files.isEmpty(), "The controller writes log files");
        String first = files.get(0).asText();
        HttpResponse<String> log = get("/v2/logs/" + URLEncoder.encode(first, StandardCharsets.UTF_8).replace("+", "%20"));
        assertEquals(200, log.statusCode(), "A listed file can be read: " + first);
    }

    @Test
    @Tag("integration")
    public void testLogLevel_readAndSetToTheSameLevel() throws Exception {
        JsonNode current = expect(200, get("/v2/admin/log-level"));
        String level = current.get("level").asText();
        assertTrue(current.hasNonNull("node"), "Says which controller node answered");

        JsonNode set = expect(200, send("PUT", "/v2/admin/log-level", "{\"level\":\"" + level.toLowerCase() + "\"}"));
        assertEquals(level, set.get("level").asText());
        expect(400, send("PUT", "/v2/admin/log-level", "{\"level\":\"LOUD\"}"));
    }

    // ---------------------------------------------------------------- helpers

    private int createUser(String name) throws Exception {
        int id = expect(201, send("POST", "/v2/admin/users", userBody(name).toString())).get("id").asInt();
        createdUserIds.add(id);
        return id;
    }

    private ObjectNode userBody(String name) {
        return objectMapper.createObjectNode()
                .put("name", name).put("email", name + "@example.com").put("password", PASSWORD);
    }

    /**
     * @return a configured group, the admin group only when asked for
     */
    private String firstGroup(boolean admin) throws Exception {
        for (String name : texts(expect(200, get("/v2/admin/groups")), "name")) {
            if (admin == name.equals("admin")) {
                return name;
            }
        }
        fail("No suitable group configured");
        return null;
    }

    private HttpResponse<String> sendAs(String token, String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + path))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, "Bearer " + token)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String userName() {
        return "it-admin-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10_000);
    }

    private static List<String> texts(JsonNode array) {
        List<String> texts = new ArrayList<>();
        array.forEach(n -> texts.add(n.asText()));
        return texts;
    }

    private static List<String> texts(JsonNode array, String field) {
        List<String> texts = new ArrayList<>();
        array.forEach(n -> texts.add(n.get(field).asText()));
        return texts;
    }
}
