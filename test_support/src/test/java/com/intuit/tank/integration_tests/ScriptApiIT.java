package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptApiIT extends BaseIT {

    private static final String SCRIPTS_ENDPOINT = "/v2/scripts";
    private final List<Integer> createdScriptIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        // Clean up any scripts created during tests
        for (Integer scriptId : createdScriptIds) {
            try {
                deleteScript(scriptId);
            } catch (Exception e) {
                // Log but don't fail the test if cleanup fails
                System.err.println("Failed to cleanup script " + scriptId + ": " + e.getMessage());
            }
        }
        createdScriptIds.clear();
    }

    @Test
    @Tag("integration")
    public void testPingScriptService() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/ping"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG ScriptServiceV2", response.body(), "Should return PONG response");
    }

    @Test
    @Tag("integration")
    public void testGetAllScripts() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT))
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
        assertTrue(responseBody.has("scripts"), "Response should contain 'scripts' field");

        JsonNode scripts = responseBody.get("scripts");
        assertTrue(scripts.isArray(), "Scripts should be an array");
        assertTrue(scripts.size() > 0, "Should contain at least one script");

        // Validate script structure
        JsonNode firstScript = scripts.get(0);
        assertTrue(firstScript.has("id"), "Script should have 'id' field");
        assertTrue(firstScript.has("name"), "Script should have 'name' field");
        assertTrue(firstScript.has("creator"), "Script should have 'creator' field");
    }

    @Test
    @Tag("integration")
    public void testGetAllScriptNames() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/names"))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        Map<String, String> scriptNames = objectMapper.readValue(response.body(), Map.class);
        assertTrue(scriptNames.size() > 0, "Should contain at least one script");

        // Validate structure - keys should be script IDs, values should be script names
        for (Map.Entry<String, String> entry : scriptNames.entrySet()) {
            assertTrue(entry.getKey().matches("\\d+"), "Key should be numeric script ID");
            assertNotNull(entry.getValue(), "Script name should not be null");
            assertFalse(entry.getValue().trim().isEmpty(), "Script name should not be empty");
        }

        // Validate specific known scripts and their IDs
        assertTrue(scriptNames.containsValue("UC7_TY24_IdleSession_final_Remove_UXO"),
                  "Response should contain 'UC7_TY24_IdleSession_final_Remove_UXO'");
        assertTrue(scriptNames.containsValue("UC1.TY24.0314.Return.474"),
                  "Response should contain 'UC1.TY24.0314.Return.474'");
        assertTrue(scriptNames.containsValue("PDS-PRF-Baseline"),
                  "Response should contain 'PDS-PRF-Baseline'");
        assertTrue(scriptNames.containsValue("Simple Endurance Script - 45 min"),
                  "Response should contain 'Simple Endurance Script - 45 min'");

        // Validate specific script ID mappings
        assertEquals("1616", scriptNames.entrySet().stream()
                .filter(entry -> "UC7_TY24_IdleSession_final_Remove_UXO".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null), "Should find script ID 1616 for 'UC7_TY24_IdleSession_final_Remove_UXO'");

        assertEquals("1636", scriptNames.entrySet().stream()
                .filter(entry -> "UC1.TY24.0314.Return.474".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null), "Should find script ID 1636 for 'UC1.TY24.0314.Return.474'");

        assertEquals("1635", scriptNames.entrySet().stream()
                .filter(entry -> "PDS-PRF-Baseline".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null), "Should find script ID 1635 for 'PDS-PRF-Baseline'");

        assertEquals("1610", scriptNames.entrySet().stream()
                .filter(entry -> "Simple Endurance Script - 45 min".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null), "Should find script ID 1610 for 'Simple Endurance Script - 45 min'");

        // Validate that we have a reasonable number of scripts
        assertTrue(scriptNames.size() > 50,
                  "Response should contain multiple scripts (at least 50)");

        // Validate some additional known scripts for robustness
        assertTrue(scriptNames.containsKey("495"), "Should contain script ID 495");
        assertEquals("Lambda", scriptNames.get("495"), "Script ID 495 should be 'Lambda'");

        assertTrue(scriptNames.containsKey("588"), "Should contain script ID 588");
        assertEquals("Simple Endurance Script - 10 m ", scriptNames.get("588"),
                    "Script ID 588 should be 'Simple Endurance Script - 10 m '");
    }

    @Test
    @Tag("integration")
    public void testGetSpecificScript() throws Exception {
        // Arrange - Use a known script ID (you may need to adjust this)
        int scriptId = 1; // Adjust based on your environment

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/" + scriptId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        if (response.statusCode() == 200) {
            JsonNode script = objectMapper.readTree(response.body());
            assertEquals(scriptId, script.get("id").asInt(), "Should return correct script ID");
            assertTrue(script.has("name"), "Script should have name field");
            assertTrue(script.has("creator"), "Script should have creator field");
            assertTrue(script.has("created"), "Script should have created field");
        } else {
            assertEquals(404, response.statusCode(), "Should return 404 if script doesn't exist");
        }
    }

    @Test
    @Tag("integration")
    public void testGetNonExistentScript() throws Exception {
        // Arrange
        int nonExistentScriptId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/" + nonExistentScriptId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent script");
    }

    @Test
    @Tag("integration")
    public void testUploadTankProxyRecording() throws Exception {
        // Arrange
        String scriptName = "Sample_Proxy_Recording_Integration_Test_" + System.currentTimeMillis();
        String suffix = "100";

        // Load the sample proxy recording file from resources
        byte[] fileContent = loadResourceFileAsBytes("testfiles/Sample_Proxy_Recording.xml");

        // Create multipart body directly (like curl -F "file=@filename")
        String boundary = "----IntegrationTestBoundary" + System.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Add file part (equivalent to -F "file=@filename")
        baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Disposition: form-data; name=\"file\"; filename=\"Sample_Proxy_Recording.xml\"\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Type: application/xml\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write(fileContent);
        baos.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "?recording&name=" + scriptName + ".Raw." + suffix))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("scriptId"), "Response should contain scriptId");
        assertTrue(responseBody.get("message").contains("uploaded"), "Response should indicate successful upload");

        // Store for cleanup
        int scriptId = Integer.parseInt(responseBody.get("scriptId"));
        createdScriptIds.add(scriptId);

        // Verify script was created
        verifyScriptExists(scriptId, scriptName + ".Raw." + suffix);
    }

    @Test
    @Tag("integration")
    public void testUploadTankProxyRecordingGzipped() throws Exception {
        String scriptName = "Sample_Proxy_Recording_Gzipped_Java_Test_" + System.currentTimeMillis();
        String suffix = "100";

        byte[] gzippedFileContent = loadResourceFileAsBytes("testfiles/Sample_Proxy_Recording.xml.gz");

        String boundary = "----IntegrationTestBoundary" + System.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Disposition: form-data; name=\"file\"; filename=\"Sample_Proxy_Recording.xml.gz\"\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Type: application/gzip\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write(gzippedFileContent);
        baos.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "?recording&name=" + scriptName + ".Raw." + suffix))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Content-Encoding", "gzip")  // Added to match shell script pattern
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created for gzipped proxy recording upload");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("scriptId"), "Response should contain scriptId");
        assertTrue(responseBody.get("message").contains("uploaded"), "Response should indicate successful upload");

        // Store for cleanup
        int scriptId = Integer.parseInt(responseBody.get("scriptId"));
        createdScriptIds.add(scriptId);

        // Verify script was created
        verifyScriptExists(scriptId, scriptName + ".Raw." + suffix);
    }

    @Test
    @Tag("integration")
    public void testUploadTankScript() throws Exception {
        int scriptId = createTestScriptFromSample();

        verifyScriptExists(scriptId, "Sample Tank Script");
    }

    @Test
    @Tag("integration")
    public void testUploadTankScriptGzipped() throws Exception {
        byte[] gzippedFileContent = loadResourceFileAsBytes("testfiles/Sample_TS.xml.gz");
        String boundary = "----IntegrationTestBoundary" + System.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Disposition: form-data; name=\"file\"; filename=\"Sample_TS.xml.gz\"\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Type: application/gzip\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write(gzippedFileContent);
        baos.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Content-Encoding", "gzip")  // Added for gzipped Tank script upload
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created for gzipped Tank script upload");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("message"), "Response should contain message");

        String message = responseBody.get("message");
        assertTrue(message.contains("script ID"), "Message should contain script ID");

        // Extract script ID using regex pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("script ID (\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        assertTrue(matcher.find(), "Should find script ID in message");

        int scriptId = Integer.parseInt(matcher.group(1));
        assertTrue(scriptId > 0, "Script ID should be positive");

        // Store for cleanup
        createdScriptIds.add(scriptId);

        // Verify script was created (Tank script uses the name from the XML file, not our parameter)
        verifyScriptExists(scriptId, "Sample Tank Script");
    }



    @Test
    @Tag("integration")
    public void testCopyExistingScript() throws Exception {
        // Arrange - First create a script to copy from using Sample_TS.xml
        int originalScriptId = createTestScriptFromSample();
        String copiedScriptName = "Copied_Script_Sample_TS" + originalScriptId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "?copy&sourceId=" + originalScriptId + "&name=" + copiedScriptName))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created for script copy");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        String message = responseBody.get("message");
        assertTrue(message.contains("script ID"), "Message should contain script ID");

        // Extract script ID using regex pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("script ID (\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        assertTrue(matcher.find(), "Should find script ID in message");

        int copiedScriptId = Integer.parseInt(matcher.group(1));
        assertNotEquals(String.valueOf(originalScriptId), copiedScriptId,
                       "New script should have different ID");

        createdScriptIds.add(copiedScriptId);

        // Verify copied script exists
        verifyScriptExists(copiedScriptId, copiedScriptName);
    }

    @Test
    @Tag("integration")
    public void testUploadInvalidScript() throws Exception {
        // Arrange
        String invalidXml = "This is not valid XML content";
        String boundary = "----IntegrationTestBoundary" + System.currentTimeMillis();
        String multipartBody = createMultipartBody(invalidXml, "invalid.xml", boundary);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() >= 400 && response.statusCode() < 500,
                  "Should return 4xx error for invalid XML");
    }

    @Test
    @Tag("integration")
    public void testDownloadScript() throws Exception {
        // Arrange - Create a script first using Sample_TS.xml
        int scriptId = createTestScriptFromSample();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/download/" + scriptId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK for script download");
        assertTrue(response.headers().firstValue("Content-Disposition").isPresent(),
                  "Should have Content-Disposition header");
        assertTrue(response.body().contains("<?xml"), "Response should contain XML content");
        assertTrue(response.body().contains("script"), "Response should contain script XML structure");
    }

    @Test
    @Tag("integration")
    public void testDownloadHarnessScript() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/harness/download/" + 1634))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK for harness download");
        assertEquals("application/xml", response.headers().firstValue("Content-Type").orElse(""),
                    "Should return XML content type");
        assertTrue(response.headers().firstValue("Content-Disposition").isPresent(),
                  "Should have Content-Disposition header");
        assertTrue(response.body().contains("<?xml"), "Response should contain XML content");
    }

    @Test
    @Tag("integration")
    public void testDownloadNonExistentScript() throws Exception {
        // Arrange
        int nonExistentScriptId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/download/" + nonExistentScriptId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent script");
    }

    @Test
    @Tag("integration")
    public void testDeleteScript() throws Exception {
        // Arrange - Create a script to delete using Sample_TS.xml
        String scriptName = "Delete_Test_Script_" + System.currentTimeMillis();
        int scriptId = createTestScriptFromSample();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/" + scriptId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(204, response.statusCode(), "Should return HTTP 204 No Content");

        // Remove from cleanup list since it's already deleted
        createdScriptIds.remove(Integer.valueOf(scriptId));

        // Verify script was deleted
        verifyScriptDeleted(scriptId);
    }

    @Test
    @Tag("integration")
    public void testDeleteNonExistentScript() throws Exception {
        // Arrange
        int nonExistentScriptId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/" + nonExistentScriptId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent script");
    }

    private String createMultipartBody(String content, String filename, String boundary) {
        return "--" + boundary + "\r\n" +
               "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n" +
               "Content-Type: application/xml\r\n\r\n" +
               content + "\r\n" +
               "--" + boundary + "--\r\n";
    }

    private int createTestScriptFromSample() throws Exception {
        // Load the sample Tank script file from resources (like testUploadTankScript)
        byte[] fileContent = loadResourceFileAsBytes("testfiles/Sample_TS.xml");

        // Create multipart body directly (like curl -F "file=@filename")
        String boundary = "----IntegrationTestBoundary" + System.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Add file part (equivalent to -F "file=@filename")
        baos.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Disposition: form-data; name=\"file\"; filename=\"Sample_TS.xml\"\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write("Content-Type: application/xml\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        baos.write(fileContent);
        baos.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(baos.toByteArray()))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Should create script successfully");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("message"), "Response should contain message");

        // Extract script ID from message: "Script Sample Tank Script with script ID 1647 updated successfully"
        String message = responseBody.get("message");
        assertTrue(message.contains("script ID"), "Message should contain script ID");

        // Extract script ID using regex pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("script ID (\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        assertTrue(matcher.find(), "Should find script ID in message");

        int scriptId = Integer.parseInt(matcher.group(1));
        assertTrue(scriptId > 0, "Script ID should be positive");

        createdScriptIds.add(scriptId);
        return scriptId;
    }

    private void verifyScriptExists(int scriptId, String expectedName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/" + scriptId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Script should exist");

        JsonNode script = objectMapper.readTree(response.body());
        assertEquals(scriptId, script.get("id").asInt(), "Should return correct script ID");
        assertEquals(expectedName, script.get("name").asText(), "Should return correct script name");
    }

    private void verifyScriptDeleted(int scriptId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/" + scriptId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Script should be deleted and return 404");
    }

    private void deleteScript(int scriptId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + SCRIPTS_ENDPOINT + "/" + scriptId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        httpClient.send(request, BodyHandlers.ofString());
        // Don't assert status code here since this is cleanup - script might already be deleted
    }
}
