package com.intuit.tank.user.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ServiceRequest model
 */
public class ServiceRequestTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSerialization() throws Exception {
        ServiceRequest request = ServiceRequest.builder()
            .operation("ACCESS")
            .identifiers(Arrays.asList("user1", "user2"))
            .jobId(UUID.randomUUID().toString())
            .authId("auth-123")
            .tid(UUID.randomUUID().toString())
            .build();

        String json = objectMapper.writeValueAsString(request);

        assertTrue(json.contains("\"operation\":\"ACCESS\""));
        assertTrue(json.contains("\"identifiers\":[\"user1\",\"user2\"]"));
        assertTrue(json.contains("\"authId\":\"auth-123\""));
        assertTrue(json.contains("\"tid\":"));
    }

    @Test
    void testDeserialization() throws Exception {
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        String json = """
            {
                "operation": "DELETE",
                "identifiers": ["user3", "user4"],
                "jobId": "%s",
                "authId": "auth-789",
                "tid": "%s"
            }
            """.formatted(jobId, tid);

        ServiceRequest request = objectMapper.readValue(json, ServiceRequest.class);

        assertEquals("DELETE", request.getOperation());
        assertEquals(Arrays.asList("user3", "user4"), request.getIdentifiers());
        assertEquals(jobId, request.getJobId());
        assertEquals("auth-789", request.getAuthId());
        assertEquals(tid, request.getTid());
    }

    @Test
    void testDeserializationWithNullValues() throws Exception {
        String jobId = UUID.randomUUID().toString();
        String json = """
            {
                "operation": "ACCESS",
                "identifiers": ["user1"],
                "jobId": "%s",
                "authId": "auth-123"
            }
            """.formatted(jobId);

        ServiceRequest request = objectMapper.readValue(json, ServiceRequest.class);

        assertEquals("ACCESS", request.getOperation());
        assertEquals(Arrays.asList("user1"), request.getIdentifiers());
        assertEquals(jobId, request.getJobId());
        assertEquals("auth-123", request.getAuthId());
        assertNull(request.getTid());
    }

    @Test
    void testEqualsAndHashCode() {
        String jobId1 = UUID.randomUUID().toString();
        String jobId2 = UUID.randomUUID().toString();
        String tid1 = UUID.randomUUID().toString();
        String tid2 = UUID.randomUUID().toString();
        
        ServiceRequest request1 = new ServiceRequest("ACCESS", Arrays.asList("user1"), jobId1, "auth1", tid1);
        ServiceRequest request2 = new ServiceRequest("ACCESS", Arrays.asList("user1"), jobId1, "auth1", tid1);
        ServiceRequest request3 = new ServiceRequest("DELETE", Arrays.asList("user2"), jobId2, "auth2", tid2);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }
} 