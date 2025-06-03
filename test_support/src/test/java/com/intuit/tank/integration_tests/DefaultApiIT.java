package com.intuit.tank.integration_tests;

import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultApiIT extends BaseIT {

    private static final String PING_ENDPOINT = "/v2/ping";

    @Test
    @Tag("integration")
    public void testPingEndpoint() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG Tank V2 API", response.body(), "Should return expected ping response");
        assertEquals("text/plain;charset=UTF-8", 
                    response.headers().firstValue("Content-Type").orElse(""),
                    "Should return text/plain content type");
    }

    @Test
    @Tag("integration")
    public void testPingEndpointWithoutAuthorization() throws Exception {
        // Arrange - Test without authorization header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
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
    public void testPingEndpointWithInvalidAuthorization() throws Exception {
        // Arrange - Test with invalid authorization token
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
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

    @Test
    @Tag("integration")
    public void testPingEndpointResponseTime() throws Exception {
        // Arrange
        long startTime = System.currentTimeMillis();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG Tank V2 API", response.body(), "Should return expected ping response");
        
        // Response time should be reasonable (less than 5 seconds for a simple ping)
        assertTrue(responseTime < 5000, 
                  "Ping response time should be less than 5 seconds, was: " + responseTime + "ms");
        
        System.out.println("Ping response time: " + responseTime + "ms");
    }

    @Test
    @Tag("integration")
    public void testPingEndpointMultipleRequests() throws Exception {
        // Test multiple consecutive requests to ensure consistency
        int numberOfRequests = 5;
        
        for (int i = 0; i < numberOfRequests; i++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
                    .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            // Assert each request
            assertEquals(200, response.statusCode(), 
                        "Request " + (i + 1) + " should return HTTP 200 OK");
            assertEquals("PONG Tank V2 API", response.body(), 
                        "Request " + (i + 1) + " should return expected ping response");
            assertEquals("text/plain;charset=UTF-8", 
                        response.headers().firstValue("Content-Type").orElse(""),
                        "Request " + (i + 1) + " should return text/plain content type");
        }
        
        System.out.println("Successfully completed " + numberOfRequests + " consecutive ping requests");
    }

    @Test
    @Tag("integration")
    public void testPingEndpointWithCustomHeaders() throws Exception {
        // Arrange - Test with additional custom headers
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("User-Agent", "Tank-Integration-Test/1.0")
                .header("X-Test-Header", "integration-test")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK with custom headers");
        assertEquals("PONG Tank V2 API", response.body(), "Should return expected ping response");
    }

    @Test
    @Tag("integration")
    public void testPingEndpointWithQueryParameters() throws Exception {
        // Arrange - Test with query parameters (should be ignored)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PING_ENDPOINT + "?test=value&another=param"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK with query parameters");
        assertEquals("PONG Tank V2 API", response.body(), 
                    "Should return expected ping response regardless of query parameters");
    }

    @Test
    @Tag("integration")
    public void testPingEndpointConcurrentRequests() throws Exception {
        // Test concurrent requests to ensure thread safety
        int numberOfThreads = 3;
        Thread[] threads = new Thread[numberOfThreads];
        boolean[] results = new boolean[numberOfThreads];
        
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
                            .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                            .timeout(Duration.ofSeconds(30))
                            .GET()
                            .build();

                    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
                    
                    results[threadIndex] = (response.statusCode() == 200 && 
                                          "PONG Tank V2 API".equals(response.body()));
                } catch (Exception e) {
                    results[threadIndex] = false;
                    System.err.println("Thread " + threadIndex + " failed: " + e.getMessage());
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(10000); // 10 second timeout
        }
        
        // Assert all requests succeeded
        for (int i = 0; i < numberOfThreads; i++) {
            assertTrue(results[i], "Concurrent request " + (i + 1) + " should succeed");
        }
        
        System.out.println("Successfully completed " + numberOfThreads + " concurrent ping requests");
    }

    @Test
    @Tag("integration")
    public void testPingEndpointResponseHeaders() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PING_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        
        // Check required headers
        assertTrue(response.headers().firstValue("Content-Type").isPresent(),
                  "Response should have Content-Type header");
        assertEquals("text/plain;charset=UTF-8", 
                    response.headers().firstValue("Content-Type").orElse(""),
                    "Content-Type should be text/plain");
        
        // Check for common security headers (may or may not be present)
        System.out.println("Response headers:");
        response.headers().map().forEach((key, values) -> 
            System.out.println("  " + key + ": " + String.join(", ", values)));
    }
}
