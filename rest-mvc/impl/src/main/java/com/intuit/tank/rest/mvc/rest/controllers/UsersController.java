package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.user.UserService;
import com.intuit.tank.user.model.ExportRequest;
import com.intuit.tank.user.model.DeleteRequest;
import com.intuit.tank.user.model.UserOperationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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

    @PostMapping(value = "/export", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Export User Data", description = "Exports all user data for compliance purposes.", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Export request accepted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<UserOperationResponse> exportUserData(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody @Parameter(description = "Export request payload", required = true) ExportRequest request) {
        

        if (!isValidAdminToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            String jobId = userService.processExportRequest(request.getUserIdentifier());
            
            UserOperationResponse response = new UserOperationResponse(
                jobId, 
                "ACKNOWLEDGED", 
                "Export request received and is being processed",
                null,
                null
            );
            
            LOG.info("Export request processed for user: {} with jobId: {}", request.getUserIdentifier(), jobId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOG.error("Error processing export request for user: {}", request.getUserIdentifier(), e);
            UserOperationResponse errorResponse = new UserOperationResponse(
                null,
                "FAILED",
                "Failed to process export request: " + e.getMessage(),
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping(value = "/delete", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Delete User Data", description = "Deletes/anonymizes all user data for compliance purposes.", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete request accepted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<UserOperationResponse> deleteUserData(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody @Parameter(description = "Delete request payload", required = true) DeleteRequest request) {
        

        if (!isValidAdminToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            String jobId = userService.processDeleteRequest(request.getUserIdentifier());
            
            UserOperationResponse response = new UserOperationResponse(
                jobId, 
                "ACKNOWLEDGED", 
                "Delete request received and is being processed",
                null,
                null
            );
            
            LOG.info("Delete request processed for user: {} with jobId: {}", request.getUserIdentifier(), jobId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOG.error("Error processing delete request for user: {}", request.getUserIdentifier(), e);
            UserOperationResponse errorResponse = new UserOperationResponse(
                null,
                "FAILED",
                "Failed to process delete request: " + e.getMessage(),
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/status/{jobId}")
    @Operation(summary = "Get Job Status", description = "Returns job status and result for the given jobId.", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    public ResponseEntity<UserOperationResponse> getJobStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("jobId") @Parameter(description = "Job ID", required = true) String jobId) {
        
        // Basic admin token validation
        if (!isValidAdminToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Map<String, Object> jobStatus = userService.getJobStatus(jobId);
            if (jobStatus == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Convert Map to UserOperationResponse
            UserOperationResponse response = new UserOperationResponse();
            response.setJobId(jobId);
            response.setStatus((String) jobStatus.get("status"));
            response.setMessage((String) jobStatus.get("message"));
            response.setData((Map<String, Object>) jobStatus.get("data"));
            response.setRecordsAffected((Long) jobStatus.get("recordsAffected"));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOG.error("Error retrieving job status for jobId: {}", jobId, e);
            UserOperationResponse errorResponse = new UserOperationResponse(
                jobId,
                "FAILED",
                "Failed to retrieve job status: " + e.getMessage(),
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    private boolean isValidAdminToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        
        String token = authHeader.substring(7);
        return "admin-token".equals(token);
    }
}
