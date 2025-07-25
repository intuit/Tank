package com.intuit.tank.rest.mvc.rest.services.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intuit.tank.project.User;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private com.intuit.tank.dao.UserDao mockUserDao;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        // Use reflection to set private fields for testing
        setPrivateField(userService, "userDao", mockUserDao);
        setPrivateField(userService, "jobResults", new ConcurrentHashMap<>());
    }

    @Test
    void testProcessExportRequest() {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("test@example.com");
        
        // Execute
        String resultJobId = userService.processExportRequest(jobId, identifiers, tid);
        
        // Verify
        assertEquals(jobId, resultJobId);
        
        // Verify job status was created with initial "acknowledged" status
        // Note: The status will change to "processing" when async processing starts
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        // The status might be "acknowledged" or "processing" depending on timing
        assertTrue(jobStatus.get("status").equals("acknowledged") || jobStatus.get("status").equals("processing"));
        assertEquals("Job accepted for processing", jobStatus.get("message"));
        assertEquals("test@example.com", jobStatus.get("userIdentifier"));
        assertEquals(tid, jobStatus.get("tid"));
    }

    @Test
    void testProcessExportRequest_WithLoginId() {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("mwong1", "test@example.com", "testuser");
        
        // Execute
        String resultJobId = userService.processExportRequest(jobId, identifiers, tid);
        
        // Verify
        assertEquals(jobId, resultJobId);
        
        // Verify job status was created with loginId prioritized
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        // The status might be "acknowledged" or "processing" depending on timing
        assertTrue(jobStatus.get("status").equals("acknowledged") || jobStatus.get("status").equals("processing"));
        assertEquals("Job accepted for processing", jobStatus.get("message"));
        assertEquals("mwong1", jobStatus.get("userIdentifier")); // Should prioritize loginId
        assertEquals(tid, jobStatus.get("tid"));
    }

    @Test
    void testProcessDeleteRequest() {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("test@example.com");
        
        // Execute
        String resultJobId = userService.processDeleteRequest(jobId, identifiers, tid);
        
        // Verify
        assertEquals(jobId, resultJobId);
        
        // Verify job status was created with initial "acknowledged" status
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        // The status might be "acknowledged" or "processing" depending on timing
        assertTrue(jobStatus.get("status").equals("acknowledged") || jobStatus.get("status").equals("processing"));
        assertEquals("Job accepted for processing", jobStatus.get("message"));
        assertEquals("test@example.com", jobStatus.get("userIdentifier"));
        assertEquals(tid, jobStatus.get("tid"));
    }

    @Test
    void testProcessDeleteRequest_WithLoginId() {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("mwong1", "test@example.com", "testuser");
        
        // Execute
        String resultJobId = userService.processDeleteRequest(jobId, identifiers, tid);
        
        // Verify
        assertEquals(jobId, resultJobId);
        
        // Verify job status was created with loginId prioritized
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        // The status might be "acknowledged" or "processing" depending on timing
        assertTrue(jobStatus.get("status").equals("acknowledged") || jobStatus.get("status").equals("processing"));
        assertEquals("Job accepted for processing", jobStatus.get("message"));
        assertEquals("mwong1", jobStatus.get("userIdentifier")); // Should prioritize loginId
        assertEquals(tid, jobStatus.get("tid"));
    }

    @Test
    void testGetJobStatus_ExistingJob() {
        // Setup
        String jobId = UUID.randomUUID().toString();
        Map<String, Object> expectedStatus = new java.util.HashMap<>();
        expectedStatus.put("status", "completed");
        expectedStatus.put("message", "Job completed successfully");
        
        ConcurrentHashMap<String, Map<String, Object>> jobResults = new ConcurrentHashMap<>();
        jobResults.put(jobId, expectedStatus);
        setPrivateField(userService, "jobResults", jobResults);
        
        // Execute
        Map<String, Object> result = userService.getJobStatus(jobId);
        
        // Verify
        assertEquals(expectedStatus, result);
    }

    @Test
    void testGetJobStatus_NonExistentJob() {
        // Setup
        String jobId = UUID.randomUUID().toString();
        
        // Execute
        Map<String, Object> result = userService.getJobStatus(jobId);
        
        // Verify
        assertNull(result);
    }

    @Test
    void testProcessExportRequest_AsyncExecution() throws InterruptedException {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("test@example.com");
        Map<String, Object> exportData = new java.util.HashMap<>();
        exportData.put("name", "testuser");
        exportData.put("email", "test@example.com");
        
        when(mockUserDao.exportUserData("test@example.com")).thenReturn(exportData);
        
        // Execute
        String resultJobId = userService.processExportRequest(jobId, identifiers, tid);
        
        // Wait for async processing to complete
        Thread.sleep(100);
        
        // Verify
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        
        // The job should eventually complete
        int maxWaitTime = 5000; // 5 seconds max wait
        int waitTime = 0;
        while (waitTime < maxWaitTime && "processing".equals(jobStatus.get("status"))) {
            Thread.sleep(100);
            waitTime += 100;
            jobStatus = userService.getJobStatus(jobId);
        }
        
        assertEquals("completed", jobStatus.get("status"));
        assertEquals(exportData, jobStatus.get("data"));
        assertEquals(2, jobStatus.get("recordCount"));
    }

    @Test
    void testProcessDeleteRequest_AsyncExecution() throws InterruptedException {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("test@example.com");
        long deletedCount = 5L;
        
        when(mockUserDao.deleteUserData("test@example.com")).thenReturn(deletedCount);
        
        // Execute
        String resultJobId = userService.processDeleteRequest(jobId, identifiers, tid);
        
        // Wait for async processing to complete
        Thread.sleep(100);
        
        // Verify
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        
        // The job should eventually complete
        int maxWaitTime = 5000; // 5 seconds max wait
        int waitTime = 0;
        while (waitTime < maxWaitTime && "processing".equals(jobStatus.get("status"))) {
            Thread.sleep(100);
            waitTime += 100;
            jobStatus = userService.getJobStatus(jobId);
        }
        
        assertEquals("completed", jobStatus.get("status"));
        assertEquals("User data successfully anonymized", jobStatus.get("message"));
        assertEquals(deletedCount, jobStatus.get("recordsAffected"));
    }

    @Test
    void testProcessExportRequest_UserNotFound() throws InterruptedException {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("nonexistent@example.com");
        
        when(mockUserDao.exportUserData("nonexistent@example.com")).thenReturn(new java.util.HashMap<>());
        
        // Execute
        String resultJobId = userService.processExportRequest(jobId, identifiers, tid);
        
        // Wait for async processing to complete
        Thread.sleep(100);
        
        // Verify
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        
        // The job should eventually complete with no data
        int maxWaitTime = 5000; // 5 seconds max wait
        int waitTime = 0;
        while (waitTime < maxWaitTime && "processing".equals(jobStatus.get("status"))) {
            Thread.sleep(100);
            waitTime += 100;
            jobStatus = userService.getJobStatus(jobId);
        }
        
        assertEquals("no_data", jobStatus.get("status"));
        assertEquals("No data found for user", jobStatus.get("message"));
    }

    @Test
    void testProcessDeleteRequest_UserNotFound() throws InterruptedException {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("nonexistent@example.com");
        
        when(mockUserDao.deleteUserData("nonexistent@example.com")).thenReturn(0L);
        
        // Execute
        String resultJobId = userService.processDeleteRequest(jobId, identifiers, tid);
        
        // Wait for async processing to complete
        Thread.sleep(100);
        
        // Verify
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        
        // The job should eventually complete with no data
        int maxWaitTime = 5000; // 5 seconds max wait
        int waitTime = 0;
        while (waitTime < maxWaitTime && "processing".equals(jobStatus.get("status"))) {
            Thread.sleep(100);
            waitTime += 100;
            jobStatus = userService.getJobStatus(jobId);
        }
        
        assertEquals("no_data", jobStatus.get("status"));
        assertEquals("No data found for user", jobStatus.get("message"));
    }

    @Test
    void testProcessExportRequest_Exception() throws InterruptedException {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("test@example.com");
        
        when(mockUserDao.exportUserData("test@example.com")).thenThrow(new RuntimeException("Database error"));
        
        // Execute
        String resultJobId = userService.processExportRequest(jobId, identifiers, tid);
        
        // Wait for async processing to complete
        Thread.sleep(100);
        
        // Verify
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        
        // The job should eventually fail
        int maxWaitTime = 5000; // 5 seconds max wait
        int waitTime = 0;
        while (waitTime < maxWaitTime && "processing".equals(jobStatus.get("status"))) {
            Thread.sleep(100);
            waitTime += 100;
            jobStatus = userService.getJobStatus(jobId);
        }
        
        assertEquals("failed", jobStatus.get("status"));
        assertTrue(jobStatus.get("message").toString().contains("Database error"));
    }

    @Test
    void testProcessDeleteRequest_Exception() throws InterruptedException {
        // Setup
        String jobId = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        List<String> identifiers = Arrays.asList("test@example.com");
        
        when(mockUserDao.deleteUserData("test@example.com")).thenThrow(new RuntimeException("Database error"));
        
        // Execute
        String resultJobId = userService.processDeleteRequest(jobId, identifiers, tid);
        
        // Wait for async processing to complete
        Thread.sleep(100);
        
        // Verify
        Map<String, Object> jobStatus = userService.getJobStatus(jobId);
        assertNotNull(jobStatus);
        
        // The job should eventually fail
        int maxWaitTime = 5000; // 5 seconds max wait
        int waitTime = 0;
        while (waitTime < maxWaitTime && "processing".equals(jobStatus.get("status"))) {
            Thread.sleep(100);
            waitTime += 100;
            jobStatus = userService.getJobStatus(jobId);
        }
        
        assertEquals("failed", jobStatus.get("status"));
        assertTrue(jobStatus.get("message").toString().contains("Database error"));
    }

    @Test
    void testJobIdGeneration_Uniqueness() {
        // Execute multiple requests to ensure unique job IDs
        String jobId1 = UUID.randomUUID().toString();
        String jobId2 = UUID.randomUUID().toString();
        String jobId3 = UUID.randomUUID().toString();
        
        String resultJobId1 = userService.processExportRequest(jobId1, Arrays.asList("user1@example.com"), UUID.randomUUID().toString());
        String resultJobId2 = userService.processExportRequest(jobId2, Arrays.asList("user2@example.com"), UUID.randomUUID().toString());
        String resultJobId3 = userService.processDeleteRequest(jobId3, Arrays.asList("user3@example.com"), UUID.randomUUID().toString());
        
        // Verify
        assertEquals(jobId1, resultJobId1);
        assertEquals(jobId2, resultJobId2);
        assertEquals(jobId3, resultJobId3);
        assertNotEquals(resultJobId1, resultJobId2);
        assertNotEquals(resultJobId1, resultJobId3);
        assertNotEquals(resultJobId2, resultJobId3);
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }
} 