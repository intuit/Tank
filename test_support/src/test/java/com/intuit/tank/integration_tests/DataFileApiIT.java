package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataFileApiIT extends BaseIT {

    private static final String DATAFILES_ENDPOINT = "/v2/datafiles";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Integer> createdDataFileIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        // Clean up any data files created during tests
        for (Integer dataFileId : createdDataFileIds) {
            try {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/" + dataFileId))
                        .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                        .timeout(Duration.ofSeconds(30))
                        .DELETE()
                        .build();
                
                httpClient.send(deleteRequest, BodyHandlers.ofString());
            } catch (Exception e) {
                System.err.println("Failed to cleanup data file " + dataFileId + ": " + e.getMessage());
            }
        }
        createdDataFileIds.clear();
    }

    @Test
    @Tag("integration")
    public void testPingDataFileService_shouldReturnPongResponse() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/ping"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG DataFileServiceV2", response.body(), "Should return PONG response");
    }

    @Test
    @Tag("integration")
    public void testGetAllDataFiles_shouldReturnDataFilesList() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT))
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
        assertTrue(responseBody.has("dataFiles"), "Response should contain 'dataFiles' field");

        JsonNode dataFiles = responseBody.get("dataFiles");
        assertTrue(dataFiles.isArray(), "DataFiles should be an array");

        assertTrue(dataFiles.size() > 0, "Should have at least one data file to test with");
        // Validate data file structure based on provided sample
        JsonNode firstDataFile = dataFiles.get(0);
        assertTrue(firstDataFile.has("id"), "DataFile should have 'id' field");
        assertTrue(firstDataFile.has("created"), "DataFile should have 'created' field");
        assertTrue(firstDataFile.has("modified"), "DataFile should have 'modified' field");
        assertTrue(firstDataFile.has("creator"), "DataFile should have 'creator' field");
        assertTrue(firstDataFile.has("name"), "DataFile should have 'name' field");
        assertTrue(firstDataFile.has("dataUrl"), "DataFile should have 'dataUrl' field");
        assertTrue(firstDataFile.has("comments"), "DataFile should have 'comments' field");

        // Validate data types
        assertTrue(firstDataFile.get("id").isInt(), "DataFile ID should be integer");
        assertTrue(firstDataFile.get("name").isTextual(), "DataFile name should be string");
        assertTrue(firstDataFile.get("creator").isTextual(), "DataFile creator should be string");

        // Validate dataUrl format
        String dataUrl = firstDataFile.get("dataUrl").asText();
        assertTrue(dataUrl.contains("/v2/datafiles/content?id="), "DataUrl should contain correct endpoint");

        // Validate that dataUrl contains the same ID as the data file
        int dataFileId = firstDataFile.get("id").asInt();
        assertTrue(dataUrl.contains("id=" + dataFileId), "DataUrl should contain matching data file ID");
    }

    @Test
    @Tag("integration")
    public void testGetAllDataFileNames_shouldReturnDataFileNamesMap() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/names"))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        Map<String, String> dataFileNames = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(dataFileNames, "DataFile names map should not be null");

        assertTrue(dataFileNames.size() > 0, "Should have at least one data file to test with");
        // Validate that keys are numeric (data file IDs) and values are strings (names)
        for (Map.Entry<String, String> entry : dataFileNames.entrySet()) {
            assertDoesNotThrow(() -> Integer.parseInt(entry.getKey()),
                              "Key should be a valid integer (data file ID)");
            assertNotNull(entry.getValue(), "Data file name should not be null");
            assertFalse(entry.getValue().trim().isEmpty(), "Data file name should not be empty");
        }
    }

    @Test
    @Tag("integration")
    public void testGetSpecificDataFile() throws Exception {
        // Arrange - First get all data files to find a valid ID
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> getAllResponse = httpClient.send(getAllRequest, BodyHandlers.ofString());
        assertEquals(200, getAllResponse.statusCode(), "Should get all data files successfully");

        JsonNode responseBody = objectMapper.readTree(getAllResponse.body());
        JsonNode dataFiles = responseBody.get("dataFiles");
        
        assertTrue(dataFiles.size() > 0, "Should have at least one data file to test with");
        int dataFileId = dataFiles.get(0).get("id").asInt();
        String expectedName = dataFiles.get(0).get("name").asText();

        // Act - Get specific data file
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/" + dataFileId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode dataFile = objectMapper.readTree(response.body());
        assertEquals(dataFileId, dataFile.get("id").asInt(), "Should return correct data file ID");
        assertEquals(expectedName, dataFile.get("name").asText(), "Should return correct data file name");
        assertTrue(dataFile.has("created"), "DataFile should have created timestamp");
        assertTrue(dataFile.has("modified"), "DataFile should have modified timestamp");
        assertTrue(dataFile.has("creator"), "DataFile should have creator");
        assertTrue(dataFile.has("dataUrl"), "DataFile should have dataUrl");
    }

    @Test
    @Tag("integration")
    public void testGetNonExistentDataFile() throws Exception {
        // Arrange
        int nonExistentDataFileId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/" + nonExistentDataFileId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent data file");
    }

    @Test
    @Tag("integration")
    public void testGetDataFileContent() throws Exception {
        int dataFileId = 1400;

        // Act - Get data file content
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/content?id=" + dataFileId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(response.statusCode(), 200);

        assertNotNull(response.body(), "Content should not be null");
        assertEquals("text/plain;charset=UTF-8", response.headers().firstValue("Content-Type").orElse(""),
                    "Should return text/plain;charset=UTF-8 content type");
    }

    @Test
    @Tag("integration")
    public void testDownloadDataFile() throws Exception {
        int dataFileId = 1400;

        // Act - Download data file
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/download/" + dataFileId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        System.out.println(response.body());

        // Assert
        assertEquals(response.statusCode(), 200);
        assertEquals("application/octet-stream",
                   response.headers().firstValue("Content-Type").orElse(""),
                   "Should return application/octet-stream content type");

        assertTrue(response.headers().firstValue("Content-Disposition").isPresent(),
                  "Should have Content-Disposition header for download");

        String contentDisposition = response.headers().firstValue("Content-Disposition").orElse("");
        assertTrue(contentDisposition.contains("attachment"),
                  "Content-Disposition should indicate attachment");
        assertTrue(contentDisposition.contains("filename="),
                  "Content-Disposition should contain filename");

        assertNotNull(response.body(), "Downloaded content should not be null");
    }

    @Test
    @Tag("integration")
    public void testDownloadNonExistentDataFile() throws Exception {
        // Arrange
        int nonExistentDataFileId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/download/" + nonExistentDataFileId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent data file");
    }

    @Test
    @Tag("integration")
    public void testUploadDataFile_shouldCreateNewDataFile() throws Exception {
        // Arrange - Create test CSV content
        String testFileName = "test_datafile_" + System.currentTimeMillis() + ".csv";
        String testContent = "id,name,email\n1,John Doe,john@example.com\n2,Jane Smith,jane@example.com\n";

        // Create multipart form data
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String multipartBody = createMultipartBody(boundary, testFileName, testContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/upload"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("datafileId"), "Response should contain datafileId");
        assertTrue(responseBody.get("message").contains("uploaded"), "Response should indicate successful upload");

        // Store for cleanup
        int dataFileId = Integer.parseInt(responseBody.get("datafileId"));
        createdDataFileIds.add(dataFileId);

        // Verify data file was created
        verifyDataFileExists(dataFileId, testFileName);
    }

    @Test
    @Tag("integration")
    public void testUploadDataFileWithExistingId_shouldOverwriteDataFile() throws Exception {
        // Arrange - First create a data file
        String testFileName = "test_overwrite_datafile_" + System.currentTimeMillis() + ".csv";
        String originalContent = "id,name\n1,Original\n";

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String multipartBody = createMultipartBody(boundary, testFileName, originalContent);

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/upload"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody))
                .build();

        HttpResponse<String> createResponse = httpClient.send(createRequest, BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode(), "Should create initial data file");

        Map<String, String> createResponseBody = objectMapper.readValue(createResponse.body(), Map.class);
        int dataFileId = Integer.parseInt(createResponseBody.get("datafileId"));
        createdDataFileIds.add(dataFileId);

        // Act - Upload new content with existing ID (overwrite)
        String updatedContent = "id,name\n1,Updated\n2,New Entry\n";
        String updatedFileName = "updated_" + testFileName;

        String newBoundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String newMultipartBody = createMultipartBody(newBoundary, updatedFileName, updatedContent);

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/upload?id=" + dataFileId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + newBoundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(newMultipartBody))
                .build();

        HttpResponse<String> updateResponse = httpClient.send(updateRequest, BodyHandlers.ofString());

        // Assert
        assertEquals(201, updateResponse.statusCode(), "Should return HTTP 201 Created for overwrite");

        Map<String, String> updateResponseBody = objectMapper.readValue(updateResponse.body(), Map.class);
        assertEquals(String.valueOf(dataFileId), updateResponseBody.get("datafileId"),
                    "Should return same datafileId for overwrite");
        assertTrue(updateResponseBody.get("message").contains("overwritten with new datafile"),
                  "Response should indicate successful upload");
    }

    @Test
    @Tag("integration")
    public void testDeleteDataFile_shouldRemoveDataFile() throws Exception {
        // Arrange - First create a data file to delete
        String testFileName = "test_delete_datafile_" + System.currentTimeMillis() + ".csv";
        String testContent = "id,name\n1,Test\n";

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String multipartBody = createMultipartBody(boundary, testFileName, testContent);

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/upload"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(multipartBody))
                .build();

        HttpResponse<String> createResponse = httpClient.send(createRequest, BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode(), "Should create data file for deletion test");

        Map<String, String> createResponseBody = objectMapper.readValue(createResponse.body(), Map.class);
        int dataFileId = Integer.parseInt(createResponseBody.get("datafileId"));

        // Act - Delete the data file
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/" + dataFileId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, BodyHandlers.ofString());

        // Assert
        assertEquals(204, deleteResponse.statusCode(), "Should return HTTP 204 No Content for successful deletion");

        // Verify data file no longer exists
        HttpRequest verifyRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/" + dataFileId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> verifyResponse = httpClient.send(verifyRequest, BodyHandlers.ofString());
        assertEquals(404, verifyResponse.statusCode(), "Data file should no longer exist after deletion");
    }

    @Test
    @Tag("integration")
    public void testDeleteNonExistentDataFile() throws Exception {
        // Arrange
        int nonExistentDataFileId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/" + nonExistentDataFileId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent data file deletion");
    }

    @Test
    @Tag("integration")
    public void testUploadInvalidDataFile_shouldReturnValidationError() throws Exception {
        // Arrange - Create invalid multipart request (missing file parameter)
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String invalidMultipartBody = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"invalid\"\r\n\r\n" +
                "invalid content\r\n" +
                "--" + boundary + "--\r\n";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/upload"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(invalidMultipartBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(400, response.statusCode(), "Should return HTTP 400 Bad Request for invalid upload");
    }

    @Test
    @Tag("integration")
    public void testGetDataFileContentWithInvalidId() throws Exception {
        // Arrange
        int invalidDataFileId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/content?id=" + invalidDataFileId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for invalid data file ID");
    }

    // Helper method to create multipart form data
    private String createMultipartBody(String boundary, String fileName, String content) {
        return "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n" +
                "Content-Type: text/csv\r\n\r\n" +
                content + "\r\n" +
                "--" + boundary + "--\r\n";
    }

    // Helper method to create multipart form data with byte content
    private String createMultipartBodyWithBytes(String boundary, String fileName, byte[] content) {
        String header = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";

        // Convert bytes to string (this is a simplified approach for testing)
        String contentStr = new String(content, StandardCharsets.ISO_8859_1);
        return header + contentStr + footer;
    }

    // Helper method to gzip content
    private byte[] gzipContent(String content) throws IOException {
        try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
             java.util.zip.GZIPOutputStream gzipOut = new java.util.zip.GZIPOutputStream(baos)) {
            gzipOut.write(content.getBytes(StandardCharsets.UTF_8));
            gzipOut.finish();
            return baos.toByteArray();
        }
    }

    // Helper method to verify data file exists
    private void verifyDataFileExists(int dataFileId, String expectedName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + DATAFILES_ENDPOINT + "/" + dataFileId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Data file should exist after creation");

        JsonNode dataFile = objectMapper.readTree(response.body());
        assertEquals(dataFileId, dataFile.get("id").asInt(), "Data file ID should match");
        assertEquals(expectedName, dataFile.get("name").asText(), "Data file name should match");
    }
}
