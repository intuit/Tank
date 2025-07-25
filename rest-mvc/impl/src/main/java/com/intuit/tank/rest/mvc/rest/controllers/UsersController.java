package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.AdminTokenService;
import com.intuit.tank.rest.mvc.rest.services.user.UserService;
import com.intuit.tank.user.model.ServiceRequest;
import com.intuit.tank.user.model.ServiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v2/users", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "User Operations", description = "Internal user data operations")
@Hidden
public class UsersController {

        private static final Logger LOG = LogManager.getLogger(UsersController.class);

        // Operation constants
        private static final String OPERATION_ACCESS = "ACCESS";
        private static final String OPERATION_DELETE = "DELETE";

        // Status constants
        private static final String STATUS_ACKNOWLEDGED = "acknowledged";
        private static final String STATUS_FAILED = "failed";

        // Header constants
        private static final String HEADER_AUTHORIZATION = "Authorization";

        @Resource
        private UserService userService;

        @Resource
        private AdminTokenService adminTokenService;

        @Resource
        private HttpServletRequest request;

        @PostMapping("/export")
        @Operation(summary = "Export user data", description = "Exports user data from Tank")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export request acknowledged"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "400", description = "Invalid request")
        })
        public ResponseEntity<ServiceResponse> exportUserData(
                        @Valid @RequestBody ServiceRequest serviceRequest) {

                LOG.info("Export request received for jobId: {} with operation: {} tid: {}",
                                serviceRequest.getJobId(), serviceRequest.getOperation(), serviceRequest.getTid());

                try {
                        // Validate admin token
                        if (!isValidAdminToken()) {
                                LOG.warn("Export request REJECTED: Invalid admin token for jobId: {} tid: {}",
                                                serviceRequest.getJobId(), serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("Invalid admin token")
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Validate operation type
                        if (!OPERATION_ACCESS.equals(serviceRequest.getOperation())) {
                                LOG.warn("Export request REJECTED: Invalid operation {} for jobId: {} tid: {}",
                                                serviceRequest.getOperation(), serviceRequest.getJobId(),
                                                serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("Invalid operation: "
                                                                                + serviceRequest.getOperation())
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Get user identifier from identifiers list
                        String userIdentifier = getLoginIdentifier(serviceRequest.getIdentifiers());
                        if (userIdentifier == null) {
                                LOG.warn("Export request REJECTED: No identifiers provided for jobId: {} tid: {}",
                                                serviceRequest.getJobId(), serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("No identifiers provided")
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Process export request using provided jobId
                        String internalJobId = userService.processExportRequest(serviceRequest.getJobId(),
                                        List.of(userIdentifier), serviceRequest.getTid());
                        LOG.info("Export request processed successfully for jobId: {} with internal jobId: {} tid: {}",
                                        serviceRequest.getJobId(), internalJobId, serviceRequest.getTid());

                        ServiceResponse response = ServiceResponse.builder()
                                        .status(STATUS_ACKNOWLEDGED)
                                        .message("Job accepted for processing")
                                        .jobId(serviceRequest.getJobId())
                                        .tid(serviceRequest.getTid())
                                        .build();

                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        LOG.error("Export request failed for jobId: {} tid: {} - Error: {}",
                                        serviceRequest.getJobId(), serviceRequest.getTid(), e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ServiceResponse.builder()
                                                        .status(STATUS_FAILED)
                                                        .message("Internal server error: " + e.getMessage())
                                                        .jobId(serviceRequest.getJobId())
                                                        .tid(serviceRequest.getTid())
                                                        .build());
                }
        }

        @PostMapping("/delete")
        @Operation(summary = "Delete user data", description = "Deletes/anonymizes user data from Tank")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Delete request acknowledged"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "400", description = "Invalid request")
        })
        public ResponseEntity<ServiceResponse> deleteUserData(
                        @Valid @RequestBody ServiceRequest serviceRequest) {

                LOG.info("Delete request received for jobId: {} with operation: {} tid: {}",
                                serviceRequest.getJobId(), serviceRequest.getOperation(), serviceRequest.getTid());

                try {
                        // Validate admin token
                        if (!isValidAdminToken()) {
                                LOG.warn("Delete request REJECTED: Invalid admin token for jobId: {} tid: {}",
                                                serviceRequest.getJobId(), serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("Invalid admin token")
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Validate operation type
                        if (!OPERATION_DELETE.equals(serviceRequest.getOperation())) {
                                LOG.warn("Delete request REJECTED: Invalid operation {} for jobId: {} tid: {}",
                                                serviceRequest.getOperation(), serviceRequest.getJobId(),
                                                serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("Invalid operation: "
                                                                                + serviceRequest.getOperation())
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Get user identifier from identifiers list
                        String userIdentifier = getLoginIdentifier(serviceRequest.getIdentifiers());
                        if (userIdentifier == null) {
                                LOG.warn("Delete request REJECTED: No identifiers provided for jobId: {} tid: {}",
                                                serviceRequest.getJobId(), serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("No identifiers provided")
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Process delete request using provided jobId
                        String internalJobId = userService.processDeleteRequest(serviceRequest.getJobId(),
                                        List.of(userIdentifier), serviceRequest.getTid());
                        LOG.info("Delete request processed successfully for jobId: {} with internal jobId: {} tid: {}",
                                        serviceRequest.getJobId(), internalJobId, serviceRequest.getTid());

                        ServiceResponse response = ServiceResponse.builder()
                                        .status(STATUS_ACKNOWLEDGED)
                                        .message("Job accepted for processing")
                                        .jobId(serviceRequest.getJobId())
                                        .tid(serviceRequest.getTid())
                                        .build();

                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        LOG.error("Delete request failed for jobId: {} tid: {} - Error: {}",
                                        serviceRequest.getJobId(), serviceRequest.getTid(), e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ServiceResponse.builder()
                                                        .status(STATUS_FAILED)
                                                        .message("Internal server error: " + e.getMessage())
                                                        .jobId(serviceRequest.getJobId())
                                                        .tid(serviceRequest.getTid())
                                                        .build());
                }
        }

        @PostMapping("/delete-check")
        @Operation(summary = "Check if user data can be deleted", description = "Validates whether user data can be safely deleted from Tank")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Delete check completed"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "400", description = "Invalid request")
        })
        public ResponseEntity<ServiceResponse> deleteCheckUserData(
                        @Valid @RequestBody ServiceRequest serviceRequest) {

                LOG.info("Delete check request received for jobId: {} with operation: {} tid: {}",
                                serviceRequest.getJobId(), serviceRequest.getOperation(), serviceRequest.getTid());

                try {
                        // Validate admin token
                        if (!isValidAdminToken()) {
                                LOG.warn("Delete check request REJECTED: Invalid admin token for jobId: {} tid: {}",
                                                serviceRequest.getJobId(), serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("Invalid admin token")
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Validate operation type
                        if (!"DELETE_CHECK".equals(serviceRequest.getOperation())) {
                                LOG.warn("Delete check request REJECTED: Invalid operation {} for jobId: {} tid: {}",
                                                serviceRequest.getOperation(), serviceRequest.getJobId(),
                                                serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("Invalid operation: "
                                                                                + serviceRequest.getOperation())
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Get user identifier from identifiers list
                        String userIdentifier = getLoginIdentifier(serviceRequest.getIdentifiers());
                        if (userIdentifier == null) {
                                LOG.warn("Delete check request REJECTED: No identifiers provided for jobId: {} tid: {}",
                                                serviceRequest.getJobId(), serviceRequest.getTid());
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("No identifiers provided")
                                                                .jobId(serviceRequest.getJobId())
                                                                .tid(serviceRequest.getTid())
                                                                .build());
                        }

                        // Validate deletion safety
                        Map<String, Object> validationResult = userService.validateDeletion(serviceRequest.getJobId(),
                                        userIdentifier);
                        LOG.info("Delete check validation completed for jobId: {} user: {} tid: {}",
                                        serviceRequest.getJobId(), userIdentifier, serviceRequest.getTid());

                        boolean canDelete = (Boolean) validationResult.get("canDelete");
                        String reason = (String) validationResult.get("reason");

                        // Create the data map with canDelete information
                        Map<String, String> data = new HashMap<>();
                        data.put("canDelete", String.valueOf(canDelete));
                        data.put("reason", reason);

                        ServiceResponse response;
                        if (canDelete) {
                                response = ServiceResponse.builder()
                                                .status("completed")
                                                .message("Data can be safely deleted: " + reason)
                                                .data(data)
                                                .jobId(serviceRequest.getJobId())
                                                .tid(serviceRequest.getTid())
                                                .build();
                        } else {
                                response = ServiceResponse.builder()
                                                .status("failed")
                                                .message("Data cannot be deleted: " + reason)
                                                .data(data)
                                                .jobId(serviceRequest.getJobId())
                                                .tid(serviceRequest.getTid())
                                                .build();
                        }

                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        LOG.error("Delete check request failed for jobId: {} tid: {} - Error: {}",
                                        serviceRequest.getJobId(), serviceRequest.getTid(), e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ServiceResponse.builder()
                                                        .status(STATUS_FAILED)
                                                        .message("Internal server error: " + e.getMessage())
                                                        .jobId(serviceRequest.getJobId())
                                                        .tid(serviceRequest.getTid())
                                                        .build());
                }
        }

        @GetMapping("/status")
        @Operation(summary = "Get job status", description = "Gets the status of a user data operation job")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job status retrieved"),
                        @ApiResponse(responseCode = "404", description = "Job not found")
        })
        public ResponseEntity<ServiceResponse> getJobStatus(@RequestParam String jobId) {

                LOG.info("Status request received for jobId: {}", jobId);

                try {
                        Map<String, Object> jobStatus = userService.getJobStatus(jobId);

                        if (jobStatus == null) {
                                LOG.warn("Job not found for jobId: {}", jobId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(ServiceResponse.builder()
                                                                .status(STATUS_FAILED)
                                                                .message("Job not found")
                                                                .jobId(jobId)
                                                                .tid(null)
                                                                .build());
                        }

                        String status = (String) jobStatus.get("status");
                        String message = "Job status: " + status;
                        String tid = (String) jobStatus.get("tid");
                        Map<String, String> data = null;

                        // If job is completed, include the user data
                        if ("completed".equals(status) && jobStatus.containsKey("data")) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> userData = (Map<String, Object>) jobStatus.get("data");
                                if (userData != null) {
                                        // Convert user data to string key-value pairs
                                        data = new HashMap<>();
                                        for (Map.Entry<String, Object> entry : userData.entrySet()) {
                                                if (entry.getValue() != null) {
                                                        data.put(entry.getKey(), entry.getValue().toString());
                                                }
                                        }
                                }
                        }

                        ServiceResponse response = ServiceResponse.builder()
                                        .status(status)
                                        .message(message)
                                        .data(data)
                                        .jobId(jobId)
                                        .tid(tid)
                                        .build();

                        return ResponseEntity.ok(response);

                } catch (Exception e) {
                        LOG.error("Error getting job status for jobId: {} - Error: {}", jobId, e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ServiceResponse.builder()
                                                        .status(STATUS_FAILED)
                                                        .message("Error retrieving job status: " + e.getMessage())
                                                        .jobId(jobId)
                                                        .tid(null)
                                                        .build());
                }
        }

        /**
         * Validates if the provided admin token is valid
         */
        private boolean isValidAdminToken() {
                String authHeader = request.getHeader(HEADER_AUTHORIZATION);

                if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
                        LOG.warn("Admin token validation failed: missing or invalid Authorization header");
                        return false;
                }

                String token = authHeader.substring(7); // Remove "Bearer " prefix
                return adminTokenService.isValidAdminToken(token);
        }

        /**
         * Gets the loginId identifier from the identifiers list, prioritizing valid
         * loginId format
         */
        private String getLoginIdentifier(List<String> identifiers) {
                if (identifiers == null || identifiers.isEmpty()) {
                        return null;
                }

                // First, try to find a loginId identifier
                for (String identifier : identifiers) {
                        if (isValidLoginId(identifier)) {
                                LOG.info("Found valid loginId identifier: {}", identifier);
                                return identifier;
                        }
                }

                // If no valid loginId found, use the first identifier
                String firstIdentifier = identifiers.get(0);
                LOG.info("No valid loginId found, using first identifier: {}", firstIdentifier);
                return firstIdentifier;
        }

        /**
         * Validates if the identifier is a valid loginId format
         * LoginId should match the username format stored in our user database
         */
        private boolean isValidLoginId(String identifier) {
                return identifier != null &&
                                !identifier.trim().isEmpty() &&
                                identifier.matches("^[a-zA-Z0-9._-]{3,20}$");
        }
}
