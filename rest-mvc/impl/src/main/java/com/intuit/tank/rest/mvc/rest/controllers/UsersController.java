package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.AdminTokenService;
import com.intuit.tank.rest.mvc.rest.services.user.UserService;
import com.intuit.tank.user.model.ExportRequest;
import com.intuit.tank.user.model.DeleteRequest;
import com.intuit.tank.user.model.UserOperationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/v2/users", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Users", description = "User data operations")
public class UsersController {

    private static final Logger LOG = LogManager.getLogger(UsersController.class);

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
    public ResponseEntity<UserOperationResponse> exportUserData(
            @Valid @RequestBody ExportRequest request) {
        
        LOG.info("Export request received for user: {}", request.getUserIdentifier());
        
        try {
            // Validate that the current user's token matches the admin token from SSM
            if (!isValidAdminToken()) {
                LOG.warn("Export request REJECTED: Invalid admin token for user: {}", request.getUserIdentifier());
                UserOperationResponse errorResponse = new UserOperationResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Invalid admin token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            // Process export request
            String jobId = userService.processExportRequest(request.getUserIdentifier());
            LOG.info("Export request processed successfully for user: {} with jobId: {}", request.getUserIdentifier(), jobId);
            
            UserOperationResponse response = new UserOperationResponse(jobId, "acknowledged", 
                    "Export request acknowledged", null, null);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOG.error("Export request FAILED for user: {} - Error: {}", request.getUserIdentifier(), e.getMessage(), e);
            UserOperationResponse errorResponse = new UserOperationResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "Delete user data", description = "Deletes/anonymizes user data from Tank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete request acknowledged"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<UserOperationResponse> deleteUserData(
            @Valid @RequestBody DeleteRequest request) {
        
        LOG.info("Delete request received for user: {}", request.getUserIdentifier());
        
        try {
            // Validate that the current user's token matches the admin token from SSM
            if (!isValidAdminToken()) {
                LOG.warn("Delete request REJECTED: Invalid admin token for user: {}", request.getUserIdentifier());
                UserOperationResponse errorResponse = new UserOperationResponse();
                errorResponse.setStatus("error");
                errorResponse.setMessage("Invalid admin token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            // Process delete request
            String jobId = userService.processDeleteRequest(request.getUserIdentifier());
            LOG.info("Delete request processed successfully for user: {} with jobId: {}", request.getUserIdentifier(), jobId);
            
            UserOperationResponse response = new UserOperationResponse(jobId, "acknowledged", 
                    "Delete request acknowledged", null, null);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOG.error("Delete request FAILED for user: {} - Error: {}", request.getUserIdentifier(), e.getMessage(), e);
            UserOperationResponse errorResponse = new UserOperationResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/status/{jobId}")
    @Operation(summary = "Get operation status", description = "Gets the status of an export or delete operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<Map<String, Object>> getOperationStatus(
            @Parameter(description = "Job ID to check status for") @PathVariable String jobId) {
        
        LOG.info("Status request received for jobId: {}", jobId);
        
        try {
            // Validate that the current user's token matches the admin token from SSM
            if (!isValidAdminToken()) {
                LOG.warn("Status request REJECTED: Invalid admin token for jobId: {}", jobId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Get job status
            Map<String, Object> status = userService.getJobStatus(jobId);
            
            if (status == null) {
                LOG.warn("Job not found: {}", jobId);
                return ResponseEntity.notFound().build();
            }
            
            LOG.info("Status retrieved successfully for jobId: {} - Status: {}", jobId, status.get("status"));
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            LOG.error("Status request FAILED for jobId: {} - Error: {}", jobId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Validates that the current user's token matches the admin token from SSM.
     * Uses Tank's existing authentication system to get the current user's token.
     * 
     * @return true if current user's token matches admin token from SSM, false otherwise
     */
    private boolean isValidAdminToken() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            LOG.debug("No valid Authorization header found");
            return false;
        }
        
        try {
            // Extract token from "Bearer <token>" format (same as RestSecurityFilter)
            String token = authHeader.substring(7);
            
            // Use AdminTokenService to validate that this token matches the admin token from SSM
            return adminTokenService.isValidAdminToken(token);
            
        } catch (Exception e) {
            LOG.error("Error validating admin token", e);
            return false;
        }
    }
}
