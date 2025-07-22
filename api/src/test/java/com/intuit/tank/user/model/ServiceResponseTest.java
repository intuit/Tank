package com.intuit.tank.user.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class ServiceResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testBuilder() {
        String status = "completed";
        String message = "Operation successful";
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        String jobId = "job-123";
        String tid = "transaction-456";

        ServiceResponse response = ServiceResponse.builder()
                .status(status)
                .message(message)
                .data(data)
                .jobId(jobId)
                .tid(tid)
                .build();

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(jobId, response.getJobId());
        assertEquals(tid, response.getTid());
    }

    @Test
    void testNoArgsConstructor() {
        ServiceResponse response = new ServiceResponse();
        assertNotNull(response);
    }

    @Test
    void testAllArgsConstructor() {
        String status = "failed";
        String message = "Operation failed";
        Map<String, String> data = new HashMap<>();
        String jobId = "job-456";
        String tid = "transaction-789";

        ServiceResponse response = new ServiceResponse(status, message, data, jobId, tid);

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(jobId, response.getJobId());
        assertEquals(tid, response.getTid());
    }

    @Test
    void testSetters() {
        ServiceResponse response = new ServiceResponse();
        String status = "acknowledged";
        String message = "Job accepted";
        Map<String, String> data = new HashMap<>();
        data.put("name", "testuser");
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();

        response.setStatus(status);
        response.setMessage(message);
        response.setData(data);
        response.setJobId(jobId);
        response.setTid(tid);

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(jobId, response.getJobId());
        assertEquals(tid, response.getTid());
    }

    @Test
    void testSerialization() throws Exception {
        String status = "acknowledged";
        String message = "Job accepted for processing";
        Map<String, String> data = new HashMap<>();
        data.put("name", "testuser");
        data.put("email", "test@example.com");
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();

        ServiceResponse response = ServiceResponse.builder()
            .status(status)
            .message(message)
            .data(data)
            .jobId(jobId)
            .tid(tid)
            .build();

        String json = objectMapper.writeValueAsString(response);

        assertTrue(json.contains("\"status\":\"acknowledged\""));
        assertTrue(json.contains("\"message\":\"Job accepted for processing\""));
        assertTrue(json.contains("\"name\":\"testuser\""));
        assertTrue(json.contains("\"email\":\"test@example.com\""));
        assertTrue(json.contains("\"tid\":"));
        assertTrue(json.contains("\"jobId\":"));
    }

    @Test
    void testDeserialization() throws Exception {
        String status = "completed";
        String message = "Job completed successfully";
        Map<String, String> data = new HashMap<>();
        data.put("name", "testuser");
        data.put("email", "test@example.com");
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();

        String json = """
            {
                "status": "%s",
                "message": "%s",
                "data": {
                    "name": "testuser",
                    "email": "test@example.com"
                },
                "jobId": "%s",
                "tid": "%s"
            }
            """.formatted(status, message, jobId, tid);

        ServiceResponse response = objectMapper.readValue(json, ServiceResponse.class);

        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        assertEquals("testuser", response.getData().get("name"));
        assertEquals("test@example.com", response.getData().get("email"));
        assertEquals(jobId, response.getJobId());
        assertEquals(tid, response.getTid());
    }

    @Test
    void testDeserializationWithNullValues() throws Exception {
        String json = """
            {
                "status": "failed",
                "message": "Job failed"
            }
            """;

        ServiceResponse response = objectMapper.readValue(json, ServiceResponse.class);

        assertEquals("failed", response.getStatus());
        assertEquals("Job failed", response.getMessage());
        assertNull(response.getData());
        assertNull(response.getJobId());
        assertNull(response.getTid());
    }

    @Test
    void testEqualsAndHashCode() {
        String status = "acknowledged";
        String message = "Job accepted";
        Map<String, String> data = new HashMap<>();
        data.put("name", "user1");
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();

        // response1 and response2 have identical field values
        ServiceResponse response1 = new ServiceResponse(status, message, data, jobId, tid);
        ServiceResponse response2 = new ServiceResponse(status, message, new HashMap<>(data), jobId, tid);

        // response3 differs in status and message
        ServiceResponse response3 = new ServiceResponse("failed", "Job failed", data, jobId, tid);

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
} 