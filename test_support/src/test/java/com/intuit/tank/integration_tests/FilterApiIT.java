package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FilterApiIT extends BaseIT {

    private static final String FILTERS_ENDPOINT = "/v2/filters";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String COVERAGE_CLASS = "FilterController";

    @Test
    @Tag("integration")
    public void testPingFilterService() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/ping"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG FilterServiceV2", response.body(), "Should return PONG response");
    }

    @Test
    @Tag("integration")
    public void testGetAllFilters() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode responseBody = objectMapper.readTree(response.body());
        assertTrue(responseBody.has("filters"), "Response should contain 'filters' field");

        JsonNode filters = responseBody.get("filters");
        assertTrue(filters.isArray(), "Filters should be an array");
        assertTrue(filters.size() > 0, "Should contain at least one filter");

        // Validate specific known filters from the response
        boolean foundTokenSubmitPath = false;
        boolean foundRemoveTTOFlash = false;
        boolean foundIntuitDomainOnly = false;

        for (JsonNode filter : filters) {
            // Validate required fields exist
            assertTrue(filter.has("id"), "Filter should have 'id' field");
            assertTrue(filter.has("name"), "Filter should have 'name' field");
            assertTrue(filter.has("productName"), "Filter should have 'productName' field");

            // Validate data types
            assertTrue(filter.get("id").isInt(), "Filter ID should be integer");
            assertTrue(filter.get("name").isTextual(), "Filter name should be string");
            assertTrue(filter.get("id").asInt() > 0, "Filter ID should be positive");
            assertFalse(filter.get("name").asText().trim().isEmpty(), "Filter name should not be empty");

            // ProductName can be null, empty string, or actual value
            JsonNode productNameNode = filter.get("productName");
            assertTrue(productNameNode.isTextual() || productNameNode.isNull(),
                      "ProductName should be string or null");

            // Check for specific known filters
            int filterId = filter.get("id").asInt();
            String filterName = filter.get("name").asText();

            if (filterId == 1 && "Token SUBMIT PATH".equals(filterName)) {
                foundTokenSubmitPath = true;
                assertEquals("", filter.get("productName").asText(),
                           "Token SUBMIT PATH should have empty productName");
            }

            if (filterId == 2 && "Remove TTO flash".equals(filterName)) {
                foundRemoveTTOFlash = true;
                assertEquals("ShowAll", filter.get("productName").asText(),
                           "Remove TTO flash should have 'ShowAll' productName");
            }

            if (filterId == 5 && "Intuit Domain Only".equals(filterName)) {
                foundIntuitDomainOnly = true;
                assertEquals("AllProducts", filter.get("productName").asText(),
                           "Intuit Domain Only should have 'AllProducts' productName");
            }
        }

        // Verify we found the expected filters
        assertTrue(foundTokenSubmitPath, "Should find 'Token SUBMIT PATH' filter with ID 1");
        assertTrue(foundRemoveTTOFlash, "Should find 'Remove TTO flash' filter with ID 2");
        assertTrue(foundIntuitDomainOnly, "Should find 'Intuit Domain Only' filter with ID 5");

        // Validate we have a reasonable number of filters
        assertTrue(filters.size() >= 30, "Should have at least 30 filters based on sample data");

        // Validate some filters have null productName (like ID 4, 8, 22, 77)
        boolean foundNullProductName = false;
        for (JsonNode filter : filters) {
            if (filter.get("productName").isNull()) {
                foundNullProductName = true;
                break;
            }
        }
        assertTrue(foundNullProductName, "Should have at least one filter with null productName");

        // Validate some filters have different productName values
        Set<String> productNames = new HashSet<>();
        for (JsonNode filter : filters) {
            JsonNode productNameNode = filter.get("productName");
            if (!productNameNode.isNull()) {
                productNames.add(productNameNode.asText());
            }
        }
        assertTrue(productNames.contains("ShowAll"), "Should have filters with 'ShowAll' productName");
        assertTrue(productNames.contains("AllProducts"), "Should have filters with 'AllProducts' productName");
        assertTrue(productNames.contains("TTO"), "Should have filters with 'TTO' productName");
    }

    @Test
    @Tag("integration")
    public void testGetAllFilterGroups() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/groups"))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode responseBody = objectMapper.readTree(response.body());
        assertTrue(responseBody.has("filterGroups"), "Response should contain 'filterGroups' field");

        JsonNode filterGroups = responseBody.get("filterGroups");
        assertTrue(filterGroups.isArray(), "Filter groups should be an array");
        assertTrue(filterGroups.size() > 0, "Should contain at least one filter group");

        // Validate specific known filter groups from the response
        boolean foundTTOProdCapacity = false;
        boolean foundAutomationBaseline = false;
        boolean foundLaunchPad = false;
        boolean foundEzE = false;

        for (JsonNode filterGroup : filterGroups) {
            // Validate required fields exist
            assertTrue(filterGroup.has("id"), "Filter group should have 'id' field");
            assertTrue(filterGroup.has("name"), "Filter group should have 'name' field");
            assertTrue(filterGroup.has("productName"), "Filter group should have 'productName' field");

            // Validate data types
            assertTrue(filterGroup.get("id").isInt(), "Filter group ID should be integer");
            assertTrue(filterGroup.get("name").isTextual(), "Filter group name should be string");
            assertTrue(filterGroup.get("id").asInt() > 0, "Filter group ID should be positive");
            assertFalse(filterGroup.get("name").asText().trim().isEmpty(), "Filter group name should not be empty");

            // ProductName can be null, empty string, or actual value
            JsonNode productNameNode = filterGroup.get("productName");
            assertTrue(productNameNode.isTextual() || productNameNode.isNull(),
                      "ProductName should be string or null");

            // Check for specific known filter groups
            int filterGroupId = filterGroup.get("id").asInt();
            String filterGroupName = filterGroup.get("name").asText();

            if (filterGroupId == 2 && "TTO Prod Capacity".equals(filterGroupName)) {
                foundTTOProdCapacity = true;
                assertEquals("TTO", filterGroup.get("productName").asText(),
                           "TTO Prod Capacity should have 'TTO' productName");
            }

            if (filterGroupId == 3 && "AutomationBaseline".equals(filterGroupName)) {
                foundAutomationBaseline = true;
                assertEquals("TTO", filterGroup.get("productName").asText(),
                           "AutomationBaseline should have 'TTO' productName");
            }
        }

        // Verify we found the expected filter groups
        assertTrue(foundTTOProdCapacity, "Should find 'TTO Prod Capacity' filter group with ID 2");
        assertTrue(foundAutomationBaseline, "Should find 'AutomationBaseline' filter group with ID 3");

        // Validate we have a reasonable number of filter groups
        assertTrue(filterGroups.size() >= 15, "Should have at least 15 filter groups based on sample data");

        // Validate some filter groups have null productName (like ID 9, 11)
        boolean foundNullProductName = false;
        for (JsonNode filterGroup : filterGroups) {
            if (filterGroup.get("productName").isNull()) {
                foundNullProductName = true;
                break;
            }
        }
        assertTrue(foundNullProductName, "Should have at least one filter group with null productName");

        // Validate some filter groups have empty productName (like ID 22, 23, 25, 27, 40)
        boolean foundEmptyProductName = false;
        for (JsonNode filterGroup : filterGroups) {
            JsonNode productNameNode = filterGroup.get("productName");
            if (!productNameNode.isNull() && productNameNode.asText().isEmpty()) {
                foundEmptyProductName = true;
                break;
            }
        }
        assertTrue(foundEmptyProductName, "Should have at least one filter group with empty productName");

        // Validate different productName values exist
        Set<String> productNames = new HashSet<>();
        for (JsonNode filterGroup : filterGroups) {
            JsonNode productNameNode = filterGroup.get("productName");
            if (!productNameNode.isNull()) {
                productNames.add(productNameNode.asText());
            }
        }
        assertTrue(productNames.contains("TTO"), "Should have filter groups with 'TTO' productName");
        assertTrue(productNames.contains("ShowAll"), "Should have filter groups with 'ShowAll' productName");
        assertTrue(productNames.contains(""), "Should have filter groups with empty productName");
    }

    @Test
    @Tag("integration")
    public void testGetSpecificFilter() throws Exception {
        int filterId = 1; // Token SUBMIT PATH (known existing filter)

        // Act - Get specific filter
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/" + filterId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode filter = objectMapper.readTree(response.body());
        assertEquals(filterId, filter.get("id").asInt(), "Should return correct filter ID");
        assertTrue(filter.has("name"), "Filter should have name field");
        assertTrue(filter.has("productName"), "Filter should have productName field");
    }

    @Test
    @Tag("integration")
    public void testGetNonExistentFilter() throws Exception {
        // Arrange
        int nonExistentFilterId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/" + nonExistentFilterId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent filter");
    }

    @Test
    @Tag("integration")
    public void testGetSpecificFilterGroup() throws Exception {
        int filterGroupId = 2; // TTO Prod Capacity (known existing filter group)

        // Act - Get specific filter group
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/groups/" + filterGroupId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode filterGroup = objectMapper.readTree(response.body());
        assertEquals(filterGroupId, filterGroup.get("id").asInt(), "Should return correct filter group ID");
        assertTrue(filterGroup.has("name"), "Filter group should have name field");
        assertTrue(filterGroup.has("productName"), "Filter group should have productName field");
    }

    @Test
    @Tag("integration")
    public void testGetNonExistentFilterGroup() throws Exception {
        // Arrange
        int nonExistentFilterGroupId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/groups/" + nonExistentFilterGroupId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent filter group");
    }

    @Test
    @Tag("integration")
    public void testApplyFiltersToScript() throws Exception {
        // Step 1: Copy an existing script to create a test script
        int sourceScriptId = 1620; // UC1PWScript.RAW (known existing script)
        String copiedScriptName = "Filter_Test_Copy_" + System.currentTimeMillis();

        HttpRequest copyRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + "/v2/scripts?copy&sourceId=" + sourceScriptId + "&name=" + copiedScriptName))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> copyResponse = httpClient.send(copyRequest, BodyHandlers.ofString());
        assertEquals(201, copyResponse.statusCode(), "Should successfully copy script");

        Map<String, String> responseBody = objectMapper.readValue(copyResponse.body(), Map.class);
        assertNotNull(responseBody.get("message"), "Copy response should contain message");

        String message = responseBody.get("message");
        assertTrue(message.contains("script ID"), "Message should contain script ID");

        // Extract script ID using regex pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("script ID (\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        assertTrue(matcher.find(), "Should find script ID in message");

        int copiedScriptId = Integer.parseInt(matcher.group(1));
        assertTrue(copiedScriptId > 0, "Script ID should be positive");

        try {
            // Step 2: Apply filters to the copied script
            int filterId = 1; // Token SUBMIT PATH (known existing filter)
            int filterGroupId = 2; // TTO Prod Capacity (known existing filter group)

            String requestBody = """
                {
                    "filterIds": [%d],
                    "filterGroupIds": [%d]
                }
                """.formatted(filterId, filterGroupId);

            HttpRequest applyFiltersRequest = HttpRequest.newBuilder()
                    .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/apply-filters/" + copiedScriptId))
                    .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                    .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Act
            HttpResponse<String> response = httpClient.send(applyFiltersRequest, BodyHandlers.ofString());

            // Assert
            assertTrue(response.statusCode() == 200, "Should return HTTP 200 OK for successful filter application");
            assertTrue(response.body().contains("applied") || response.body().contains("success") || response.body().contains("filter"),
                          "Response should indicate successful filter application");
        } finally {
            // Step 3: Clean up - Delete the copied script
            HttpRequest deleteRequest = HttpRequest.newBuilder()
                    .uri(URI.create(QA_BASE_URL + "/v2/scripts/" + copiedScriptId))
                    .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                    .timeout(Duration.ofSeconds(30))
                    .DELETE()
                    .build();

            HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, BodyHandlers.ofString());
            assertEquals(204, deleteResponse.statusCode(), "Should successfully delete copied script");
        }
    }

    @Test
    @Tag("integration")
    public void testApplyFiltersWithInvalidRequest() throws Exception {
        // Arrange
        int testScriptId = 1616;
        String invalidRequestBody = "{ invalid json }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/apply-filters/" + testScriptId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(invalidRequestBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(400, response.statusCode(), "Should return HTTP 400 Bad Request for invalid JSON");
        assertTrue(response.body().contains("Bad") || response.body().contains("request"),
                  "Response should indicate bad request");
    }

    @Test
    @Tag("integration")
    public void testApplyFiltersToNonExistentScript() throws Exception {
        // Arrange
        int nonExistentScriptId = 999999;
        String requestBody = """
            {
                "filterIds": [1],
                "filterGroupIds": []
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/apply-filters/" + nonExistentScriptId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.body().contains("Script with that script ID does not exist"));
    }

    @Test
    @Tag("integration")
    public void testDeleteNonExistentFilter() throws Exception {
        // Arrange
        int nonExistentFilterId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/" + nonExistentFilterId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent filter");
        assertTrue(response.body().contains("not exist") || response.body().contains("not found"),
                  "Response should indicate filter not found");
    }

    @Test
    @Tag("integration")
    public void testDeleteNonExistentFilterGroup() throws Exception {
        // Arrange
        int nonExistentFilterGroupId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + FILTERS_ENDPOINT + "/groups/" + nonExistentFilterGroupId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent filter group");
        assertTrue(response.body().contains("not exist") || response.body().contains("not found"),
                  "Response should indicate filter group not found");
    }
}
