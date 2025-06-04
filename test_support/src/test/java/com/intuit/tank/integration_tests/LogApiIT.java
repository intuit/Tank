package com.intuit.tank.integration_tests;

import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class LogApiIT extends BaseIT {

    private static final String LOGS_ENDPOINT = "/v2/logs";
    private static final String TEST_LOG_FILENAME = "catalina.out";

    @Test
    @Tag("integration")
    public void testGetLogFile() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + LOGS_ENDPOINT + "/" + TEST_LOG_FILENAME))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200);

        assertEquals("application/octet-stream",
                    response.headers().firstValue("Content-Type").orElse(""),
                    "Should return application/octet-stream content type");
        assertNotNull(response.body(), "Log file content should not be null");

        // Basic validation that we got some log content
        String logContent = response.body();
        assertFalse(logContent.isEmpty(), "Log file should not be empty");
    }

    @Test
    @Tag("integration")
    public void testGetLogFileWithFromParameter() throws Exception {
        // Arrange - Test with 'from' parameter to get log content starting from a specific position
        String fromPosition = "100"; // Start from position 100
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + LOGS_ENDPOINT + "/" + TEST_LOG_FILENAME + "?from=" + fromPosition))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(response.statusCode(), 200);
        assertEquals("application/octet-stream",
                    response.headers().firstValue("Content-Type").orElse(""),
                    "Should return application/octet-stream content type");
        assertNotNull(response.body(), "Log file content should not be null");

        String logContent = response.body();
        assertFalse(logContent.isEmpty(), "Log file should not be empty");
    }

    @Test
    @Tag("integration")
    public void testGetNonExistentLogFile() throws Exception {
        // Arrange
        String nonExistentFilename = "non-existent-log-" + System.currentTimeMillis() + ".log";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + LOGS_ENDPOINT + "/" + nonExistentFilename))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), 
                    "Should return HTTP 404 Not Found for non-existent log file");
    }

    @Test
    @Tag("integration")
    public void testGetLogFileWithInvalidPath() throws Exception {
        // Arrange - Test with path traversal attempt (should be blocked)
        String invalidFilename = "../../../etc/passwd";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + LOGS_ENDPOINT + "/" + invalidFilename))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(400, response.statusCode(), 
                    "Should return HTTP 400 Bad Request for invalid file path with path traversal");
    }

    @Test
    @Tag("integration")
    public void testGetLogFileWithLargeFromParameter() throws Exception {
        // Arrange - Test with very large 'from' parameter (beyond file size)
        String largeFromPosition = "999999999";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + LOGS_ENDPOINT + "/" + TEST_LOG_FILENAME + "?from=" + largeFromPosition))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200, "Should handle large 'from' parameter gracefully");

        String logContent = response.body();
        assertNotNull(logContent, "Response body should not be null");
        assertTrue(logContent.isEmpty(), "Should return empty content for 'from' beyond file size");
    }

    @Test
    @Tag("integration")
    public void testGetLogFileWithoutAuthorization() throws Exception {
        // Arrange - Test without authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + LOGS_ENDPOINT + "/" + TEST_LOG_FILENAME))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(401, response.statusCode(), 
                    "Should return HTTP 401 Unauthorized without authorization header");
    }

    @Test
    @Tag("integration")
    public void testGetLogFileWithInvalidAuthorization() throws Exception {
        // Arrange - Test with invalid authorization token
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + LOGS_ENDPOINT + "/" + TEST_LOG_FILENAME))
                .header(AUTHORIZATION_HEADER, "Bearer invalid-token")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(401, response.statusCode(), 
                    "Should return HTTP 401 Unauthorized with invalid authorization token");
    }
}
