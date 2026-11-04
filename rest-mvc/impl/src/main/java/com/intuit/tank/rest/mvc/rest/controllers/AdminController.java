/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.AdminGroup;
import com.intuit.tank.rest.mvc.rest.models.AdminUser;
import com.intuit.tank.rest.mvc.rest.models.AdminUserRequest;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.LogLevelSetting;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.services.admin.AdminServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * The admin pages. Every endpoint needs a signed-in user in the {@code admin} group.
 */
@RestController
@RequestMapping(value = "/v2/admin", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Admin")
public class AdminController {

    @Resource
    private AdminServiceV2 adminService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @Operation(description = "Lists users one page at a time. API tokens are shown only as their last characters",
            summary = "List users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the page"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size or sort", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content)
    })
    public ResponseEntity<PageResponse<AdminUser>> listUsers(
            @RequestParam(required = false) @Parameter(description = "Zero-based page number (default 0)") Integer page,
            @RequestParam(required = false) @Parameter(description = "Page size, 1 to 200 (default 25)") Integer size,
            @RequestParam(required = false) @Parameter(description = "id, name, email, lastLoginTs, created or "
                    + "modified, optionally followed by ,asc or ,desc (default name,asc)") String sort,
            @RequestParam(required = false) @Parameter(description = "Text the name or email contains") String q) {
        return ResponseEntity.ok(adminService.listUsers(page, size, sort, q));
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    @Operation(description = "Returns one user", summary = "Get a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such user", content = @Content)
    })
    public ResponseEntity<AdminUser> getUser(@PathVariable @Parameter(description = "The user ID", required = true) Integer userId) {
        return ResponseEntity.ok(adminService.getUser(userId));
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creates a user with a name, email, password and groups. Without groups the user gets "
            + "the default groups", summary = "Create a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created; returns the user"),
            @ApiResponse(responseCode = "400", description = "Missing name, invalid email, short password or unknown group", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content),
            @ApiResponse(responseCode = "409", description = "A user with that name exists", content = @Content)
    })
    public ResponseEntity<AdminUser> createUser(@RequestBody AdminUserRequest request) {
        AdminUser user = adminService.createUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v2/admin/users/{id}").buildAndExpand(user.id()).toUri();
        return ResponseEntity.created(location).body(user);
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Changes a user's email, password and/or groups; null fields are unchanged. The name "
            + "cannot change, and admins cannot remove themselves from the admin group", summary = "Update a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved; returns the user"),
            @ApiResponse(responseCode = "400", description = "Invalid email, short password, unknown group or name change", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such user", content = @Content)
    })
    public ResponseEntity<AdminUser> updateUser(
            @PathVariable @Parameter(description = "The user ID", required = true) Integer userId,
            @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminService.updateUser(userId, request));
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
    @Operation(description = "Anonymizes the user's data and deletes the user. Refused while the user owns projects",
            summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "400", description = "Admins cannot delete themselves", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such user", content = @Content),
            @ApiResponse(responseCode = "409", description = "The user owns projects; lists them", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(@PathVariable @Parameter(description = "The user ID", required = true) Integer userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/users/{userId}/api-token", method = RequestMethod.POST)
    @Operation(description = "Generates a new API token for the user, replacing any existing one. The token is only "
            + "returned in this response", summary = "Generate a user's API token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated"),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such user", content = @Content)
    })
    public ResponseEntity<ApiTokenResponse> createApiToken(
            @PathVariable @Parameter(description = "The user ID", required = true) Integer userId) {
        return ResponseEntity.ok(adminService.createApiToken(userId));
    }

    @RequestMapping(value = "/users/{userId}/api-token", method = RequestMethod.DELETE)
    @Operation(description = "Deletes the user's API token", summary = "Delete a user's API token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted, or the user had none"),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such user", content = @Content)
    })
    public ResponseEntity<Void> deleteApiToken(@PathVariable @Parameter(description = "The user ID", required = true) Integer userId) {
        adminService.deleteApiToken(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/users/{userId}/preferences", method = RequestMethod.DELETE)
    @Operation(description = "Resets the user's table column preferences to the defaults", summary = "Reset a user's preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reset, or the user had none"),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such user", content = @Content)
    })
    public ResponseEntity<Void> resetPreferences(@PathVariable @Parameter(description = "The user ID", required = true) Integer userId) {
        adminService.resetPreferences(userId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @Operation(description = "Returns the configured security groups and which ones new users get",
            summary = "List security groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the groups"),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content)
    })
    public ResponseEntity<List<AdminGroup>> getGroups() {
        return ResponseEntity.ok(adminService.getGroups());
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    @Operation(description = "Lists the log files on the node that serves the request, Tank logs first. Read one "
            + "with GET /v2/logs/{file}", summary = "List log files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully listed the log files"),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content)
    })
    public ResponseEntity<List<String>> listLogFiles() {
        return ResponseEntity.ok(adminService.listLogFiles());
    }

    @RequestMapping(value = "/log-level", method = RequestMethod.GET)
    @Operation(description = "Returns the log level of the node that serves the request. Each node has its own level",
            summary = "Get the log level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the level and the node name"),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content)
    })
    public ResponseEntity<LogLevelSetting> getLogLevel() {
        return ResponseEntity.ok(adminService.getLogLevel());
    }

    @RequestMapping(value = "/log-level", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Sets the log level of the node that serves the request only, until it restarts",
            summary = "Set the log level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set; returns the level and the node name"),
            @ApiResponse(responseCode = "400", description = "Not a log level", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not an admin", content = @Content)
    })
    public ResponseEntity<LogLevelSetting> setLogLevel(@RequestBody LogLevelSetting request) {
        return ResponseEntity.ok(adminService.setLogLevel(request));
    }
}
