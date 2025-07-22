package com.intuit.tank.rest.mvc.rest.services.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

class UserServiceTest {

    @Test
    void testUserServiceInterface() {
        // Test that UserService interface has the expected methods
        // This is more of a compile-time check to ensure the interface contract
        
        // Check that the interface exists and has the expected methods
        assertDoesNotThrow(() -> {
            UserService.class.getMethod("processExportRequest", String.class, List.class, String.class);
            UserService.class.getMethod("processDeleteRequest", String.class, List.class, String.class);
            UserService.class.getMethod("getJobStatus", String.class);
            UserService.class.getMethod("validateDeletion", String.class, String.class);
        });
    }

    @Test
    void testUserServiceMethodSignatures() {
        // Verify method return types
        try {
            assertEquals(String.class, UserService.class.getMethod("processExportRequest", String.class, List.class, String.class).getReturnType());
            assertEquals(String.class, UserService.class.getMethod("processDeleteRequest", String.class, List.class, String.class).getReturnType());
            assertEquals(Map.class, UserService.class.getMethod("getJobStatus", String.class).getReturnType());
            assertEquals(Map.class, UserService.class.getMethod("validateDeletion", String.class, String.class).getReturnType());
        } catch (NoSuchMethodException e) {
            fail("UserService interface methods not found: " + e.getMessage());
        }
    }
} 