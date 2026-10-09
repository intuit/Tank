package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseIT {

    private static final Logger LOG = LogManager.getLogger(BaseIT.class);
    private static final String SSM_PARAMETER_NAME = "/Tank/qa/integration-tests/api/token";
    private static final String CONFIG_FILE = "test-config.properties";
    private static final String API_TOKEN_PROPERTY = "tank.api.token";

    // Required for API calls
    public static final String QA_BASE_URL = "https://qa-tank.perf.a.intuit.com";
    protected static final String API_TOKEN = getApiToken();
    protected static final String API_TOKEN_HEADER = "Bearer " + API_TOKEN;
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String CONTENT_TYPE_HEADER = "Content-Type";
    protected static final String CONTENT_TYPE_VALUE = "application/json";
    protected static final String ACCEPT_HEADER = "Accept";
    protected static final String ACCEPT_VALUE = "application/json";
    protected static final HttpClient httpClient = getHttpClient();

    protected final ObjectMapper objectMapper = new ObjectMapper();


    protected static HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Gets the API token using two techniques:
     * 1. From test-config.properties file in resources
     * 2. If not found, from AWS SSM Parameter Store
     *
     * @return The API token or null if not found
     */
    private static String getApiToken() {
        String token = getTokenFromProperties();

        if (token == null || token.isEmpty()) {
            // If not found in properties, try SSM
            token = getTokenFromSSM();
        }

        return token;
    }

    /**
     * Attempts to read the API token from test-config.properties file
     * This will be used when running locally
     *
     * @return The API token or null if not found
     */
    private static String getTokenFromProperties() {
        try {
            Properties props = new Properties();
            InputStream is = BaseIT.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

            if (is != null) {
                props.load(is);
                is.close();

                String token = props.getProperty(API_TOKEN_PROPERTY);
                if (token != null && !token.isEmpty()) {
                    return token;
                }
            } else {
                LOG.debug("Properties file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            LOG.debug("Error loading properties file: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Attempts to read the API token from AWS SSM Parameter Store
     *
     * @return The API token or null if not found
     */
    private static String getTokenFromSSM() {
        try {
            try (SsmClient ssmClient = SsmClient.builder().build()) {
                GetParameterResponse response = ssmClient.getParameter(
                        GetParameterRequest.builder()
                                .name(SSM_PARAMETER_NAME)
                                .withDecryption(true)
                                .build());

                String token = response.parameter().value();
                if (token != null && !token.isEmpty()) {
                    return token;
                }
            }
        } catch (Exception e) {
            LOG.error("Error retrieving token from SSM: " + e.getMessage());
        }

        return null;
    }

    /**
     * Sends a request with the API token. The body, when given, is sent as JSON.
     *
     * @param path the path after the base URL, such as {@code /v2/me}
     */
    protected HttpResponse<String> send(String method, String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + path))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30));
        if (jsonBody != null) {
            builder.header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                    .method(method, HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> get(String path) throws IOException, InterruptedException {
        return send("GET", path, null);
    }

    /**
     * Sends a request without any credentials.
     */
    protected HttpResponse<String> sendAnonymous(String method, String path, String jsonBody)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + path))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .timeout(Duration.ofSeconds(30));
        if (jsonBody != null) {
            builder.header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                    .method(method, HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Asserts the status code, showing the response body when it differs.
     *
     * @return the body parsed as JSON, or null when it is empty
     */
    protected JsonNode expect(int status, HttpResponse<String> response) throws IOException {
        assertEquals(status, response.statusCode(),
                () -> response.request().method() + " " + response.uri().getPath() + " returned " + response.body());
        String body = response.body();
        return body == null || body.isBlank() ? null : objectMapper.readTree(body);
    }

    /**
     * Posts files as {@code multipart/form-data} with the API token, each under the given form field name.
     *
     * @param files file names to content, in the order they are sent
     */
    protected HttpResponse<String> postFiles(String path, String fieldName, java.util.Map<String, byte[]> files)
            throws IOException, InterruptedException {
        String boundary = "----TankIntegrationTest" + System.nanoTime();
        java.io.ByteArrayOutputStream body = new java.io.ByteArrayOutputStream();
        for (java.util.Map.Entry<String, byte[]> file : files.entrySet()) {
            body.write(("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + file.getKey() + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(file.getValue());
            body.write("\r\n".getBytes(StandardCharsets.UTF_8));
        }
        body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + path))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(CONTENT_TYPE_HEADER, "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** A script that exists on QA; the project and job tests run it. */
    protected static final int QA_SCRIPT_ID = 1;

    /**
     * Creates a project with one region of 10 users and one test plan running {@link #QA_SCRIPT_ID}. The caller
     * deletes it.
     *
     * @return the project ID
     */
    protected int createRunnableProject(String name) throws IOException, InterruptedException {
        String body = String.format("""
            {
                "name": "%s",
                "productName": "Integration Test Project",
                "comments": "Created by integration test",
                "rampTime": "60s",
                "simulationTime": "300s",
                "userIntervalIncrement": 1,
                "location": "unspecified",
                "stopBehavior": "END_OF_SCRIPT_GROUP",
                "workloadType": "increasing",
                "terminationPolicy": "script",
                "variables": { "testVar1": "value1" },
                "jobRegions": [ { "region": "US_WEST_2", "users": "10", "percentage": "100" } ],
                "testPlans": [ {
                    "name": "Main Test Plan",
                    "userPercentage": 100,
                    "scriptGroups": [ {
                        "name": "Main Group",
                        "loop": 1,
                        "scripts": [ { "scriptId": %d, "loop": 1 } ]
                    } ]
                } ]
            }
            """, name, QA_SCRIPT_ID);
        JsonNode created = expect(201, send("POST", "/v2/projects", body));
        return Integer.parseInt(created.get("ProjectId").asText());
    }

    /**
     * @return the name of the user the API token belongs to
     */
    protected String currentUserName() throws IOException, InterruptedException {
        return expect(200, get("/v2/me")).get("name").asText();
    }

    /**
     * @return a name no other test run uses, starting with "IT"
     */
    protected static String uniqueName(String label) {
        return "IT " + label + " " + System.currentTimeMillis() + "-" + (int) (Math.random() * 10_000);
    }

    // Helper method to load file as bytes (like curl @filename)
    protected byte[] loadResourceFileAsBytes(String filename) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new IOException("Resource file not found: " + filename);
            }
            return is.readAllBytes();
        }
    }
}
