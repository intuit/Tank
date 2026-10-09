package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Filter and filter group editor endpoints: update with conflict checks, copies, group create and update, group
 * cleanup when a filter is deleted, and the action editor field rules (React migration phase 6).
 */
public class FilterEditorApiIT extends BaseIT {

    private static final int NO_SUCH_ID = 999_999_999;

    private final List<Integer> createdFilterIds = new ArrayList<>();
    private final List<Integer> createdGroupIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        for (Integer id : createdGroupIds) {
            try {
                send("DELETE", "/v2/filters/groups/" + id, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up filter group " + id + ": " + e.getMessage());
            }
        }
        createdGroupIds.clear();
        for (Integer id : createdFilterIds) {
            try {
                send("DELETE", "/v2/filters/" + id, null);
            } catch (Exception e) {
                System.err.println("Failed to clean up filter " + id + ": " + e.getMessage());
            }
        }
        createdFilterIds.clear();
    }

    // ---------------------------------------------------------------- filters

    @Test
    @Tag("integration")
    public void testCreate_ownerIsTheCallerNotTheRequest() throws Exception {
        ObjectNode body = filterBody(uniqueName("Filter owner"));
        body.put("creator", "someone-else");

        JsonNode filter = expect(201, send("POST", "/v2/filters", body.toString()));
        createdFilterIds.add(filter.get("id").asInt());

        assertEquals(currentUserName(), filter.get("creator").asText(), "The creator in the request is ignored");
    }

    @Test
    @Tag("integration")
    public void testUpdate_savesAndRejectsStaleCopies() throws Exception {
        int id = createFilter(uniqueName("Filter edit"));
        ObjectNode filter = (ObjectNode) expect(200, get("/v2/filters/" + id));
        JsonNode loadedModified = filter.get("modified");

        // saves are compared to the second, so make sure this one gets a later modified time than the create
        Thread.sleep(1_100);
        String newName = uniqueName("Filter renamed");
        filter.put("name", newName);
        ((ObjectNode) filter.get("conditions").get(0)).put("value", "/changed");
        JsonNode saved = expect(200, send("PUT", "/v2/filters/" + id, filter.toString()));

        assertEquals(newName, saved.get("name").asText());
        assertEquals("/changed", saved.get("conditions").get(0).get("value").asText());
        assertEquals(currentUserName(), saved.get("creator").asText(), "Updates keep the owner");

        filter.set("modified", loadedModified);
        expect(409, send("PUT", "/v2/filters/" + id, filter.toString()));
        filter.remove("modified");
        expect(400, send("PUT", "/v2/filters/" + id, filter.toString()));
        expect(404, send("PUT", "/v2/filters/" + NO_SUCH_ID, filterBody("x").toString()));
    }

    @Test
    @Tag("integration")
    public void testCopy_copiesConditionsAndActions() throws Exception {
        int id = createFilter(uniqueName("Filter source"));
        String copyName = uniqueName("Filter copy");

        JsonNode copy = expect(201, send("POST", "/v2/filters/" + id + "/copy", "{\"name\":\"" + copyName + "\"}"));
        int copyId = copy.get("id").asInt();
        createdFilterIds.add(copyId);

        assertNotEquals(id, copyId);
        assertEquals(copyName, copy.get("name").asText());
        assertEquals(currentUserName(), copy.get("creator").asText());
        JsonNode source = expect(200, get("/v2/filters/" + id));
        assertEquals(source.get("conditions"), copy.get("conditions"));
        assertEquals(source.get("actions"), copy.get("actions"));
        expect(400, send("POST", "/v2/filters/" + id + "/copy", "{\"name\":\" \"}"));
        expect(404, send("POST", "/v2/filters/" + NO_SUCH_ID + "/copy", "{\"name\":\"x\"}"));
    }

    // ---------------------------------------------------------------- groups

    @Test
    @Tag("integration")
    public void testGroupCreateUpdateAndCopy() throws Exception {
        int first = createFilter(uniqueName("Group member A"));
        int second = createFilter(uniqueName("Group member B"));

        JsonNode group = expect(201, send("POST", "/v2/filters/groups", groupBody(uniqueName("Group"), first).toString()));
        int groupId = group.get("id").asInt();
        createdGroupIds.add(groupId);
        assertEquals(currentUserName(), group.get("creator").asText());
        assertEquals(List.of(first), ids(group.get("filterIds")));
        assertEquals(first, group.get("filters").get(0).get("id").asInt(), "The response includes the filters");

        Thread.sleep(1_100);
        ObjectNode update = groupBody(uniqueName("Group renamed"), first, second);
        update.set("modified", group.get("modified"));
        JsonNode updated = expect(200, send("PUT", "/v2/filters/groups/" + groupId, update.toString()));
        assertEquals(List.of(first, second), ids(updated.get("filterIds")));
        expect(409, send("PUT", "/v2/filters/groups/" + groupId, update.toString()));

        String copyName = uniqueName("Group copy");
        JsonNode copy = expect(201, send("POST", "/v2/filters/groups/" + groupId + "/copy", "{\"name\":\"" + copyName + "\"}"));
        createdGroupIds.add(copy.get("id").asInt());
        assertEquals(copyName, copy.get("name").asText());
        assertEquals(List.of(first, second), ids(copy.get("filterIds")), "The copy holds the same filters");
    }

    @Test
    @Tag("integration")
    public void testGroupCreate_rejectsUnknownFiltersAndBlankNames() throws Exception {
        int member = createFilter(uniqueName("Group member"));

        JsonNode error = expect(400, send("POST", "/v2/filters/groups",
                groupBody(uniqueName("Bad group"), member, NO_SUCH_ID).toString()));
        assertTrue(error.toString().contains(Integer.toString(NO_SUCH_ID)), "The error should name the unknown filter: " + error);
        expect(400, send("POST", "/v2/filters/groups", groupBody(" ", member).toString()));
        expect(404, send("PUT", "/v2/filters/groups/" + NO_SUCH_ID, groupBody("x", member).toString()));
    }

    @Test
    @Tag("integration")
    public void testDeleteFilter_removesItFromItsGroups() throws Exception {
        int kept = createFilter(uniqueName("Kept member"));
        int doomed = createFilter(uniqueName("Deleted member"));
        JsonNode group = expect(201, send("POST", "/v2/filters/groups",
                groupBody(uniqueName("Cleanup group"), kept, doomed).toString()));
        int groupId = group.get("id").asInt();
        createdGroupIds.add(groupId);

        expect(204, send("DELETE", "/v2/filters/" + doomed, null));
        createdFilterIds.remove(Integer.valueOf(doomed));

        assertEquals(List.of(kept), ids(expect(200, get("/v2/filters/groups/" + groupId)).get("filterIds")),
                "A deleted filter must not stay in a group");
    }

    // ---------------------------------------------------------------- editor options

    @Test
    @Tag("integration")
    public void testConfigOptions_describeActionEditorFields() throws Exception {
        JsonNode fields = expect(200, get("/v2/config/options")).get("filterActionFields");

        assertTrue(fields.isArray() && !fields.isEmpty());
        JsonNode onFail = field(fields, "replace", "onFailure");
        assertTrue(onFail.get("onFail").asBoolean());
        assertFalse(onFail.get("key").asBoolean());
        assertFalse(onFail.get("value").asBoolean());
        assertEquals("ASSIGNMENT", field(fields, "add", "assignment").get("prefix").asText());
        assertFalse(field(fields, "remove", "request").get("key").asBoolean());
    }

    // ---------------------------------------------------------------- helpers

    private int createFilter(String name) throws Exception {
        int id = expect(201, send("POST", "/v2/filters", filterBody(name).toString())).get("id").asInt();
        createdFilterIds.add(id);
        return id;
    }

    private ObjectNode filterBody(String name) throws Exception {
        return (ObjectNode) objectMapper.readTree("""
                {
                  "name": "%s",
                  "productName": "IntegrationTest",
                  "allConditionsMustPass": true,
                  "conditions": [{ "scope": "path", "condition": "contains", "value": "/filter-editor-it" }],
                  "actions": [{ "action": "replace", "scope": "host", "value": "filtered.example.com" }]
                }
                """.formatted(name));
    }

    private ObjectNode groupBody(String name, int... filterIds) {
        ObjectNode body = objectMapper.createObjectNode().put("name", name).put("productName", "IntegrationTest");
        body.set("filterIds", objectMapper.valueToTree(filterIds));
        return body;
    }

    private static JsonNode field(JsonNode fields, String actionType, String scope) {
        for (JsonNode field : fields) {
            if (actionType.equals(field.get("actionType").asText()) && scope.equals(field.get("scope").asText())) {
                return field;
            }
        }
        fail("No action field for " + actionType + " " + scope);
        return null;
    }

    private static List<Integer> ids(JsonNode array) {
        List<Integer> ids = new ArrayList<>();
        array.forEach(n -> ids.add(n.asInt()));
        return ids;
    }
}
