/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.AccountUpdate;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.ColumnPreferenceUpdate;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.TablePreferences;
import com.intuit.tank.rest.mvc.rest.services.me.MeServiceV2;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The signed-in user's own account. Requires a user (session or API token), whatever the
 * {@code rest-security-enabled} setting.
 */
@RestController
@RequestMapping(value = "/v2/me", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Me")
public class MeController {

    @Resource
    private MeServiceV2 meService;

    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns the signed-in user, their groups and which rights they hold",
            summary = "Get the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the current user"),
            @ApiResponse(responseCode = "401", description = "Not signed in", content = @Content)
    })
    public ResponseEntity<CurrentUser> getCurrentUser() {
        return ResponseEntity.ok(meService.getCurrentUser());
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Changes the email address and/or password. Changing the password requires the current password",
            summary = "Update the current user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated; returns the current user"),
            @ApiResponse(responseCode = "400", description = "Invalid email, short password or wrong current password", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not signed in", content = @Content)
    })
    public ResponseEntity<CurrentUser> updateAccount(@RequestBody AccountUpdate update) {
        return ResponseEntity.ok(meService.updateAccount(update));
    }

    @RequestMapping(value = "/api-token", method = RequestMethod.POST)
    @Operation(description = "Generates a new API token, replacing any existing one. The token is only returned in this response",
            summary = "Generate an API token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated"),
            @ApiResponse(responseCode = "401", description = "Not signed in", content = @Content)
    })
    public ResponseEntity<ApiTokenResponse> createApiToken() {
        return ResponseEntity.ok(meService.createApiToken());
    }

    @RequestMapping(value = "/api-token", method = RequestMethod.DELETE)
    @Operation(description = "Deletes the API token", summary = "Delete the API token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token deleted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not signed in", content = @Content)
    })
    public ResponseEntity<Void> deleteApiToken() {
        meService.deleteApiToken();
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/preferences", method = RequestMethod.GET)
    @Operation(description = "Returns the column settings for each table", summary = "Get table preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the preferences"),
            @ApiResponse(responseCode = "401", description = "Not signed in", content = @Content)
    })
    public ResponseEntity<TablePreferences> getPreferences() {
        return ResponseEntity.ok(meService.getPreferences());
    }

    @RequestMapping(value = "/preferences/tables/{table}", method = RequestMethod.PUT,
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Changes the width and/or visibility of columns of one table. Columns not listed are unchanged",
            summary = "Update one table's columns")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences updated; returns all tables"),
            @ApiResponse(responseCode = "400", description = "Unknown column, invalid size, or hiding a column that cannot be hidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Unknown table", content = @Content)
    })
    public ResponseEntity<TablePreferences> updateTablePreferences(
            @PathVariable @Parameter(description = "projects, scripts, scriptSteps, datafiles or jobs", required = true) String table,
            @RequestBody List<ColumnPreferenceUpdate> columns) {
        return ResponseEntity.ok(meService.updateTablePreferences(table, columns));
    }

    @RequestMapping(value = "/preferences", method = RequestMethod.DELETE)
    @Operation(description = "Deletes all table preferences so the defaults apply", summary = "Reset table preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Preferences reset", content = @Content)
    })
    public ResponseEntity<Void> resetPreferences() {
        meService.resetPreferences();
        return ResponseEntity.noContent().build();
    }
}
