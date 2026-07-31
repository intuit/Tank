package com.intuit.tank.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST helper for job creation against the embedded Tank runtime.
 */
public final class TankApiClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TankIntegrationEnvironment env;

    public TankApiClient(TankIntegrationEnvironment env) {
        this.env = env;
    }

    public int uploadScript(String scriptName) throws Exception {
        byte[] xml = loadFixture("fixtures/Minimal_TS.xml");
        String xmlText = new String(xml, StandardCharsets.UTF_8)
                .replace("<name>IT Minimal Script</name>", "<name>" + scriptName + "</name>");
        xml = xmlText.getBytes(StandardCharsets.UTF_8);

        String boundary = "----TankBoundary" + UUID.randomUUID().toString().replace("-", "");
        byte[] body = multipartBody(boundary, "file", scriptName + ".xml", "application/xml", xml);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(env.baseUrl() + "/v2/scripts"))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = env.httpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new IllegalStateException("Script upload failed: HTTP " + response.statusCode()
                    + " body=" + response.body());
        }
        JsonNode node = MAPPER.readTree(response.body());
        if (node.has("scriptId")) {
            return Integer.parseInt(node.get("scriptId").asText());
        }
        if (node.has("message")) {
            String message = node.get("message").asText();
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("script ID\\s+(\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(message);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        throw new IllegalStateException("Could not parse scriptId from: " + response.body());
    }

    public int createProject(String projectName, List<Integer> scriptIds) throws Exception {
        if (scriptIds.isEmpty()) {
            throw new IllegalArgumentException("Project requires at least one script");
        }
        StringBuilder scripts = new StringBuilder();
        for (int position = 0; position < scriptIds.size(); position++) {
            if (!scripts.isEmpty()) {
                scripts.append(",\n");
            }
            scripts.append("""
                    {
                      "scriptId": %d,
                      "loop": 1,
                      "position": %d
                    }
                    """.formatted(scriptIds.get(position), position));
        }

        String body = """
                {
                  "name": "%s",
                  "productName": "System Test Project",
                  "comments": "Created by JobCreationIT",
                  "rampTime": "2m",
                  "simulationTime": "5m",
                  "userIntervalIncrement": 1,
                  "location": "unspecified",
                  "stopBehavior": "END_OF_SCRIPT_GROUP",
                  "workloadType": "increasing",
                  "terminationPolicy": "script",
                  "jobRegions": [
                    {
                      "region": "US_EAST_2",
                      "users": "2",
                      "percentage": "100"
                    }
                  ],
                  "testPlans": [
                    {
                      "name": "Main",
                      "userPercentage": 100,
                      "scriptGroups": [
                        {
                          "name": "Main Group",
                          "loop": 1,
                          "scripts": [
                            %s
                          ]
                        }
                      ]
                    }
                  ]
                }
                """.formatted(projectName, scripts);

        HttpResponse<String> response = env.postJson("/v2/projects", body);
        if (response.statusCode() != 201) {
            throw new IllegalStateException("Project create failed: HTTP " + response.statusCode()
                    + " body=" + response.body());
        }
        JsonNode node = MAPPER.readTree(response.body());
        if (node.has("ProjectId")) {
            return Integer.parseInt(node.get("ProjectId").asText());
        }
        if (node.has("projectId")) {
            return Integer.parseInt(node.get("projectId").asText());
        }
        if (node.has("id")) {
            return node.get("id").asInt();
        }
        throw new IllegalStateException("Could not parse project id from: " + response.body());
    }

    public List<Integer> getProjectScriptIds(int projectId) throws Exception {
        HttpResponse<String> response = env.get("/v2/projects/" + projectId);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Get project failed: HTTP " + response.statusCode()
                    + " body=" + response.body());
        }

        List<Integer> scriptIds = new ArrayList<>();
        JsonNode project = MAPPER.readTree(response.body());
        project.path("testPlans").forEach(testPlan ->
                testPlan.path("scriptGroups").forEach(scriptGroup ->
                        scriptGroup.path("scripts").forEach(script ->
                                scriptIds.add(script.path("scriptId").asInt()))));
        return scriptIds;
    }

    public CreatedJob createJobFromContract(JobCreationContract.Profile profile,
                                            int projectId,
                                            String jobName) throws Exception {
        String body = JobCreationContract.buildRequestJson(profile, projectId, jobName);
        JobCreationContract.assertMatchesContract(body, profile, projectId, jobName);
        return createJob(body, profile);
    }

    private CreatedJob createJob(String body, JobCreationContract.Profile profile) throws Exception {
        HttpResponse<String> response = env.postJson("/v2/jobs", body);
        if (response.statusCode() != 201) {
            throw new IllegalStateException("Job create failed: HTTP " + response.statusCode()
                    + " body=" + response.body());
        }
        JsonNode node = MAPPER.readTree(response.body());
        int jobId = Integer.parseInt(node.get("JobId").asText());
        String status = node.get("status").asText();
        return new CreatedJob(jobId, status, body, profile);
    }

    public JsonNode getJob(int jobId) throws Exception {
        HttpResponse<String> response = env.get("/v2/jobs/" + jobId);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Get job failed: HTTP " + response.statusCode()
                    + " body=" + response.body());
        }
        return MAPPER.readTree(response.body());
    }

    public int getVmStatusCount(int jobId) throws Exception {
        HttpResponse<String> response = env.get("/v2/jobs/instance-status/" + jobId);
        if (response.statusCode() == 404) {
            return 0;
        }
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Get instance-status failed: HTTP " + response.statusCode()
                    + " body=" + response.body());
        }
        JsonNode node = MAPPER.readTree(response.body());
        if (node.has("statuses") && node.get("statuses").isArray()) {
            return node.get("statuses").size();
        }
        if (node.has("statuses") && node.get("statuses").isObject()) {
            return node.get("statuses").size();
        }
        return 0;
    }

    public PersistedJobConfiguration queryJobConfiguration(int jobId) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                env.jdbcUrl(), env.jdbcUser(), env.jdbcPassword());
             PreparedStatement statement = connection.prepareStatement(
                     """
                     SELECT workload_type, target_rate, target_rate_per_agent
                     FROM job_instance ji
                     WHERE ji.id = ?
                     """)) {
            statement.setInt(1, jobId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No job_instance row for id=" + jobId);
                }
                return new PersistedJobConfiguration(
                        rs.getString("workload_type"),
                        rs.getDouble("target_rate"),
                        rs.getDouble("target_rate_per_agent"));
            }
        }
    }

    public int findJobIdByName(String jobName) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                env.jdbcUrl(), env.jdbcUser(), env.jdbcPassword());
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id FROM job_instance WHERE name = ? ORDER BY id DESC")) {
            statement.setString(1, jobName);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No job_instance row for name=" + jobName);
                }
                return rs.getInt("id");
            }
        }
    }

    public void deleteProject(int projectId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(env.baseUrl() + "/v2/projects/" + projectId))
                    .timeout(Duration.ofSeconds(30))
                    .DELETE()
                    .build();
            env.httpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
            // best-effort cleanup
        }
    }

    public void deleteScript(int scriptId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(env.baseUrl() + "/v2/scripts/" + scriptId))
                    .timeout(Duration.ofSeconds(30))
                    .DELETE()
                    .build();
            env.httpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
            // best-effort cleanup
        }
    }

    private static byte[] loadFixture(String classpathPath) throws IOException {
        try (InputStream in = TankApiClient.class.getClassLoader().getResourceAsStream(classpathPath)) {
            if (in == null) {
                throw new IOException("Missing classpath resource: " + classpathPath);
            }
            return in.readAllBytes();
        }
    }

    private static byte[] multipartBody(String boundary, String fieldName, String filename,
                                        String contentType, byte[] fileBytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String preamble = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + filename + "\"\r\n"
                + "Content-Type: " + contentType + "\r\n\r\n";
        out.write(preamble.getBytes(StandardCharsets.UTF_8));
        out.write(fileBytes);
        out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    public record CreatedJob(int jobId,
                             String createStatus,
                             String requestBody,
                             JobCreationContract.Profile profile) {}

    public record PersistedJobConfiguration(String workloadType,
                                            double targetRampRate,
                                            double targetRatePerAgent) {}
}
