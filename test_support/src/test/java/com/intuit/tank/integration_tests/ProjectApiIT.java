package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectApiIT extends BaseIT {
    
    @Test
    @Tag("integration")
    public void testGetProjectNames() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + "/v2/projects/names"))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> projectNames = objectMapper.readValue(response.body(), Map.class);
        assertTrue(projectNames.containsValue("Simple Endurance Project"), 
                "Response should contain 'Simple Endurance Project'");
        assertTrue(projectNames.containsValue("PDS_Perf_Baseline"), 
                "Response should contain 'PDS_Perf_Baseline'");

        assertTrue(projectNames.size() > 10, 
                "Response should contain multiple projects (at least 10)");

        assertEquals(298, Integer.parseInt(projectNames.entrySet().stream()
                .filter(entry -> "Simple Endurance Project".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null)), "Should find project ID for 'Simple Endurance Project'");

        assertEquals(367, Integer.parseInt(projectNames.entrySet().stream()
                    .filter(entry -> "PDS_Perf_Baseline".equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null)), "Should find project ID for 'PDS_Perf_Baseline'");
    }
}