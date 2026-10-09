package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Session, current user and reference data endpoints (React migration phase 1).
 * <p>
 * These tests never call {@code POST/DELETE /v2/me/api-token} or {@code PUT /v2/me}: that would replace the
 * token or change the account that every integration test runs as.
 */
public class SessionApiIT extends BaseIT {

    @Test
    @Tag("integration")
    public void testAuthConfig_isPublicAndDescribesTheController() throws Exception {
        JsonNode config = expect(200, sendAnonymous("GET", "/v2/auth/config", null));

        assertTrue(config.has("ssoEnabled"), "Should say whether SSO is on");
        assertTrue(config.has("standalone"), "Should say whether the controller is standalone");
        assertTrue(config.hasNonNull("version"), "Should include the build version");
        assertTrue(config.hasNonNull("controllerUrl"), "Should include the controller URL");
    }

    @Test
    @Tag("integration")
    public void testLoginWithBadCredentials_returns401Json() throws Exception {
        String body = "{\"username\":\"it-no-such-user-" + System.currentTimeMillis() + "\",\"password\":\"wrong-password\"}";

        HttpResponse<String> response = sendAnonymous("POST", "/v2/auth/login", body);

        assertEquals(401, response.statusCode(), "Bad credentials should be refused: " + response.body());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("json"),
                "The 401 should be JSON, not a redirect to the login page");
    }

    @Test
    @Tag("integration")
    public void testMe_describesTheTokenUser() throws Exception {
        JsonNode me = expect(200, get("/v2/me"));

        assertTrue(me.hasNonNull("name"), "Should include the user name");
        assertTrue(me.get("groups").isArray(), "Should list the user's groups");
        assertTrue(me.has("admin"), "Should say whether the user is an admin");
        assertTrue(me.get("hasApiToken").asBoolean(), "The integration test user signs in with an API token");
        JsonNode rights = me.get("rights");
        assertTrue(rights.has("CREATE_PROJECT") && rights.has("CONTROL_JOB"), "Should map every access right");
        assertFalse(me.has("password"), "Must never include the password");
        assertFalse(me.has("apiToken"), "Must never include the token itself");
    }

    @Test
    @Tag("integration")
    public void testMeWithoutCredentials_returns401() throws Exception {
        HttpResponse<String> response = sendAnonymous("GET", "/v2/me", null);
        assertEquals(401, response.statusCode(), "Anonymous callers have no current user");
    }

    @Test
    @Tag("integration")
    public void testPreferences_listsEveryTable() throws Exception {
        JsonNode tables = expect(200, get("/v2/me/preferences")).get("tables");

        for (String table : List.of("projects", "scripts", "scriptSteps", "datafiles", "jobs")) {
            assertTrue(tables.has(table), "Should include the " + table + " table");
            JsonNode first = tables.get(table).get(0);
            assertTrue(first.hasNonNull("colName") && first.has("visible") && first.has("hideable"),
                    "Each column should have a name, visibility and whether it can be hidden");
        }
    }

    @Test
    @Tag("integration")
    public void testUpdateTablePreference_changesAndRestoresAColumnSize() throws Exception {
        JsonNode column = expect(200, get("/v2/me/preferences")).get("tables").get("projects").get(0);
        String colName = column.get("colName").asText();
        int originalSize = column.get("size").asInt();
        int newSize = originalSize == 123 ? 124 : 123;

        try {
            JsonNode updated = expect(200, send("PUT", "/v2/me/preferences/tables/projects",
                    "[{\"colName\":\"" + colName + "\",\"size\":" + newSize + "}]"));
            assertEquals(newSize, findColumn(updated.get("tables").get("projects"), colName).get("size").asInt());
        } finally {
            send("PUT", "/v2/me/preferences/tables/projects",
                    "[{\"colName\":\"" + colName + "\",\"size\":" + originalSize + "}]");
        }
    }

    @Test
    @Tag("integration")
    public void testUpdateTablePreference_rejectsUnknownTableAndColumn() throws Exception {
        expect(404, send("PUT", "/v2/me/preferences/tables/no-such-table", "[]"));
        expect(400, send("PUT", "/v2/me/preferences/tables/projects",
                "[{\"colName\":\"no-such-column\",\"size\":100}]"));
    }

    @Test
    @Tag("integration")
    public void testConfigOptions_returnsEveryPickList() throws Exception {
        JsonNode options = expect(200, get("/v2/config/options"));

        for (String field : List.of("products", "locations", "regions", "loggingProfiles", "stopBehaviors",
                "terminationPolicies", "incrementStrategies", "vmInstanceTypes", "httpClients", "reportingModes")) {
            assertTrue(options.get(field).isArray(), field + " should be a list");
        }
        assertFalse(options.get("regions").isEmpty(), "QA should offer at least one region");
        assertFalse(options.get("vmInstanceTypes").isEmpty(), "QA should offer at least one instance type");
        assertTrue(options.get("stepOptions").isObject(), "Should include the step editor options");
        assertTrue(options.get("filterOptions").has("conditionScopes"), "Should include the filter editor options");
        assertTrue(options.get("logicStep").has("insertBefore"), "Should include the logic step template");
    }

    @Test
    @Tag("integration")
    public void testUserNames_includesTheTokenUser() throws Exception {
        String me = expect(200, get("/v2/me")).get("name").asText();
        JsonNode names = expect(200, get("/v2/users/names"));

        assertTrue(names.isArray(), "Should be a list of names");
        boolean found = false;
        for (JsonNode name : names) {
            assertFalse(name.asText().startsWith("deleted_user_"), "Deleted users should not be offered as owners");
            found |= name.asText().equals(me);
        }
        assertTrue(found, "Should include the current user");
    }

    @Test
    @Tag("integration")
    public void testConfigOptionsWithoutCredentials_returns401() throws Exception {
        assertEquals(401, sendAnonymous("GET", "/v2/config/options", null).statusCode());
    }

    private static JsonNode findColumn(JsonNode columns, String colName) {
        for (JsonNode column : columns) {
            if (colName.equals(column.get("colName").asText())) {
                return column;
            }
        }
        fail("No column " + colName);
        return null;
    }
}
